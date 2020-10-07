package com.va.removeconsult.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.va.removeconsult.dao.ResourceDao;
import com.va.removeconsult.pojo.His;
import com.va.removeconsult.pojo.Lis;
import com.va.removeconsult.pojo.OperateroomType;
import com.va.removeconsult.pojo.Pacs;
import com.va.removeconsult.pojo.Resource;
import com.va.removeconsult.pojo.Ris;
import com.va.removeconsult.pojo.Video;
import com.va.removeconsult.pojo.organization;

@Service
public class ResourceService {

	@Autowired
	private ResourceDao resourceDao;

	// 查找资源所有的资源类型
	public List<Resource> getResourceType() {
		return resourceDao.getResourceType();
	}

	// 定位到当前机构所在的层级
	public int getCurrentType(int id) {
		return resourceDao.getCurrentType(id);
	}

	// 是否在机构表中增加HIS资源 id当前机构所在id 对应机构表中的pid name:资源名称 对应机构表中的机构名
	public boolean isAddHisInOrg(int id, String name) {
		List<organization> list = resourceDao.isHasHisInOrg(id, name);
		System.out.println("org表中该机构id：" + id + "已有的HIS资源为" + list.size());
		if (list.size() >= 1) {
			return false;
		} else {
			return true;
		}
	}

	// 在机构表中增加HIS资源
	public int insertHisInOrg(organization organization) {
		System.out.println("service层insertHisInOrg方法" + organization.toString());
		int num = resourceDao.insertHisInOrg(organization);
		return num;
	}

	// 增加对资源管理的增删改查操作 add by:Heian 2019/02/24
	public Map<String, Object> getResource(int id) {
		return resourceDao.getResource(id);
	}

	/**
	 * 查找各个资源信息
	 */
	public List<His> selectHisByOrgid(int orgid) {
		return resourceDao.selectHisByOrgid(orgid);
	}

	public List<Lis> selectLisByOrgid(int orgid) {
		return resourceDao.selectLisByOrgid(orgid);
	}

	public List<Pacs> selectPacsByOrgid(int orgid) {
		return resourceDao.selectPacsByOrgid(orgid);
	}

	public List<Ris> selectRisByOrgid(int orgid) {
		return resourceDao.selectRisByOrgid(orgid);
	}

	public List<Video> selectVideoByOrgid(int orgid) {
		return resourceDao.selectVideoByOrgid(orgid);
	}

	// 增加His资源
	public int insertHisInHis(His his) {
		int id = -1;
		int isSave = resourceDao.insertHisInHis(his);
		if (isSave == 1) {
			List<His> list = resourceDao.findHisList(his);
			id = list != null && list.size() > 0 ? list.get(0).getId() : id;
		}
		return id;
	}

	// 删除HIS资源
	public int deleteHis(int id) {
		return resourceDao.deleteHis(id);
	}

	// 修改His
	public int updateHis(His his) {
		return resourceDao.updateHis(his);
	}

	// 查找His
	public List<His> searchHisbyId(int id) {
		return resourceDao.searchHisbyId(id);
	}

	// 增加Lis资源
	public int insertLisInHis(Lis lis) {
		int id = -1;
		int isSave = resourceDao.insertLisInHis(lis);
		if (isSave == 1) {
			List<Lis> list = resourceDao.findLisList(lis);
			id = list != null && list.size() > 0 ? list.get(0).getId() : id;
		}
		return id;
	}

	// 删除LIS资源
	public int deleteLis(int id) {
		return resourceDao.deleteLis(id);
	}

	// 查找Lis资源
	public List<Lis> searchLisbyId(int id) {
		return resourceDao.searchLisbyId(id);
	}

	// 修改Lis资源
	public int updateLis(Lis lis) {
		return resourceDao.updateLis(lis);
	}

	// 增加Pacs
	public int insertPacs(Pacs pacs) {
		int id = -1;
		int isSave = resourceDao.insertPacs(pacs);
		if (isSave == 1) {
			List<Pacs> list = resourceDao.findPacsList(pacs);
			id = list != null && list.size() > 0 ? list.get(0).getId() : id;
		}
		return id;
	}

	// 删除Pacs
	public int deletePacs(int id) {
		return resourceDao.deletePacs(id);
	}

