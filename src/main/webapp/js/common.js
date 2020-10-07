var Utility = {
	// 获取url参数
	getUrlParam : function(name) {
		var search = location.search.slice(1);
		var arr = search.split("&");
		for (var i = 0; i < arr.length; i++) {
			var ar = arr[i].split("=");
			if (ar[0] == name) {
				if (unescape(ar[1]) == 'undefined') {
					return "";
				} else {
					return decodeURI(ar[1]);
				}
			}
		}
		return "";
	},
	// 格式化时间戳
	formatTS : function(ts, format) {
		var date = new Date(ts), ymd = [
				this.paddingZero(date.getFullYear(), 4),
				this.paddingZero(date.getMonth() + 1),
				this.paddingZero(date.getDate()) ], hms = [
				this.paddingZero(date.getHours()),
				this.paddingZero(date.getMinutes()),
				this.paddingZero(date.getSeconds()) ];

		format = format || 'yyyy-MM-dd HH:mm:ss';

		return format.replace(/yyyy/g, ymd[0]).replace(/MM/g, ymd[1]).replace(
				/dd/g, ymd[2]).replace(/HH/g, hms[0]).replace(/mm/g, hms[1])
				.replace(/ss/g, hms[2]);

	},
	// 数字前置补0
	paddingZero : function(num, length) {
		var str = '';
		num = String(num);
		length = length || 2;
		for (var i = num.length; i < length; i++) {
			str += '0';
		}
		return num < Math.pow(10, length) ? str + (num | 0) : num;
	}
}
