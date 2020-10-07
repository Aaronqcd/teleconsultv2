package web.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class Helper {
	public static String DateTimeFromat(int time,String format){
		java.text.DateFormat format2 = new java.text.SimpleDateFormat(format);
		String s = format2.format(time);
		return s;
	}
	
	public static String DateTimeFromat(Timestamp time,String format){
		java.text.DateFormat format2 = new java.text.SimpleDateFormat(format);
		String s = format2.format(time);
		return s;
	}
	
	public static String StringSplitMerge(String strs,String str){
		String result="";
		Set<String> sSet=new HashSet<String>();
		String [] sbox=strs.split(",");
		for(String s:sbox){
			sSet.add(s);
		}
		sSet.add(str);
		String spl="";
		for(String s:sSet){
			if(s.equals(""))continue;
			result+=spl+s;
			spl=",";
		}
		return result;
	}
	public static String findNotExistsStringIds(String strs,String str) {
		String result="";
		if(!StringUtils.isEmpty(str)) {
			String[] sbox = strs.split(",");
			String[] lbox = str.split(",");
			boolean isExist = false;
			for (String s : sbox) {
				isExist = false;
				for (String l : lbox) {
					if(s.equals(l)) {
						isExist = true;
						break;
					}
				}
				if(!isExist) {
					result += (s+",");
				}
			}
			result = result.length()>0?result.substring(0, result.length()-1):result;
		} else {
			result = strs;
		}
		return result;
	}
	public static void main(String[] args) {
		System.err.println(StringSplitMerge("1,2,3,4","6"));
		System.err.println(findNotExistsStringIds("1,2,3,4", "2,3,"));
	}
}
