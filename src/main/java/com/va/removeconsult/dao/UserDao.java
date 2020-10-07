package com.va.removeconsult.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import com.va.removeconsult.bean.User;

public interface UserDao {
    /**
     * @param userId
     * @return User
     */
	@Select("SELECT user.*,group.groupname,o1.name selfOrgan,o2.name parentOrgan FROM user left join `group` on `group`.`key`=user.group left join organization o1 on o1.id=user.belong left join organization o2 on o2.id=o1.pid WHERE user.user = #{user}")
    public Map<String,Object> getUserByUser(String user);
	
	@Insert("insert into user(user,password,`group`,email,belong,name,sex,job,special,phone)"
		+ " values(#{user},#{password},#{group},#{email},#{belong},#{name},#{sex},#{job},#{special},#{phone})")
	@Options(useGeneratedKeys=true,keyProperty="id")
	public int insertUser(Map user);
	
	@Select("select * from user where id=#{id}")
	public Map<String,Object> getUserById(int id);
	
	@Update("update user set last_login_time= #{lastLoginTime} where id=#{id}")
	public void updateLastLoginTime(@Param("id")int id,@Param("lastLoginTime")Date date);
	
	@Select("select count(0) from user where belong = #{belong} and `group` = #{group}")
	public int checkBelongAndGroup(@Param("belong")int belong,@Param("group")String group);
    
	@Select("select * from user where belong = #{belong} ")
	public List<Map> getByBelong(@Param("belong")int belong);
	
	@Select("select * from user where `group` = #{group} ")
	public List<Map> findByGroups(@Param("group")String group);
	

	@Select("SELECT user,`group`,email,belong,name,sex,job,special,phone FROM user WHERE user = #{user} and password=#{pwd}")
    public Map<String,Object> getUserByUserAndPwd(@Param("user")String user,@Param("pwd")String pwd);
	
	@Update("update user set password= #{pwd} where user = #{user}")
	public int updatePwd(@Param("user")String user,@Param("pwd")String pwd);
	
	@SelectProvider(type=UserListSql.class,method="getUser")
    public List<Map> getUserList(Map<String,Object> param);
	
	@SelectProvider(type=UserListSql.class,method="getUserCount")
	public int getUserListCount(Map param);
	
	@Update("update user set password= #{password}, `group`= #{group}, email= #{email},name= #{name},avatar=#{avatar}, "
			+ "`belong`= #{belong},sex= #{sex},job= #{job},special= #{special},phone= #{phone},videoRole= #{videoRole},logintype = 0 where id = #{id}")
	public int update(Map user);
	
	@Delete("delete from user where id = #{id}")
	public int delete(int id);

	@Update("update user set codenum = codenum+1 where id = #{userid}")
	public void UpdateCodeNum(Integer userid);

	@Select("select * from user where user = #{username}")
	public List<User> findUser(String username);

	@Select("select * from user where user = #{username}")
	public User findUserList(@Param("username")String username);
	
	@Select("select * from user where id = #{userid}")
	public User findUserLoginType(int userid);

	@Update("update user set logintype  = 1 where id = #{userid}")
	public void updateLogintyepe(int userid);
	
	@Update("update user set logintype  = 0 where id = #{userid}")
	public void updateLogintyepes(int userid);
	
	@Update("update user set passwordtime  = now() where id = #{userid}")
	public void updatePasswordTime(int userid);

	@Update("update user set codenum = 0 where id = #{userid}")
	public void updateCodenums(int userid);

	@Select("select * from user where user = #{username} and password = #{spass}")
	public User findUserNameByUserpassword(@Param("username")String username, @Param("spass")String spass);

	@Select("select * from user where user = #{username}")
	public User findUserLoginTypes(String username);

	@Update("update user set password = #{password},logintype = 0 where id = #{id}")
	public void updatePassword(@Param("password")String password, @Param("id")Integer id);

}