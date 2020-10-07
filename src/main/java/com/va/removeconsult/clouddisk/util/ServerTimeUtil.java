package com.va.removeconsult.clouddisk.util;

import java.util.*;
import java.text.*;

public class ServerTimeUtil
{
    public static String accurateToSecond() {
        final Date d = new Date();
        final DateFormat df = new SimpleDateFormat("YYYY\u5e74MM\u6708dd\u65e5 HH:mm:ss");
        return df.format(d);
    }
    
    public static String accurateToMinute() {
        final Date d = new Date();
        final DateFormat df = new SimpleDateFormat("YYYY\u5e74MM\u6708dd\u65e5 HH:mm");
        return df.format(d);
    }
    
    public static String accurateToDay() {
        final Date d = new Date();
        final DateFormat df = new SimpleDateFormat("YYYY\u5e74MM\u6708dd\u65e5 HH:mm:ss");
        return df.format(d);
    }
    
    public static Date accurateToStr(String str) throws ParseException{
    	  final DateFormat df = new SimpleDateFormat("yyyy��MM��dd��");
    	  return df.parse(str);
    }
    
    
    public static Date accurateToSecond(String str) throws ParseException {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.parse(str);
    }
    
    /** 
	 * ʱ��Ӽ����� 
	 * @param startDate Ҫ�����ʱ�䣬Null��Ϊ��ǰʱ�� 
	 * @param days �Ӽ������� 
	 * @return Date 
	 */
	public static Date dateAddDays(Date startDate, int days) {
		if (startDate == null) {
			startDate = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.set(Calendar.DATE, c.get(Calendar.DATE) + days);
		return c.getTime();
	}
 
	/** 
	 * ʱ��Ƚϣ����myDate>compareDate����1��<����-1����ȷ���0�� 
	 * @param myDate ʱ�� 
	 * @param compareDate Ҫ�Ƚϵ�ʱ�� 
	 * @return int 
	 */
	public static int dateCompare(Date myDate, Date compareDate) {
		Calendar myCal = Calendar.getInstance();
		Calendar compareCal = Calendar.getInstance();
		myCal.setTime(myDate);
		compareCal.setTime(compareDate);
		return myCal.compareTo(compareCal);
	}
    
    public static String accurateToLogName() {
        final Date d = new Date();
        final DateFormat df = new SimpleDateFormat("YYYY_MM_dd");
        return df.format(d);
    }
    
    public static String getDateTime() {
        final Date d = new Date();
        final DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return df.format(d);
    }
    
    public static Date getServerTime() {
        return new Date();
    }
}
