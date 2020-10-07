package com.va.removeconsult.clouddisk.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;


public class EncodeUtil {
	
	private EncodeUtil() {}

	
	public static String getFileNameByUTF8(String name) {
		try {
			return URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20").replaceAll("%28", "\\(")
					.replaceAll("%29", "\\)").replaceAll("%3B", ";").replaceAll("%40", "@").replaceAll("%23", "\\#")
					.replaceAll("%26", "\\&");
		} catch (UnsupportedEncodingException e) {
			
			return name.replaceAll("\\+", "%20").replaceAll("%28", "\\(").replaceAll("%29", "\\)")
					.replaceAll("%3B", ";").replaceAll("%40", "@").replaceAll("%23", "\\#").replaceAll("%26", "\\&");
		}
	}
	
	public static void toDecode(String h){
		if(StringUtils.isNotBlank(h)){
			KeyUtil.unlockKey(h);
		}
	}

}
