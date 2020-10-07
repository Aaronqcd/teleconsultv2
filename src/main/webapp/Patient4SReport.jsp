<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="css/meeting.4s.css">
<c:if test="${param.restype=='PACS' || param.restype=='RIS' }">
	<div class="rpt-page">
		<div class="rpt-title">${ORG.name}</div>
		<div class="rpt-title2">彩色超声报告</div>
		<div class="rpt-content">
			<table class="rpt-table">
				<tr>
					<td><span class='rpt-label'>检查设备：</span> ${RIS.device}</td>
					<td style="width: 180px;"><span class='rpt-label'>检查时间：</span>
						<fmt:formatDate value="${RIS.exam_date}" pattern="yyyy-MM-dd" /></td>
				</tr>
			</table>
		</div>
		<hr class="rpt-line" />
		<div class="rpt-content">
			<table class="rpt-table" style="margin: 15px 0px;">
				<tr>
					<td style="width: 25%;"><span class='rpt-label'>姓名：</span>${Patient.name}</td>
					<td style="width: 25%;"><span class='rpt-label'>性别：</span>${Patient.sex}</td>
					<td style="width: 25%"><span class='rpt-label'>年龄：</span>${Patient.age}</td>
					<td style="width: 25%;"><span class='rpt-label'>送检科室：</span>${RIS.exam_room}</td>
				</tr>
				<tr>
					<td style="width: 25%"><span class='rpt-label'>超声号：</span>${RIS.sound_no}</td>
					<td style="width: 25%"><span class='rpt-label'>住院号：</span>${Patient.inp_no}</td>
					<td style="width: 25%"><span class='rpt-label'>序号：</span>${RIS.exam_no}</td>
					<td style="width: 25%;"><span class='rpt-label'>检查部位：</span>${RIS.exam_body}</td>
				</tr>
			</table>
		</div>
		<hr class="rpt-line" />
		<ul class="rpt-gly">
			<c:forEach items="${RIS._img_urls }" var="item">
				<li><img src="<c:out value='${item}'></c:out>" /></li>
			</c:forEach>
		</ul>
		<hr class="rpt-line" />
		<div class="rpt-content">
			<div class="rpt-section">
				<div class="rpt-sec-title">超声描述：</div>
				<div class="rpt-sec-body">${RIS.description}</div>
			</div>
			<div class="rpt-section">
				<div class="rpt-sec-title">超声提示：</div>
				<div class="rpt-sec-body">${RIS.recommendation}</div>
			</div>
		</div>
		<hr class="rpt-line" />
		<div class="rpt-content">
			<table class="rpt-table">
				<tr>
					<td><span class='rpt-label'>报告时间：</span> <fmt:formatDate
							value="${Context.time}" pattern="yyyy-MM-dd" /></td>
					<td style="width: 180px;"><span class='rpt-label'>报告医生：</span>
						${RIS.exam_doctor}</td>
				</tr>
				<tr>
					<td colspan="4"><span>此报告仅供参考，检查医生签字有效。</span></td>
				</tr>
			</table>
		</div>
	</div>
</c:if>
<c:if test="${param.restype=='LIS' }">
	<div class="rpt-page">
		<div class="rpt-title">${LIS.master.test_name}检验结果</div>
		<div class="rpt-content">
			<table class="rpt-table">
				<tr>
					<td><span class='rpt-label'></span>住院号：${Patient.inp_no}</td>
					<td style="width: 180px;"><span class='rpt-label'></span>报告日期：
						<fmt:formatDate value="${Context.time}" pattern="yyyy-MM-dd" /></td>
				</tr>
			</table>
		</div>
		<hr class="rpt-line" />
		<div class="rpt-content">
			<table class="rpt-table" style="margin: 15px 0px;">
				<tr>
					<td style="width: 25%"><span class='rpt-label'></span>姓名：${Patient.name}</td>
					<td style="width: 25%"><span class='rpt-label'></span>性别：${Patient.sex}</td>
					<td style="width: 25%"><span class='rpt-label'></span>年龄：${Patient.age}</td>
					<td style="width: 25%;"><span class='rpt-label'></span>体重：${Patient.weight}
						KG</td>
				</tr>
				<tr>
					<td style="width: 25%"><span class='rpt-label'></span>身高：${Patient.height}
						CM</td>
					<td style="width: 25%"><span class='rpt-label'></span>病人ID：${Patient.patient_id}</td>
					<td style="width: 25%"><span class='rpt-label'></span>剩余预交金：<fmt:formatNumber
							value="${Patient.deposit}" pattern="#.#" type="number" /></td>
					<td style="width: 25%;"><span class='rpt-label'></span>费别：${Patient.charge_type}</td>
				</tr>
			</table>
		</div>
		<table class="rpt-data-table">
			<thead>
				<tr>
					<th>报告项目名称</th>
					<th>结果</th>
					<th>异常</th>
					<th>单位</th>
					<th>正常参考值</th>
					<th>范围参考值</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${LIS.details}">
					<tr>
						<td><c:out value="${item.report_item_name }"></c:out></td>
						<td><c:out value="${item.result }"></c:out></td>
						<td><c:out value="${item.abnormal_indicator }"></c:out></td>
						<td><c:out value="${item.units }"></c:out></td>
						<td><c:out value="${item.normal_value }"></c:out></td>
						<td><c:out value="${item.min_max_value }"></c:out></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</c:if>
