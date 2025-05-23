<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.cifp.*"
%>
	<table class="TableSpacing_0px DS-full-width">

	<tr class="DS-back-gray DS-text-bold DS-text-italic DS-border-updn">
		<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="left">Location</td>
		<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="left">Type</td>
		<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">Status</td>
		<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="left" ColSpan="3">Description</td>
		<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">Start Date/Time</td>
		<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center">Stop Date/Time</td>
		<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" align="center" ColSpan="3">Last Closure Update</td>
	</tr>
<%
	Database DB = null;

	try {

		DB = new Database();
		Bookmark UBK = new Bookmark(DB.getConnection());
		Vector<Bookmark.Data> vecUbkData = UBK.getAll(SysTool.getCurrentUser(request));

		if (vecUbkData.isEmpty()) {
%>
			<tr class="DS-border-full">
				<td class="DS-back-pastel-red DS-padding-16px" align="center" ColSpan="10">
					<div class="DS-text-large DS-text-exception">No bookmarks found for user <%= SysTool.getCurrentUser(request) %></div>
				</td>
			</tr>
<%
		} else {

			for (Bookmark.Data ubkData : vecUbkData) {
%>
				<tr class="DS-border-updn">

					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-lfrg" nowrap align="left"><%= ubkData.getLocation() %></td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-rg" align="left" title="<%= ubkData.getSubType() %>"><%= ubkData.getType() %></td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-border-rg" align="center"><%= ubkData.getStatus().getSymbol() %></td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px"><%= ubkData.getDescr().trim().equals("") ? "<span class=\"DS-text-disabled\">[no description]</span>" : ubkData.getDescr() %></td>

					<td class="" align="center" width="24px">
						<div class="DS-padding-top-4px DS-padding-bottom-0px">
							<span class="DS-cursor-pointer" title="Edit Closure" onClick="window.location.href='<%= "../manage/gid_edit.jsp?gid=" + ubkData.getGroup() %>';">
								<%= IcoTool.Symbol.RndExtn("edit_calendar", true, "20px", "RoyalBlue", "", "") %>
							</span>
						</div>
					</td>

					<td class="DS-border-rg" align="center" width="20px">
						<div class="DS-padding-top-4px DS-padding-bottom-0px">
							<span class="DS-cursor-pointer" title="Delete Bookmark" onClick="DeleteBookmark('<%= ubkData.getID() %>');">
								<%= IcoTool.Symbol.RndExtn("cancel", true, "20px", "Crimson", "", "") %>
							</span>
						</div>
					</td>

					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-text-fixed-compact DS-border-rg" nowrap align="center"><%= FmtTool.fmtDateTimeNoSecs(ubkData.getPeriodMin()) %></td>
					<td class="DS-padding-updn-2px DS-padding-lfrg-4px DS-text-fixed-compact DS-border-rg" nowrap align="center"><%= FmtTool.fmtDateTimeNoSecs(ubkData.getPeriodMax()) %></td>
					<td class="DS-padding-updn-2px DS-text-fixed-compact" nowrap align="right"><%= FmtTool.fmtDateTimeNoSecs(ubkData.getUpdatedAt()) %></td>
					<td class="DS-padding-updn-2px" nowrap align="center" width="24px"><span class="DS-text-gray DS-text-italic"><%= ubkData.getUpdatedAt().equals(FmtTool.DATEZERO) ? "" : "by" %></span></td>
					<td class="DS-padding-updn-2px DS-border-rg" nowrap align="left"><%= ubkData.getUpdatedBy() %></td>

				</tr>
<%
			}
		}

	} catch (Exception e) {
%>
		<tr class="DS-border-full">
			<td class="DS-back-pastel-red DS-padding-16px" align="center" ColSpan="10">
				<div class="DS-text-large DS-text-exception"><%= e.toString() %></div>
			</td>
		</tr>
<%
	}

	if (DB != null)
		DB.destroy();
%>
	</table>
