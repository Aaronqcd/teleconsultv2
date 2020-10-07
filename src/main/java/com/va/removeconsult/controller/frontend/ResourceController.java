package com.va.removeconsult.controller.frontend;

import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import com.va.removeconsult.pojo.*;
import com.va.removeconsult.service.*;
import com.va.removeconsult.util.StringUtil;

@Controller
public class ResourceController {
	@Resource
	private UserService userService;
	@Resource
	private MeetingService meetingService;
	@Resource
	private DbService db;
	@Resource
	private ResourceService resourceService;

	/**
	 * 加载机构视图 add by:Heian
	 */
	@RequestMapping("/ResourceManage")
	public ModelAndView ResourceManage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("ResourceManage");
		List<com.va.removeconsult.pojo.Resource> list = resourceService.getResourceType();
		mv.addObject("resourceTypeList", list);
		List<OperateroomType> list2 = resourceService.getRoomType();
		for (int i = 0; i < list2.size(); i++) {
			if (i == 0) {
				OperateroomType o = list2.get(i);
				o.setRoomtype("手术示教室");
			} else if (i == 1) {
				OperateroomType o = list2.get(i);
				o.setRoomtype("数字化手术室");
			}

		}

		mv.addObject("roomType", list2);
		Map<String, Object> map = userService.getLoginInfo(request);

		Map<String, Object> currentUserMap = userService.getUserByUser(map.get("user").toString());
		int currentOrgId = (int) currentUserMap.get("belong");// 所在机构的id

