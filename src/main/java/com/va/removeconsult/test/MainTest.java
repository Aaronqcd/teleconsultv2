package com.va.removeconsult.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

public class MainTest {
 
	@Test
	public void main11() throws IOException, ParseException {
		
//		File directory = new File(System.getProperty("user.dir")+"/upload","1.txt");// 参数为空
//        String courseFile = directory.getCanonicalPath();
//        System.out.println(directory.exists()+"-"+courseFile);
//        System.out.println(System.getProperty("user.dir"));
//        
//        System.out.println(System.getProperty("java.class.path"));
//
        File f2 = new File(this.getClass().getResource("").getPath());
        System.out.println(f2);
        System.out.println((int)((Math.random()*9+1)*100000));
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d= format.parse("2018-12-12 14:46:00");
        Long s = (System.currentTimeMillis() - d.getTime()) / (1000 * 60);
        System.out.println(s>60L);
	}
}
