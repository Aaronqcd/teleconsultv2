package com.va.removeconsult.dao;

import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

public class UserListSql {
	
	public String getUser(final Map<String,Object>param){

		String sql= new SQL(){
			{
				SELECT("*");
				FROM("user");
				if(param.get("name")!=null && param.get("name")!=""){
					WHERE("name like '%"+param.get("name")+"%'");
				}
				if(param.get("type")!=null && param.get("type")!=""){
					WHERE("type = '"+param.get("type")+"'");
				}
			}
		}.toString();
		if(param.get("limit")!=null)sql+=" limit "+param.get("limit");
		System.out.println(sql);
		return sql;
	}
	public String getUserCount(final Map<String,Object>param){

		String sql= new SQL(){
			{
				SELECT("count(0)");
				FROM("user");
				if(param.get("name")!=null && param.get("name")!=""){
					WHERE("name like '"+"%"+param.get("name")+"%"+"'");
				}
				if(param.get("name")!=null && param.get("name")!=""){
					WHERE("name = '"+param.get("type")+"'");
				}
			}
		}.toString();
		
		System.out.println(sql);
		return sql;
	}
}