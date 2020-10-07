package com.va.removeconsult.clouddisk.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.va.removeconsult.clouddisk.enumeration.AccountAuth;
import com.va.removeconsult.clouddisk.enumeration.SysConfig;
import com.va.removeconsult.clouddisk.model.Folder;
import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.clouddisk.pojo.FolderTree;
import com.va.removeconsult.clouddisk.service.FolderService;
import com.va.removeconsult.clouddisk.util.ConfigureReader;
import com.va.removeconsult.clouddisk.util.FileBlockUtil;
import com.va.removeconsult.clouddisk.util.FolderUtil;
import com.va.removeconsult.clouddisk.util.LogUtil;
import com.va.removeconsult.clouddisk.util.ServerTimeUtil;
import com.va.removeconsult.clouddisk.util.TextFormateUtil;
import com.va.removeconsult.dao.FolderDao;
import com.va.removeconsult.dao.NodeDao;
import com.va.removeconsult.pojo.SysConf;
import com.va.removeconsult.service.DbService;
import com.va.removeconsult.service.MeetingService;
import com.va.removeconsult.service.UserService;

@Service
public class FolderServiceImpl implements FolderService {
	@Resource
	private FolderDao fm;
	@Resource
	private NodeDao nm;
	@Resource
	private NodeDao fim;
	@Resource
	private FolderUtil fu;
	@Resource
	private LogUtil lu;
	@Resource
	private UserService userService;
	@Resource
	private MeetingService meetingService;
	@Resource
	private DbService db;
	@Resource
	private FileBlockUtil fbu;
	
	private static final Logger logger = LoggerFactory.getLogger(FolderServiceImpl.class);

	public String newFolder(final HttpServletRequest request) {
		final String parentId = request.getParameter("parentId");
		final String folderName = request.getParameter("folderName");
		final String folderConstraint = request.getParameter("folderConstraint");
		String account = (String) request.getSession().getAttribute("ACCOUNT");
		Map<String, Object> loginInfo = userService.getLoginInfo(request);

		if (MapUtils.isNotEmpty(loginInfo)) {
			account = loginInfo.get("name").toString();
		} else {
			return "noAuthorized";// 如无访问权限则直接返回该字段，提示暂无网盘权限
		}

		Integer userId = (Integer) loginInfo.get("id");

		if (parentId == null || folderName == null || parentId.length() <= 0 || folderName.length() <= 0) {
			return "errorParameter";
		}
		if (!TextFormateUtil.instance().matcherFolderName(folderName) || folderName.indexOf(".") == 0) {
			return "errorParameter";
		}
		final Folder parentFolder = this.fm.queryById(parentId);
		if (parentFolder == null) {
			return "errorParameter";
		}
		if (fm.queryByParentId(parentId).parallelStream().anyMatch((e) -> e.getFolderName().equals(folderName))) {
			return "nameOccupied";
		}

		Folder f = new Folder();
		// 设置子文件夹约束等级，不允许子文件夹的约束等级比父文件夹低
		int pc = parentFolder.getFolderConstraint();
		if (folderConstraint != null) {
			try {
				int ifc = Integer.parseInt(folderConstraint);
				if (ifc > 0 && account == null) {
					return "errorParameter";
				}
				if (ifc < pc) {
					return "errorParameter";
				} else {
					f.setFolderConstraint(ifc);
				}
			} catch (Exception e) {
				// TODO: handle exception
				return "errorParameter";
			}
		} else {
			return "errorParameter";
		}
		f.setFolderUserId(userId);
		f.setFolderId(UUID.randomUUID().toString());
		f.setFolderName(folderName);
		f.setFolderCreationDate(ServerTimeUtil.accurateToDay());
		if (account != null) {
			f.setFolderCreator(account);
		} else {
			f.setFolderCreator("匿名用户");
		}
		f.setFolderParent(parentId);
		f.setFolderSize(0L);
		int i = 0;
		while (true) {
			try {
				System.out.println(f);
				final int r = this.fm.insertNewFolder(f);
				if (r > 0) {
					this.lu.writeCreateFolderEvent(request, f);
					return "createFolderSuccess";
				}
				break;
			} catch (Exception e) {
				f.setFolderId(UUID.randomUUID().toString());
				i++;
			}
			if (i >= 10) {
				break;
			}
		}
		return "cannotCreateFolder";
	}

