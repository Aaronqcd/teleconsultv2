package com.va.removeconsult.clouddisk.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.va.removeconsult.clouddisk.enumeration.AccountAuth;
import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.clouddisk.service.ExternalDownloadService;
import com.va.removeconsult.clouddisk.util.ConfigureReader;
import com.va.removeconsult.clouddisk.util.FileBlockUtil;
import com.va.removeconsult.clouddisk.util.LogUtil;
import com.va.removeconsult.clouddisk.util.RangeFileStreamWriter;
import com.va.removeconsult.dao.NodeDao;

@Service
public class ExternalDownloadServiceImpl extends RangeFileStreamWriter implements ExternalDownloadService {

	private static Map<String, String> downloadKeyMap = new HashMap<>();
	private static final String CONTENT_TYPE = "application/octet-stream";

	@Resource
	private NodeDao nm;
	@Resource
	private LogUtil lu;
	@Resource
	private FileBlockUtil fbu;

	@Override
	public String getDownloadKey(HttpServletRequest request) {
		
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		
		if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
			
			final String fileId = request.getParameter("fId");
			if (fileId != null) {
				final Node f = this.nm.queryById(fileId);
				if (f != null) {
					
					synchronized (downloadKeyMap) {
						
						this.lu.writeShareFileURLEvent(request, f);
						if (downloadKeyMap.containsValue(f.getFileId())) {
							Entry<String, String> k = downloadKeyMap.entrySet().parallelStream()
									.filter((e) -> e.getValue().equals(f.getFileId())).findFirst().get();
							return k.getKey();
						} else {
							String dKey = UUID.randomUUID().toString();
							downloadKeyMap.put(dKey, f.getFileId());
							return dKey;
						}
					}
				}
			}
		}
		return "ERROR";
	}

	@Override
	public void downloadFileByKey(HttpServletRequest request, HttpServletResponse response) {
		final String dkey = request.getParameter("dkey");
		
		if (dkey != null) {
			
			String fId = null;
			synchronized (downloadKeyMap) {
				fId = downloadKeyMap.get(dkey);
			}
			if (fId != null) {
				Node f = this.nm.queryById(fId);
				if (f != null) {
					File target = this.fbu.getFileFromBlocks(f);
					if (target != null && target.isFile()) {
						writeRangeFileStream(request, response, target, f.getFileName(), CONTENT_TYPE);
						if (request.getHeader("Range") == null) {
							this.lu.writeDownloadFileByKeyEvent(f);
						}
						return;
					}
				}
			}
		}
		try {
			
			response.sendError(404);
		} catch (IOException e) {

		}
	}

}
