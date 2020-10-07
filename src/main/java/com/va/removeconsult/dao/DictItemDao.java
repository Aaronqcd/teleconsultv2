package com.va.removeconsult.dao;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

public interface DictItemDao {
	@Select("SELECT * FROM dictitem WHERE type = #{type}")
    public ArrayList<Map<String,String>> selectItems(String type);
}
