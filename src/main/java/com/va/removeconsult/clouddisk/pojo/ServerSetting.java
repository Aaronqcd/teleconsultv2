package com.va.removeconsult.clouddisk.pojo;

import com.va.removeconsult.clouddisk.enumeration.LogLevel;

public class ServerSetting
{
    private boolean mustLogin;     
    private int buffSize;
    private LogLevel log;
    private int port;
    private String fsPath;
    private String scheme;
    
    public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public boolean isMustLogin() {
        return this.mustLogin;
    }
    
    public void setMustLogin(final boolean mustLogin) {
        this.mustLogin = mustLogin;
    }
    
    public int getBuffSize() {
        return this.buffSize;
    }
    
    public void setBuffSize(final int buffSize) {
        this.buffSize = buffSize;
    }
    
    public LogLevel getLog() {
        return this.log;
    }
    
    public void setLog(final LogLevel log) {
        this.log = log;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public String getFsPath() {
        return this.fsPath;
    }
    
    public void setFsPath(final String fsPath) {
        this.fsPath = fsPath;
    }
}
