package com.va.removeconsult.util;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;

public class StringUtil {

	private static final Pattern DATE_PATTERN = Pattern.compile(
			"^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(T)?(\\s)?((((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");

	private static final Pattern BLANK_PATTERN = Pattern.compile("\t|\r|\n");

	private static final Pattern IP_PATTERN = Pattern.compile(
			"^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]" + "|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");

	public static boolean isIP(String str) {
		return IP_PATTERN.matcher(str).matches();
	}

	/**
	 * 功能：判断字符串是否为日期格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDate(String strDate) {
		Matcher m = DATE_PATTERN.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 去掉制表符 换行
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {

		String dest = "";
		if (str != null) {
			Matcher m = BLANK_PATTERN.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;

	}

	public static String ascii2native(String asciicode) {
		String[] asciis = asciicode.split("\\\\u");
		String nativeValue = asciis[0];
		try {
			for (int i = 1; i < asciis.length; i++) {
				String code = asciis[i];
				nativeValue += (char) Integer.parseInt(code.substring(0, 4), 16);
				if (code.length() > 4) {
					nativeValue += code.substring(4, code.length());
				}
			}
		} catch (NumberFormatException e) {
			return asciicode;
		}
		return nativeValue;
	}

	public static String removeStrArrayByKey(String str, String key) {
		String[] aStrings = str.split("\\,");
		StringBuffer sBuffer = new StringBuffer();
		if (str.indexOf(key) != -1 && aStrings.length > 0) {
			int flag = 0;
			for (int i = 0; i < aStrings.length; i++) {
				flag += 1;
				if (!aStrings[i].equals(key)) {
					sBuffer.append(aStrings[i] + ",");
				}
			}
		}
		String string = sBuffer.toString();
		String res = string.indexOf(",") == -1 ? string : string.substring(0, string.length() - 1).trim();
		return res;
	}

	public static String iso2U8(String src) {
		if (src == null)
			return null;
		try {
			return new String(src.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return src;
	}

	public static void main(String[] args) throws ParseException {

		String removeStrArrayByKey = removeStrArrayByKey("1", "34");
		// String reg =
		// "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(T)?(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
		// System.out.println( StringUtil.isDate("2016-08-09T01:01:01"));
		// System.out.println(DateUtil.parseAll(("2016-08-09T12:01:01")));
		// System.out.println(StringUtil.replaceBlank("just do it!"));
		// /System.out.println(DateUtil.dateFormat(new Date(), "yyyy-MMM-ddd"));

		System.out.println(replaceBlank("index = test\n\t\t\n\n\t\t\t\n\t\n\t\t\t"));
	}

}
