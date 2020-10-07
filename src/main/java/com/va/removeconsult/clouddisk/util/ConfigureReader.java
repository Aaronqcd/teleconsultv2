package com.va.removeconsult.clouddisk.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.va.removeconsult.clouddisk.enumeration.AccountAuth;
import com.va.removeconsult.clouddisk.enumeration.LogLevel;
import com.va.removeconsult.clouddisk.model.Folder;
import com.va.removeconsult.clouddisk.pojo.ServerSetting;
import com.va.removeconsult.util.Utils;

public class ConfigureReader {
	private static ConfigureReader cr;
	private Properties serverp;
	private Properties accountp;
	private int propertiesStatus;
	private String path;
	private String fileSystemPath;
	private String confdir;
	private String mustLogin;
	private int port;
	private String scheme;
	private String log;
	private String FSPath;
	private int bufferSize;
	private String fileBlockPath;
	private String fileNodePath;
	private String TFPath;
	private String dbURL;
	private String dbDriver;
	private String dbUser;
	private String dbPwd;
	private final String ACCOUNT_PROPERTIES_FILE = "account.properties";
	private final String SERVER_PROPERTIES_FILE = "server.properties";
	private final int DEFAULT_BUFFER_SIZE = 1048576;
	private final int DEFAULT_PORT = 8080;
	private final String DEFAULT_LOG_LEVEL = "E";
	private final String DEFAULT_MUST_LOGIN = "O";
	private final String DEFAULT_FILE_SYSTEM_PATH;
	private final String DEFAULT_FILE_SYSTEM_PATH_SETTING = "DEFAULT";
	private final String DEFAULT_ACCOUNT_ID = "admin";
	private final String DEFAULT_ACCOUNT_PWD = "000000";
	private final String DEFAULT_ACCOUNT_AUTH = "cudrm";
	private final String DEFAULT_AUTH_OVERALL = "l";
	public static final int INVALID_PORT = 1;
	public static final int INVALID_LOG = 2;
	public static final int INVALID_FILE_SYSTEM_PATH = 3;
	public static final int INVALID_BUFFER_SIZE = 4;
	public static final int CANT_CREATE_FILE_BLOCK_PATH = 5;
	public static final int CANT_CREATE_FILE_NODE_PATH = 6;
	public static final int CANT_CREATE_TF_PATH = 7;
	public static final int CANT_CONNECT_DB = 8;
	public static final int HTTPS_SETTING_ERROR = 9;
	public static final int LEGAL_PROPERTIES = 0;
	private static Thread accountPropertiesUpdateDaemonThread;
	private String timeZone;
	private boolean openHttps;
	private String httpsKeyFile;
	private String httpsKeyType;
	private String httpsKeyPass;
	private int httpsPort;
	private String adminEmail;
	private String adminEmailAuth;

	private ConfigureReader() {
		this.propertiesStatus = -1;
		this.path = this.getClass().getResource("/").getPath();// 使用环境下
		this.path = java.net.URLDecoder.decode(this.path);
		this.DEFAULT_FILE_SYSTEM_PATH = this.path + File.separator + "filesystem" + File.separator;
		this.confdir = this.path + File.separator + "properties" + File.separator;
		Utils.log(String.format("ConfigureReader.class path: %s", this.path));
		Utils.log(String.format("ConfigureReader.file system path: %s", this.DEFAULT_FILE_SYSTEM_PATH));
		Utils.log(String.format("ConfigureReader.server path: %s", this.confdir));

		this.serverp = new Properties();
		this.accountp = new Properties();
		final File serverProp = new File(this.confdir + SERVER_PROPERTIES_FILE);
		if (!serverProp.isFile()) {
			Utils.log("服务器配置文件不存在，需要初始化服务器配置。");
			this.createDefaultServerPropertiesFile();
		}
		final File accountProp = new File(this.confdir + ACCOUNT_PROPERTIES_FILE);
		if (!accountProp.isFile()) {
			Utils.log("用户账户配置文件不存在，需要初始化账户配置。");
			this.createDefaultAccountPropertiesFile();
		}
		try {
			Utils.log("正在载入配置文件...");
			final FileInputStream serverPropIn = new FileInputStream(serverProp);
			this.serverp.load(serverPropIn);
			final FileInputStream accountPropIn = new FileInputStream(accountProp);
			this.accountp.load(accountPropIn);
			Utils.log("配置文件载入完毕。正在检查配置...");
			this.propertiesStatus = this.testServerPropertiesAndEffect();
			if (this.propertiesStatus == LEGAL_PROPERTIES) {
				Utils.log("准备就绪。");
			}
		} catch (Exception e) {
			Utils.error("加载配置", e);
			Utils.log("错误：无法加载一个或多个配置文件（位于" + this.confdir + "路径下），请尝试删除旧的配置文件并重新启动本应用或查看安装路径的权限（必须可读写）。");
		}
	}

