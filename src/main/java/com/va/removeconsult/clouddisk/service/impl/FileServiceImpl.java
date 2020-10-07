package com.va.removeconsult.clouddisk.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.va.removeconsult.clouddisk.enumeration.AccountAuth;
import com.va.removeconsult.clouddisk.enumeration.SysConfig;
import com.va.removeconsult.clouddisk.model.Folder;
import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.clouddisk.pojo.CheckUploadFilesRespons;
import com.va.removeconsult.clouddisk.pojo.UploadKeyCertificate;
import com.va.removeconsult.clouddisk.service.FileService;
import com.va.removeconsult.clouddisk.service.FolderService;
import com.va.removeconsult.clouddisk.util.ConfigureReader;
import com.va.removeconsult.clouddisk.util.FileBlockUtil;
import com.va.removeconsult.clouddisk.util.FileNodeUtil;
import com.va.removeconsult.clouddisk.util.FolderUtil;
import com.va.removeconsult.clouddisk.util.LogUtil;
import com.va.removeconsult.clouddisk.util.RangeFileStreamWriter;
import com.va.removeconsult.clouddisk.util.ServerTimeUtil;
import com.va.removeconsult.clouddisk.util.TextFormateUtil;
import com.va.removeconsult.dao.FolderDao;
import com.va.removeconsult.dao.NodeDao;
import com.va.removeconsult.service.UserService;


@Service
public class FileServiceImpl extends RangeFileStreamWriter implements FileService {
	private static final String ERROR_PARAMETER = "errorParameter";
	private static final String NO_AUTHORIZED = "noAuthorized";
	private static final String UPLOADSUCCESS = "uploadsuccess";
	private static final String UPLOADERROR = "uploaderror";
	private static final String UPLOADLIMITERROR = "uploadlimiterror";
	private static final String FILE_TYPE_ERROR = "fileTypeError";

	private static Map<String, UploadKeyCertificate> keyEffecMap = new HashMap<>();

	Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	
	@Resource
	private NodeDao fm;
	@Resource
	private FolderDao flm;
	@Resource
	private LogUtil lu;
	@Resource
	private Gson gson;
	@Resource
	private FileBlockUtil fbu;
	@Resource
	private FolderUtil fu;
	@Resource
	private UserService userService;
	@Resource
	private FolderService folderService;
	
