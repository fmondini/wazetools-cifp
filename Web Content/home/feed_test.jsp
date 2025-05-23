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
	private static final String PAGE_Title = AppCfg.getAppName() + " Feed Test";
	private static final String PAGE_Keywords = "Waze, Tools, CIFP, Closure, Incident, Feed";
	private static final String PAGE_Description = AppCfg.getAppAbstract();

	private static final String FEED_SERVLET_URL = AppCfg.getServerHomeUrl() + "/feed/get";

	private static String Boxed(String dataValue) {
		return(
			"<b class=\"DS-text-fixed-compact DS-text-DarkMagenta DS-back-AliceBlue DS-padding-lfrg-4px DS-padding-updn-2px DS-border-full\">" +
				dataValue +
			"</b>"
		);
	}
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

	try {

		DB = new Database();
		Group GRP = new Group(DB.getConnection());
		GeoIso GEO = new GeoIso(DB.getConnection());
%>
		<div class="DS-padding-updn-8px">
			<div class="DS-text-title-shadow"><%= PAGE_Title %></div>
		</div>

		<div class="DS-padding-top-8px DS-padding-bottom-16px">
			<div class="DS-text-big DS-text-italic">Select one of the available feeds</div>
			<div class="DS-text-small DS-text-gray DS-text-italic">NOTE: Only countries that have at least one closure are displayed</div>
		</div>

		<div class="DS-padding-bottom-16px">
			<table class="TableSpacing_0px">

				<tr class="DS-back-gray DS-text-bold DS-border-updn">
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center" ColSpan="2" RowSpan="2">Country</td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center" ColSpan="2" RowSpan="1">Feed</td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center" ColSpan="2" RowSpan="1">Feed</td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center" ColSpan="4">Closures currently in the database</td>
				</tr>

				<tr class="DS-back-gray DS-text-italic DS-border-updn">
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" ColSpan="2" align="center">Standard</td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" ColSpan="2" align="center">Extended</td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">Total</td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">Orphan</td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">Paused</td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">Active</td>
				</tr>

				<% for (String ctryIso : GRP.getAllCtryIso()) { %>

					<% int totClosures = GRP.countByCountry(ctryIso, CifpStatus.ALL); %>
					<% int orpClosures = GRP.countByCountry(ctryIso, CifpStatus.ORPHAN); %>
					<% int pauClosures = GRP.countByCountry(ctryIso, CifpStatus.PAUSED); %>
					<% int actClosures = GRP.countByCountry(ctryIso, CifpStatus.ACTIVE); %>

					<tr class="DS-border-dn">
						<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">
							<span class="DS-text-fixed-compact DS-text-bold"><%= ctryIso %></span>
						</td>
						<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg">
							<div><%= GEO.getDesc(ctryIso) %></div>
						</td>
						<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg">
							<a href="<%= FEED_SERVLET_URL %>?f=xml&t=s&c=<%= ctryIso %>" target="_blank">XML</a>
						</td>
						<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg">
							<a href="<%= FEED_SERVLET_URL %>?f=json&t=s&c=<%= ctryIso %>" target="_blank">JSON</a>
						</td>
						<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg">
							<a href="<%= FEED_SERVLET_URL %>?f=xml&t=x&c=<%= ctryIso %>" target="_blank">XML</a>
						</td>
						<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg">
							<a href="<%= FEED_SERVLET_URL %>?f=json&t=x&c=<%= ctryIso %>" target="_blank">JSON</a>
						</td>
						<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">
							<span class="DS-text-italic"><%= totClosures %></span>
						</td>
						<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">
							<span class="DS-text-darkred DS-text-italic"><%= orpClosures == 0 ? "<span class=\"DS-text-compact DS-text-disabled\">none</span>" : orpClosures %></span>
						</td>
						<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">
							<span class="DS-text-gray DS-text-italic"><%= pauClosures == 0 ? "<span class=\"DS-text-compact DS-text-disabled\">none</span>" : pauClosures %></span>
						</td>
						<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">
							<span class="DS-text-green DS-text-italic"><%= actClosures == 0 ? "<span class=\"DS-text-compact DS-text-disabled\">none</span>" : "<b>" + actClosures + "</b>" %></span>
						</td>
					</tr>

				<% } %>

			</table>
		</div>

		<div class="DS-padding-bottom-16px">
			<div class="DS-padding-updn-8px">
				<div class="DS-text-big DS-text-italic">URL Syntax</div>
			</div>
			<div class="DS-padding-updn-8px">
				<div class="DS-text-fixed-compact">
					<span class="DS-padding-8px DS-back-AliceBlue DS-border-full"><%= FEED_SERVLET_URL %>?f=<b class="DS-text-RoyalBlue">{format}</b>&amp;t=<b class="DS-text-green">{type}</b>&amp;c=<b class="DS-text-maroon">{country}</b></span>
				</div>
			</div>
			<div class="DS-padding-updn-8px">
				<div>
					Where:
					<ul class="DS-ul-padding">
						<li class="DS-li-padding"><b class="DS-text-fixed-compact DS-text-RoyalBlue">{format}</b> &#129054; Feed Format - Can be <%= Boxed("XML") %> or <%= Boxed("JSON") %></li>
						<li class="DS-li-padding"><b class="DS-text-fixed-compact DS-text-green">{type}</b> &#129054; Feed Type - Can be <%= Boxed("S") %> for standard output or <%= Boxed("X") %> for extended output</li>
						<li class="DS-li-padding"><b class="DS-text-fixed-compact DS-text-maroon">{country}</b> &#129054; Feed Country in ISO 3166 3-chars code, i.e. <%= Boxed("ITA") %> for Italy</li>
					</ul>
				</div>
			</div>
		</div>

		</div>
		</div>
		</div>
<%
	} catch (Exception e) {
		MsgTool MSG = new MsgTool(session);
		MSG.setSlideText("Internal Error", e.toString());
		RedirectTo = "../home/";
	}

	if (DB != null)
		DB.destroy();
%>
	<jsp:include page="../_common/footer.jsp">
		<jsp:param name="RedirectTo" value="<%= RedirectTo %>"/>
	</jsp:include>

	</body>
	</html>