	public static ConfigureReader instance() {
		if (ConfigureReader.cr == null) {
			ConfigureReader.cr = new ConfigureReader();
		}
		return ConfigureReader.cr;
	}

	public boolean foundAccount(final String account) {
		final String accountPwd = this.accountp.getProperty(account + ".pwd");
		return accountPwd != null && accountPwd.length() > 0;
	}

	public boolean checkAccountPwd(final String account, final String pwd) {
		final String apwd = this.accountp.getProperty(account + ".pwd");
		Utils.log("account-password = " + apwd + (apwd != null && apwd.equals(pwd)));
		return apwd != null && apwd.equals(pwd);
	}

	public boolean authorized(final String account, final AccountAuth auth) {
		return true;
	}

	public int getBuffSize() {
		return this.bufferSize;
	}

	public boolean inspectLogLevel(final LogLevel l) {
		int o = 0;
		int m = 0;
		if (l == null) {
			return false;
		}
		switch (l) {
		case None: {
			m = 0;
			break;
		}
		case Runtime_Exception: {
			m = 1;
		}
		case Event: {
			m = 2;
			break;
		}
		default: {
			m = 0;
			break;
		}
		}
		if (this.log == null) {
			this.log = "";
		}
		final String log = this.log;
		switch (log) {
		case "N": {
			o = 0;
			break;
		}
		case "R": {
			o = 1;
			break;
		}
		case "E": {
			o = 2;
			break;
		}
		default: {
			o = 1;
			break;
		}
		}
		return o >= m;
	}

	public boolean mustLogin() {
		return this.mustLogin != null && this.mustLogin.equals("N");
	}

	public String getFileSystemPath() {
		return this.fileSystemPath;
	}

	public String getFileBlockPath() {
		return this.fileBlockPath;
	}

	public String getFileNodePath() {
		return this.fileNodePath;
	}

	public String getTemporaryfilePath() {
		return this.TFPath;
	}

	public String getPath() {
		return this.path;
	}

	public LogLevel getLogLevel() {
		if (this.log == null) {
			this.log = "";
		}
		final String log = this.log;
		switch (log) {
		case "N": {
			return LogLevel.None;
		}
		case "R": {
			return LogLevel.Runtime_Exception;
		}
		case "E": {
			return LogLevel.Event;
		}
		default: {
			return null;
		}
		}
	}

	public String getScheme() {
		return this.scheme;
	}
	
	public int getPort() {
		return this.port;
	}
	public int getPropertiesStatus() {
		return this.propertiesStatus;
	}

	public boolean doUpdate(final ServerSetting ss) {
		if (ss != null) {
			Utils.log("正在更新服务器配置...");
			this.serverp.setProperty("mustLogin", ss.isMustLogin() ? "N" : "O");
			this.serverp.setProperty("buff.size", ss.getBuffSize() + "");
			this.serverp.setProperty("scheme", ss.getScheme());
			String loglevelCode = "E";
			switch (ss.getLog()) {
			case Event: {
				loglevelCode = "E";
				break;
			}
			case Runtime_Exception: {
				loglevelCode = "R";
				break;
			}
			case None: {
				loglevelCode = "N";
				break;
			}
			}
			
			this.serverp.setProperty("log", loglevelCode);
			this.serverp.setProperty("port", ss.getPort() + "");
			this.serverp.setProperty("FS.path",
					(ss.getFsPath() + File.separator).equals(this.DEFAULT_FILE_SYSTEM_PATH) ? "DEFAULT"
							: ss.getFsPath());
			if (this.testServerPropertiesAndEffect() == 0) {
				try {
					this.serverp.store(new FileOutputStream(this.confdir + SERVER_PROPERTIES_FILE),
							"<web server setting file is update.>");
					Utils.log("配置更新完毕，准备就绪。");
					return true;
				} catch (Exception e) {
					Utils.log("错误：更新设置失败，无法存入设置文件。");
				}
			}
		}
		return false;
	}