		mv.addObject("currentOrgId", currentOrgId);
		mv.addObject("gropuname", map.get("group"));// admins managers users
		return mv;
	}

	// 加载机构树返回数据到前台
	@RequestMapping(value = "loadtree", produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String loadtree(int currentId) {
		List<organization> currentOrg = resourceService.getOrgPid(currentId);
		System.out.println("加载机构树开始：id" + currentId + "医院名称为：" + currentOrg.get(0).getName());
		// 建立每个资源的文件夹，文件夹内放入map实例(资源节点实例)

		// -----------------HIS-------------------------
		Map<String, Object> hisFloderMap = new HashMap<String, Object>();
		// His资源集合
		List<His> hisList = resourceService.selectHisByOrgid(currentId);
		ArrayList<Map<String, String>> hisArray = new ArrayList<Map<String, String>>();
		for (His his : hisList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", String.valueOf(his.getId()));
			map.put("text", his.getServername());
			map.put("text2", "HIS");
			map.put("icon", "tree-icon tree-database");
			hisArray.add(map);
		}
		hisFloderMap.put("nodes", hisArray);
		hisFloderMap.put("id", "0");
		hisFloderMap.put("text", "HIS");
		hisFloderMap.put("icon", "tree-icon tree-database");
		if (hisList.size() >= 1) {
			// hisFloderMap.put("state", "closed");
		}

		// -----------------Lis-------------------------
		Map<String, Object> lisFloderMap = new HashMap<String, Object>();
		// Lis资源集合
		List<Lis> lisList = resourceService.selectLisByOrgid(currentId);
		ArrayList<Map<String, String>> lisArray = new ArrayList<Map<String, String>>();
		for (Lis lis : lisList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", String.valueOf(lis.getId()));
			map.put("text", lis.getServername());
			map.put("text2", "LIS");
			map.put("icon", "tree-icon tree-database");
			lisArray.add(map);
		}
		lisFloderMap.put("nodes", lisArray);
		lisFloderMap.put("id", "0");
		lisFloderMap.put("text", "LIS");
		lisFloderMap.put("icon", "tree-icon tree-database");
		// lisFloderMap.put("icon", "layui-icon layui-icon-circle icon-status-2");

		if (lisList.size() >= 1) {
			// lisFloderMap.put("state", "closed");
		}

		// -----------------Pacs-------------------------
		Map<String, Object> pacsFloderMap = new HashMap<String, Object>();
		// Pacs资源集合
		List<Pacs> pacsList = resourceService.selectPacsByOrgid(currentId);
		ArrayList<Map<String, String>> pacsArray = new ArrayList<Map<String, String>>();
		for (Pacs pacs : pacsList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", String.valueOf(pacs.getId()));
			map.put("text", pacs.getServer());
			map.put("text2", "PACS");
			map.put("icon", "tree-icon tree-database");
			pacsArray.add(map);
		}
		pacsFloderMap.put("nodes", pacsArray);
		pacsFloderMap.put("id", "0");
		pacsFloderMap.put("text", "PACS");
		pacsFloderMap.put("icon", "tree-icon tree-database");

		if (pacsList.size() >= 1) {
			// pacsFloderMap.put("state", "closed");
		}

		// -----------------Ris-------------------------
		Map<String, Object> risFloderMap = new HashMap<String, Object>();
		// Ris资源集合
		List<Ris> risList = resourceService.selectRisByOrgid(currentId);
		ArrayList<Map<String, String>> risArray = new ArrayList<Map<String, String>>();
		for (Ris ris : risList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", String.valueOf(ris.getId()));
			map.put("text", ris.getServer());
			map.put("text2", "RIS");
			map.put("icon", "tree-icon tree-database");
			risArray.add(map);
		}
		risFloderMap.put("nodes", risArray);
		risFloderMap.put("id", "0");
		risFloderMap.put("text", "RIS");
		risFloderMap.put("icon", "tree-icon tree-database");

		if (risArray.size() >= 1) {
			// risFloderMap.put("state", "closed");
		}

		// -----------------Video-------------------------
		Map<String, Object> videoFloderMap = new HashMap<String, Object>();
		// Video资源集合
		List<Video> videoList = resourceService.selectVideoByOrgid(currentId);
		ArrayList<Map<String, Object>> videoArray = new ArrayList<Map<String, Object>>();// 每一个map就是一行数据
		for (Video video : videoList) {
			ArrayList<Map<String, String>> videoSonArray = new ArrayList<Map<String, String>>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", String.valueOf(video.getId()));
			map.put("text", video.getRoomname());
			map.put("text2", "VIDEO");
			map.put("icon", "tree-icon tree-operationRoom");
			if (!StringUtils.isEmpty(video.getV1name())) {
				Map<String, String> mp = new HashMap<String, String>();
				mp.put("text", video.getV1name());
				mp.put("icon", "tree-icon tree-video");
				videoSonArray.add(mp);
			}
			if (!StringUtils.isEmpty(video.getV2name())) {
				Map<String, String> mp = new HashMap<String, String>();
				mp.put("text", video.getV2name());
				mp.put("icon", "tree-icon tree-video");
				videoSonArray.add(mp);
			}
			if (!StringUtils.isEmpty(video.getV3name())) {
				Map<String, String> mp = new HashMap<String, String>();
				mp.put("text", video.getV3name());
				mp.put("icon", "tree-icon tree-video");
				videoSonArray.add(mp);
			}
			if (!StringUtils.isEmpty(video.getV4name())) {
				Map<String, String> mp = new HashMap<String, String>();
				mp.put("text", video.getV4name());
				mp.put("icon", "tree-icon tree-video");
				videoSonArray.add(mp);
			}
			if (!StringUtils.isEmpty(video.getV5name())) {
				Map<String, String> mp = new HashMap<String, String>();
				mp.put("text", video.getV5name());
				mp.put("icon", "tree-icon tree-video");
				videoSonArray.add(mp);
			}
			if (!StringUtils.isEmpty(video.getV6name())) {
				Map<String, String> mp = new HashMap<String, String>();
				mp.put("text", video.getV6name());
				mp.put("icon", "tree-icon tree-video");
				videoSonArray.add(mp);
			}
			if (!StringUtils.isEmpty(video.getV7name())) {
				Map<String, String> mp = new HashMap<String, String>();
				mp.put("text", video.getV7name());
				mp.put("icon", "tree-icon tree-video");
				videoSonArray.add(mp);
			}
			if (!StringUtils.isEmpty(video.getV8name())) {
				Map<String, String> mp = new HashMap<String, String>();
				mp.put("text", video.getV8name());
				mp.put("icon", "tree-icon tree-video");
				videoSonArray.add(mp);
			}
			map.put("nodes", videoSonArray);
			videoArray.add(map);
		}
		videoFloderMap.put("nodes", videoArray);
		videoFloderMap.put("id", "0");
		videoFloderMap.put("text", "视频源");
		videoFloderMap.put("icon", "tree-icon tree-video");
		if (videoList.size() >= 1) {
			// videoFloderMap.put("state", "closed");
		}
		ArrayList<Map<String, ?>> arrTwo = new ArrayList<Map<String, ?>>();
		if (hisList.size() != 0)
			arrTwo.add(hisFloderMap);
		if (lisList.size() != 0)
			arrTwo.add(lisFloderMap);
		if (pacsList.size() != 0)
			arrTwo.add(pacsFloderMap);
		if (risList.size() != 0)
			arrTwo.add(risFloderMap);
		if (videoList.size() != 0)
			arrTwo.add(videoFloderMap);
		// 默认只显示当前用户机构的资源
		/*
		 * "iconCls": "icon-second", "id": 2, "name": "南山医院", "pid": 1, "text": "南山医院",
		 * "type": 1
		 */
		List<Map> list = new ArrayList<Map>();
		Map<String, Object> totalMap = new HashMap<String, Object>();
		int type = currentOrg.get(0).getType();// 层级
		if (type == 0) {
			// list = db.getTreeOrganList2(currentId);
			totalMap.put("id", currentId);
			totalMap.put("text", currentOrg.get(0).getName());
			totalMap.put("icon", "layui-icon layui-icon-dialogue icon-meeting");
			totalMap.put("nodes", arrTwo);
			list.add(totalMap);
		} else if (type == 1) {
			totalMap.put("id", currentId);
			totalMap.put("text", currentOrg.get(0).getName());
			totalMap.put("icon", "layui-icon layui-icon-dialogue icon-meeting");
			totalMap.put("nodes", arrTwo);
			list.add(totalMap);
		} else if (type == 2) {
			totalMap.put("id", currentId);
			totalMap.put("text", currentOrg.get(0).getName());
			totalMap.put("icon", "layui-icon layui-icon-dialogue icon-meeting");
			totalMap.put("nodes", arrTwo);
			list.add(totalMap);
		}
		System.out.println(JSON.toJSONString(totalMap));
		return JSON.toJSONString(list);
	}

	@RequestMapping(value = "meetingrestree", produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String getMeetingRestree(int currentId) {
		return resourceService.getMeetingResTree(currentId);
	}

	@RequestMapping(value = "insertHisResource", method = RequestMethod.POST)
	@ResponseBody
	public String insertHisResource(His his) {
		Map<String, String> result = new HashMap<String, String>();
		String isSuccess = "0";
		his.setServername(StringUtil.iso2U8(his.getServername()));
		his.setServerip(StringUtil.iso2U8(his.getServerip()));
		his.setDbport(StringUtil.iso2U8(his.getDbport()));
		his.setDatesource(StringUtil.iso2U8(his.getDatesource()));
		his.setLoginname(StringUtil.iso2U8(his.getLoginname()));
		his.setLoginkey(StringUtil.iso2U8(his.getLoginkey()));
		// 判断该资源是否存在
		if (!resourceService.isExistResource("HIS", his.getOrgid(), his.getServername(), 0)) {
			int id = resourceService.insertHisInHis(his);
			isSuccess = "1";
			result.put("id", String.valueOf(id));
		} else {
			isSuccess = "2";
		}
		result.put("success", isSuccess);
		return JSON.toJSONString(result);
	}

	@RequestMapping(value = "deleteHisResource")
	@ResponseBody
	public String deleteHis(int id) {
		int num = resourceService.deleteHis(id);
		return String.valueOf(num);
	}

	@RequestMapping(value = "searchHisResource", produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String searchHisbyId(int id) {
		List<His> hisList = resourceService.searchHisbyId(id);
		return JSON.toJSONString(hisList);
	}

	@RequestMapping(value = "updateHisResource")
	@ResponseBody
	public String updateHis(His his) {
		Map<String, String> result = new HashMap<String, String>();
		String isSuccess = "0";
		his.setServername(StringUtil.iso2U8(his.getServername()));
		his.setServerip(StringUtil.iso2U8(his.getServerip()));
		his.setDbport(StringUtil.iso2U8(his.getDbport()));
		his.setDatesource(StringUtil.iso2U8(his.getDatesource()));
		his.setLoginname(StringUtil.iso2U8(his.getLoginname()));
		his.setLoginkey(StringUtil.iso2U8(his.getLoginkey()));

		if (!resourceService.isExistResource("HIS", his.getOrgid(), his.getServername(), his.getId())) {
			resourceService.updateHis(his);
			isSuccess = "1";
			result.put("id", String.valueOf(his.getId()));
		} else {
			isSuccess = "2";
		}
		result.put("success", isSuccess);
		return JSON.toJSONString(result);
	}

	@RequestMapping(value = "insertLisResource", method = RequestMethod.POST)
	@ResponseBody
	public String insertLisInHis(Lis lis) {
		Map<String, String> result = new HashMap<String, String>();
		String isSuccess = "0";
		lis.setServername(StringUtil.iso2U8(lis.getServername()));
		lis.setServerip(StringUtil.iso2U8(lis.getServerip()));
		lis.setDbport(StringUtil.iso2U8(lis.getDbport()));
		lis.setDatesource(StringUtil.iso2U8(lis.getDatesource()));
		lis.setLoginname(StringUtil.iso2U8(lis.getLoginname()));
		lis.setLoginkey(StringUtil.iso2U8(lis.getLoginkey()));

		// 判断该资源是否存在
		if (!resourceService.isExistResource("LIS", lis.getOrgid(), lis.getServername(), 0)) {
			int id = resourceService.insertLisInHis(lis);
			isSuccess = "1";
			result.put("id", String.valueOf(id));
		} else {
			isSuccess = "2";
		}
		result.put("success", isSuccess);
		return JSON.toJSONString(result);
	}

	@RequestMapping(value = "deleteLisResource")
	@ResponseBody
	public String deleteLis(int id) {
		int num = resourceService.deleteLis(id);
		return String.valueOf(num);
	}

	@RequestMapping(value = "searchLisResource", produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String searchLisResource(int id) {
		List<Lis> list = resourceService.searchLisbyId(id);
		return JSON.toJSONString(list);
	}

	@RequestMapping(value = "updateLisResource")
	@ResponseBody
	public String updateLis(Lis lis) {
		Map<String, String> result = new HashMap<String, String>();
		String isSuccess = "0";
		lis.setServername(StringUtil.iso2U8(lis.getServername()));
		lis.setServerip(StringUtil.iso2U8(lis.getServerip()));
		lis.setDbport(StringUtil.iso2U8(lis.getDbport()));
		lis.setDatesource(StringUtil.iso2U8(lis.getDatesource()));
		lis.setLoginname(StringUtil.iso2U8(lis.getLoginname()));
		lis.setLoginkey(StringUtil.iso2U8(lis.getLoginkey()));
		if (!resourceService.isExistResource("LIS", lis.getOrgid(), lis.getServername(), lis.getId())) {
			resourceService.updateLis(lis);
			isSuccess = "1";
			result.put("id", String.valueOf(lis.getId()));
		} else {
			isSuccess = "2";
		}
		result.put("success", isSuccess);
		return JSON.toJSONString(result);
	}

	@RequestMapping(value = "insertPacsResource", method = RequestMethod.POST)
	@ResponseBody
	public String insertPacs(Pacs pacs) {
		Map<String, String> result = new HashMap<String, String>();
		String isSuccess = "0";
		pacs.setServer(StringUtil.iso2U8(pacs.getServer()));
		pacs.setType(StringUtil.iso2U8(pacs.getType()));
		pacs.setZaet(StringUtil.iso2U8(pacs.getZaet()));
		pacs.setBaet(StringUtil.iso2U8(pacs.getBaet()));
		pacs.setDbip(StringUtil.iso2U8(pacs.getDbip()));
		pacs.setDbname(StringUtil.iso2U8(pacs.getDbname()));
		pacs.setDbport(StringUtil.iso2U8(pacs.getDbport()));
		pacs.setDbuser(StringUtil.iso2U8(pacs.getDbuser()));
		pacs.setDbpwd(StringUtil.iso2U8(pacs.getDbpwd()));

		// 判断该资源是否存在
		if (!resourceService.isExistResource("PACS", Integer.parseInt(pacs.getOrgid()), pacs.getServer(), 0)) {
			int id = resourceService.insertPacs(pacs);
			isSuccess = "1";
			result.put("id", String.valueOf(id));
		} else {
			isSuccess = "2";
		}
		result.put("success", isSuccess);
		return JSON.toJSONString(result);
	}

	@RequestMapping(value = "deletePacsResource")
	@ResponseBody
	public String deletePacs(int id) {
		int num = resourceService.deletePacs(id);
		return String.valueOf(num);
	}

	@RequestMapping(value = "searchPacsResource", produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String searchPacsbyId(int id) {
		List<Pacs> list = resourceService.searchPacsbyId(id);
		return JSON.toJSONString(list);
	}

	@RequestMapping(value = "updatePacsResource")
	@ResponseBody
	public String updatePacs(Pacs pacs) {
		Map<String, String> result = new HashMap<String, String>();
		String isSuccess = "0";
		pacs.setServer(StringUtil.iso2U8(pacs.getServer()));
		pacs.setType(StringUtil.iso2U8(pacs.getType()));
		pacs.setZaet(StringUtil.iso2U8(pacs.getZaet()));
		pacs.setBaet(StringUtil.iso2U8(pacs.getBaet()));
		pacs.setDbip(StringUtil.iso2U8(pacs.getDbip()));
		pacs.setDbname(StringUtil.iso2U8(pacs.getDbname()));
		pacs.setDbport(StringUtil.iso2U8(pacs.getDbport()));
		pacs.setDbuser(StringUtil.iso2U8(pacs.getDbuser()));
		pacs.setDbpwd(StringUtil.iso2U8(pacs.getDbpwd()));
		if (!resourceService.isExistResource("PACS", Integer.parseInt(pacs.getOrgid()), pacs.getServer(),
				pacs.getId())) {
			resourceService.updatePacs(pacs);
			isSuccess = "1";
			result.put("id", String.valueOf(pacs.getId()));
		} else {
			isSuccess = "2";
		}
		result.put("success", isSuccess);
		return JSON.toJSONString(result);
	}

	@RequestMapping(value = "insertRisResource", method = RequestMethod.POST)
	@ResponseBody
	public String insertRis(Ris ris) {
		Map<String, String> result = new HashMap<String, String>();
		String isSuccess = "0";
		ris.setServer(StringUtil.iso2U8(ris.getServer()));
		ris.setType(StringUtil.iso2U8(ris.getType()));
		ris.setZaet(StringUtil.iso2U8(ris.getZaet()));
		ris.setBaet(StringUtil.iso2U8(ris.getBaet()));
		ris.setDbip(StringUtil.iso2U8(ris.getDbip()));
		ris.setDbname(StringUtil.iso2U8(ris.getDbname()));
		ris.setDbport(StringUtil.iso2U8(ris.getDbport()));
		ris.setDbuser(StringUtil.iso2U8(ris.getDbuser()));
		ris.setDbpwd(StringUtil.iso2U8(ris.getDbpwd()));
		// 判断该资源是否存在
		if (!resourceService.isExistResource("RIS", Integer.parseInt(ris.getOrgid()), ris.getServer(), 0)) {
			int id = resourceService.insertRis(ris);
			isSuccess = "1";
			result.put("id", String.valueOf(id));
		} else {
			isSuccess = "2";
		}

		result.put("success", isSuccess);
		return JSON.toJSONString(result);
	}

	@RequestMapping(value = "deleteRisResource")
	@ResponseBody
	public String deleteRis(int id) {
		int num = resourceService.deleteRis(id);
		return String.valueOf(num);
	}

	@RequestMapping(value = "searchRisResource", produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String searchRisbyId(int id) {
		List<Ris> list = resourceService.searchRisbyId(id);
		return JSON.toJSONString(list);
	}

	@RequestMapping(value = "updateRisResource")
	@ResponseBody
	public String updateRis(Ris ris) {
		Map<String, String> result = new HashMap<String, String>();
		String isSuccess = "0";
		ris.setServer(StringUtil.iso2U8(ris.getServer()));
		ris.setType(StringUtil.iso2U8(ris.getType()));
		ris.setZaet(StringUtil.iso2U8(ris.getZaet()));
		ris.setBaet(StringUtil.iso2U8(ris.getBaet()));
		ris.setDbip(StringUtil.iso2U8(ris.getDbip()));
		ris.setDbname(StringUtil.iso2U8(ris.getDbname()));
		ris.setDbport(StringUtil.iso2U8(ris.getDbport()));
		ris.setDbuser(StringUtil.iso2U8(ris.getDbuser()));
		ris.setDbpwd(StringUtil.iso2U8(ris.getDbpwd()));
		// 判断该资源是否存在
		if (!resourceService.isExistResource("RIS", Integer.parseInt(ris.getOrgid()), ris.getServer(), ris.getId())) {
			resourceService.updateRis(ris);
			isSuccess = "1";
			result.put("id", String.valueOf(ris.getId()));
		} else {
			isSuccess = "2";
		}
		result.put("success", isSuccess);
		return JSON.toJSONString(result);
	}

	@RequestMapping(value = "insertVideoResource", method = RequestMethod.POST)
	@ResponseBody
	public String insertVideo(Video video, HttpServletRequest request) {// 配置filter无效，出此下策
		Map<String, String> result = new HashMap<String, String>();
		String isSuccess = "0";
		try {
			// 判断该资源是否存在
			if (!resourceService.isExistResource("VIDEO", video.getOrgid(), video.getRoomname(), 0)) {
				int id = resourceService.insertVideo(video);
				isSuccess = "1";
				result.put("id", String.valueOf(id));
			} else {
				isSuccess = "2";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("success", isSuccess);
		return JSON.toJSONString(result);
	}

	@RequestMapping(value = "deleteVideoResource")
	@ResponseBody
	public String deleteVideo(int id) {
		int num = resourceService.deleteVideo(id);
		return String.valueOf(num);
	}

	@RequestMapping(value = "searchVideoResource", produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String searchVideo(int id, HttpServletResponse response) {
		List<Video> list = resourceService.searchVideo(id);
		return JSON.toJSONString(list);
	}

	@RequestMapping(value = "updateVideoResource")
	@ResponseBody
	public String updateVideo(Video video) throws UnsupportedEncodingException {
		Map<String, String> result = new HashMap<String, String>();
		String isSuccess = "0";
		try {
			// String roomtype = new
			// String(video.getRoomtype().getBytes("iso-8859-1"),"utf-8");
			// 判断该资源是否存在
			if (!resourceService.isExistResource("VIDEO", video.getOrgid(), video.getRoomname(), video.getId())) {
				resourceService.updateVideo(video);
				isSuccess = "1";
				result.put("id", String.valueOf(video.getId()));
			} else {
				isSuccess = "2";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("success", isSuccess);
		return JSON.toJSONString(result);
	}

	@RequestMapping(value = "findResource", produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String findResource(String servername, int id, HttpServletRequest request) {
		String str = "";
		str = servername;
		List<His> list = resourceService.findHis(str, id);
		Map<String, Object> map = new HashMap<String, Object>();
		if (list.size() >= 1) {
			map.put("HIS", list.get(0));
		} else {
			List<Lis> list2 = resourceService.findLis(str, id);
			if (list2.size() >= 1) {
				map.put("LIS", list2.get(0));
			} else {
				List<Pacs> list3 = resourceService.findPacs(str, id);
				if (list3.size() >= 1) {
					map.put("PACS", list3.get(0));
				} else {
					List<Ris> list4 = resourceService.findRis(str, id);
					if (list4.size() >= 1) {
						map.put("RIS", list4.get(0));
					} else {
						List<Video> list5 = resourceService.findVideo(str, id);
						if (list5.size() >= 1) {
							map.put("VIDEO", list5.get(0));
						}
					}
				}
			}
		}
		return JSON.toJSONString(map);
	}
}
