package com.va.removeconsult.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.va.removeconsult.pojo.His;
import com.va.removeconsult.pojo.Lis;
import com.va.removeconsult.pojo.OperateroomType;
import com.va.removeconsult.pojo.Pacs;
import com.va.removeconsult.pojo.Resource;
import com.va.removeconsult.pojo.Ris;
import com.va.removeconsult.pojo.Video;
import com.va.removeconsult.pojo.organization;

public interface ResourceDao {

	// 增加对资源管理的增删改查操作开始 add by:Heian 2019/02/24
	@Select("select * from resource") // 遍历资源的类型
	public List<Resource> getResourceType();

	@Select("select * from operateroomtype") // 遍历手术室的类型
	public List<OperateroomType> getRoomType();

	@Select("select type from organization where id=#{id}") // 查出当前机构所在的层级
	public int getCurrentType(int id);

	@Select("select * from organization where pid=#{pid} and name=#{name}") // 如果有则不在机构表中增加（机构表只会保存一条HIS资源记录）,不能重复，因为父级机构只能有一个子级资源
	public List<organization> isHasHisInOrg(@Param("pid") int pid, @Param("name") String name);

	@Insert("insert into organization(pid,type,name) values(#{pid},#{type},#{name})")
	public int insertHisInOrg(organization organization);

	@Select("select * from resource where resid=#{resid}")
	public Map<String, Object> getResource(int resid);// 获取该机构的资源

	/**
	 * 加载5个资源表的所有信息
	 */
	@Select("select * from his where orgid = #{orgid}")
	public List<His> selectHisByOrgid(int orgid);

	@Select("select * from lis where orgid = #{orgid}")
	public List<Lis> selectLisByOrgid(int orgid);

	@Select("select * from pacs where orgid = #{orgid}")
	public List<Pacs> selectPacsByOrgid(int orgid);

	@Select("select * from ris where orgid = #{orgid}")
	public List<Ris> selectRisByOrgid(int orgid);

	@Select("select * from video where orgid = #{orgid}")
	public List<Video> selectVideoByOrgid(int orgid);

	// 增加HIS资源 in HIS表
	@Insert("insert into his(servername,serverip,dbport,datesource,loginname,loginkey,orgid)"
			+ " values(#{servername},#{serverip},#{dbport},#{datesource},#{loginname},#{loginkey},#{orgid})") // 在his表中增加his资源
	public int insertHisInHis(His his);

	// 删除HIS
	@Delete("delete from his where id= #{id}")
	public int deleteHis(int id);

	// 修改His
	@Update("update his set `servername`= #{servername},`serverip`=#{serverip},`dbport`=#{dbport},`datesource`= #{datesource},"
			+ "`loginname`=#{loginname},`loginkey`= #{loginkey} where `id` = #{id}")
	public int updateHis(His his);

	// 查找His资源
	@Select("select * from his where id=#{id}")
	public List<His> searchHisbyId(int id);

	// 增加Lis
	@Insert("insert into lis(servername,serverip,dbport,datesource,loginname,loginkey,orgid)"
			+ " values(#{servername},#{serverip},#{dbport},#{datesource},#{loginname},#{loginkey},#{orgid})") // 在his表中增加his资源
	public int insertLisInHis(Lis lis);

	// 删除LIS
	@Delete("delete from lis where id= #{id}")
	public int deleteLis(int id);

	// 查找Lis资源
	@Select("select * from lis where id=#{id}")
	public List<Lis> searchLisbyId(int id);

	// 修改Lis资源
	@Update("update lis set `servername`= #{servername},`serverip`=#{serverip},`dbport`=#{dbport},`datesource`= #{datesource},"
			+ "`loginname`=#{loginname},`loginkey`= #{loginkey} where `id` = #{id}")
	public int updateLis(Lis lis);

	// 增加PACS
	@Insert("INSERT INTO pacs (/*`servername`,*/`server`,`type`,`ipaddress`,`port`,`zaet`,`baet`,`orgid`,`dbip`,`dbport`,`dbname`,`dbuser`,`dbpwd`) VALUES (/*#{servername},*/#{server},#{type},#{ipaddress},#{port},#{zaet},#{baet},#{orgid},#{dbip},#{dbport},#{dbname},#{dbuser},#{dbpwd})")
	public int insertPacs(Pacs pacs);

	// 删除PACS
	@Delete("delete from pacs where id= #{id}")
	public int deletePacs(int id);

	// 查找Pacs
	@Select("select * from pacs where id=#{id}")
	public List<Pacs> searchPacsbyId(int id);

	// 修改Pacs
	@Update("update pacs set /*`servername` = #{servername},*/ `server` = #{server}, `type` = #{type}, `ipaddress` = #{ipaddress}, `port` = #{port}, `zaet` = #{zaet}, "
			+ "`baet` = #{baet}, `orgid` = #{orgid}, `dbip`= #{dbip}, `dbport`= #{dbport}, `dbname`= #{dbname}, `dbuser`= #{dbuser}, `dbpwd`= #{dbpwd} WHERE `id` = #{id}")
	public int updatePacs(Pacs pacs);

