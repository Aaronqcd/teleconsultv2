package com.va.removeconsult.service;

import java.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import com.va.removeconsult.dao.*;
import com.va.removeconsult.pojo.*;

@Service
public class RecordService {
	@Autowired
	private RecordDao daoRecord;

	public int countVideo(Map<String, Object> params) {
		return daoRecord.countVideo(params);
	}

	public List<RecordVideo> queryVideo(Map<String, Object> params) {
		return daoRecord.queryVideo(params);
	}

	public RecordVideo detailVideo(int id) {
		return daoRecord.detailVideo(id);
	}

	public void deleteVideo(int id) {
		daoRecord.deleteVideo(id);
	}

	public void deleteVideos(String ids) {
		daoRecord.deleteVideos(ids);
	}

	public void renameVideo(int id, String name) {
		daoRecord.renameVideo(id, name);
	}

	public List<Map<String, Object>> queryMeetings(Map<String, Object> params) {
		List<Map<String, Object>> result = daoRecord.queryMeetings(params);
		if (result != null && result.size() > 0) {
			for (Map<String, Object> item : result) {
				item.put("text", item.get("no"));
			}
			
			Map<String,Object> all = new HashMap<String,Object>();
			all.put("id", 0);
			all.put("text", "--- 全部 ---");
			result.add(0, all);
		}
		return result;
	}
}