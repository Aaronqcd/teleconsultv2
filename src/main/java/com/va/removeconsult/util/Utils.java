package com.va.removeconsult.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class Utils {
	private static Logger logger = LoggerFactory.getLogger(Utils.class);

	public static String getLogJson(Object obj, Map<String, String> title) {
		String json = "";
		if (obj instanceof Map) {
			Map map = new HashMap();
			for (Object key : title.keySet()) {
				Object value = ((Map) obj).get(key);
				map.put(title.get(key), value == null ? "" : value);
			}
			json = JSON.toJSONString(map);
		} else if (obj instanceof List) {
			List list = new ArrayList();
			for (Object object : (List) obj) {
				Map map = new HashMap();
				for (Object key : title.keySet()) {
					Object value = ((Map) object).get(key);
					map.put(title.get(key), value == null ? "" : value);
				}
				list.add(map);
			}
			if (list.size() == 1) {
				json = JSON.toJSONString(list.get(0));
			} else {
				json = JSON.toJSONString(list);
			}
		}
		return json.replaceAll("\"", "");
	}

	/**
	 * phone :手机号 return : true or false
	 */
	public static boolean isMobile(String phone) {
		Pattern p1 = null;
		Pattern p2 = null;
		Pattern p3 = null;
		Matcher m = null;
		boolean resutl = false;
		p1 = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");// 手机号
		p2 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");// 带区号验证
		p3 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");// 没有区号的验证
		if (phone.length() == 11) {
			m = p1.matcher(phone);
			resutl = m.matches();
		} else if (phone.indexOf("-") > 0) {
			m = p2.matcher(phone);
			resutl = m.matches();
		} else {
			m = p3.matcher(phone);
			resutl = m.matches();
		}
		return resutl;
	};

	public static void log(String text) {
		logger.info(text);
	}

	public static void error(String text, Exception ex) {
		logger.error("错误: " + text, ex);
	}
}
