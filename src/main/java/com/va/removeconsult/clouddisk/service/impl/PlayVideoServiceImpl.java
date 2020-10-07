package com.va.removeconsult.clouddisk.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.va.removeconsult.clouddisk.enumeration.AccountAuth;
import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.clouddisk.pojo.VideoInfo;
import com.va.removeconsult.clouddisk.service.PlayVideoService;
import com.va.removeconsult.clouddisk.util.ConfigureReader;
import com.va.removeconsult.clouddisk.util.FileBlockUtil;
import com.va.removeconsult.dao.NodeDao;

import ws.schild.jave.MultimediaObject;

@Service
public class PlayVideoServiceImpl implements PlayVideoService {
	@Resource
	private NodeDao fm;
	@Resource
	private Gson gson;
	@Resource
	private FileBlockUtil fbu;

	private VideoInfo foundVideo(final HttpServletRequest request) {
		final String fileId = request.getParameter("fileId");
		if (fileId != null && fileId.length() > 0) {
			final Node f = this.fm.queryById(fileId);
			final VideoInfo vi = new VideoInfo(f);
			if (f != null) {
				final String account = (String) request.getSession().getAttribute("ACCOUNT");
				if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
					final String fileName = f.getFileName();
					
					final String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					switch (suffix) {
					case "mp4":
					case "mov":
						
						MultimediaObject mo = new MultimediaObject(fbu.getFileFromBlocks(f));
						try {
							if (mo.getInfo().getVideo().getDecoder().indexOf("h264") >= 0) {
								vi.setNeedEncode("N");
								return vi;
							}
						} catch (Exception e) {

						}
						
						vi.setNeedEncode("Y");
						return vi;
					case "webm":
					case "avi":
					case "wmv":
					case "mkv":
					case "flv":
						vi.setNeedEncode("Y");
						return vi;
					default:
						break;
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getPlayVideoJson(final HttpServletRequest request) {
		final VideoInfo v = this.foundVideo(request);
		if (v != null) {
			return gson.toJson((Object) v);
		}
		return "ERROR";
	}
}