<c:if test="${param.restype=='HIS' }">
	<div class="rpt-page" style="min-width: 800px;">
		<div class="rpt-title">药品处方</div>
		<div class="rpt-content">
			<table class="rpt-table">
				<tr>
					<td style="width: 180px;"><span class='rpt-label'></span>病人ID：${Patient.patient_id}</td>
					<td><span class='rpt-label'></span>姓名：${Patient.name}</td>
					<td style="width: 180px;"><span class='rpt-label'></span>费别：${Patient.charge_type}</td>
				</tr>
			</table>
		</div>
		<hr class="rpt-line" />
		<div class="rpt-content">
			<table class="rpt-table" style="margin: 15px 0px;">
				<tr>
					<td style="width: 180px;"><span class='rpt-label'></span>身份：${Patient.identity}</td>
					<td colspan="3"><span class='rpt-label'></span>年龄：${Patient.age}</td>
					<td style="width: 180px;"><span class='rpt-label'></span>处方日期：<fmt:formatDate
							value="${HIS.master.presc_date}" pattern="yyyy-MM-dd" /></td>
				</tr>
				<tr>
					<td colspan="4"><span class='rpt-label'></span>合同单位：${Patient.unit_in_contract}</td>
					<td style="width: 180px;"><span class='rpt-label'></span>开单医生：${HIS.master.prescribed_by}</td>
				</tr>
				<tr>
					<td style="width: 180px"><span class='rpt-label'></span>开单科室：${HIS.master.ordered_by}</td>
					<td><span class='rpt-label'></span>录入人员：${HIS.master.entered_by}</td>
					<td><span class='rpt-label'></span>剂数：${HIS.master.repetition}</td>
					<td><span class='rpt-label'></span>煎药次数：${HIS.master.count_per_repetition }</td>
					<td style="width: 180px;"><span class='rpt-label'></span>处方属性：${HIS.master.presc_attr}</td>
				</tr>
			</table>
		</div>
		<table class="rpt-data-table">
			<thead>
				<tr>
					<th>级别</th>
					<th>药品名称</th>
					<th>厂商</th>
					<th>剂量</th>
					<th>规格</th>
					<th>数量</th>
					<th>单位</th>
					<th>单价</th>
					<th>应收</th>
					<th>实收</th>
					<th>用法</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${HIS.details}">
					<tr>
						<td><c:out value="${item.drug_grade}"></c:out></td>
						<td><c:out value="${item.drug_name}"></c:out></td>
						<td><c:out value="${item.firm_id}"></c:out></td>
						<td><fmt:formatNumber value="${item.dosage_each}"
								pattern="#.#" type="number" /></td>
						<td><c:out value="${item.package_spec}"></c:out></td>
						<td><fmt:formatNumber value="${item.quantity}" pattern="#.#"
								type="number" /></td>
						<td><c:out value="${item.package_units}"></c:out></td>
						<td><fmt:formatNumber value="${item.price}" pattern="#.#"
								type="number" /></td>
						<td><fmt:formatNumber value="${item.costs}" pattern="#.00"
								type="number" /></td>
						<td><fmt:formatNumber value="${item.payments}" pattern="#.00"
								type="number" /></td>
						<td><c:out value="${item.administration}"></c:out></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th>总计</th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th><fmt:formatNumber value="${HIS.summary.payments}"
							pattern="#.00" type="number" /></th>
					<th></th>
				</tr>
			</tfoot>
		</table>
	</div>
</c:if>