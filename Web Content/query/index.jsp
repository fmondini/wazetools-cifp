<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wtlib.auth.*"
	import="net.danisoft.wazetools.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%!
	private static final String PAGE_Title = AppCfg.getAppName() + " Queries";
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

	<jsp:include page="../_common/header.jsp" />

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
		<div class="DS-padding-top-16px" align="center">
			<div class="DS-text-title-shadow">Available queries for your countries</div>
		</div>

		<div class="DS-padding-updn24px">
			<div class="mdc-layout-grid__inner">
				<div class="<%= MdcTool.Layout.Cell(2, 4, 4) %>"></div>
				<div class="<%= MdcTool.Layout.Cell(2, 4, 4) %>">
					<div class="DS-padding-lfrg-8px" align="center">
						<%= MdcTool.Layout.IconCard(
							true,
							"", // divClass
							"notifications_active",
							"./results.jsp?type=" + CifpStatus.ACTIVE.toString(),
							CifpStatus.ACTIVE.getDescr(),
							CifpStatus.ACTIVE.getDescr() + " Closures",
							CifpStatus.ACTIVE.getDescr() + " closures currently published in the feed (text)",
							true,
							true
						) %>
					</div>
				</div>
				<div class="<%= MdcTool.Layout.Cell(2, 4, 4) %>">
					<div class="DS-padding-lfrg-8px" align="center">
						<%= MdcTool.Layout.IconCard(
							true,
							"", // divClass
							"notifications_paused",
							"./results.jsp?type=" + CifpStatus.PAUSED.toString(),
							CifpStatus.PAUSED.getDescr(),
							CifpStatus.PAUSED.getDescr() + " Closures",
							CifpStatus.PAUSED.getDescr() + " closures not present in the feed (text)",
							true,
							true
						) %>
					</div>
				</div>
				<div class="<%= MdcTool.Layout.Cell(2, 4, 4) %>">
					<div class="DS-padding-lfrg-8px" align="center">
						<%= MdcTool.Layout.IconCard(
							true,
							"", // divClass
							"notification_important",
							"./results.jsp?type=" + CifpStatus.ORPHAN.toString(),
							CifpStatus.ORPHAN.getDescr(),
							CifpStatus.ORPHAN.getDescr() + " Closures",
							"Newly created closures to be activated (text)",
							true,
							true
						) %>
					</div>
				</div>
				<div class="<%= MdcTool.Layout.Cell(2, 4, 4) %>">
					<div class="DS-padding-lfrg-8px" align="center">
						<%= MdcTool.Layout.IconCard(
							true,
							"", // divClass
							"map",
							"./gmap.jsp",
							"GMaps",
							"All Closures",
							"Graphical situation of all closures in the feed (map)",
							true,
							true
						) %>
					</div>
				</div>
				<div class="<%= MdcTool.Layout.Cell(2, 4, 4) %>"></div>
			</div>
		</div>

		<div class="DS-padding-bottom-16px" align="center">
			<div class="DS-text-italic DS-text-LightSlateGrey">Connection usually occurs every <b>5</b> minutes, and it's been <b><%= FeedLastCheck.getTime() %></b> since the last feed refresh from Waze.</div>
			<div class="DS-text-small DS-text-italic DS-text-LightSlateGrey">The last GET happened at <b><%= FeedLastCheck.getDate() %></b> by <b><%= FeedLastCheck.getUser() %></b> from <b><%= FeedLastCheck.getAddr() %></b></div>
		</div>
<%
	} catch (Exception e) {
		MSG.setSlideText("Query Fatal Error", e.toString());
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
