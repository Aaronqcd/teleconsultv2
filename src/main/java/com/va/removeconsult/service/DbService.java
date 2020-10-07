package com.va.removeconsult.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.va.removeconsult.dao.DbDao;

import web.util.Helper;
import web.util.SqlHelper;

@Service
public class DbService {
	@Autowired  
    private DbDao db;
	public SqlHelper sqlHelper;
	
	public DbService(){
		sqlHelper=new SqlHelper();
	}
	
	public SqlHelper getSqlHelper(){
		return sqlHelper;
	}
	
	public List<Map> getOrganListByPid(int pid){
		String sql="select * from organization where pid ="+pid;
		return db.select(sql);
	}
	
	public int dropOrgan(int id) {
		String sql="delete from organization where id="+id;
		return db.delete(sql);
	}
	
	public int getOrganUserCount(int id){
		String sql="select count(*) from user where belong="+id;
		return db.selectOne(sql);
	}
	
	public Map getOrganListByName(String name){
		String sql="select * from organization where name= '"+name+"'";
		return db.selectRow(sql);
	}
	
	public int getOrganSonsCount(int id){
		String sql="select count(*) from organization where pid="+id;
		return db.selectOne(sql);
	}
	
	public List<Map> getTreeOrganList(int pid){
		List<Map> rows=getOrganListByPid(pid);
		List<String> icons=new ArrayList<String>();
		icons.add("tree-icon tree-first");
		icons.add("tree-icon tree-second");
		icons.add("tree-icon tree-third");
		List<String> tags=new ArrayList<String>();
		tags.add("...");
		for(Map row:rows){
			List<Map> sons=getTreeOrganList((int) row.get("id"));
			row.put("nodes", sons);
			row.put("text",row.get("name"));
			row.put("icon",icons.get((int)row.get("type")));
			row.put("tags",tags);
		}
		return rows;
	}
	
	public List<Map> getUsers(Object where,Map<String,Object> pagination){
		where += " and user.user <> 'root' ";
		String w=sqlHelper.getWhere(where);
		String limit="";
		int page=(int) pagination.get("page")-1;
		int size=(int) pagination.get("size");
		limit=" ORDER BY `user`.id ASC  limit "+page*size+","+size;
		String sql="select user.*,`group`.groupname,o1.name selfOrgan,o2.name parentOrgan from user ";
		sql+="left join `group` on `group`.`key`=user.group ";
		sql+="left join organization o1 on o1.id=user.belong ";
		sql+="left join organization o2 on o2.id=o1.pid ";
		sql+="where "+w+limit;
		return db.select(sql);
	}
	
	public int getUserCount(Object where){
		where += " and user.user <> 'root' ";
		String w=sqlHelper.getWhere(where);
		String sql="select count(*) from user ";
		sql+="left join `group` on `group`.`key`=user.group ";
		sql+="left join organization o1 on o1.id=user.belong ";
		sql+="left join organization o2 on o2.id=o1.pid ";
		sql+="where "+w;
		return db.selectOne(sql);
	}

	public Map getUser(int id) {
		String sql="select * from user where id="+id;
		return db.selectRow(sql);
	}
	
	public int dropUser(int id){
		String sql="delete from user where id="+id;
		return db.delete(sql);
	}
	
	public List<Map> getMeetings(Object where,Map<String,Object> pagination){
		String w=sqlHelper.getWhere(where);
		String limit="";
		int page=(int) pagination.get("page")-1;
		int size=(int) pagination.get("size");
		limit=" limit "+page*size+","+size;
		String sql="select *,user.user user_user,user.name user_name,ifnull(meeting.conclusion,'无') note,(select a.value from dictitem a where a.type='meetingtype' and a.pid=0 and a.key=meeting.type limit 1) meetingtype from meeting left join user on user.id=meeting.user where meeting.is_del_metting =0  AND   "+w+limit;
		return db.select(sql);
	}
	
	public int getMeetingsCount(Object where){
		String w=sqlHelper.getWhere(where);
		String sql="select count(*) from meeting left join user on user.id=meeting.user where meeting.is_del_metting =0  AND"+w;
		return db.selectOne(sql);
	}
	