	public String deleteFolder(final HttpServletRequest request) {
		final String folderId = request.getParameter("folderId");
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		if (!ConfigureReader.instance().authorized(account, AccountAuth.DELETE_FILE_OR_FOLDER)) {
			return "noAuthorized";
		}
		if (folderId == null || folderId.length() <= 0) {
			return "errorParameter";
		}
		final Folder folder = this.fm.queryById(folderId);
		if (folder == null) {
			return "deleteFolderSuccess";
		}
		final List<Folder> l = this.fu.getParentList(folderId);
		if (this.fu.deleteAllChildFolder(folderId) > 0) {
			this.lu.writeDeleteFolderEvent(request, folder, l);
			return "deleteFolderSuccess";
		}
		return "cannotDeleteFolder";
	}

	public String renameFolder(final HttpServletRequest request) {
		final String folderId = request.getParameter("folderId");
		final String newName = request.getParameter("newName");
		final String folderConstraint = request.getParameter("folderConstraint");
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		if (!ConfigureReader.instance().authorized(account, AccountAuth.RENAME_FILE_OR_FOLDER)) {
			return "noAuthorized";
		}
		if (folderId == null || folderId.length() <= 0 || newName == null || newName.length() <= 0) {
			return "errorParameter";
		}
		if (!TextFormateUtil.instance().matcherFolderName(newName) || newName.indexOf(".") == 0) {
			return "errorParameter";
		}
		final Folder folder = this.fm.queryById(folderId);
		if (folder == null) {
			return "errorParameter";
		}
		final Folder parentFolder = this.fm.queryById(folder.getFolderParent());
		int pc = parentFolder.getFolderConstraint();
		if (folderConstraint != null) {
			try {
				int ifc = Integer.parseInt(folderConstraint);
				if (ifc > 0 && account == null) {
					return "errorParameter";
				}
				if (ifc < pc) {
					return "errorParameter";
				} else {
					Map<String, Object> map = new HashMap<>();
					map.put("newConstraint", ifc);
					map.put("folderId", folderId);
					fm.updateFolderConstraintById(map);
					changeChildFolderConstraint(folderId, ifc);
					if (!folder.getFolderName().equals(newName)) {
						if (fm.queryByParentId(parentFolder.getFolderId()).parallelStream()
								.anyMatch((e) -> e.getFolderName().equals(newName))) {
							return "nameOccupied";
						}
						Map<String, String> map2 = new HashMap<String, String>();
						map2.put("folderId", folderId);
						map2.put("newName", newName);
						if (this.fm.updateFolderNameById(map2) == 0) {
							return "errorParameter";
						}
					}
					this.lu.writeRenameFolderEvent(request, folder, newName, folderConstraint);
					return "renameFolderSuccess";
				}
			} catch (Exception e) {
				// TODO: handle exception
				return "errorParameter";
			}
		} else {
			return "errorParameter";
		}
	}