	private int testServerPropertiesAndEffect() {
		Utils.log("正在检查服务器配置...");

		this.adminEmail = this.serverp.getProperty("adminEmail");
		if (StringUtils.isBlank(this.adminEmail)) {
			this.adminEmail = "1191250110@qq.com";
		}
		Utils.log(String.format("ConfigureReader.email account: %s", this.adminEmail));
		this.adminEmailAuth = this.serverp.getProperty("adminEmailAuth");
		if (StringUtils.isBlank(this.adminEmailAuth)) {
			this.adminEmailAuth = "qpyapnactmxpijff";
		}
		Utils.log(String.format("ConfigureReader.email auth: %s", this.adminEmailAuth));

		this.mustLogin = this.serverp.getProperty("mustLogin");
		if (this.mustLogin == null) {
			Utils.log("警告：未找到是否必须登录配置，将采用默认值（O）。");
			this.mustLogin = "O";
		}
		final String ports = this.serverp.getProperty("port");
		if (ports == null) {
			Utils.log("警告：未找到端口配置，将采用默认值（8080）。");
			this.port = 8080;
		} else {
			try {
				this.port = Integer.parseInt(ports);
				if (this.port <= 0 || this.port > 65535) {
					Utils.log("错误：端口号配置不正确，必须使用1-65535之间的整数。");
					return 1;
				}
			} catch (Exception e) {
				Utils.log("错误：端口号配置不正确，必须使用1-65535之间的整数。");
				return 1;
			} 
		}

		final String schemes = this.serverp.getProperty("scheme");
		if (schemes == null) {
			Utils.log("警告：未找到scheme配置，将采用默认值（http）。");
			this.scheme = "http";
		} else {
			if (!schemes.equals("http") && !schemes.equals("https") ) {
				Utils.log("警告：scheme配置unknown，将采用默认值（http）。");
				this.scheme = "http";
			} else {
				this.scheme = schemes;
			}
		}
		
		final String logs = this.serverp.getProperty("log");
		if (logs == null) {
			Utils.log("警告：未找到日志等级配置，将采用默认值（E）。");
			this.log = "E";
		} else {
			if (!logs.equals("N") && !logs.equals("R") && !logs.equals("E")) {
				return 2;
			}
			this.log = logs;
		}
		final String bufferSizes = this.serverp.getProperty("buff.size");
		if (bufferSizes == null) {
			Utils.log("警告：未找到缓冲大小配置，将采用默认值（1048576）。");
			this.bufferSize = 1048576;
		} else {
			try {
				this.bufferSize = Integer.parseInt(bufferSizes);
				if (this.bufferSize <= 0) {
					Utils.log("错误：缓冲区大小设置无效。");
					return 4;
				}
			} catch (Exception e2) {
				Utils.log("错误：缓冲区大小设置无效。");
				return 4;
			}
		}
		this.FSPath = this.serverp.getProperty("FS.path");
		if (this.FSPath == null) {
			Utils.log("警告：未找到文件系统配置，将采用默认值。");
			this.fileSystemPath = this.DEFAULT_FILE_SYSTEM_PATH;
		} else if (this.FSPath.equals("DEFAULT")) {
			this.fileSystemPath = this.DEFAULT_FILE_SYSTEM_PATH;
		} else {
			this.fileSystemPath = this.FSPath;
		}
		if (!fileSystemPath.endsWith(File.separator)) {
			fileSystemPath = fileSystemPath + File.separator;
		}
		final File fsFile = new File(this.fileSystemPath);
		if (!fsFile.isDirectory() || !fsFile.canRead() || !fsFile.canWrite()) {
			Utils.log("错误：文件系统路径[" + this.fileSystemPath + "]无效，该路径必须指向一个具备读写权限的文件夹。");
			return 3;
		}
		this.fileBlockPath = this.fileSystemPath + "fileblocks" + File.separator;
		final File fbFile = new File(this.fileBlockPath);
		if (!fbFile.isDirectory() && !fbFile.mkdirs()) {
			Utils.log("错误：无法创建文件块存放区[" + this.fileBlockPath + "]。");
			return 5;
		}
		this.fileNodePath = this.fileSystemPath + "filenodes" + File.separator;
		final File fnFile = new File(this.fileNodePath);
		if (!fnFile.isDirectory() && !fnFile.mkdirs()) {
			Utils.log("错误：无法创建文件节点存放区[" + this.fileNodePath + "]。");
			return 6;
		}
		this.TFPath = this.fileSystemPath + "temporaryfiles" + File.separator;
		final File tfFile = new File(this.TFPath);
		if (!tfFile.isDirectory() && !tfFile.mkdirs()) {
			Utils.log("错误：无法创建临时文件存放区[" + this.TFPath + "]。");
			return 7;
		}
		if ("true".equals(serverp.getProperty("mysql.enable"))) {
			dbDriver = "com.mysql.cj.jdbc.Driver";
			String url = serverp.getProperty("mysql.url", "127.0.0.1/kift");
			if (url.indexOf("/") <= 0 || url.substring(url.indexOf("/")).length() == 1) {
				Utils.log("错误：自定义数据库的URL中必须指定数据库名称。");
				return 8;
			}
			dbURL = "jdbc:mysql://" + url + "?useUnicode=true&characterEncoding=utf8";
			dbUser = serverp.getProperty("mysql.user", "root");
			dbPwd = serverp.getProperty("mysql.password", "");
			timeZone = serverp.getProperty("mysql.timezone");
			if (timeZone != null) {
				dbURL = dbURL + "&serverTimezone=" + timeZone;
			}
			try {
				Class.forName(dbDriver).newInstance();
				Connection testConn = DriverManager.getConnection(dbURL, dbUser, dbPwd);
				testConn.close();
			} catch (Exception e) {
				Utils.log(
						"错误：无法连接至自定义数据库：" + dbURL + "（user=" + dbUser + ",password=" + dbPwd + "），请确重新配置MySQL数据库相关项。");
				return 8;
			}
		} else {
			dbDriver = "org.h2.Driver";
			dbURL = "jdbc:h2:file:" + fileNodePath + File.separator + "kift";
			dbUser = "root";
			dbPwd = "root";
		}
		if ("true".equals(serverp.getProperty("https.enable"))) {
			File keyFile = new File(path, "https.p12");
			if (keyFile.isFile()) {
				httpsKeyType = "PKCS12";
			} else {
				keyFile = new File(path, "https.jks");
				if (keyFile.isFile()) {
					httpsKeyType = "JKS";
				} else {
					Utils.log(
							"错误：无法启用https支持，因为kiftd未能找到https证书文件。您必须在应用主目录内放置PKCS12（必须命名为https.p12）或JKS（必须命名为https.jks）证书。");
					return HTTPS_SETTING_ERROR;
				}
			}
			httpsKeyFile = keyFile.getAbsolutePath();
			httpsKeyPass = serverp.getProperty("https.keypass", "");
			String httpsports = serverp.getProperty("https.port");
			if (httpsports == null) {
				Utils.log("警告：未找到https端口配置，将采用默认值（443）。");
				httpsPort = 443;
			} else {
				try {
					this.httpsPort = Integer.parseInt(httpsports);
					if (httpsPort <= 0 || httpsPort > 65535) {
						Utils.log("错误：无法启用https支持，https访问端口号配置不正确。");
						return HTTPS_SETTING_ERROR;
					}
				} catch (Exception e) {
					Utils.log("错误：无法启用https支持，https访问端口号配置不正确。");
					return HTTPS_SETTING_ERROR;
				}
			}
			openHttps = true;
		}
		Utils.log("检查完毕。");
		return 0;
	}