	public List<Map> getMeetingsWithAdmin(Object where,Map<String,Object> pagination){
		String w=sqlHelper.getWhere(where);
		String limit="";
		int page=(int) pagination.get("page")-1;
		int size=(int) pagination.get("size");
		limit=" limit "+page*size+","+size;
		String sql="select *,user.user user_user,user.name user_name,ifnull(meeting.conclusion,'无') note from meeting left join user on user.id=meeting.user where  "+w+limit;
		return db.select(sql);
	}
	public int getMeetingsCountWithAdmin(Object where){
		String w=sqlHelper.getWhere(where);
		String sql="select count(*) from meeting left join user on user.id=meeting.user where "+w;
		return db.selectOne(sql);
	}
	
	
	public int dropUserMeetings(String ids, int user) {
		String sql="delete from meeting where id in("+sqlHelper.Addslashes(ids)+") and user="+user;
		return db.delete(sql);
	}
	
	//update by and
	public int dropMeetingsByUser(String user,int status ) {
		String sql="delete from meeting where  user="+user +" and status <>"+status;
		return db.delete(sql);
	}
	
	public int dropMeetingsById(int id) {
		String sql="delete from meeting where  id="+id ;
		return db.delete(sql);
	}
	
	public int dropMettingByIds(String ids, int status){
		String sql="delete from meeting where id in("+sqlHelper.Addslashes(ids)+") and status !="+status;
		return db.delete(sql);
	}
	
	public List<Map> selectMetting(String user,int status ) {
		String sql="select * from meeting where  user="+user +" and status <>"+status;
		return db.select(sql);
	}
	
	public int updateIsDelMetting(int id,String attends) {
		String attend = StringUtils.isNotBlank(attends)? "'"+attends+"'".trim() : "''";
		String sql="update meeting set attends="+attend+" where id="+id;
		return db.update(sql);
	}
	
	public List<Map> getDictItems(String type,int pid){
		String sql="select * from dictitem where type='"+type+"' and pid="+pid;
		return db.select(sql);
	}
	
	public Map<String,Object> getDictItemsKV(String type,int pid){
		Map<String,Object> result=new HashMap<String,Object>();
		List<Map> rows=getDictItems(type,pid);
		for(Map row:rows){
			result.put(String.valueOf(row.get("key")), row.get("value"));
		}
		return result;
	}

	public List<Map> getGroups() {
		String sql="select * from `group`";
		return db.select(sql);
	}
	
	public List<Map> getGroups(String where) {
		String sql="select * from `group` where 1 "+where;
		return db.select(sql);
	}

	public List<Map<String, Object>> getOrganTree(int pid) {
		List<Map> rows=null;
		List<Map<String, Object>> rows2=new ArrayList<Map<String,Object>>();
		String sql="select * from organization where pid="+pid;
		rows=db.select(sql);
		for(Map<String,Object> row:rows){
			List<Map<String, Object>> sons=getOrganTree((int)row.get("id"));
			rows2.add(row);
			for(Map son:sons){
				rows2.add(son);
			}
		}
		return rows2;
	}
	
	public boolean isUserUsedEmail(String email){
		String sql="select count(*) from user where email=\""+sqlHelper.Addslashes(email)+"\"";
		return db.selectOne(sql)>0;
	}
	
	public boolean haveUser(String user){
		String sql="select count(*) from user where user=\""+sqlHelper.Addslashes(user)+"\"";
		return db.selectOne(sql)>0;
	}

	public int AddUser(Map<String, Object> user) {
		if(!user.get("phone").toString().contains("AC5615684452")) {
			user.put("phone", "AC5615684452"+user.get("phone").toString());
		}
		SqlHelper sqlhelper=new SqlHelper();
		String sql=sqlhelper.Insert(user).From("user").toString();
		System.out.println(sql);
		return db.insert(sql);
	}

	public int EditUser(Map<String, Object> user, int id) {
		SqlHelper sqlhelper=new SqlHelper();
		String sql=sqlhelper.Update(user).From("user").Where(" id="+id).toString();
		return db.update(sql);
	}

