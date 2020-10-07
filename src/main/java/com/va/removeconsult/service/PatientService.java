package com.va.removeconsult.service;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.va.removeconsult.dao.*;
import com.va.removeconsult.pojo.*;

@Service
public class PatientService {
	@Autowired
	private ResourceDao resDao;

	private PatientDao getPatientDao(String resType, int resId) {
		PatientDao.DbContext context = new PatientDao.DbContext();
		switch (resType.toUpperCase()) {
		case "HIS":
			List<His> hisList = resDao.searchHisbyId(resId);
			if (hisList == null || hisList.size() < 1)
				return null;
			context.IP = hisList.get(0).getServerip();
			context.DbName = hisList.get(0).getDatesource();
			context.Port = hisList.get(0).getDbport();
			context.User = hisList.get(0).getLoginname();
			context.Password = hisList.get(0).getLoginkey();
			break;
		case "LIS":
			List<Lis> lisList = resDao.searchLisbyId(resId);
			if (lisList == null || lisList.size() < 1)
				return null;
			context.IP = lisList.get(0).getServerip();
			context.Port = lisList.get(0).getDbport();
			context.DbName = lisList.get(0).getDatesource();
			context.User = lisList.get(0).getLoginname();
			context.Password = lisList.get(0).getLoginkey();
			break;
		case "PACS":
			List<Pacs> pacsList = resDao.searchPacsbyId(resId);
			if (pacsList == null || pacsList.size() < 1)
				return null;
			context.IP = pacsList.get(0).getDbip();
			context.Port = pacsList.get(0).getDbport();
			context.DbName = pacsList.get(0).getDbname();
			context.User = pacsList.get(0).getDbuser();
			context.Password = pacsList.get(0).getDbpwd();
			break;
		case "RIS":
			List<Ris> risList = resDao.searchRisbyId(resId);
			if (risList == null || risList.size() < 1)
				return null;
			context.IP = risList.get(0).getDbip();
			context.Port = risList.get(0).getDbport();
			context.DbName = risList.get(0).getDbname();
			context.User = risList.get(0).getDbuser();
			context.Password = risList.get(0).getDbpwd();
			break;
		default:
			return null;
		}
		if (context.IP == null || context.IP == "") {
			context.IP = "127.0.0.1";
		}
		if (context.Port == null || context.Port == "") {
			context.Port = "3306";
		}
		PatientDao result = new PatientDao();
		result.setDbContext(context);
		return result;
	}

	public List<Map<String, Object>> queryPatient(Map<String, Object> filters) {
		if (!filters.containsKey("resType") || !filters.containsKey("resId")) {
			return null;
		}
		String resType = (String) filters.get("resType");
		int resId = Integer.valueOf((String) filters.get("resId"));
		PatientDao dao = this.getPatientDao(resType, resId);

		String timeField = null, groupBy = null;
		StringBuilder sql = new StringBuilder();
		switch (resType.toUpperCase()) {
		case "HIS":
			sql.append("select patient_id, presc_date as indate, presc_no as no_id from drug_presc_master");
			timeField = "presc_date";
			break;
		case "LIS":
			sql.append("select patient_id, test_date as indate, test_no as no_id from lab_result_master");
			timeField = "test_date";
			// groupBy = " group by patient_id, indate";
			break;
		case "PACS":
		case "RIS":
			sql.append("select patient_id, exam_date as indate, exam_no as no_id from exam_report");
			timeField = "exam_date";
			break;
		default:
			return null;
		}
		StringBuilder where = new StringBuilder();
		if (filters.containsKey("patientId")) {
			String patientId = (String) filters.get("patientId");
			if (patientId != null && patientId != "") {
				if (where.length() > 0)
					where.append(" and ");
				where.append(String.format("patient_id like '%%%s%%'", patientId));
			}
		}
		if (filters.containsKey("inDate")) {
			String inDate = (String) filters.get("inDate");
			if (inDate != null && inDate != "") {
				if (where.length() > 0)
					where.append(" and ");
				String[] ary = inDate.split(" - ");
				where.append(String.format("%s between '%s' and '%s'", timeField, ary[0], ary[1]));
			}
		}

		if (where.length() > 0) {
			sql.append(" where ");
			sql.append(where);
		}
		if (groupBy != null) {
			sql.append(groupBy);
		}
		if (filters.containsKey("page")) {
			int page = (int) filters.get("page");
			int limit = (int) filters.get("limit");
			sql.append(String.format(" limit %d, %d", (page - 1) * limit, limit));
		}

		return dao.query(sql.toString(), null);
	}

