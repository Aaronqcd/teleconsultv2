
	//二级菜单 显示隐藏
	$(".showon dl").click(function() {
		$(".son").not($(this).next()).slideUp();
		$(this).next().slideToggle();

	});
	$(".showdown dl").click(function() {
		$(".showdown>div").slideToggle();

	});
	$(".showdown>div>ul>li").click(function() {
		$(".showdown>div").css("display","block");

	});
	//	二级菜单 显示隐藏 end

	//色板菜单 隐藏

    $(document).bind("click", function (e) {
		//色板菜单
        if($(e.target).closest(".input_cxcolor").length<=0){
            $(".cxcolor").hide();
        }
        //二级div
		if($(e.target).closest(".meeting-tool-borad>div").length<=0){
			$(".meeting-tool-borad>div:not(.showdown)").children("div").hide();
		}
    });
    //色板菜单 隐藏 end

	//获取 设置线条粗细 值
	$('input:radio[name="lines"]').change(function() {
		$("#linevalue").val($(this).val());
	});
	//获取 设置线条粗细 值 end


	//初始化 设置当前背景颜色
	$("#color_a").cxColor({
		color: "#ffffff"
	});
	var acolorval = $("#color_a").css("background-color");
	$("#color_a").attr("value", acolorval)
	$("#color_a").bind("change", function() {
		var acolorval = $(this).css("background-color");
		$(this).attr("value", acolorval)
	});
	//初始化 设置当前背景颜色 end

	//初始化 设置全局背景颜色
	$("#color_b").cxColor({
		color: "#ffffff"
	});
	var bcolorval = $("#color_b").css("background-color");
	$("#color_b").attr("value", acolorval)
	$("#color_b").bind("change", function() {
		var acolorval = $(this).css("background-color");
		$(this).attr("value", acolorval)
	});
	//初始化 设置全局背景颜色 end

	//初始化 设置画笔颜色
	$("#color_c").cxColor({
		color: "#E50112"
	});
	var ccolorval = $("#color_c").css("background-color");
	$("#color_c").attr("value", acolorval)
	$("#color_c").bind("change", function() {
		var acolorval = $(this).css("background-color");
		$(this).attr("value", acolorval)
	});
	//初始化 设置画笔颜色 end

	//初始化 设置文本颜色
	$("#color_d").cxColor({
		color: "#E50112"
	});
	var dcolorval = $("#color_d").css("background-color");
	$("#color_d").attr("value", acolorval)
	$("#color_d").bind("change", function() {
		var acolorval = $(this).css("background-color");
		$(this).attr("value", acolorval)
	});
	//初始化 设置文本颜色 end