	// 查找Pacs
	public List<Pacs> searchPacsbyId(int id) {
		return resourceDao.searchPacsbyId(id);
	}

	// 修改Pacs
	public int updatePacs(Pacs pacs) {
		return resourceDao.updatePacs(pacs);
	}

	// 增加Ris
	public int insertRis(Ris ris) {
		int id = -1;
		int isSave = resourceDao.insertRis(ris);
		if (isSave == 1) {
			List<Ris> list = resourceDao.findRisList(ris);
			id = list != null && list.size() > 0 ? list.get(0).getId() : id;
		}
		return id;
	}

	// 删除Ris
	public int deleteRis(int id) {
		return resourceDao.deleteRis(id);
	}

	// 查找Ris
	public List<Ris> searchRisbyId(int id) {
		return resourceDao.searchRisbyId(id);
	}

	// 修改Ris
	public int updateRis(Ris ris) {
		return resourceDao.updateRis(ris);
	}

	// 增加video
	public int insertVideo(Video video) {
		int id = -1;
		int isSave = resourceDao.insertVideo(video);
		if (isSave == 1) {
			List<Video> list = resourceDao.findVideoList(video);
			id = list != null && list.size() > 0 ? list.get(0).getId() : id;
		}
		return id;
	}

	// 删除Video
	public int deleteVideo(int id) {
		return resourceDao.deleteVideo(id);
	}

	// 查找Video
	public List<Video> searchVideo(int id) {
		return resourceDao.searchVideo(id);
	}

	// 修改Video
	public int updateVideo(Video video) {
		return resourceDao.updateVideo(video);
	}

	// 遍历手术室类型
	public List<OperateroomType> getRoomType() {
		return resourceDao.getRoomType();
	}

	public List<organization> getOrgPid(int id) {
		return resourceDao.getOrgPid(id);
	}

	// 模糊查询
	public List<His> findHis(String servername, int id) {
		return resourceDao.findHis(servername, id);
	}

	public List<Lis> findLis(String servername, int id) {
		return resourceDao.findLis(servername, id);
	}

	public List<Pacs> findPacs(String servername, int id) {
		return resourceDao.findPacs(servername, id);
	}

	public List<Ris> findRis(String servername, int id) {
		return resourceDao.findRis(servername, id);
	}

	public List<Video> findVideo(String vname, int id) {
		return resourceDao.findVideo(vname, id);
	}

	// 判断该资源是否存在
	public boolean isExistResource(String type, int orgid, String value, int id) {
		boolean isExist = false;
		int count = -1;
		List<?> list = new ArrayList<>();
		if ("HIS".equals(type)) {
			His his = new His();
			his.setOrgid(orgid);
			his.setServername(value);
			his.setId(id);
			list = resourceDao.findHisList(his);
		} else if ("LIS".equals(type)) {
			Lis lis = new Lis();
			lis.setOrgid(orgid);
			lis.setServername(value);
			lis.setId(id);
			list = resourceDao.findLisList(lis);
		} else if ("PACS".equals(type)) {
			Pacs pacs = new Pacs();
			pacs.setOrgid(String.valueOf(orgid));
			pacs.setServer(value);
			pacs.setId(id);
			list = resourceDao.findPacsList(pacs);
		} else if ("RIS".equals(type)) {
			Ris ris = new Ris();
			ris.setOrgid(String.valueOf(orgid));
			ris.setServer(value);
			ris.setId(id);
			list = resourceDao.findRisList(ris);
		} else if ("VIDEO".equals(type)) {
			Video video = new Video();
			video.setOrgid(orgid);
			video.setRoomname(value);
			video.setId(id);
			list = resourceDao.findVideoList(video);
		}
		count = list != null && list.size() > 0 ? list.size() : count;
		if (count > 0) {
			isExist = true;
		}
		return isExist;
	}