	public List<Map> getAttends(int apply) {
		String sql="select * from user where id<>"+apply+ " and user <> 'root'";
		return db.select(sql);
	}

	public int AddMeeting(Map<String, Object> data) {
		SqlHelper sqlhelper=new SqlHelper();
		String sql=sqlhelper.Insert(data).From("meeting").toString();
		return db.insert(sql);
	}
	
	public int dropMeetings(String ids) {
		String sql="delete from meeting where id in("+SqlHelper.Addslashes(ids)+")";
		return db.delete(sql);
	}
	
	public int addLog(Map<String, Object> data){
		SqlHelper sqlhelper=new SqlHelper();
		String sql=sqlhelper.Insert(data).From("log").toString();
		return db.insert(sql);
	}
	
	public int addLog(String context,Map user){
		if(user==null || user.get("id")==null)return 0;
		Map<String, Object> data=new HashMap<String,Object>();
		data.put("context", context);
		data.put("user", user.get("id"));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = df.format(new Date());
		data.put("time", time);
		return addLog(data);
	}

	public List<Map> getLogs(String where, Map<String, Object> pagination ,String type) {
		if("1".equals(type)){
			where += " and user.user <> 'root' ";
		}else{
			where += " and user.user = 'root' ";
		}
		String w=sqlHelper.getWhere(where);
		String limit="";
		int page=(int) pagination.get("page")-1;
		int size=(int) pagination.get("size");
		limit=" limit "+page*size+","+size;
		String sql="select log.*,user.user as u,user.name from log left join user on log.user=user.id where "+w+" order by log.time desc "+limit;
		return db.select(sql);
	}

	public int getLogCount(String where,String type) {
		if("1".equals(type)){
			where += " and user.user <> 'root' ";
		}else{
			where += " and user.user = 'root' ";
		}
		String w=sqlHelper.getWhere(where);
		String sql="select count(*) from log left join user on log.user=user.id where "+w;
		return db.selectOne(sql);
	}
	
	public int dropLogs(String ids) {
		String sql="delete from log where id in("+sqlHelper.Addslashes(ids)+")";
		return db.delete(sql);
	}

	public int EditOrgan(Map<String, Object> data, int id) {
		SqlHelper sqlhelper=new SqlHelper();
		String sql=sqlhelper.Update(data).From("organization").Where(" id="+id).toString();
		return db.update(sql);
	}

	public int AddOrgan(Map<String, Object> data) {
		SqlHelper sqlhelper=new SqlHelper();
		String sql=sqlhelper.Insert(data).From("organization").toString();
		return db.insert(sql);
	}

	public Map getOrgan(int id) {
		String sql="select * from organization where id="+id;
		return db.selectRow(sql);
	}

	public String getUserNames(String ids,String spl) {
		String sql="select name from user where id in("+ids+")";
		String result="";
		String spl2="";
		List<Map> rows=db.select(sql);
		for(Map row:rows){
			result+=spl2+row.get("name");
			spl2=spl;
		}
		return result;
	}
	
	public String getUserUsers(String ids,String spl) {
		String sql="select user from user where id in("+ids+")";
		String result="";
		String spl2="";
		List<Map> rows=db.select(sql);
		for(Map row:rows){
			result+=spl2+row.get("user");
			spl2=spl;
		}
		return result;
	}
	
	
	

	
	public Map getSysConf(int id) {
		String sql="select * from sys_conf where id = "+id;
		return db.selectRow(sql);
	}
	
	public int EditSysConf(Map<String, Object> sysConf, int id) {
		SqlHelper sqlhelper=new SqlHelper();
		String sql=sqlhelper.Update(sysConf).From("sys_conf").Where(" id="+id).toString();
		return db.update(sql);
	}

	
	/**
	 * @Title: getUserIsNotAdminsCount
	 * @Description: TODO(统计除了管理员外的用户数)
	 * @param @param groupName
	 * @param @return 设定文件
	 * @return int 返回类型
	 */
	public int getUserIsNotAdminsCount(String groupName){
		String sql="select count(*) from user where `group` <> '"+groupName+"'";
		return db.selectOne(sql);
	}
	
	
	
