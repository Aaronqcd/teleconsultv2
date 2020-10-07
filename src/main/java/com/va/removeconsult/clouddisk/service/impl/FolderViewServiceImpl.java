package com.va.removeconsult.clouddisk.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.va.removeconsult.clouddisk.model.Folder;
import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.clouddisk.pojo.FolderView;
import com.va.removeconsult.clouddisk.pojo.SreachView;
import com.va.removeconsult.clouddisk.pojo.TableView;
import com.va.removeconsult.clouddisk.service.FolderViewService;
import com.va.removeconsult.clouddisk.util.ConfigureReader;
import com.va.removeconsult.clouddisk.util.FolderUtil;
import com.va.removeconsult.clouddisk.util.ServerTimeUtil;
import com.va.removeconsult.dao.FolderDao;
import com.va.removeconsult.dao.NodeDao;
import com.va.removeconsult.service.UserService;

@Service
public class FolderViewServiceImpl implements FolderViewService {
	@Resource
	private FolderUtil fu;
	@Resource
	private FolderDao fm;
	@Resource
	private NodeDao flm;
	@Resource
	private Gson gson;
	@Resource
	private UserService userService;

	@Override
	public String getFolderViewToJson(final String fid, final HttpSession session, final HttpServletRequest request) {
		final ConfigureReader cr = ConfigureReader.instance();
		if (fid == null || fid.length() == 0) {
			return "ERROR";
		}
		
		Map<String, Object> loginInfo = userService.getLoginInfo(request);
		
		String account = null;
		
		if(MapUtils.isNotEmpty(loginInfo)){
			account = loginInfo.get("name").toString();
		}else{
			return "notAccess";
		}
		
		Folder vf = this.getFolderByUserId(fid, (int)loginInfo.get("id"));
		
		if(vf == null){
			return "notData";
		}
		
		
		final FolderView fv = new FolderView();
		fv.setFolder(vf);
		fv.setParentList(this.fu.getParentList(fid));
		List<Folder> fs = new LinkedList<>();
		for (Folder f : this.fm.queryByParentId(fid)) {
			if (ConfigureReader.instance().accessFolder(f, account)) {
				fs.add(f);
			}
		}
		fv.setFolderList(fs);
		fv.setFileList(this.flm.queryByParentFolderId(fid));
		if (account != null) {
			fv.setAccount(account);
		}
		final List<String> authList = new ArrayList<String>();
		authList.add("U");
		authList.add("C");
		authList.add("D");
		authList.add("R");
		authList.add("L");
		authList.add("M");
		fv.setAuthList(authList);
		fv.setPublishTime(ServerTimeUtil.accurateToMinute());
		return gson.toJson(fv);
	}

	@Override
	public String getSreachViewToJson(HttpServletRequest request) {
		final ConfigureReader cr = ConfigureReader.instance();
		String fid = request.getParameter("fid");
		String keyWorld = request.getParameter("keyworld");
		if (fid == null || fid.length() == 0 || keyWorld == null) {
			return "ERROR";
		}
		
		if(keyWorld.length() == 0) {
			return getFolderViewToJson(fid, request.getSession(), request);
		}
		
		
		
		
		
		Map<String, Object> loginInfo = userService.getLoginInfo(request);
		
		String account = null;
		
		if(MapUtils.isNotEmpty(loginInfo)){
			account = loginInfo.get("name").toString();
		}else{
			return "notAccess";
		}
		
		Folder vf = this.fm.queryRootByUserId((int)loginInfo.get("id"));
		
		final SreachView sv = new SreachView();
		
		Folder sf = new Folder();
		sf.setFolderId(vf.getFolderId());
		sf.setFolderName("在“" + (vf.getFolderName().equalsIgnoreCase("root") ? "我的云盘" :  vf.getFolderName())+ "”内搜索“" + keyWorld + "”的结果...");
		sf.setFolderParent(vf.getFolderId());
		sf.setFolderCreator("--");
		sf.setFolderCreationDate("--");
		sf.setFolderConstraint(vf.getFolderConstraint());
		sv.setFolder(sf);
		
		List<Folder> pl = this.fu.getParentList(vf.getFolderId());
		pl.add(vf);
		sv.setParentList(pl);
		
		List<Node> ns = new LinkedList<>();
		List<Folder> fs = new LinkedList<>();
		sreachFilesAndFolders(vf.getFolderId(), keyWorld, account, ns, fs);
		sv.setFileList(ns);
		sv.setFolderList(fs);
		
		if (account != null) {
			sv.setAccount(account);
		}
		
		final List<String> authList = new ArrayList<String>();
		
		authList.add("L");
		
		authList.add("O");
		sv.setAuthList(authList);
		
		sv.setPublishTime(ServerTimeUtil.accurateToMinute());
		
		sv.setKeyWorld(keyWorld);
		return gson.toJson(sv);
	}

	
	private void sreachFilesAndFolders(String fid, String key, String account, List<Node> ns, List<Folder> fs) {
		for (Folder f : this.fm.queryByParentId(fid)) {
			if (ConfigureReader.instance().accessFolder(f, account)) {
				if (f.getFolderName().indexOf(key) >= 0) {
					f.setFolderName(f.getFolderName());
					fs.add(f);
				}
				sreachFilesAndFolders(f.getFolderId(), key, account, ns, fs);
			}
		}
		for (Node n : this.flm.queryByParentFolderId(fid)) {
			if (n.getFileName().indexOf(key) >= 0) {
				n.setFileName(n.getFileName());
				ns.add(n);
			}
		}
	}
	
	public Folder getFolderByUserId(String fid, int userId){
		final Map<String, Object> map = new HashMap<>();
		map.put("fid", fid);
		map.put("userId", userId);
		List<Folder> folders = this.fm.queryByUserId(map);
		if(CollectionUtils.isNotEmpty(folders)){
			return folders.get(0);
		}
		return null;
	}

	@Override
	public String getTableViewToJson(String fid, HttpSession session, HttpServletRequest request) {
		final ConfigureReader cr = ConfigureReader.instance();
		
		TableView tv = new TableView();
		
		if (fid == null || fid.length() == 0) {
			return "ERROR";
		}
		
		Map<String, Object> loginInfo = userService.getLoginInfo(request);
		
		String account = null;
		
		if(MapUtils.isNotEmpty(loginInfo)){
			account = loginInfo.get("name").toString();
		}else{
			return "notAccess";
		}
		
		Folder vf = this.getFolderByUserId(fid, (int)loginInfo.get("id"));
		
		if(vf == null){
			return "notData";
		}
		
		
		final FolderView fv = new FolderView();
		fv.setFolder(vf);
		fv.setParentList(this.fu.getParentList(fid));
		List<Folder> fs = new LinkedList<>();
		for (Folder f : this.fm.queryByParentId(fid)) {
			if (ConfigureReader.instance().accessFolder(f, account)) {
				fs.add(f);
			}
		}
		fv.setFolderList(fs);
		fv.setFileList(this.flm.queryByParentFolderId(fid));
		if (account != null) {
			fv.setAccount(account);
		}
		final List<String> authList = new ArrayList<String>();
		authList.add("U");
		authList.add("C");
		authList.add("D");
		authList.add("R");
		authList.add("L");
		authList.add("M");
		fv.setAuthList(authList);
		fv.setPublishTime(ServerTimeUtil.accurateToMinute());
		return gson.toJson(fv);
	}
	
}
