/*
	抓拍
*/
var videoElem;
var videoDiv;

function videoType(video){
    var returnType='';
    if(video.canPlayType('video/mp4')=='probably'||video.canPlayType('video/mp4')=='maybe'){
    	returnType= 'mp4';
    }else if(video.canPlayType('video/ogg')=='probably'||video.canPlayType('video/ogg')=='maybe'){
    	returnType= 'ogg'; 
    }else if(video.canPlayType('video/webm')=='probably'||video.canPlayType('video/webm')=='maybe'){
	 	returnType= 'webm';
    }
    return returnType;
}

function createVideo(){
     videoElem=document.createElement("video");//创建video
     videoElem.width="500";
     videoElem.height="400";
     videoElem.loop=true;
     videoDiv=document.getElementById("videopanel");//获取video的外层容器
     videoDiv.appendChild(videoElem);
     var vtype=videoType(videoElem);//判断浏览器支持的格式
     if(vtype==""){
        videoDiv.innerHtml('不支持video')
    }else{
        videoElem.setAttribute('src',"video."+vtype);
    }

}  
function drawvideo(){	
	icanvas=document.getElementById("icanvas");
	var context=icanvas.getContext("2d");//第一个复制video的画布
	context.drawImage(videoElem,0,0,videoElem.width,videoElem.height);
}

function drawimg(){
	var firstcanvas=document.getElementById("icanvas");
	var icanvasimg=document.getElementById("icanvasimg");
	var contextimg=icanvasimg.getContext("2d");//第二个截图的画布
	contextimg.drawImage(firstcanvas,0,0,firstcanvas.width,firstcanvas.height);
}

window.onload=function(){
	createVideo();
	videoElem.play();
	setInterval(drawvideo,100);
	
	var clickbtn=document.getElementById('clickme');
	clickbtn.onclick=function(){
		drawimg();
	};
}