	private static final String CONTENT_TYPE = "application/octet-stream";

	
	public String checkUploadFile(final HttpServletRequest request, final HttpServletResponse response) {
		String account = (String) request.getSession().getAttribute("ACCOUNT");
		final String folderId = request.getParameter("folderId");
		final String nameList = request.getParameter("namelist");
		
		
		
		Map<String, Object> loginInfo = userService.getLoginInfo(request);
		
		if(MapUtils.isNotEmpty(loginInfo)){
			account = loginInfo.get("name").toString();
		}else{
			return UPLOADERROR;
		}

		String name = nameList.replaceAll(" ","");
		final List<String> namelistObj = gson.fromJson(name, new TypeToken<List<String>>() {
		}.getType());
		final List<String> pereFileNameList = new ArrayList<>();
		
		for (final String fileName : namelistObj) {
			String localName = fileName.replaceAll("＂","");
			String fileType = localName.substring(localName.lastIndexOf('.') + 1,localName.length()).toLowerCase();
			//if(!"txt".equals(fileType) && !"doc".equals(fileType) && !"xls".equals(fileType)  && !"ppt".equals(fileType) && !"docx".equals(fileType) && !"xlsx".equals(fileType)
				//	&& !"dcm".equals(fileType) && !"flv".equals(fileType) && !"rmvb".equals(fileType) && !"mp4".equals(fileType) && !"mvb".equals(fileType)) {
				//return FILE_TYPE_ERROR;
			//}
			if("php".equals(fileType) || "asp".equals(fileType) || "jsp".equals(fileType)) {
			    return FILE_TYPE_ERROR;
			}
			if (folderId == null || folderId.length() <= 0 || localName == null || localName.length() <= 0) {
				return ERROR_PARAMETER;
			}
			final List<Node> files = this.fm.queryByParentFolderId(folderId);
			if (files.stream().parallel().anyMatch((n) -> n.getFileName()
					.equals(new String(localName.getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8"))))) {
				pereFileNameList.add(localName);
			}
		}
		
		String key = UUID.randomUUID().toString();
		synchronized (keyEffecMap) {
			keyEffecMap.put(key, new UploadKeyCertificate(namelistObj.size(), account));
		}
		
		CheckUploadFilesRespons cufr = new CheckUploadFilesRespons();
		cufr.setUploadKey(key);
		
		if (pereFileNameList.size() > 0) {
			cufr.setCheckResult("hasExistsNames");
			cufr.setPereFileNameList(pereFileNameList);
		} else {
			cufr.setCheckResult("permitUpload");
			cufr.setPereFileNameList(new ArrayList<String>());
		}
		return gson.toJson(cufr);
	}

	
	public String doUploadFile(final HttpServletRequest request, final HttpServletResponse response,
			final MultipartFile file) {
		try{
			String account = (String) request.getSession().getAttribute("ACCOUNT");
			final String folderId = request.getParameter("folderId");
			final String originalFileName = new String(file.getOriginalFilename().getBytes(Charset.forName("UTF-8")),
					Charset.forName("UTF-8"));
			String fileName = originalFileName;
			final String repeType = request.getParameter("repeType");
			
			Map<String, Object> loginInfo = userService.getLoginInfo(request);
			
			if(MapUtils.isNotEmpty(loginInfo)){
				account = loginInfo.get("name").toString();
			}else{
				return UPLOADERROR;
			}
			
			Integer userId = (Integer) loginInfo.get("id");
			
			
			if (folderId == null || folderId.length() <= 0 || originalFileName == null || originalFileName.length() <= 0) {
				return UPLOADERROR;
			}
			
			
			String sysDiskSizeStr = this.folderService.querySysConfValue(SysConfig.DISK_SIZE.getKey());
			long sysDiskSize = Long.valueOf(sysDiskSizeStr);
			
			
			
			long rootFolderSize = this.folderService.queryRootFolderSize(userId);
			
			
			
			if(rootFolderSize + file.getSize() > sysDiskSize){
				return UPLOADLIMITERROR;
			}
			
			
			String uploadKey = request.getParameter("uploadKey");
			if (uploadKey != null) {
				synchronized (keyEffecMap) {
					UploadKeyCertificate c = keyEffecMap.get(uploadKey);
					if (c != null && c.isEffective()) {
						c.checked();
						account = c.getAccount();
						if (!c.isEffective()) {
							keyEffecMap.remove(uploadKey);
						}
					} else {
						return UPLOADERROR;
					}
				}
			} else {
				return UPLOADERROR;
			}
			
			final List<Node> files = this.fm.queryByParentFolderId(folderId);
			if (files.parallelStream().anyMatch((e) -> e.getFileName().equals(originalFileName))) {
				
				if (repeType != null) {
					switch (repeType) {
					
					case "skip":
						return UPLOADSUCCESS;
					
					case "cover":
						
						if (!ConfigureReader.instance().authorized(account, AccountAuth.DELETE_FILE_OR_FOLDER)) {
							return UPLOADERROR;
						}
						for (Node f : files) {
							if (f.getFileName().equals(originalFileName)) {
								File file2 = fbu.getFileFromBlocks(f);
								try {
									file.transferTo(file2);
									f.setFileSize(fbu.getFileSize(file));
									f.setFileCreationDate(ServerTimeUtil.accurateToDay());
									if (account != null) {
										f.setFileCreator(account);
									} else {
										f.setFileCreator("\u533f\u540d\u7528\u6237");
									}
									if (fm.update(f) > 0) {
										this.lu.writeUploadFileEvent(f, account);
										return UPLOADSUCCESS;
									} else {
										return UPLOADERROR;
									}
								} catch (Exception e) {
									
									return UPLOADERROR;
								}
							}
						}
						return UPLOADERROR;
					
					case "both":
						
						fileName = FileNodeUtil.getNewNodeName(originalFileName, files);
						break;
					default:
						
						return UPLOADERROR;
					}
				} else {
					
					return UPLOADERROR;
				}
			}
			
			final String path = this.fbu.saveToFileBlocks(file);
			if (path.equals("ERROR")) {
				return UPLOADERROR;
			}
			final String fsize = this.fbu.getFileSize(file);
			final Node f2 = new Node();
			f2.setFileId(UUID.randomUUID().toString());
			if (account != null) {
				f2.setFileCreator(account);
			} else {
				f2.setFileCreator("\u533f\u540d\u7528\u6237");
			}
			f2.setFileUserId(userId);
			f2.setFileCreationDate(ServerTimeUtil.accurateToDay());
			f2.setFileName(fileName);
			f2.setFileParentFolder(folderId);
			f2.setFilePath(path);
			f2.setFileSize(fsize);
			int i = 0;
			
			while (true) {
				try {
					if (this.fm.insert(f2) > 0) {
						this.lu.writeUploadFileEvent(f2, account);
						return UPLOADSUCCESS;
					}
					break;
				} catch (Exception e) {
					f2.setFileId(UUID.randomUUID().toString());
					i++;
				}
				if (i >= 10) {
					break;
				}
			}
		}catch(MaxUploadSizeExceededException e){
			logger.error("上传文件超过限制:"+e.getMessage(),e);
			return UPLOADERROR;
		}catch (Exception e) {
			logger.error("上传文件错误:"+e.getMessage(),e);
		}
		
		return UPLOADERROR;
	}

	
	public String deleteFile(final HttpServletRequest request) {
		
		final String fileId = request.getParameter("fileId");
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		if (!ConfigureReader.instance().authorized(account, AccountAuth.DELETE_FILE_OR_FOLDER)) {
			return NO_AUTHORIZED;
		}
		if (fileId == null || fileId.length() <= 0) {
			return ERROR_PARAMETER;
		}
		
		final Node file = this.fm.queryById(fileId);
		if (file == null) {
			return ERROR_PARAMETER;
		}
		
		if (!this.fbu.deleteFromFileBlocks(file)) {
			return "cannotDeleteFile";
		}
		
		if (this.fm.deleteById(fileId) > 0) {
			this.lu.writeDeleteFileEvent(request, file);
			return "deleteFileSuccess";
		}
		return "cannotDeleteFile";
	}

	
	public void doDownloadFile(final HttpServletRequest request, final HttpServletResponse response) {
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		
		if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
			
			final String fileId = request.getParameter("fileId");
			if (fileId != null) {
				final Node f = this.fm.queryById(fileId);
				if (f != null) {
					
					final File fo = this.fbu.getFileFromBlocks(f);
					writeRangeFileStream(request, response, fo, f.getFileName(), CONTENT_TYPE);
					
					if (request.getHeader("Range") == null) {
						this.lu.writeDownloadFileEvent(request, f);
					}
					return;
				}
			}
		}
		try {
			
			response.sendError(404);
		} catch (IOException e) {
			

		}
	}

	
	public String doRenameFile(final HttpServletRequest request) {
		final String fileId = request.getParameter("fileId");
		final String newFileName = request.getParameter("newFileName");
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		
		if (!ConfigureReader.instance().authorized(account, AccountAuth.RENAME_FILE_OR_FOLDER)) {
			return NO_AUTHORIZED;
		}
		
		if (fileId == null || fileId.length() <= 0 || newFileName == null || newFileName.length() <= 0) {
			return ERROR_PARAMETER;
		}
		if (!TextFormateUtil.instance().matcherFileName(newFileName) || newFileName.indexOf(".") == 0) {
			return ERROR_PARAMETER;
		}
		final Node file = this.fm.queryById(fileId);
		if (file == null) {
			return ERROR_PARAMETER;
		}
		if (!file.getFileName().equals(newFileName)) {
			
			if (fm.queryBySomeFolder(fileId).parallelStream().anyMatch((e) -> e.getFileName().equals(newFileName))) {
				return "nameOccupied";
			}
			
			final Map<String, String> map = new HashMap<String, String>();
			map.put("fileId", fileId);
			map.put("newFileName", newFileName);
			if (this.fm.updateFileNameById(map) == 0) {
				
				return "cannotRenameFile";
			}
		}
		this.lu.writeRenameFileEvent(request, file, newFileName);
		return "renameFileSuccess";
	}

	
	public String deleteCheckedFiles(final HttpServletRequest request) {
		final String strIdList = request.getParameter("strIdList").replace("＂","");
		final String strFidList = request.getParameter("strFidList").replace("＂","");
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		
		if (ConfigureReader.instance().authorized(account, AccountAuth.DELETE_FILE_OR_FOLDER)) {
			try {
				
				final List<String> idList = gson.fromJson(strIdList, new TypeToken<List<String>>() {
				}.getType());
				
				for (final String fileId : idList) {
					if (fileId == null || fileId.length() <= 0) {
						return ERROR_PARAMETER;
					}
					final Node file = this.fm.queryById(fileId);
					if (file == null) {
						return "deleteFileSuccess";
					}
					
					if (!this.fbu.deleteFromFileBlocks(file)) {
						return "cannotDeleteFile";
					}
					
					if (this.fm.deleteById(fileId) <= 0) {
						return "cannotDeleteFile";
					}
					
					this.lu.writeDeleteFileEvent(request, file);
				}
				
				final List<String> fidList = gson.fromJson(strFidList, new TypeToken<List<String>>() {
				}.getType());
				for (String fid : fidList) {
					Folder folder = flm.queryById(fid);
					final List<Folder> l = this.fu.getParentList(fid);
					if (fu.deleteAllChildFolder(fid) <= 0) {
						return "cannotDeleteFile";
					} else {
						this.lu.writeDeleteFolderEvent(request, folder, l);
					}
				}
				return "deleteFileSuccess";
			} catch (Exception e) {
				return ERROR_PARAMETER;
			}
		}
		return NO_AUTHORIZED;
	}

	
	public String downloadCheckedFiles(final HttpServletRequest request) {
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		
		if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
			final String strIdList = request.getParameter("strIdList").replace("＂","");
			final String strFidList = request.getParameter("strFidList").replace("＂","");
			try {
				
				final List<String> idList = gson.fromJson(strIdList, new TypeToken<List<String>>() {
				}.getType());
				final List<String> fidList = gson.fromJson(strFidList, new TypeToken<List<String>>() {
				}.getType());
				
				if (idList.size() > 0 || fidList.size() > 0) {
					final String zipname = this.fbu.createZip(idList, fidList, account);
					this.lu.writeDownloadCheckedFileEvent(request, idList);
					
					return zipname;
				}
			} catch (Exception ex) {
			}
		}
		return "ERROR";
	}

	
	public void downloadCheckedFilesZip(final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		final String zipname = request.getParameter("zipId");
		if (zipname != null && !zipname.equals("ERROR")) {
			final String tfPath = ConfigureReader.instance().getTemporaryfilePath();
			final File zip = new File(tfPath, zipname);
			String fname = ServerTimeUtil.accurateToDay() + "_\u6253\u5305\u4e0b\u8f7d.zip";
			if (zip.exists()) {
				writeRangeFileStream(request, response, zip, fname, CONTENT_TYPE);
				zip.delete();
			}
		}
	}

	public String getPackTime(final HttpServletRequest request) {
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
			final String strIdList = request.getParameter("strIdList").replace("＂","");
			final String strFidList = request.getParameter("strFidList").replace("＂","");
			try {
				final List<String> idList = gson.fromJson(strIdList, new TypeToken<List<String>>() {
				}.getType());
				final List<String> fidList = gson.fromJson(strFidList, new TypeToken<List<String>>() {
				}.getType());
				for (String fid : fidList) {
					countFolderFilesId(account, fid, fidList);
				}
				long packTime = 0L;
				for (final String fid : idList) {
					final Node n = this.fm.queryById(fid);
					final File f = new File(ConfigureReader.instance().getFileBlockPath(), n.getFilePath());
					if (f.exists()) {
						packTime += f.length() / 25000000L;
					}
				}
				if (packTime < 4L) {
					return "\u9a6c\u4e0a\u5b8c\u6210";
				}
				if (packTime >= 4L && packTime < 10L) {
					return "\u5927\u7ea610\u79d2";
				}
				if (packTime >= 10L && packTime < 35L) {
					return "\u4e0d\u5230\u534a\u5206\u949f";
				}
				if (packTime >= 35L && packTime < 65L) {
					return "\u5927\u7ea61\u5206\u949f";
				}
				if (packTime >= 65L) {
					return "\u8d85\u8fc7" + packTime / 60L
							+ "\u5206\u949f\uff0c\u8017\u65f6\u8f83\u957f\uff0c\u5efa\u8bae\u76f4\u63a5\u4e0b\u8f7d";
				}
			} catch (Exception ex) {
			}
		}
		return "0";
	}

	
	private void countFolderFilesId(String account, String fid, List<String> idList) {
		Folder f = flm.queryById(fid);
		if (ConfigureReader.instance().accessFolder(f, account)) {
			idList.addAll(Arrays.asList(
					fm.queryByParentFolderId(fid).parallelStream().map((e) -> e.getFileId()).toArray(String[]::new)));
			List<Folder> cFolders = flm.queryByParentId(fid);
			for (Folder cFolder : cFolders) {
				countFolderFilesId(account, cFolder.getFolderId(), idList);
			}
		}
	}

