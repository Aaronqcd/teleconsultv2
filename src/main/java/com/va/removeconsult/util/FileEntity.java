package com.va.removeconsult.util;
import java.sql.Timestamp;

public class FileEntity {
    private String type;
    private String size;
    private String path;
    private String titleOrig;
    private String titleAlter;
    private Timestamp uploadTime;
    private String dcmURL;
    
	public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getTitleOrig() {
        return titleOrig;
    }
    public void setTitleOrig(String titleOrig) {
        this.titleOrig = titleOrig;
    }
    public String getTitleAlter() {
        return titleAlter;
    }
    public void setTitleAlter(String titleAlter) {
        this.titleAlter = titleAlter;
    }
    public Timestamp getUploadTime() {
        return uploadTime;
    }
    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }
    
    
    public String getDcmURL() {
		return dcmURL;
	}
	public void setDcmURL(String dcmURL) {
		this.dcmURL = dcmURL;
	}
	
	@Override
	public String toString() {
		return "FileEntity [type=" + type + ", size=" + size + ", path=" + path + ", titleOrig=" + titleOrig
				+ ", titleAlter=" + titleAlter + ", uploadTime=" + uploadTime  
				+ " dcmURL=" + dcmURL + "]"
				;
	}
	    
}