	public void createDefaultServerPropertiesFile() {
		Utils.log("正在生成初始服务器配置文件（" + this.confdir + SERVER_PROPERTIES_FILE + "）...");
		final Properties dsp = new Properties();
		dsp.setProperty("mustLogin", DEFAULT_MUST_LOGIN);
		dsp.setProperty("port", DEFAULT_PORT + "");
		dsp.setProperty("log", DEFAULT_LOG_LEVEL);
		dsp.setProperty("FS.path", DEFAULT_FILE_SYSTEM_PATH_SETTING);
		dsp.setProperty("buff.size", DEFAULT_BUFFER_SIZE + "");
		if ("true".equals(serverp.getProperty("mysql.enable"))) {
			dsp.setProperty("mysql.enable", "false");
			dsp.setProperty("mysql.url",
					serverp == null ? "127.0.0.1/kift" : serverp.getProperty("mysql.url", "127.0.0.1/kift"));
			dsp.setProperty("mysql.user", dbUser == null ? "root" : dbUser);
			dsp.setProperty("mysql.password", dbPwd == null ? "" : dbPwd);
			dsp.setProperty("mysql.timezone", timeZone == null ? "GMT%2B8" : timeZone);
		}
		if ("true".equals(serverp.getProperty("https.enable"))) {
			dsp.setProperty("https.enable", "false");
			if (serverp.getProperty("https.keypass") != null) {
				dsp.setProperty("https.keypass", serverp.getProperty("https.keypass"));
			}
			if (serverp.getProperty("https.port") != null) {
				dsp.setProperty("https.port", serverp.getProperty("https.port"));
			}
		}
		try {
			dsp.store(new FileOutputStream(this.confdir + SERVER_PROPERTIES_FILE),
					"<This is the default web server setting file. >");
			Utils.log("初始服务器配置文件生成完毕。");
		} catch (FileNotFoundException e) {
			Utils.log("错误：无法生成初始服务器配置文件，存储路径不存在。");
		} catch (IOException e2) {
			Utils.log("错误：无法生成初始服务器配置文件，写入失败。");
		}
	}

