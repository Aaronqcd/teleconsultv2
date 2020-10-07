package com.va.removeconsult.pojo;

public class organization {
	private int id;
	private int pid;
	private int type;
	private String name;
	private int isteudborad;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIsteudborad() {return isteudborad;}
	public void setIsteudborad(int isteudborad) {this.isteudborad = isteudborad;}
}
