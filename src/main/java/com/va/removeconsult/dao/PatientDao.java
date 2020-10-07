package com.va.removeconsult.dao;

import java.util.*;
import java.sql.*;
import java.util.Map;

public class PatientDao {
	public static class DbContext {
		public String IP;
		public String Port;
		public String DbName;
		public String User;
		public String Password;
	}

	private DbContext dbContext;

	public DbContext getDbContext() {
		return dbContext;
	}

	public void setDbContext(DbContext dbContext) {
		this.dbContext = dbContext;
	}

	private Connection getConnection() throws SQLException {
		Connection result = null;
		if (this.dbContext == null) {
			return null;
		}
		String url = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8", dbContext.IP,
				dbContext.Port, dbContext.DbName);
		result = DriverManager.getConnection(url, dbContext.User, dbContext.Password);
		return result;
	}

	public List<Map<String, Object>> query(String sql, List<Object> pms) {
		try {
			Connection con = this.getConnection();
			PreparedStatement cmd = con.prepareStatement(sql);
			if (pms != null && pms.size() > 0) {
				for (int i = 0; i < pms.size(); i++) {
					cmd.setObject(i + 1, pms.get(i));
				}
			}
			ResultSet rs = cmd.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int colSize = meta.getColumnCount();
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			while (rs.next()) {
				Map<String, Object> row = new HashMap<String, Object>();
				for (int i = 0; i < colSize; i++) {
					row.put(meta.getColumnName(i + 1), rs.getObject(i + 1));
				}
				result.add(row);
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Object> queryOne(String sql, List<Object> pms) {
		List<Map<String, Object>> all = this.query(sql, pms);
		if (all != null && all.size() > 0) {
			return all.get(0);
		}
		return null;
	}
}