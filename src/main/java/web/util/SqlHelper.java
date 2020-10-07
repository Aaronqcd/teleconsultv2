package web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringEscapeUtils;

public class SqlHelper {
	private Map<String,Object> param;
	private String sqlType;
	
	public SqlHelper(){
		param=new HashMap<String,Object>();
	}
	
	public static String Addslashes(String str){
		return str.replace("\\", "\\\\").replace("\"", "\\\"");
	}
	
	public String getWhere(Object where){
		if(where==null)return "1";
		if(where instanceof String) return (String) where;
		Map<String,Object> whereMap=(Map<String,Object>)where;
		String sql="1";
		String spl=" and ";
		for(Entry fv:whereMap.entrySet()){
			String v=String.valueOf(fv.getValue());
			if(fv.getValue()==null)continue;
			sql+=spl+fv.getKey()+"=\""+Addslashes(v)+"\"";
		}
		return sql;
	}
	
	public String toString(){
		String sql=null;
		String tab=(String) param.get("from");
		String where=getWhere(param.get("where"));
		
		if(sqlType.equals("select")){
			String field="";
			if(param.get("select")!=null){
				String[] fields=(String[]) param.get("select");
				String spl="";
				for(String f:fields){
					field+=spl+f;
					spl=",";
				}
				
			}
			else{
				field="*";
			}
			
			
			String leftjoin="";
			HashMap<String, Object> leftjoins=(HashMap<String, Object>)param.get("leftjoin");
			if(leftjoins!=null){
				for(Entry lj:leftjoins.entrySet()){
					leftjoin+=" left join "+lj.getKey()+" on ("+lj.getValue()+")";
				}
			}
			
			String limit=(String) param.get("limit");
			if(limit==null)limit="";
			sql="select "+field+" from "+tab+" "+leftjoin+" where "+where+limit;
		}
		else if(sqlType.equals("insert")){
			Map<String,Object> insert=(Map<String, Object>) param.get("insert");
			String fields="";
			String values="";
			String spl="";
			for(Entry<String,Object> fv:insert.entrySet()){
				String v=String.valueOf(fv.getValue()) ;
				fields+=spl+"`"+fv.getKey()+"`";
				values+=spl+"\""+Addslashes(v)+"\"";
				spl=",";
			}
			sql="insert into "+tab+"("+fields+") values("+values+")";
		}
		else if(sqlType.equals("update")){
			Map<String,Object> update=(Map<String, Object>) param.get("update");
			String fieldvalues="";
			String spl="";
			for(Entry<String,Object> fv:update.entrySet()){
				String v=String.valueOf(fv.getValue()) ;
				fieldvalues+=spl+"`"+fv.getKey()+"`=\""+Addslashes(v)+"\"";
				spl=",";
			}
			sql="update "+tab+" set "+fieldvalues+" where "+where;
		}
		else if(sqlType.equals("delete")){
			sql="delete from "+tab+" where "+where;
		}
		return sql.toString();
	}
	
	public SqlHelper From(String from){
		param.put("from",from);
		return this;
	}
	
	public SqlHelper Select(String [] fields){
		sqlType="select";
		param.put("select", fields);
		return this;
	}
	
	public SqlHelper Select(){
		sqlType="select";
		param.put("select", null);
		return this;
	}
	
	public String Count(){
		String sql;
		sql="select count(*) from ("+this.toString()+") as tab";
		return sql;
	}
	
	public SqlHelper LeftJoin(Map<String,Object> leftjoin){
		param.put("leftjoin",leftjoin);
		return this;
	}
	
	public SqlHelper Where(Object where){
		param.put("where", where);
		return this;
	}
	
	public SqlHelper Limit(String limit){
		param.put("limit", limit);
		return this;
	}
	
	public SqlHelper Insert(Map<String,Object> insert){
		sqlType="insert";
		param.put("insert", insert);
		return this;
	}
	
	public SqlHelper Update(Map<String,Object> update){
		sqlType="update";
		param.put("update",update);
		return this;
	}
	
	public SqlHelper Delete(){
		sqlType="delete";
		return this;
	}
}
