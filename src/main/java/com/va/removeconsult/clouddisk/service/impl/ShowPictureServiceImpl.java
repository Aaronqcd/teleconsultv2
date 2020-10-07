package com.va.removeconsult.clouddisk.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.va.removeconsult.clouddisk.enumeration.AccountAuth;
import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.clouddisk.pojo.PictureViewList;
import com.va.removeconsult.clouddisk.service.ShowPictureService;
import com.va.removeconsult.clouddisk.util.ConfigureReader;
import com.va.removeconsult.dao.NodeDao;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ShowPictureServiceImpl implements ShowPictureService {
	@Resource
	private NodeDao fm;
	@Resource
	private Gson gson;

	
	private PictureViewList foundPictures(final HttpServletRequest request) {
		final String fileId = request.getParameter("fileId");
		if (fileId != null && fileId.length() > 0) {
			final String account = (String) request.getSession().getAttribute("ACCOUNT");
			if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
				final List<Node> nodes = this.fm.queryBySomeFolder(fileId);
				final List<Node> pictureViewList = new ArrayList<Node>();
				int index = 0;
				for (final Node n : nodes) {
					final String fileName = n.getFileName();
					final String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					if (suffix.equals("jpg") || suffix.equals("jpeg") || suffix.equals("gif") || suffix.equals("bmp")
							|| suffix.equals("png")) {
						int pSize = Integer.parseInt(n.getFileSize());
						if (pSize > 1) {
							n.setFilePath("homeController/showCondensedPicture.do?fileId=" + n.getFileId());
						}
						pictureViewList.add(n);
						if (!n.getFileId().equals(fileId)) {
							continue;
						}
						index = pictureViewList.size() - 1;
					}
				}
				final PictureViewList pvl = new PictureViewList();
				pvl.setIndex(index);
				pvl.setPictureViewList(pictureViewList);
				return pvl;
			}
		}
		return null;
	}

	public String getPreviewPictureJson(final HttpServletRequest request) {
		final PictureViewList pvl = this.foundPictures(request);
		if (pvl != null) {
			return gson.toJson((Object) pvl);
		}
		return "ERROR";
	}

	@Override
	public void getCondensedPicture(final HttpServletRequest request, final HttpServletResponse response) {
		
		if (ConfigureReader.instance().authorized((String) request.getSession().getAttribute("ACCOUNT"),
				AccountAuth.DOWNLOAD_FILES)) {
			String fileId = request.getParameter("fileId");
			if (fileId != null) {
				Node node = fm.queryById(fileId);
				if (node != null) {
					File pBlock = new File(ConfigureReader.instance().getFileBlockPath(), node.getFilePath());
					if (pBlock.exists()) {
						try {
						Files.copy(pBlock.toPath(), response.getOutputStream());
					} catch (Exception e1) {
						
					}
//						try {
//							int pSize = Integer.parseInt(node.getFileSize());
//							if (pSize < 3) {
//								Thumbnails.of(pBlock).size(1024, 1024).outputFormat("JPG")
//										.toOutputStream(response.getOutputStream());
//							} else if (pSize < 5) {
//								Thumbnails.of(pBlock).size(1440, 1440).outputFormat("JPG")
//										.toOutputStream(response.getOutputStream());
//							} else {
//								Thumbnails.of(pBlock).size(1680, 1680).outputFormat("JPG")
//										.toOutputStream(response.getOutputStream());
//							}
//						} catch (Exception e) {							
//							try {
//								Files.copy(pBlock.toPath(), response.getOutputStream());
//							} catch (IOException e1) {
//								
//							}
//						}
					}
				}
			}
		}
	}
}