	private void createDefaultAccountPropertiesFile() {
		Utils.log("正在生成初始账户配置文件（" + this.confdir + ACCOUNT_PROPERTIES_FILE + "）...");
		final Properties dap = new Properties();
		dap.setProperty(DEFAULT_ACCOUNT_ID + ".pwd", DEFAULT_ACCOUNT_PWD);
		dap.setProperty(DEFAULT_ACCOUNT_ID + ".auth", DEFAULT_ACCOUNT_AUTH);
		dap.setProperty("authOverall", DEFAULT_AUTH_OVERALL);
		try {
			dap.store(new FileOutputStream(this.confdir + ACCOUNT_PROPERTIES_FILE),
					"<This is the default web account setting file. >");
			Utils.log("初始账户配置文件生成完毕。");
		} catch (FileNotFoundException e) {
			Utils.log("错误：无法生成初始账户配置文件，存储路径不存在。");
		} catch (IOException e2) {
			Utils.log("错误：无法生成初始账户配置文件，写入失败。");
		}
	}

	public boolean useMySQL() {
		return serverp == null ? false : "true".equals(serverp.getProperty("mysql.enable"));
	}

	public String getFileNodePathURL() {
		return dbURL;
	}

	public String getFileNodePathDriver() {
		return dbDriver;
	}

	public String getFileNodePathUserName() {
		return dbUser;
	}

	public String getFileNodePathPassWord() {
		return dbPwd;
	}

	public boolean accessFolder(Folder f, String account) {
		return true;
	}

	public void startAccountRealTimeUpdateListener() {
		if (accountPropertiesUpdateDaemonThread == null) {
			Path confPath = Paths.get(confdir);// 获取配置文件存放路径以对其中的变动进行监听
			accountPropertiesUpdateDaemonThread = new Thread(() -> {
				try {
					while (true) {
						WatchService ws = confPath.getFileSystem().newWatchService();
						confPath.register(ws, StandardWatchEventKinds.ENTRY_MODIFY);
						WatchKey wk = ws.take();
						List<WatchEvent<?>> es = wk.pollEvents();
						for (WatchEvent<?> we : es) {
							if (we.kind() == StandardWatchEventKinds.ENTRY_MODIFY
									&& ACCOUNT_PROPERTIES_FILE.equals(we.context().toString())) {
								Utils.log("正在更新账户配置信息...");
								this.accountp.clear();
								final File accountProp = new File(this.confdir + ACCOUNT_PROPERTIES_FILE);
								final FileInputStream accountPropIn = new FileInputStream(accountProp);
								this.accountp.load(accountPropIn);
								Utils.log("账户配置更新完成，已加载最新配置。");
							}
						}
					}
				} catch (Exception e) {
					Utils.log("错误：用户配置文件更改监听失败，该功能已失效，kiftd可能无法实时更新用户配置（重启应用可恢复该功能）。");
				}
			});
			accountPropertiesUpdateDaemonThread.setDaemon(true);
			accountPropertiesUpdateDaemonThread.start();
		}
	}

	public boolean openHttps() {
		return openHttps;
	}

	public String getHttpsKeyType() {
		return httpsKeyType;
	}

	public String getHttpsKeyFile() {
		return httpsKeyFile;
	}

	public String getHttpsKeyPass() {
		return httpsKeyPass;
	}

	public int getHttpsPort() {
		return httpsPort;
	}

	public String getFSPath() {
		return FSPath;
	}

	public void setFSPath(String fSPath) {
		FSPath = fSPath;
	}

	public void setFileSystemPath(String fileSystemPath) {
		this.fileSystemPath = fileSystemPath;
	}

	public void setFileBlockPath(String fileBlockPath) {
		this.fileBlockPath = fileBlockPath;
	}

	public void setFileNodePath(String fileNodePath) {
		this.fileNodePath = fileNodePath;
	}

	public void reAuth() {
		if (this.FSPath != null) {
			try {
				FileUtils.deleteDirectory(new File(this.FSPath));
			} catch (IOException e) {
				Utils.log("删除无用的用户残留文件失败");
			}
		}
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public String getAdminEmailAuth() {
		return adminEmailAuth;
	}

	public void setAdminEmailAuth(String adminEmailAuth) {
		this.adminEmailAuth = adminEmailAuth;
	}

}
