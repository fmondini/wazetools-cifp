<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.*"
%>
<%!
	private static final String PAGE_Title = AppCfg.getAppName() + " Statistics";
	private static final String PAGE_Keywords = "";
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

	<jsp:include page="../_common/header.jsp">
		<jsp:param name="Page-Title" value="<%= PAGE_Title %>"/>
	</jsp:include>

	<div class="mdc-layout-grid DS-layout-body">
	<div class="mdc-layout-grid__inner">
	<div class="<%= MdcTool.Layout.Cell(12, 8, 4) %>">
<%
	String RedirectTo = "";

	Database DB = null;
	MsgTool MSG = new MsgTool(session);

	try {

		DB = new Database();

%>
		<div class="DS-padding-32px DS-text-centered">
			<div class="DS-text-subtitle DS-text-orangered DS-text-shadow">STATS PAGE - Work in progress...</div>
		</div>
<%
		//
		// BOTTOM BUTTONS
		//
%>
		<div class="DS-card-foot">
			<%= MdcTool.Button.BackTextIcon("Back", "../home/") %>
		</div>
<%
	} catch (Exception e) {
		MSG.setSlideText("Stats Fatal Error", e.getMessage());
		RedirectTo = "../home/";
	}

	if (DB != null)
		DB.destroy();
%>
	</div>
	</div>
	</div>

	<jsp:include page="../_common/footer.jsp">
		<jsp:param name="RedirectTo" value="<%= RedirectTo %>"/>
	</jsp:include>

</body>
</html>
