var Util = {
	serializeObject: function(form) {
		var formEL = $(form);
		var o = {};
		var a = formEL.serializeArray();
		$.each(a, function() {
			if(o[this.name]) {
				if(!o[this.name].push) {
					o[this.name] = [o[this.name]];
				}
				o[this.name].push(this.value || '');
			} else {
				o[this.name] = this.value || '';
			}
		});
		return o;
	},
 
	fillFormData: function(form, obj, isStatus) {
		var formEL = $(form);
		$.each(obj, function(index, item) {
		   formEL.find("[name=" + index + "]").val(item);
		});
	},
	empty: function(data) {
		if(null == data || "" == data) return true;
		else return false;
	}
}