	/**
	 * 
	 * @Title: getSysConfs
	 * @Description: TODO(获取所有的配置信息)
	 * @param @param where
	 * @param @param pagination
	 * @param @return 设定文件
	 * @return List<Map> 返回类型
	 */
	public List<Map> getSysConfs(String where, Map<String, Object> pagination) {
		String w=sqlHelper.getWhere(where);
		String limit="";
		int page=(int) pagination.get("page")-1;
		int size=(int) pagination.get("size");
		limit=" limit "+page*size+","+size;
		String sql="select * from sys_conf  where "+w+" order by id DESC "+limit;
		return db.select(sql);
	}
	
	/**
	 * @Title: getSysConfCount
	 * @Description: TODO(记录配置总数)
	 * @param @param where
	 * @param @return 设定文件
	 * @return int 返回类型
	 */
	public int getSysConfCount(String where) {
		String w=sqlHelper.getWhere(where);
		String sql="select count(*) from sys_conf  where "+w;
		return db.selectOne(sql);
	}
	
	

	public List<Map> getMeetingStatus() {
		String sql="select * from meeting_status order by id";
		return db.select(sql);
	}
	
	public int batchDelUser(String ids) {
		String sql="delete from user where id in ("+ids+")";
		return db.delete(sql);
	}
	
	public int updatePwdByEmail(String email,String pwd) {
		String sql="update user set password = '"+pwd+"' where email = '"+email+"'";
		return db.update(sql);
	}

	public Map getByEmail(String email) {
		String sql="select * from user where email = '"+email+"'" + "or user= '"+email+"'";
		return db.selectRow(sql);
	}
	
	public Map getByUser(String id) {
		String sql="select * from user where id = '"+id+"'";
		return db.selectRow(sql);
	}
	
	public Map getMeeting(String id) {
		String sql="select * from meeting where id in ("+id+")";
		return db.selectRow(sql);
	}
	
	public int updateMeetingStatus(int id,int status){
		String sql="update meeting set status="+status+" where id="+id;
		return db.update(sql);
	}
	
	//update by and 
	public int updateMeetingAttends(int id,String attends){
		String sql="update meeting set attends="+attends+" where id="+id;
		return db.update(sql);
	}
	
	public int addMeetingAccept(Map meeting,int userId){
		int id=(int) meeting.get("id");
		String attendaccepts=(String)meeting.get("attendaccepts");
		attendaccepts=Helper.StringSplitMerge(attendaccepts, String.valueOf(userId));
		String sql="update meeting set attendaccepts=\""+attendaccepts+"\" where id="+id;
		return db.update(sql);
	}
	
	public Map<String,Object> getMeetingStatus(String role){
		String sql;
		if(role.equals("admin"))sql="select id,adminname name from meeting_status";
		else if(role.equals("apply"))sql="select id,applyname name from meeting_status";
		else sql="select id,attendname name from meeting_status";
		List<Map> rows=db.select(sql);
		
		Map<String,Object> result=new HashMap<String,Object>();
		for(Map row:rows){
			result.put(String.valueOf(row.get("id")), row.get("name"));
		}
		return result;
	}

	public int getOrganAdminUserCount(int belong) {
		String sql="select count(*) from user where `group`='managers' and belong="+belong;
		return db.selectOne(sql);
	}
	
	public Map getOrganAdminUser(int belong) {
		String sql="select * from user where `group`='managers' and belong="+belong+" limit 1";
		return db.selectRow(sql);
	}
	
	public List<Map> getMeetingSearchStatus(int role){
		String where;
		if(role==1){
			where="type=0 or type=1";
		}
		else{
			where="type=0 or type=2 or type=3";
		}
		String sql="select * from meeting_search_status where "+where;
		return db.select(sql);
	}
	
	public Map getMailTemplate(String templateCode) {
		String sql = "select * from template t where t.template_code='"+templateCode+"' limit 1";
		return db.selectRow(sql);
	}
	public int addAttr(Map<String, Object> data){
		SqlHelper sqlhelper=new SqlHelper();
		String sql=sqlhelper.Insert(data).From("attachment").toString();
		return db.insert(sql);
	}
	public Map getAttr(String id) {
		String sql = "select * from attachment t where t.id='"+id+"' limit 1";
		return db.selectRow(sql);
	}

