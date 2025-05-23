<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%
	final String cellClassLF = "DS-padding-lfrg-4px DS-padding-updn-3px DS-text-black DS-text-bold DS-back-gray DS-border-rg DS-text-centered";
	final String cellClassRG = "DS-padding-lfrg-4px DS-padding-updn-3px DS-back-white";

	final String GidUUID = EnvTool.getStr(request, "gid", "");

	Database DB = null;

	try {

		DB = new Database();

		Group GID = new Group(DB.getConnection());
		Group.Data gidData = GID.Read(GidUUID);

		Segment SEG = new Segment(DB.getConnection());
		Vector<Segment.Data> vecSegObj = SEG.getAll(gidData.getID());
%>
		<table class="TableSpacing_0px DS-text-compact DS-full-width">
			<tr class="DS-border-full">
				<td class="<%= cellClassLF %>">UUID</td>
				<td class="<%= cellClassRG %> DS-text-fixed-reduced DS-text-bold DS-text-gray">
					<%= gidData.getID() %>
				</td>
			</tr>
			<tr class="DS-border-full">
				<td class="<%= cellClassLF %>">Source</td>
				<td class="<%= cellClassRG %> DS-text-gray">Script version <%= gidData.getSource() %></td>
			</tr>
			<tr class="DS-border-full">
				<td class="<%= cellClassLF %>">Location</td>
				<td class="<%= cellClassRG %> DS-text-gray"><%= gidData.getCountry() %> / <%= gidData.getState() %> / <%= gidData.getCity() %></td>
			</tr>
			<tr class="DS-border-full">
				<td class="<%= cellClassLF %>">Content</td>
				<td class="<%= cellClassRG %> DS-text-gray">This closure contains <b><%= Segment.countSegments(vecSegObj) %></b> segment(s) in <b><%= Segment.countStreets(vecSegObj) %></b> street(s)</td>
			</tr>

			<tr class="DS-border-full">
				<td class="<%= cellClassLF %>">Created</td>
				<td class="<%= cellClassRG %> DS-text-gray"><%= FmtTool.fmtDateTime(gidData.getCreatedAt()) %> by <%= gidData.getCreatedBy() %></td>
			</tr>
			<tr class="DS-border-full">
				<td class="<%= cellClassLF %>">Updated</td>
				<td class="<%= cellClassRG %> DS-text-gray"><%= FmtTool.fmtDateTime(gidData.getUpdatedAt()) %> by <%= gidData.getUpdatedBy() %></td>
			</tr>
		</table>
<%
	} catch (Exception e) {
		out.println(e.getMessage());
	}

	if (DB != null)
		DB.destroy();
%>