	// 增加Ris
	@Insert("INSERT INTO ris (/*`servername`,*/`server`,`type`,`ipaddress`,`port`,`zaet`,`baet`,`orgid`,`dbip`,`dbport`,`dbname`,`dbuser`,`dbpwd`) VALUES (/*#{servername},*/#{server},#{type},#{ipaddress},#{port},#{zaet},#{baet},#{orgid},#{dbip},#{dbport},#{dbname},#{dbuser},#{dbpwd})")
	public int insertRis(Ris ris);

	// 删除Ris
	@Delete("delete from ris where id= #{id}")
	public int deleteRis(int id);

	// 查找Ris
	@Select("select * from ris where id=#{id}")
	public List<Ris> searchRisbyId(int id);

	// 修改ris
	@Update("update ris set /*`servername` = #{servername},*/ `server` = #{server}, `type` = #{type}, `ipaddress` = #{ipaddress}, `port` = #{port}, `zaet` = #{zaet}, "
			+ "`baet` = #{baet}, `orgid` = #{orgid}, `dbip`= #{dbip}, `dbport`= #{dbport}, `dbname`= #{dbname}, `dbuser`= #{dbuser}, `dbpwd`= #{dbpwd} WHERE `id` = #{id}")
	public int updateRis(Ris ris);

	// 增加Video
	@Insert("insert into video (/*`vname`,*/`roomname`,`roomip`,`roomtype`,`v1name`,`v1add`,`v2name`,`v2add`,`v3name`,`v3add`,`v4name`,`v4add`,`v5name`,`v5add`,`v6name`,`v6add`,`v7name`,`v7add`,`v8name`,`v8add`,`orgid`) "
			+ "values (/*#{vname},*/#{roomname},#{roomip},#{roomtype},#{v1name},#{v1add},#{v2name},#{v2add},"
			+ "#{v3name},#{v3add},#{v4name},#{v4add},#{v5name},#{v5add},#{v6name},#{v6add},#{v7name},#{v7add},#{v8name},#{v8add},#{orgid})")
	public int insertVideo(Video video);

	// 删除Video
	@Delete("delete from video where id=#{id}")
	public int deleteVideo(int id);

	// 查找Video
	@Select("select * from video where id=#{id}")
	public List<Video> searchVideo(int id);

	// 修改Video
	@Update("update video set /*`vname`=#{vname},*/`roomname`=#{roomname},`roomip`=#{roomip},`roomtype`=#{roomtype},`v1name`=#{v1name},"
			+ "`v1add`=#{v1add},`v2name`=#{v2name},`v2add`=#{v2add},`v3name`=#{v3name},`v3name`=#{v3name},`v3add`=#{v3add},"
			+ "`v4name`=#{v4name},`v4add`=#{v4add},`v5name`=#{v5name},`v5add`=#{v5add},`v6name`=#{v6name},`v6add`=#{v6add},"
			+ "`v7name`=#{v7name},`v7add`=#{v7add},`v8name`=#{v8name},`v8add`=#{v8add} where id=#{id}")
	public int updateVideo(Video video);

	// 根据机构表当前机构
	@Select("select * from organization where id=#{id}")
	public List<organization> getOrgPid(int id);

	@Select("select * from his where servername  like '%${servername}%' and orgid=#{orgid}")
	public List<His> findHis(@Param(value = "servername") String servername, @Param(value = "orgid") int orgid);

	@Select("select * from his where servername=#{servername} and orgid=#{orgid} and ((#{id}!=id) or (#{id}=0))")
	public List<His> findHisList(His his);

	@Select("select * from lis where servername  like '%${servername}%' and orgid=#{orgid}")
	public List<Lis> findLis(@Param(value = "servername") String servername, @Param(value = "orgid") int orgid);

	@Select("select * from lis where servername=#{servername} and orgid=#{orgid} and ((#{id}!=id) or (#{id}=0))")
	public List<Lis> findLisList(Lis lis);

	@Select("select * from pacs where server  like '%${server}%' and orgid=#{orgid}")
	public List<Pacs> findPacs(@Param(value = "server") String server, @Param(value = "orgid") int orgid);

	@Select("select * from pacs where server=#{server} and orgid=#{orgid} and ((#{id}!=id) or (#{id}=0))")
	public List<Pacs> findPacsList(Pacs pacs);

	@Select("select * from ris where server  like '%${server}%' and orgid=#{orgid}")
	public List<Ris> findRis(@Param(value = "server") String server, @Param(value = "orgid") int orgid);

	@Select("select * from ris where server=#{server} and orgid=#{orgid} and ((#{id}!=id) or (#{id}=0))")
	public List<Ris> findRisList(Ris ris);

	@Select("select * from video where roomname  like '%${roomname}%' and orgid=#{orgid}")
	public List<Video> findVideo(@Param(value = "roomname") String roomname, @Param(value = "orgid") int orgid);
	
	@Select("select * from video where roomip = '${roomip}' and orgid=#{orgid}")
	public Video getVideoByIP(@Param(value = "roomip") String roomip, @Param(value = "orgid") int orgid);
	
	@Select("select * from video where id=#{id}")
	public Video getVideo(int id);

	@Select("select * from video where roomname=#{roomname} and orgid=#{orgid} and ((#{id}!=id) or (#{id}=0))")
	public List<Video> findVideoList(Video video);
	// 增加对资源管理的增删改查操作结束 add by:Heian 2019/02/28 #{}自带单引号 $不带单引号

}