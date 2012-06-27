<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<eRedG4:html title="${sysTitle}" showLoading="false" exportParams="true"
	isSubPage="false">
<eRedG4:import src="/arm/js/login.js" />
<eRedG4:body>
	<div id="hello-win" class="x-hidden">
	<div id="hello-tabs"><img border="0" width="450" height="70"
		src="<%=request.getAttribute("bannerPath") == null ? request.getContextPath()
							+ "/resource/image/login_banner.png" : request.getAttribute("bannerPath")%>" />
	</div>
	</div>
	<div id="aboutDiv" class="x-hidden"
		style='color: black; padding-left: 10px; padding-top: 10px; font-size: 12px'>
	G4系统集成与应用开发平台 (G4Studio&reg)<br>
	<br>
	<br>
	官方网站:<a href="http://www.g4studio.org" target="_blank">www.g4studio.org</a>
	</div>
	<div id="infoDiv" class="x-hidden"
		style='color: black; padding-left: 10px; padding-top: 10px; font-size: 12px'>
	登录帐户[用户名/密码]...<br>
	[developer/111111][super/111111]<br>
	[xiongchun_a_3/111111][xiongchun_b_3/111111]
	
	
	</div>
</eRedG4:body>
</eRedG4:html>