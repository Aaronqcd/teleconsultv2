package com.va.removeconsult.clouddisk.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.va.removeconsult.clouddisk.util.FileNodeUtil;


public class H2DBinitListener implements ServletContextListener {
	public void contextInitialized(final ServletContextEvent sce) {
		FileNodeUtil.initNodeTableToDataBase();
	}

	public void contextDestroyed(final ServletContextEvent sce) {
	}
}