	public boolean isExists(String table,Map<String,Object> param,Map<String,Object> debarMap) {
		if(param == null) {
			return false;
		}
		StringBuffer buffer = new StringBuffer();
		for (String key : param.keySet()) {
			Object value = param.get(key);
			if(value instanceof String || value instanceof Character) {
				buffer.append(" and "+key+"='"+value+"'");
			} else {
				buffer.append(" and "+key+"="+value);
			}
		}
		if(debarMap != null && !debarMap.isEmpty()) {
			for (String key : debarMap.keySet()) {
				Object value= debarMap.get(key);
				if(value != null && !"".equals(value.toString().trim())) {
					buffer.append(" and "+key+"!="+debarMap.get(key));
				}
			}
		}
		String sql = "select count(1) from "+table+" where 1=1"+buffer.toString();
		return db.selectOne(sql)>0;
	}
	
	
	public List<Map> getPathForm(String code) {
		String sql = "select t.sysName,t.fileName,t.path from attachment t left join meeting_appends a on a.file=t.id left join meeting m on m.code = a.meeting where m.code = '"+code+"'";
		return  db.select(sql);
	}
	
	//meeting_appends表新增一条数据
	public int AddMeetingAppends(Map<String, Object> data) {
		SqlHelper sqlHelper = new SqlHelper();
		Collection<Object> str = data.values();
		String sql = sqlHelper.Insert(data).From("meeting_appends").toString();
		return db.insert(sql);
	}
	
	
	//update by and 
	public int updateAttachmentByShareUser(String id,String shareUser){
		String sql="update attachment set share_user='"+shareUser+"' where id='"+id+"'";
		return db.update(sql);
	}
	
	//update by and 
	public int updateAttachmentByIsShare(String id,int isShare){
		String sql="update attachment set is_share="+isShare+" where id='"+id+"'";
		return db.update(sql);
	}

	
	/**
	 * @Title: AddMeetingCode
	 * @Description: TODO(添加统计会议信息)
	 * @param @param data
	 * @param @return 设定文件
	 * @return int 返回类型
	 */
	public int AddMeetingCode(Map<String, Object> data) {
		SqlHelper sqlhelper=new SqlHelper();
		String sql=sqlhelper.Insert(data).From("meet_count").toString();
		return db.insert(sql);
	}
	
	/**
	 * @Title: updateMeetingBycode
	 * @Description: TODO(修改统计会议信息)
	 * @param @param data
	 * @param @param code
	 * @param @return 设定文件
	 * @return int 返回类型
	 */
	public int updateMeetingBycode(Map<String, Object> data, String code) {
		SqlHelper sqlhelper=new SqlHelper();
		String sql=sqlhelper.Update(data).From("meet_count").Where(" code='"+code+"'").toString();
		return db.update(sql);
	}
	
	/**
	 * @Title: getMeetingCount
	 * @Description: TODO(根据编号获取会议统计信息)
	 * @param @param code
	 * @param @return 设定文件
	 * @return Map 返回类型
	 */
	public Map getMeetingCount(String code ) {
		String sql="select * from meet_count m where m.type = 0 AND m.code = '"+code+"' ";
		return db.selectRow(sql);
	}
	
	public List<Map> getMeetingCounts(Object where,Map<String,Object> pagination){
		String w=sqlHelper.getWhere(where);
		String limit="";
		int page=(int) pagination.get("page")-1;
		int size=(int) pagination.get("size");
		limit=" limit "+page*size+","+size;
		String sql="select *  from meet_count m where  m.type = 1  AND   "+w+" order by id asc "+limit;
		return db.select(sql);
	}
	
	public int getMeetingsRowsCount(Object where){
		String w=sqlHelper.getWhere(where);
		String sql="select count(*) from meet_count m where m.type = 1  AND"+w;
		return db.selectOne(sql);
	}
	
	public int getMeetingsTimesCount(Object where){
		String w=sqlHelper.getWhere(where);
		String sql="select sum(m.duration_count) from meet_count m where m.type = 1  AND"+w;
		return db.selectOne(sql);
	}
	
}