	@Override
	public String doMoveFiles(HttpServletRequest request) {
		
		final String strIdList = request.getParameter("strIdList").replace("＂","");
		final String strFidList = request.getParameter("strFidList").replace("＂","");
		final String strOptMap = request.getParameter("strOptMap");
		final String locationpath = request.getParameter("locationpath");
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		if (ConfigureReader.instance().authorized(account, AccountAuth.MOVE_FILES)) {
			try {
				final List<String> idList = gson.fromJson(strIdList, new TypeToken<List<String>>() {
				}.getType());
				final Map<String, String> optMap = gson.fromJson(strOptMap, new TypeToken<Map<String, String>>() {
				}.getType());
				for (final String id : idList) {
					if (id == null || id.length() <= 0) {
						return ERROR_PARAMETER;
					}
					final Node node = this.fm.queryById(id);
					if (node == null) {
						return ERROR_PARAMETER;
					}
					if (node.getFileParentFolder().equals(locationpath)) {
						break;
					}
					if (fm.queryByParentFolderId(locationpath).parallelStream()
							.anyMatch((e) -> e.getFileName().equals(node.getFileName()))) {
						if (optMap.get(id) == null) {
							return ERROR_PARAMETER;
						}
						switch (optMap.get(id)) {
						case "cover":
							if (!ConfigureReader.instance().authorized(account, AccountAuth.DELETE_FILE_OR_FOLDER)) {
								return NO_AUTHORIZED;
							}
							Node n = fm.queryByParentFolderId(locationpath).parallelStream()
									.filter((e) -> e.getFileName().equals(node.getFileName())).findFirst().get();
							if (fm.deleteById(n.getFileId()) > 0) {
								Map<String, String> map = new HashMap<>();
								map.put("fileId", node.getFileId());
								map.put("locationpath", locationpath);
								if (this.fm.moveById(map) <= 0) {
									return "cannotMoveFiles";
								}
							} else {
								return "cannotMoveFiles";
							}
							this.lu.writeMoveFileEvent(request, node);
							break;
						case "both":
							node.setFileName(FileNodeUtil.getNewNodeName(node.getFileName(),
									fm.queryByParentFolderId(locationpath)));
							if (fm.update(node) <= 0) {
								return "cannotMoveFiles";
							}
							Map<String, String> map = new HashMap<>();
							map.put("fileId", node.getFileId());
							map.put("locationpath", locationpath);
							if (this.fm.moveById(map) <= 0) {
								return "cannotMoveFiles";
							}
							this.lu.writeMoveFileEvent(request, node);
							break;
						case "skip":
							break;
						default:
							return ERROR_PARAMETER;
						}
					} else {
						Map<String, String> map = new HashMap<>();
						map.put("fileId", node.getFileId());
						map.put("locationpath", locationpath);
						if (this.fm.moveById(map) <= 0) {
							return "cannotMoveFiles";
						}
						this.lu.writeMoveFileEvent(request, node);
					}
				}
				final List<String> fidList = gson.fromJson(strFidList, new TypeToken<List<String>>() {
				}.getType());
				for (final String fid : fidList) {
					if (fid == null || fid.length() <= 0) {
						return ERROR_PARAMETER;
					}
					final Folder folder = this.flm.queryById(fid);
					if (folder == null) {
						return ERROR_PARAMETER;
					}
					if (folder.getFolderParent().equals(locationpath)) {
						break;
					}
					if (fid.equals(locationpath) || fu.getParentList(locationpath).parallelStream()
							.anyMatch((e) -> e.getFolderId().equals(folder.getFolderId()))) {
						return ERROR_PARAMETER;
					}
					if (flm.queryByParentId(locationpath).parallelStream()
							.anyMatch((e) -> e.getFolderName().equals(folder.getFolderName()))) {
						if (optMap.get(fid) == null) {
							return ERROR_PARAMETER;
						}
						switch (optMap.get(fid)) {
						case "cover":
							if (!ConfigureReader.instance().authorized(account, AccountAuth.DELETE_FILE_OR_FOLDER)) {
								return NO_AUTHORIZED;
							}
							Folder f = flm.queryByParentId(locationpath).parallelStream()
									.filter((e) -> e.getFolderName().equals(folder.getFolderName())).findFirst().get();
							Map<String, String> map = new HashMap<>();
							map.put("folderId", folder.getFolderId());
							map.put("locationpath", locationpath);
							if (this.flm.moveById(map) > 0) {
								if (fu.deleteAllChildFolder(f.getFolderId()) > 0) {
									this.lu.writeMoveFileEvent(request, folder);
									break;
								}
							}
							return "cannotMoveFiles";
						case "both":
							Map<String, String> map3 = new HashMap<>();
							map3.put("folderId", folder.getFolderId());
							map3.put("locationpath", locationpath);
							if (this.flm.moveById(map3) > 0) {
								Map<String, String> map2 = new HashMap<String, String>();
								map2.put("folderId", folder.getFolderId());
								map2.put("newName", FileNodeUtil.getNewFolderName(folder.getFolderName(),
										flm.queryByParentId(locationpath)));
								if (flm.updateFolderNameById(map2) <= 0) {
									return "cannotMoveFiles";
								}
								this.lu.writeMoveFileEvent(request, folder);
								break;
							}
							this.lu.writeMoveFileEvent(request, folder);
							break;
						case "skip":
							break;
						default:
							return ERROR_PARAMETER;
						}
					} else {
						Map<String, String> map = new HashMap<>();
						map.put("folderId", folder.getFolderId());
						map.put("locationpath", locationpath);
						if (this.flm.moveById(map) > 0) {
							this.lu.writeMoveFileEvent(request, folder);
						} else {
							return "cannotMoveFiles";
						}
					}
				}
				return "moveFilesSuccess";
			} catch (Exception e) {
				logger.error("�ƶ��ļ����� = " + e.getMessage() , e);
				return ERROR_PARAMETER;
			}
		}
		return NO_AUTHORIZED;
	}

