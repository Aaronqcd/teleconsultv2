package com.va.removeconsult.clouddisk.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.va.removeconsult.clouddisk.service.AccountService;
import com.va.removeconsult.clouddisk.service.FileService;
import com.va.removeconsult.clouddisk.service.FolderService;
import com.va.removeconsult.clouddisk.service.FolderViewService;
import com.va.removeconsult.clouddisk.service.PlayAudioService;
import com.va.removeconsult.clouddisk.service.PlayVideoService;
import com.va.removeconsult.clouddisk.service.ServerInfoService;
import com.va.removeconsult.clouddisk.service.ShowPictureService;


@Controller
@RequestMapping({ "/homeController" })
public class HomeController {
	private static final String CHARSET_BY_AJAX = "text/html; charset=utf-8";
	@Resource
	private ServerInfoService si;
	@Resource
	private AccountService as;
	@Resource
	private FolderViewService fvs;
	@Resource
	private FolderService fs;
	@Resource
	private FileService fis;
	@Resource
	private PlayVideoService pvs;
	@Resource
	private ShowPictureService sps;
	@Resource
	private PlayAudioService pas;

	Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping({ "/getServerOS.ajax" })
	@ResponseBody
	public String getServerOS() {
		return this.si.getOSName();
	}

	@RequestMapping(value = { "/getPublicKey.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String getPublicKey() {
		return this.as.getPublicKey();
	}

	@RequestMapping({ "/doLogin.ajax" })
	@ResponseBody
	public String doLogin(final HttpServletRequest request, final HttpSession session) {
		return this.as.checkLoginRequest(request, session);
	}

	
	@RequestMapping({ "/getNewVerCode.do" })
	public void getNewVerCode(final HttpServletRequest request, final HttpServletResponse response,
			final HttpSession session) {
		as.getNewLoginVerCode(request, response, session);
	}

	@RequestMapping(value = { "/getFolderView.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String getFolderView(final String fid, final HttpSession session, final HttpServletRequest request) {
		return fvs.getFolderViewToJson(fid, session, request);
	}

	@RequestMapping({ "/doLogout.ajax" })
	public @ResponseBody String doLogout(final HttpSession session) {
		this.as.logout(session);
		return "SUCCESS";
	}

	
	@RequestMapping({ "/newFolder.ajax" })
	@ResponseBody
	public String newFolder(final HttpServletRequest request) {
		return this.fs.newFolder(request);
	}

	
	@RequestMapping({ "/deleteFolder.ajax" })
	@ResponseBody
	public String deleteFolder(final HttpServletRequest request) {
		return this.fs.deleteFolder(request);
	}

	@RequestMapping({ "/renameFolder.ajax" })
	@ResponseBody
	public String renameFolder(final HttpServletRequest request) {
		return this.fs.renameFolder(request);
	}

	@RequestMapping(value = { "/douploadFile.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String douploadFile(final HttpServletRequest request, final HttpServletResponse response,
			final MultipartFile file) {
		try {
			return this.fis.doUploadFile(request, response, file);
		}catch(MaxUploadSizeExceededException e){
			logger.error("上传文件超过限制:"+e.getMessage(),e);
			return "uploaderror";
		}catch (Exception e) {
			logger.error("上传文件错误:"+e.getMessage(),e);
			return "uploaderror";
		}
		
		
	}

	
	@RequestMapping(value = { "/checkUploadFile.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String checkUploadFile(final HttpServletRequest request, final HttpServletResponse response) {
		return this.fis.checkUploadFile(request, response);
	}

	@RequestMapping({ "/deleteFile.ajax" })
	@ResponseBody
	public String deleteFile(final HttpServletRequest request) {
		return this.fis.deleteFile(request);
	}

	@RequestMapping({ "/downloadFile.do" })
	public void downloadFile(final HttpServletRequest request, final HttpServletResponse response) {
		this.fis.doDownloadFile(request, response);
	}

	@RequestMapping({ "/renameFile.ajax" })
	@ResponseBody
	public String renameFile(final HttpServletRequest request) {
		return this.fis.doRenameFile(request);
	}

	@RequestMapping(value = { "/playVideo.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String playVideo(final HttpServletRequest request, final HttpServletResponse response) {
		return this.pvs.getPlayVideoJson(request);
	}

	
	@RequestMapping(value = { "/getPrePicture.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String getPrePicture(final HttpServletRequest request) {
		return this.sps.getPreviewPictureJson(request);
	}

	
	@RequestMapping({ "/showCondensedPicture.do" })
	public void showCondensedPicture(final HttpServletRequest request, final HttpServletResponse response) {
		sps.getCondensedPicture(request, response);
	}

	@RequestMapping({ "/deleteCheckedFiles.ajax" })
	@ResponseBody
	public String deleteCheckedFiles(final HttpServletRequest request) {
		return this.fis.deleteCheckedFiles(request);
	}

	@RequestMapping({ "/getPackTime.ajax" })
	@ResponseBody
	public String getPackTime(final HttpServletRequest request) {
		return this.fis.getPackTime(request);
	}

	@RequestMapping({ "/downloadCheckedFiles.ajax" })
	@ResponseBody
	public String downloadCheckedFiles(final HttpServletRequest request) {
		return this.fis.downloadCheckedFiles(request);
	}

	@RequestMapping({ "/downloadCheckedFilesZip.do" })
	public void downloadCheckedFilesZip(final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		this.fis.downloadCheckedFilesZip(request, response);
	}

	@RequestMapping(value = { "/playAudios.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String playAudios(final HttpServletRequest request) {
		return this.pas.getAudioInfoListByJson(request);
	}

	@RequestMapping(value = { "/confirmMoveFiles.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String confirmMoveFiles(final HttpServletRequest request) {
		return fis.confirmMoveFiles(request);
	}

	@RequestMapping({ "/moveCheckedFiles.ajax" })
	@ResponseBody
	public String moveCheckedFiles(final HttpServletRequest request) {
		return fis.doMoveFiles(request);
	}

	@RequestMapping(value = { "/sreachInCompletePath.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String sreachInCompletePath(final HttpServletRequest request) {
		return fvs.getSreachViewToJson(request);
	}
	
	@RequestMapping(value = { "/getFolderTreeJson.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String getFolderTreeJson(final HttpServletRequest request) {
		return this.fs.getFolderTreeJson(request);
	}
	
	
	
	@RequestMapping(value = { "/getTableView.ajax" }, produces = { CHARSET_BY_AJAX })
	@ResponseBody
	public String getTableView(final String fid, final HttpSession session, final HttpServletRequest request) {
		return fvs.getTableViewToJson(fid, session, request);
	}
	
	
}