	/**
	 * 
	 * <h2>迭代修改子文件夹约束</h2>
	 * <p>
	 * 当某一文件夹的约束被修改时，其所有子文件夹的约束等级均不得低于其父文件夹。 例如：
	 * 父文件夹的约束等级改为1（仅小组）时，所有约束等级为0（公开的）的子文件夹的约束等级也会提升为1，
	 * 而所有约束等级为2（仅自己）的子文件夹则不会受影响。
	 * </p>
	 * 
	 * @author 青阳龙野(kohgylw)
	 * @param folderId
	 *            要修改的文件夹ID
	 * @param c
	 *            约束等级
	 */
	private void changeChildFolderConstraint(String folderId, int c) {
		List<Folder> cfs = fm.queryByParentId(folderId);
		for (Folder cf : cfs) {
			if (cf.getFolderConstraint() < c) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("newConstraint", c);
				map.put("folderId", cf.getFolderId());
				fm.updateFolderConstraintById(map);
			}
			changeChildFolderConstraint(cf.getFolderId(), c);
		}
	}

	public String getFolderTreeJson(HttpServletRequest request) {
		
		Map<String, Object> loginInfo = userService.getLoginInfo(request);

		if (MapUtils.isEmpty(loginInfo)) {
			return "noAuthorized";// 如无访问权限则直接返回该字段，提示暂无网盘权限
		}

		Integer userId = (Integer) loginInfo.get("id");
		
		final Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		List<Folder> folders = this.fm.queryByUserId(map);
		map.put("fid", "null");
		Folder rootFolder = this.fm.queryRootByUserId(userId);
		
		String nodes =null;
		if (CollectionUtils.isNotEmpty(folders)) {

			FolderTree folderTree = new FolderTree(folders);
			List<Folder> folderList = folderTree.builTree(rootFolder.getFolderId());
			/* 转为json看看效果 */
			nodes = JSON.toJSONString(folderList);
			System.out.println(nodes);
			/*
			 * text: '会诊中心', type:'all', icon: 'layui-icon layui-icon-dialogue
			 * icon-meeting', nodes: ${node}
			 */
		}

		
		/*
		 * List<Map> items=db.getMeetingSearchStatus(type);
		 * List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();
		 * for(Map<String,String> item:items){ Map<String,Object> row=new
		 * HashMap<String,Object>(); String
		 * icon="layui-icon layui-icon-circle icon-status-"+String.valueOf(item.
		 * get("key")); row.put("text", item.get("value")); row.put("type",
		 * String.valueOf(item.get("key"))); row.put("roletype",
		 * item.get("type")); row.put("icon", icon); rows.add(row); }
		 */
//		nodes = JSON.toJSONString(rows);
		return nodes;
	}

	@Override
	public void createRootFolder(HttpServletRequest request) {
		Map<String, Object> user = userService.getLoginInfo(request);
		if(MapUtils.isEmpty(user)){
			return;
		}
		int userId = (int) user.get("id");
		String userAccount = (String) user.get("user");
		Folder rootFolder = this.fm.queryRootByUserId(userId);
		if(rootFolder != null){
			return;
		}
		Folder f = new Folder();
		f.setFolderUserId(userId);
		f.setFolderId(UUID.randomUUID().toString());
		f.setFolderName("root");
		f.setFolderCreationDate(ServerTimeUtil.accurateToDay());
		f.setFolderConstraint(0);
		f.setFolderCreator(userAccount);
		f.setFolderParent("null");
		f.setFolderSize(0L);
		this.fm.insertNewFolder(f);	
	}

	@Override
	public Folder queryRootFolder(HttpServletRequest request) {
		Map<String, Object> user = userService.getLoginInfo(request);
		if(MapUtils.isEmpty(user)){
			return null;
		}
		int userId = (int) user.get("id");
		Folder rootFolder = this.fm.queryRootByUserId(userId);
		if(rootFolder != null){
			return rootFolder;
		}
		return null;
	}

	@Override
	public void createMeetingFolder(HttpServletRequest request, int meetingId) {
		Map<String, Object> user = userService.getLoginInfo(request);
		if(MapUtils.isEmpty(user)){
			return;
		}
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		if(MapUtils.isEmpty(meeting)){
			return;
		}
		int userId = (int) user.get("id");
		String userAccount = (String) user.get("user");
		Folder rootFolder = this.queryRootFolder(request);
		if(rootFolder == null){
			return;
		}
		final Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("fname", meeting.get("no").toString());
		List<Folder> folders = this.fm.queryByNameAndUser(map);
		if(CollectionUtils.isNotEmpty(folders)){
			return;
		}
		Folder f = new Folder();
		f.setFolderUserId(userId);
		f.setFolderId(UUID.randomUUID().toString());
		f.setFolderName(meeting.get("no").toString());
		f.setFolderCreationDate(ServerTimeUtil.accurateToDay());
		f.setFolderConstraint(0);
		f.setFolderCreator(userAccount);
		f.setFolderParent(rootFolder.getFolderId());
		f.setFolderSize(0L);
		this.fm.insertNewFolder(f);
	}

	/**
	 * 获取系统配置
	 */
	public String querySysConfValue(String key) {
		
		SysConf querySysConf = this.fm.querySysConf(key);
		//如果获取不到对应的系统配置
		if(querySysConf == null ||  StringUtils.isEmpty(querySysConf.getConfValue())){
			//默认个人云盘容量为2GB
			if(key.equals(SysConfig.DISK_SIZE.getKey())){
				return SysConfig.DISK_SIZE.getValue();
			//默认最大上传文件大小为500MB
			}else if (key.equals(SysConfig.MAX_UPLOAD_SIZE.getKey())){
				return SysConfig.MAX_UPLOAD_SIZE.getValue();
			}else if (key.equals(SysConfig.CONF_TEUDBORAD.getKey())) {
				return SysConfig.CONF_TEUDBORAD.getValue();
			//添加和批量导入用户的最大限制
			}else if (key.equals(SysConfig.USER_SIZE.getKey())) {
				return SysConfig.USER_SIZE.getValue();
				//邮件模板要替换的医院名称
			}else if (key.equals(SysConfig.EMAIL_HOSPITAL_NAME.getKey())) {
				return SysConfig.EMAIL_HOSPITAL_NAME.getValue();
			//邮件模板要替换的医院访问地址
			}else if (key.equals(SysConfig.EMAIL_HOSPITAL_URL.getKey())) {
				return SysConfig.EMAIL_HOSPITAL_URL.getValue();
			//系统邮箱地址
			}else if (key.equals(SysConfig.EMAIL_ADDRESS.getKey())) {
				return SysConfig.EMAIL_ADDRESS.getValue();
				//系统邮箱授权码
			}else if (key.equals(SysConfig.EMAIL_AUTHORIZATION_CODE.getKey())) {
				return SysConfig.EMAIL_AUTHORIZATION_CODE.getValue();
			//会诊数据存储周期，单位(天)
			}else if (key.equals(SysConfig.MEETING_CYCLE.getKey())) {
				return SysConfig.MEETING_CYCLE.getValue();
			//LOGO
			}else if (key.equals(SysConfig.SYS_LOGO.getKey())) {
				return SysConfig.SYS_LOGO.getValue();
			}else{
				return null;
			}
		}
		if(key.equals(SysConfig.MEETING_CYCLE.getKey())){
			if("00".equals(querySysConf.getConfValue())){
				return "30";
			}else if("000".equals(querySysConf.getConfValue())){
				return "90";
			} else if("0000".equals(querySysConf.getConfValue())){
				return "180";
			}else{
				return querySysConf.getConfValue();
			}
		}else{
			return querySysConf.getConfValue();
		}
	}
	
	
	/**
	 * 获取用户云盘配置
	 */
	public long queryUserDiskConf(String key, Map user) {
		
		long uploadSize = (long) user.get("upload_size");
		
		long diskSize = (long) user.get("disk_size");
		
		//默认个人云盘容量为2GB
		if(key.equals(SysConfig.DISK_SIZE.getKey()) ){
			if(uploadSize == 0L){
				return Long.valueOf(SysConfig.DISK_SIZE.getValue());
			}else{
				return uploadSize;
			}
		}
		
		//默认最大上传文件大小为500MB
		if (key.equals(SysConfig.MAX_UPLOAD_SIZE.getKey()) && diskSize == 0L){
			return Long.valueOf(SysConfig.MAX_UPLOAD_SIZE.getValue());
		}else{
			return diskSize;
		}
	}
	

	/**
	 	alter table `user` add `upload_size` bigint(20) DEFAULT '0' COMMENT '个人最大上传文件大小(单位字节)';
		alter table `user` add `disk_size` bigint(20) DEFAULT '0' COMMENT '个人云盘最大容量(单位字节)';
	 */
	@Override
	public long queryRootFolderSize(Integer userId) {
		
		final Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		List<Node> nodes = this.nm.queryByUserId(map);
		long folderSize = 0L;
		if(CollectionUtils.isNotEmpty(nodes)){
			for (Node node : nodes) {
				folderSize += Long.valueOf(node.getFileSize());
			}
		}
		return folderSize;
	}

	@Override
	public void createMeetingFolder(HttpServletRequest request, String meetingNo, String attaId) {
		Map<String, Object> user = userService.getLoginInfo(request);
		if(MapUtils.isEmpty(user)){
			return;
		}
		int userId = (int) user.get("id");
		String userAccount = (String) user.get("user");
		Folder rootFolder = this.queryRootFolder(request);
		if(rootFolder == null){
			return;
		}
		final Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("fname", meetingNo);
		List<Folder> folders = this.fm.queryByNameAndUser(map);
		if(CollectionUtils.isNotEmpty(folders)){
			return;
		}
		//根据会诊编号创建对应的云盘目录
		Folder f = new Folder();
		f.setFolderUserId(userId);
		f.setFolderId(UUID.randomUUID().toString());
		f.setFolderName(meetingNo);
		f.setFolderCreationDate(ServerTimeUtil.accurateToDay());
		f.setFolderConstraint(0);
		f.setFolderCreator(userAccount);
		f.setFolderParent(rootFolder.getFolderId());
		f.setFolderSize(0L);
		this.fm.insertNewFolder(f);
		
		//得到会诊资料文件对象
		if(StringUtils.isBlank(attaId)){
			return;
		}
		Map attr = db.getAttr(attaId);
		if(MapUtils.isEmpty(attr)){
			return;
		}
		String fileName = (String) attr.get("fileName");
		String sysName = (String) attr.get("sysName");
		String path = (String) attr.get("path");
		File attaFile = new File(path+sysName);
		//得到上传的文件路径
		final String fileBlocks = ConfigureReader.instance().getFileBlockPath();
		final String id = UUID.randomUUID().toString().replace("-", "");
		final String filePath = "file_" + id + ".block";
		final File blockFile = new File(fileBlocks, filePath);
		try {
			FileUtils.copyFile(attaFile, blockFile);
		} catch (IOException e) {
			logger.error("会诊资料文件拷贝到云盘出错: " + e.getMessage());
			return;
		}
		
		final Node f2 = new Node();
		f2.setFileId(UUID.randomUUID().toString());
		f2.setFileCreator((String)user.get("name"));
		f2.setFileUserId(userId);
		f2.setFileCreationDate(ServerTimeUtil.accurateToDay());
		f2.setFileName(fileName);
		f2.setFileParentFolder(f.getFolderId());
		f2.setFilePath(filePath);
		final String fsize = ""+FileUtils.sizeOf(attaFile);
		f2.setFileSize(fsize);
		this.nm.insert(f2);
		
	}
	
	
	@Override
	public void createMeetingFolder(Map<String, Object> user ,int meetingId, String attaId) {
		if(MapUtils.isEmpty(user)){
			return;
		}
		int userId = (int) user.get("id");
		String userAccount = (String) user.get("user");
		
		//得到会诊资料文件对象
		if(StringUtils.isBlank(attaId)){
			return;
		}
		Map attr = db.getAttr(attaId);
		if(MapUtils.isEmpty(attr)){
			return;
		}
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		
		String fileName = (String) attr.get("fileName");
		String sysName = (String) attr.get("sysName");
		String path = (String) attr.get("path");
		File attaFile = new File(path+sysName);
		//得到上传的文件路径
		final String fileBlocks = ConfigureReader.instance().getFileBlockPath();
		final String id = UUID.randomUUID().toString().replace("-", "");
		final String filePath = "file_" + id + ".block";
		final File blockFile = new File(fileBlocks, filePath);
		try {
			FileUtils.copyFile(attaFile, blockFile);
		} catch (IOException e) {
			logger.error("会诊资料文件拷贝到云盘出错: " + e.getMessage());
			return;
		}
		
		Map<String,Object> folder=this.fm.queryByFolderName(meeting.get("no").toString());
		if(folder==null){
			Folder rootFolder = this.fm.queryRootByUserId(userId);
			if(rootFolder != null){
				Folder f = new Folder();
				f.setFolderUserId(userId);
				f.setFolderId(UUID.randomUUID().toString());
				f.setFolderName(meeting.get("no").toString());
				f.setFolderCreationDate(ServerTimeUtil.accurateToDay());
				f.setFolderConstraint(0);
				f.setFolderCreator(userAccount);
				f.setFolderParent(rootFolder.getFolderId());
				f.setFolderSize(0L);
				this.fm.insertNewFolder(f);
				folder=this.fm.queryByFolderName(meeting.get("no").toString());
			}
		}
		
		final Node f2 = new Node();
		f2.setFileId(UUID.randomUUID().toString());
		f2.setFileCreator((String)user.get("name"));
		f2.setFileUserId(userId);
		f2.setFileCreationDate(ServerTimeUtil.accurateToDay());
		f2.setFileName(fileName);
		f2.setFileParentFolder(folder.get("folder_id").toString());
		f2.setFilePath(filePath);
		final String fsize = ""+FileUtils.sizeOf(attaFile);
		f2.setFileSize(fsize);
		this.nm.insert(f2);
		
	}

	@Override
	public void createMeetingFolderShare(Map<String, Object> user,int meetingId, String attaId) {
	
		if(MapUtils.isEmpty(user)){
			return;
		}
		int userId = (int) user.get("id");
		String userAccount = (String) user.get("user");
		
		//得到会诊资料文件对象
		if(StringUtils.isBlank(attaId)){
			return;
		}
		Map attr = db.getAttr(attaId.trim());
		if(MapUtils.isEmpty(attr)){
			return;
		}
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		
		String fileName = (String) attr.get("fileName");
		String sysName = (String) attr.get("sysName");
		String path = (String) attr.get("path");
		File attaFile = new File(path+sysName);
		//得到上传的文件路径
		final String fileBlocks = ConfigureReader.instance().getFileBlockPath();
		final String id = UUID.randomUUID().toString().replace("-", "");
		final String filePath = "file_" + id + ".block";
		final File blockFile = new File(fileBlocks, filePath);
		try {
			FileUtils.copyFile(attaFile, blockFile);
		} catch (IOException e) {
			logger.error("会诊资料文件拷贝到云盘出错: " + e.getMessage());
			return;
		}
		
		
		Map<String,String> folderParam=new HashMap<String, String>();
		folderParam.put("userId", String.valueOf(userId));
		folderParam.put("folderName", meeting.get("no").toString());
		
		Map<String,Object> folder=this.fm.queryByFolderNameByUserId(folderParam);
		if(folder==null){
			Folder rootFolder = this.fm.queryRootByUserId(userId);
			if(rootFolder != null){
				Folder f = new Folder();
				f.setFolderUserId(userId);
				f.setFolderId(UUID.randomUUID().toString());
				f.setFolderName(meeting.get("no").toString());
				f.setFolderCreationDate(ServerTimeUtil.accurateToDay());
				f.setFolderConstraint(0);
				f.setFolderCreator(userAccount);
				f.setFolderParent(rootFolder.getFolderId());
				f.setFolderSize(0L);
				this.fm.insertNewFolder(f);
				folder=this.fm.queryByFolderNameByUserId(folderParam);

			}
		}
		
		
		if(folder!=null){
			folderParam.put("folderName", "共享");
			folderParam.put("folderParent", folder.get("folder_id").toString());
			Map<String,Object> sFolder=this.fm.queryByFolderNameByUserIdParent(folderParam);
			if(sFolder==null) {
				Folder f = new Folder();
				f.setFolderUserId(userId);
				f.setFolderId(UUID.randomUUID().toString());
				f.setFolderName("共享");
				f.setFolderCreationDate(ServerTimeUtil.accurateToDay());
				f.setFolderConstraint(0);
				f.setFolderCreator(userAccount);
				f.setFolderParent(folder.get("folder_id").toString());
				f.setFolderSize(0L);
				this.fm.insertNewFolder(f);
			}
			folder=this.fm.queryByFolderNameByUserIdParent(folderParam);
		}
		
		if(folder!=null){
			final Node f2 = new Node();
			f2.setFileId(UUID.randomUUID().toString());
			f2.setFileCreator((String)user.get("name"));
			f2.setFileUserId(userId);
			f2.setFileCreationDate(ServerTimeUtil.accurateToDay());
			f2.setFileName(fileName);
			f2.setFileParentFolder(folder.get("folder_id").toString());
			f2.setFilePath(filePath);
			f2.setFileSourceMeeting(String.valueOf(attaId));
			final String fsize = ""+FileUtils.sizeOf(attaFile);
			f2.setFileSize(fsize);
			this.nm.insert(f2);
		}

	}

	@Override
	public void deleteByParentSourceMeeting(String mid) {
		this.fim.deleteByParentSourceMeeting(mid);
	}

	@Override
	public int updateAttachmentByShareUser(String id, String shareUser) {
		return db.updateAttachmentByShareUser(id, shareUser);
	}

	@Override
	public int updateAttachmentByIsShare(String id, int isShare) {
		return db.updateAttachmentByIsShare(id, isShare);
	}

	@Override
	public void deleteOverdueFolder( String meetimgCycle) {
		 List<Folder> fList = new ArrayList<Folder>();
		 List<Folder> folders =  fm.queryOverdueFolder();
		 if(CollectionUtils.isEmpty(folders)){
				return;
		 }
		 try {
			 for(Folder fl : folders ){
				Date d =  ServerTimeUtil.accurateToStr(fl.getFolderCreationDate());
				Date date = ServerTimeUtil.dateAddDays(d, Integer.parseInt(meetimgCycle));
				if(ServerTimeUtil.dateCompare(new Date(), date) >0){
					fList.add(fl);
				}
			 }
		 } catch (ParseException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
		 for(Folder fo : fList ){
			final String folderId = fo.getFolderId();
			final String account = null;
			if (!ConfigureReader.instance().authorized(account, AccountAuth.DELETE_FILE_OR_FOLDER)) {
				return ;
			}
			if (folderId == null || folderId.length() <= 0) {
				return;
			}
			final Folder folder = fo;
			if (folder == null) {
				return ;
			}
			final List<Folder> l = this.fu.getParentList(folderId);
				if(this.fu.deleteAllChildFolder(folderId)>0){
					this.lu.writeDeleteFolderEvent(null, folder, l);
					db.addLog("定时删除会诊文件  文件名【"+folder.getFolderName()+"】,文件创建日期："+folder.getFolderCreationDate()+"", userService.getUserByUser("root"));
					return ;
				}
			}
		 
		 
	}
	
	
}