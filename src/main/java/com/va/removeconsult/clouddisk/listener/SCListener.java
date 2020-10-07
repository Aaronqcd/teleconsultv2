package com.va.removeconsult.clouddisk.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.va.removeconsult.clouddisk.util.ConfigureReader;
import com.va.removeconsult.clouddisk.util.FileBlockUtil;
import com.va.removeconsult.clouddisk.util.Printer;


public class SCListener implements ServletContextListener {
	public void contextInitialized(final ServletContextEvent sce) {
		System.out.println("\u6587\u4ef6\u7cfb\u7edf\u8282\u70b9\u4fe1\u606f\u6821\u5bf9...");
		
		String realPath = sce.getServletContext().getRealPath(File.separator);
		
		System.out.println(" 当前web项目根目录 "+  realPath);
		
		ConfigureReader.instance().setFileSystemPath(realPath + File.separator + "filesystem" + File.separator);
		
		final String fsp = ConfigureReader.instance().getFileSystemPath();
		
		System.out.println("之后的文件目录 = " + fsp);
		final File fspf = new File(fsp);
		if (fspf.isDirectory() && fspf.canRead() && fspf.canWrite()) {
			final ApplicationContext context = (ApplicationContext) WebApplicationContextUtils
					.getWebApplicationContext(sce.getServletContext());
			final FileBlockUtil fbu = context.getBean(FileBlockUtil.class);
			fbu.checkFileBlocks();
			final String tfPath = ConfigureReader.instance().getTemporaryfilePath();
			final File f = new File(tfPath);
			if (!f.exists()) {
				f.mkdir();
			} else {
				final File[] listFiles = f.listFiles();
				for (final File fs : listFiles) {
					fs.delete();
				}
			}
			System.out.println("\u6821\u5bf9\u5b8c\u6210\u3002");
		} else {
			System.out.println(
					"\u9519\u8bef\uff1a\u6587\u4ef6\u7cfb\u7edf\u8282\u70b9\u4fe1\u606f\u6821\u5bf9\u5931\u8d25\uff0c\u5b58\u50a8\u4f4d\u7f6e\u65e0\u6cd5\u8bfb\u5199\u6216\u4e0d\u5b58\u5728\u3002");
		}
	}

	public void contextDestroyed(final ServletContextEvent sce) {
		System.out.println("\u6e05\u7406\u4e34\u65f6\u6587\u4ef6...");
		final String tfPath = ConfigureReader.instance().getTemporaryfilePath();
		final File f = new File(tfPath);
		final File[] listFiles = f.listFiles();
		for (final File fs : listFiles) {
			fs.delete();
		}
	}
}