	public Map<String, Object> get4SData(String resType, int resId, String patientId, String inDate, String noId) {
		// // indate: 2019-05-28: 09:32:04
		if (inDate == null)
			return null;
		String fromDate = inDate.substring(0, 10) + " 0:0:0";
		String toDate = inDate.substring(0, 10) + " 23:59:59";

		Map<String, Object> result = new HashMap<String, Object>();
		PatientDao dao = this.getPatientDao(resType, resId);
		String sql = null;
		// 获取病人信息
		sql = String.format("select * from patient where patient_id='%s'", patientId);
		Map<String, Object> patient = dao.queryOne(sql, null);
		if (patient != null) {
			result.put("Patient", patient);
			Date dt = (Date) patient.get("date_of_birth");
			if (dt != null) {
				Calendar dtNow = Calendar.getInstance();
				Calendar dtBirth = Calendar.getInstance();
				dtBirth.setTime(dt);
				patient.put("age", dtNow.get(Calendar.YEAR) - dtBirth.get(Calendar.YEAR));
			}
		}
		switch (resType) {
		case "HIS":
			// 获取HIS主记录
			Map<String, Object> his = new HashMap<String, Object>();
			sql = String.format(
					"select * from drug_presc_master where patient_id='%s' and presc_date between '%s' and '%s' and presc_no='%s'",
					patientId, fromDate, toDate, noId);
			Map<String, Object> drugMaster = dao.queryOne(sql, null);
			if (drugMaster != null) {
				his.put("master", drugMaster);
				// 获取HIS子记录
				sql = String.format(
						"select * from drug_presc_detail where presc_date between '%s' and '%s' and presc_no='%s'",
						fromDate, toDate, noId);

				List<Map<String, Object>> drugDetails = dao.query(sql, null);
				his.put("details", drugDetails);

				if (drugDetails != null && drugDetails.size() > 0) {
					Map<String, Object> summary = new HashMap<String, Object>();
					float sumCost = 0, sumPay = 0;
					for (Map<String, Object> item : drugDetails) {
						sumCost += ((java.math.BigDecimal) item.get("costs")).floatValue();
						sumPay += ((java.math.BigDecimal) item.get("payments")).floatValue();
					}
					summary.put("costs", sumCost);
					summary.put("payments", sumPay);
					his.put("summary", summary);
				}
				result.put("HIS", his);
			}
			break;
		case "LIS":
			Map<String, Object> lis = new HashMap<String, Object>();
			// 获取LIS 主记录
			sql = String.format(
					"select * from lab_result_master where patient_id='%s' and test_date between '%s' and '%s' and test_no='%s'",
					patientId, fromDate, toDate, noId);
			Map<String, Object> lisMaster = dao.queryOne(sql, null);
			lis.put("master", lisMaster);
			// 获取LIS 子记录
			sql = String.format(
					"select * from lab_result_detail where result_date_time between '%s' and '%s' and test_no='%s'",
					fromDate, toDate, noId);
			List<Map<String, Object>> lisDetails = dao.query(sql, null);
			lis.put("details", lisDetails);
			result.put("LIS", lis);
			break;
		case "PACS":
		case "RIS":
			// 获取PACS/RIS
			sql = String.format(
					"select * from exam_report where patient_id='%s' and exam_date between '%s' and '%s' and exam_no='%s'",
					patientId, fromDate, toDate, noId);
			Map<String, Object> ris = dao.queryOne(sql, null);
			String imgUrls = (String) ris.get("img_all_url");
			ris.put("_img_urls", this.parseImageUrl(imgUrls, patientId));
			result.put("RIS", ris);
			break;
		}
		return result;
	}

	private List<String> parseImageUrl(String src, String patientId) {
		if (src == null || src.length() == 0) {
			return null;
		}
		ResourceBundle bundle = ResourceBundle.getBundle("properties/config");
		String url = bundle.getString("data.pacs.url");
		String path = bundle.getString("data.pacs.path");
		String delim = bundle.getString("data.pacs.delim");
		if (url == null)
			url = "";
		if (path == null)
			path = "";
		String[] ary = src.split(delim);
		List<String> result = new ArrayList<String>();
		for (String item : ary) {
			result.add(url + path.replace("${PatientId}", patientId) + item);
		}
		return result;
	}
}