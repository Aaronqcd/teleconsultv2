function initSelectForm() {
	layui.use('laydate', function() {
		var laydate = layui.laydate;
		laydate.render({
			elem : "#iptInDate",
			type : "date",
			range : true
		});
	});
	layui.use("table", function() {
		var table = layui.table;
		table.init("mainTable", {
			page : true
		});
	});
	$("#btnSearch").click(searchPatients);
}

function searchPatients() {
	var table = layui.table;
	// table.reload('mainTable', {
	// where : {
	// restype : Utility.getUrlParam("restype"),
	// resid : Utility.getUrlParam("resid"),
	// patientid : $("#iptPatientId").val(),
	// indate : $("#iptInDate").val(),
	// }
	// });

	table.render({
		elem : '#mainTable',
		method : "post",
		url : 'select',
		where : {
			restype : Utility.getUrlParam("restype"),
			resid : Utility.getUrlParam("resid"),
			patientid : $("#iptPatientId").val(),
			indate : $("#iptInDate").val(),
		},
		cols : [ [ {
			type : 'radio',
		}, {
			field : 'patient_id',
			title : '病人ID'
		}, {
			field : 'indate',
			title : '日期',
			templet : function(d) {
				var time = Utility.formatTS(d.indate);
				return "<div>" + time + "</div>";
			}
		} ] ],
		id : 'mainTable',
		page : true
	});
}
$(function() {
	initSelectForm();
});