	public String getMeetingResTree(int orgId) {
		List<organization> currentOrg = this.getOrgPid(orgId);

		List<String> tags = new ArrayList<String>();
		tags.add("<i class=\"layui-icon layui-icon-search\" onclick=\"showPatientDialog();\"></i>");

		// -----------------HIS-------------------------
		List<His> hisData = this.selectHisByOrgid(orgId);
		Map<String, Object> hisRoot = new HashMap<String, Object>();
		ArrayList<Map<String, Object>> hisNodes = new ArrayList<Map<String, Object>>();
		for (His data : hisData) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", String.valueOf(data.getId()));
			node.put("text", data.getServername());
			node.put("icon", "tree-icon tree-database");
			node.put("flag", "HIS");
			node.put("tags", tags);
			hisNodes.add(node);
		}
		hisRoot.put("id", "0");
		hisRoot.put("text", "HIS");
		hisRoot.put("icon", "tree-icon tree-database");
		hisRoot.put("nodes", hisNodes);

		// -----------------Lis-------------------------
		List<Lis> lisData = this.selectLisByOrgid(orgId);
		Map<String, Object> lisRoot = new HashMap<String, Object>();
		ArrayList<Map<String, Object>> lisNodes = new ArrayList<Map<String, Object>>();
		for (Lis data : lisData) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", String.valueOf(data.getId()));
			node.put("text", data.getServername());
			node.put("icon", "tree-icon tree-database");
			node.put("flag", "LIS");
			node.put("tags", tags);
			lisNodes.add(node);
		}
		lisRoot.put("id", "0");
		lisRoot.put("text", "LIS");
		lisRoot.put("icon", "tree-icon tree-database");
		lisRoot.put("nodes", lisNodes);

		// -----------------Pacs-------------------------
		List<Pacs> pacsData = this.selectPacsByOrgid(orgId);
		Map<String, Object> pacsRoot = new HashMap<String, Object>();
		ArrayList<Map<String, Object>> pacsNodes = new ArrayList<Map<String, Object>>();
		for (Pacs data : pacsData) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", String.valueOf(data.getId()));
			node.put("text", data.getServer());
			node.put("icon", "tree-icon tree-database");
			node.put("flag", "PACS");
			node.put("tags", tags);
			pacsNodes.add(node);
		}
		pacsRoot.put("id", "0");
		pacsRoot.put("text", "PACS");
		pacsRoot.put("icon", "tree-icon tree-database");
		pacsRoot.put("nodes", pacsNodes);

		// -----------------Ris-------------------------
		List<Ris> risData = this.selectRisByOrgid(orgId);
		Map<String, Object> risRoot = new HashMap<String, Object>();
		ArrayList<Map<String, Object>> risNodes = new ArrayList<Map<String, Object>>();
		for (Ris data : risData) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", String.valueOf(data.getId()));
			node.put("text", data.getServer());
			node.put("icon", "tree-icon tree-database");
			node.put("flag", "RIS");
			node.put("tags", tags);
			risNodes.add(node);
		}
		risRoot.put("id", "0");
		risRoot.put("text", "RIS");
		risRoot.put("icon", "tree-icon tree-database");
		risRoot.put("nodes", risNodes);

		// -----------------Video-------------------------
		List<Video> videoData = this.selectVideoByOrgid(orgId);
		Map<String, Object> videoRoot = new HashMap<String, Object>();
		ArrayList<Map<String, Object>> videoNodes = new ArrayList<Map<String, Object>>();
		for (Video video : videoData) {
			ArrayList<Map<String, String>> videoSonNodes = new ArrayList<Map<String, String>>();
			Map<String, Object> videoNode = new HashMap<String, Object>();
			videoNode.put("id", String.valueOf(video.getId()));
			videoNode.put("text", video.getRoomname());
			videoNode.put("icon", "tree-icon tree-operationRoom");
			if (!StringUtils.isEmpty(video.getV1name())) {
				Map<String, String> sonNode = new HashMap<String, String>();
				sonNode.put("id", String.valueOf(video.getId()));
				sonNode.put("device", video.getRoomip());
				sonNode.put("channel", "1");
				sonNode.put("flag", "LIVE");
				sonNode.put("text", video.getV1name());
				sonNode.put("icon", "tree-icon tree-video");
				videoSonNodes.add(sonNode);
			}
			if (!StringUtils.isEmpty(video.getV2name())) {
				Map<String, String> sonNode = new HashMap<String, String>();
				sonNode.put("id", String.valueOf(video.getId()));
				sonNode.put("device", video.getRoomip());
				sonNode.put("channel", "2");
				sonNode.put("flag", "LIVE");
				sonNode.put("text", video.getV2name());
				sonNode.put("icon", "tree-icon tree-video");
				videoSonNodes.add(sonNode);
			}
			if (!StringUtils.isEmpty(video.getV3name())) {
				Map<String, String> sonNode = new HashMap<String, String>();
				sonNode.put("id", String.valueOf(video.getId()));
				sonNode.put("device", video.getRoomip());
				sonNode.put("channel", "3");
				sonNode.put("flag", "LIVE");
				sonNode.put("text", video.getV3name());
				sonNode.put("icon", "tree-icon tree-video");
				videoSonNodes.add(sonNode);
			}
			if (!StringUtils.isEmpty(video.getV4name())) {
				Map<String, String> sonNode = new HashMap<String, String>();
				sonNode.put("id", String.valueOf(video.getId()));
				sonNode.put("device", video.getRoomip());
				sonNode.put("channel", "4");
				sonNode.put("flag", "LIVE");
				sonNode.put("text", video.getV4name());
				sonNode.put("icon", "tree-icon tree-video");
				videoSonNodes.add(sonNode);
			}
			if (!StringUtils.isEmpty(video.getV5name())) {
				Map<String, String> sonNode = new HashMap<String, String>();
				sonNode.put("id", String.valueOf(video.getId()));
				sonNode.put("device", video.getRoomip());
				sonNode.put("channel", "5");
				sonNode.put("flag", "LIVE");
				sonNode.put("text", video.getV5name());
				sonNode.put("icon", "tree-icon tree-video");
				videoSonNodes.add(sonNode);
			}
			if (!StringUtils.isEmpty(video.getV6name())) {
				Map<String, String> sonNode = new HashMap<String, String>();
				sonNode.put("id", String.valueOf(video.getId()));
				sonNode.put("device", video.getRoomip());
				sonNode.put("channel", "6");
				sonNode.put("flag", "LIVE");
				sonNode.put("text", video.getV6name());
				sonNode.put("icon", "tree-icon tree-video");
				videoSonNodes.add(sonNode);
			}
			if (!StringUtils.isEmpty(video.getV7name())) {
				Map<String, String> sonNode = new HashMap<String, String>();
				sonNode.put("id", String.valueOf(video.getId()));
				sonNode.put("device", video.getRoomip());
				sonNode.put("channel", "7");
				sonNode.put("flag", "LIVE");
				sonNode.put("text", video.getV7name());
				sonNode.put("icon", "tree-icon tree-video");
				videoSonNodes.add(sonNode);
			}
			if (!StringUtils.isEmpty(video.getV8name())) {
				Map<String, String> sonNode = new HashMap<String, String>();
				sonNode.put("id", String.valueOf(video.getId()));
				sonNode.put("device", video.getRoomip());
				sonNode.put("channel", "8");
				sonNode.put("flag", "LIVE");
				sonNode.put("text", video.getV8name());
				sonNode.put("icon", "tree-icon tree-video");
				videoSonNodes.add(sonNode);
			}
			videoNode.put("nodes", videoSonNodes);
			videoNodes.add(videoNode);
		}
		videoRoot.put("id", "0");
		videoRoot.put("text", "视频源");
		videoRoot.put("icon", "tree-icon tree-video");
		videoRoot.put("nodes", videoNodes);

		ArrayList<Map<String, ?>> resNodes = new ArrayList<Map<String, ?>>();
		if (hisData.size() != 0)
			resNodes.add(hisRoot);
		if (lisData.size() != 0)
			resNodes.add(lisRoot);
		if (pacsData.size() != 0)
			resNodes.add(pacsRoot);
		if (risData.size() != 0)
			resNodes.add(risRoot);
		if (videoData.size() != 0)
			resNodes.add(videoRoot);

		List<Map> result = new ArrayList<Map>();
		Map<String, Object> rootNode = new HashMap<String, Object>();
		rootNode.put("id", orgId);
		rootNode.put("text", currentOrg.get(0).getName());
		rootNode.put("icon", "layui-icon layui-icon-dialogue icon-meeting");
		rootNode.put("nodes", resNodes);
		result.add(rootNode);
		return JSON.toJSONString(result);
	}
}