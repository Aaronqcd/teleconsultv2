package com.va.removeconsult.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.va.removeconsult.bean.User;

public interface DbDao {
	@Select("${value}")
    public List<Map> select(String value);
	
	@Insert("${value}")
	public int insert(String value);
	
	@Update("${value}")
	public int update(String value);
	
	@Delete("${value}")
	public int delete(String value);

	@Select("${value}")
	public int selectOne(String sql);
	
	@Select("${value}")
	public Map selectRow(String sql);
}
