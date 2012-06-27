<!--
此页面代码由易道系统集成与应用开发平台模板引擎组件强力驱动生成
     copyRight(c) 2007-2010, 易道软件实验室(eRedLab).
                  www.eredlab.org
-->
<!-- 您请求的URL为:$requestURL -->
<!-- 导入DOCTYPE会引起登陆页面表单元素错位的Bug -->
#if(${doctypeEnable} == "true")
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
#end
<html>
<head>
<title>${title}</title>
<meta http-equiv="keywords" content="eRedG4,eRedLab,ExtJS,Demo,在线演示,易道系统集成与应用开发平台,易道软件实验室">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="description" content="It's Based eRedG4！copyRight(c) eRedLab">
<meta http-equiv="pragma" content="no-cache">  
<meta http-equiv="cache-control" content="no-cache, must-revalidate">  
<meta http-equiv="expires" content="0">
<link rel="shortcut icon" href="${contextPath}/resource/image/${titleIcon}" />  
#if(${extDisabled} == "false")
<link rel="stylesheet" type="text/css" href="${contextPath}/resource/css/ext_icon.css"/>
<link rel="stylesheet" type="text/css" href="${contextPath}/resource/theme/${theme}/resources/css/ext-all.css"/>
<script type="text/javascript" src="${contextPath}/resource/extjs3.1/adapter/ext/ext-base.js"></script>
  #if(${extMode} == "debug")
<script type="text/javascript" src="${contextPath}/resource/extjs3.1/ext-all-debug.js"></script>
  #else
<script type="text/javascript" src="${contextPath}/resource/extjs3.1/ext-all.js"></script>
  #end
<link rel="stylesheet" type="text/css" href="${contextPath}/resource/css/ext_css_patch.css" />
<script type="text/javascript" src="${contextPath}/resource/commonjs/ext-lang-zh_CN.js"></script>
#end
#if(${jqueryEnabled} == "true")
<script type="text/javascript" src="${contextPath}/resource/jquery/jquery-1.3.2.min.js"></script>
#end
<script type="text/javascript" src="${contextPath}/resource/commonjs/eredg4.js"></script>
<link rel="stylesheet" type="text/css" href="${contextPath}/resource/css/eredg4.css"/>
#if(${uxEnabled} == "true")
<script type="text/javascript" src="${contextPath}/resource/extjs3.1/ux/ux-all.js"></script>
<link rel="stylesheet" type="text/css" href="${contextPath}/resource/extjs3.1/ux/css/ux-all.css"/>
#end
#if(${fcfEnabled} == "true")
<script type="text/javascript" src="${contextPath}/resource/commonjs/FusionCharts.js"></script>
#end
<script type="text/javascript">
  var webContext = '${contextPath}';
  var runMode = '${runMode}';
  var ajaxErrCode = '${ajaxErrCode}';
  var micolor = 'color:${micolor};';
  Ext.QuickTips.init();
  Ext.form.Field.prototype.msgTarget = 'qtip';
  Ext.BLANK_IMAGE_URL = '${contextPath}/resource/image/ext/s.gif';
  
  Ext.Ajax.on('requestexception', function(conn, response, options) {
		if (response.status == ajaxErrCode) {
			setTimeout(function(){
				//延时避免被failure回调函数中的aler覆盖
				top.Ext.MessageBox.alert('提示', '由于长时间未操作,空闲会话已超时;您将被强制重定向到登录页面!', function() {
					parent.location.href = webContext + '/login.ered?reqCode=init';
				});
			},200);
		}
  });
  
  Ext.Ajax.on('requestexception', function(conn, response, options) {
		if (response.status == '998') {
			setTimeout(function(){
				//延时避免被failure回调函数中的aler覆盖
				top.Ext.MessageBox.alert('提示', '您的会话连接由于在其它窗口上被注销而失效,系统将把您强制重定向到登录界面.', function() {
					parent.location.href = webContext + '/login.ered?reqCode=init';
				});
			},200);
		}
  });
#if(${exportParams} == "true")
//全局参数表
#foreach($param in $paramList)var ${param.paramkey}='${param.paramvalue}';#end
#end

#if(${exportUserinfo} == "true")
//当前登录用户信息
var userid = '${userInfo.getUserid()}';var username = '${userInfo.getUsername()}';var account = '${userInfo.getAccount()}';
var deptid = '${userInfo.getDeptid()}';var customId = '${userInfo.getCustomId().trim()}';var theme = '${userInfo.getTheme()}';
var explorer = '${userInfo.getExplorer()}';
#end

#if(${isSubPage} == "true")
#if(${urlSecurity}=="1" && ${urlSecurity2}=="true")
window.onload=function(){
  if(parent.userid != '${userInfo.getUserid()}'){
     top.Ext.MessageBox.alert('提示', '这是一个非法请求或者您的会话连接由于在其它窗口上被注销而失效,系统将把您强制重定向到登录界面.', function() {
		parent.window.location.href = webContext + '/login.ered?reqCode=init';
	 });
  }
};
#end
Ext.Ajax.on('beforerequest', function(conn, opts) {
  Ext.Ajax.extraParams={'loginuserid':parent.userid};
  });
#else
Ext.Ajax.on('beforerequest', function(conn, opts) {
  if(window.userid) Ext.Ajax.extraParams={'loginuserid':userid};
  });
#end

</script>
</head>
#if(${showLoading} == "true")
<style type="text/css">
 #loading-mask {
	Z-INDEX: 20000;
	LEFT: 0px;
	WIDTH: 100%;
	POSITION: absolute;
	TOP: 0px;
	HEIGHT: 100%;
	BACKGROUND-COLOR: white
}
#loading {
	PADDING-RIGHT: 2px;
	PADDING-LEFT: 2px;
	Z-INDEX: 20001;
	LEFT: 32%;
	PADDING-BOTTOM: 2px;
	PADDING-TOP: 2px;
	POSITION: absolute;
	TOP: 40%;
	HEIGHT: auto
}
#loading IMG {
	MARGIN-BOTTOM: 5px
}
#loading .loading-indicator {
	PADDING-RIGHT: 10px;
	PADDING-LEFT: 10px;
	BACKGROUND: white;
	PADDING-BOTTOM: 10px;
	MARGIN: 0px;
	FONT: 12px 宋体, arial, helvetica;
	COLOR: #555;
	PADDING-TOP: 10px;
	HEIGHT: auto;
	TEXT-ALIGN: center
}
</style>
<script type="text/javascript">
Ext.EventManager.on(window, 'load', function(){
	 setTimeout(
		 function() {
			Ext.get('loading').remove();
			Ext.get('loading-mask').fadeOut({remove:true});
			}, 1); 
});
</script>
<DIV id=loading-mask></DIV>
<DIV id=loading>
<DIV class=loading-indicator><IMG style="MARGIN-RIGHT: 5px" 
	height=16
	src="${contextPath}/resource/image/ajax.gif"
	width=16 align=absMiddle>${pageLoadMsg}</DIV>
</DIV>
#end 