<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.*"
%>
<%!
	private static final String PAGE_Title = AppCfg.getAppName();
	private static final String PAGE_Keywords = "Waze, Tools, CIFP, Closure, Incident, Feed";
	private static final String PAGE_Description = AppCfg.getAppAbstract();
%>
	<!DOCTYPE html>
	<html>
	<head>
		<jsp:include page="../_common/head.jsp">
			<jsp:param name="PAGE_Title" value="<%= PAGE_Title %>"/>
			<jsp:param name="PAGE_Keywords" value="<%= PAGE_Keywords %>"/>
			<jsp:param name="PAGE_Description" value="<%= PAGE_Description %>"/>
		</jsp:include>
	</head>

	<body>

		<jsp:include page="../_common/header.jsp" />

		<div class="mdc-layout-grid DS-layout-body">
			<div class="mdc-layout-grid__inner">
				<div class="<%= MdcTool.Layout.Cell(12, 8, 4) %>">
					<div class="DS-padding-updn16px" align="center">
						<a href="../query/"><img src="../images/splash.png" width="800px" height="auto"></a>
					</div>
					<div class="DS-padding-bottom-16px" align="center">
						<div class="DS-text-title-shadow">Click on the image above to enter</div>
					</div>
					<div class="DS-padding-bottom-16px" align="center">
						<div class="DS-text-large DS-text-italic">
							<div class="DS-text-Crimson">To log in, you must have a <b>CIFP</b>-enabled <b>AUTH</b> account</div>
							<div class="DS-text-black">If you don't have an AUTH account, <a href="<%= AppCfg.getServerHomeUrl().replace("cifp", "auth") %>/anonymous/register.jsp" target="_blank">register here</a></div>
						</div>
					</div>
					<div class="DS-padding-bottom-16px" align="center">
						<div class="DS-text-large DS-text-italic DS-text-DarkSlateGray">
							<div>If you are already registered but your account is not CIFP-enabled</div>
							<div>ask the <a href="<%= AppCfg.getServerHomeUrl().replace("cifp", "auth") %>/anonymous/hierarchy.jsp" target="_blank">Waze coordinator in your country</a> to enable it</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<jsp:include page="../_common/footer.jsp">
			<jsp:param name="RedirectTo" value=""/>
		</jsp:include>

	</body>
	</html>
