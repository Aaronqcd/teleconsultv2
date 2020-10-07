package com.va.removeconsult.dao;

import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.ibatis.jdbc.SQL;

public class MeetingListSql {
	public String selectWhitParam(Map<String,Object>param){
		final Map<String,String> where;
		Map<String,String> page;
		where=(Map<String, String>) param.get("where");
		page=(Map<String, String>) param.get("page");
		String sql= new SQL(){
			{
				SELECT("*");
				FROM("meeting");
				if(where.get("no")!=null && where.get("no")!=""){
					WHERE("no='"+StringEscapeUtils.escapeJava(where.get("no"))+"'");
				}
				if(where.get("status")!=null && where.get("status")!=""){
					WHERE("status='"+StringEscapeUtils.escapeJava(where.get("status"))+"'");
				}
			}
		}.toString();
		if(page.get("limit")!=null)sql+=" limit "+page.get("limit");
		return sql;
	}
	
	public String selectCountWhitParam(Map<String,Object>param){
		final Map<String,String> where;
		where=(Map<String, String>) param.get("where");
		String sql= new SQL(){
			{
				SELECT("count(*) num");
				FROM("meeting");
				if(where.get("no")!=null && where.get("no")!=""){
					WHERE("no='"+StringEscapeUtils.escapeJava(where.get("no"))+"'");
				}
				if(where.get("status")!=null && where.get("status")!=""){
					WHERE("status='"+StringEscapeUtils.escapeJava(where.get("status"))+"'");
				}
			}
		}.toString();
		return sql;
	}
}