	@Override
	public String confirmMoveFiles(HttpServletRequest request) {
		
		final String strIdList = request.getParameter("strIdList").replace("＂","");
		final String strFidList = request.getParameter("strFidList").replace("＂","");
		final String locationpath = request.getParameter("locationpath");
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		if (ConfigureReader.instance().authorized(account, AccountAuth.MOVE_FILES)) {
			try {
				final List<String> idList = gson.fromJson(strIdList, new TypeToken<List<String>>() {
				}.getType());
				final List<String> fidList = gson.fromJson(strFidList, new TypeToken<List<String>>() {
				}.getType());
				List<Node> repeNodes = new ArrayList<>();
				List<Folder> repeFolders = new ArrayList<>();
				for (final String fileId : idList) {
					if (fileId == null || fileId.length() <= 0) {
						return ERROR_PARAMETER;
					}
					final Node node = this.fm.queryById(fileId);
					if (node == null) {
						return ERROR_PARAMETER;
					}
					if (node.getFileParentFolder().equals(locationpath)) {
						break;
					}
					if (fm.queryByParentFolderId(locationpath).parallelStream()
							.anyMatch((e) -> e.getFileName().equals(node.getFileName()))) {
						repeNodes.add(node);
					}
				}
				for (final String folderId : fidList) {
					if (folderId == null || folderId.length() <= 0) {
						return ERROR_PARAMETER;
					}
					final Folder folder = this.flm.queryById(folderId);
					if (folder == null) {
						return ERROR_PARAMETER;
					}
					if (folder.getFolderParent().equals(locationpath)) {
						break;
					}
					List<Folder> parentList = fu.getParentList(locationpath);
					if (folderId.equals(locationpath)){
						if(CollectionUtils.isNotEmpty(parentList)){
							for (Folder target : parentList) {
								if(target.getFolderId().equals(folder.getFolderId())){
									return "CANT_MOVE_TO_INSIDE:" + folder.getFolderName();
								}
							}
						}
					}
					if(CollectionUtils.isNotEmpty(parentList)){
						for (Folder target : parentList) {
							if(target.getFolderName().equals(folder.getFolderName())){
								repeFolders.add(folder);
								break;
							}
						}
					}
					
					
				}
				if (repeNodes.size() > 0 || repeFolders.size() > 0) {
					Map<String, List<? extends Object>> repeMap = new HashMap<>();
					repeMap.put("repeFolders", repeFolders);
					repeMap.put("repeNodes", repeNodes);
					return "duplicationFileName:" + gson.toJson(repeMap);
				}
				return "confirmMoveFiles";
			} catch (Exception e) {
				e.printStackTrace();
				return ERROR_PARAMETER;
			}
		}
		return NO_AUTHORIZED;
	}

}
