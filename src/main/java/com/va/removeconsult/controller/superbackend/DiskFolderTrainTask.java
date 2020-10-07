package com.va.removeconsult.controller.superbackend;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.va.removeconsult.clouddisk.service.FolderService;


@Component
public class DiskFolderTrainTask {
	
	@Resource
	private FolderService fs;
	
	 @Scheduled(cron = "0 30 23 * * ? ") 
	    public void queryMiddleBUdgetInfo() {
		 String meetimgCycle = fs.querySysConfValue("MEETING_CYCLE");
		 if("0".equals(meetimgCycle)){
		 }else{
			 System.out.println("SpringMVC框架配置定时任务启动删除文件 时间："+new Date(System.currentTimeMillis()));
			 fs.deleteOverdueFolder(meetimgCycle);
		 }
		
	    }
}
