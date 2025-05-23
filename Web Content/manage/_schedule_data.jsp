<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="org.json.*"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%
	final String cellClassHead = "DS-padding-lfrg-4px DS-padding-updn-2px DS-back-gray";

	final String GidUUID = EnvTool.getStr(request, "gid", "");

	Database DB = null;

	try {

		DB = new Database();
		JSONArray jaSchedule;
		String minValue, maxValue;

		Group GID = new Group(DB.getConnection());
		Group.Data gidData = GID.Read(GidUUID);
%>
		<div class="<%= MdcTool.Elevation.Light() %>">

			<table class="TableSpacing_0px DS-full-width">

				<tr class="DS-border-full DS-back-darkgray">
					<td class="DS-padding-4px" ColSpan="5">
						<div class="DS-text-big">Schedule Table</div>
					</td>
					<td class="DS-padding-4px" ColSpan="5" align="right">
						<table class="TableSpacing_0px DS-text-blue DS-cursor-pointer" title="Refresh table from database" onClick="ReadSchedule('<%= gidData.getID() %>');">
							<tr class="DS-padding-top-8px">
								<td class="DS-padding-top-4px">
									<span class="material-icons">refresh</span>
								</td>
							</tr>
						</table>
					</td>
				</tr>

				<tr class="DS-border-full DS-text-bold DS-back-gray">
					<td class="<%= cellClassHead %> DS-border-rg" ColSpan="2">Weekday</td>
					<td class="<%= cellClassHead %> DS-border-rg" ColSpan="2" align="center">TimeSlot #1</td>
					<td class="<%= cellClassHead %> DS-border-rg" ColSpan="2" align="center">TimeSlot #2</td>
					<td class="<%= cellClassHead %> DS-border-rg" ColSpan="2" align="center">TimeSlot #3</td>
					<td class="<%= cellClassHead %>" ColSpan="2" align="center">TimeSlot #4</td>
				</tr>

				<% for (int i=0; i<Schedule.DAYS_NO; i++) { %>
					<tr class="DS-border-full">
						<td class="DS-padding-lf-4px DS-padding-rg-0px DS-padding-updn-2px DS-back-gray"><%= Schedule.DayNameFull[i] %></td>
						<td class="DS-padding-lf-0px DS-padding-rg-2px DS-padding-top-4px DS-padding-bottom-0px DS-back-gray DS-border-rg">
							<span class="material-icons DS-text-compact DS-text-green DS-cursor-pointer" title="Create slot" onClick="AddSchedule('<%= gidData.getID() %>', '<%= Schedule.DayNameAbbr[i] %>');">add_circle</span>
						</td>
						<% jaSchedule = Schedule.getDayData(gidData.getSchedule(), Schedule.DayNameAbbr[i]); %>
						<% for (int x = 0; x < 4; x++) { %>
							<% minValue = jaSchedule.getJSONObject(x).getString("min"); %>
							<% maxValue = jaSchedule.getJSONObject(x).getString("max"); %>
							<td class="DS-padding-lfrg-2px DS-padding-updn-2px" align="center">
								<span class="DS-text-fixed-small <%= minValue.equals(Schedule.NO_TIME) ? "DS-text-disabled" : "DS-text-black" %>"><%= minValue %></span>
								<span class="<%= minValue.equals(Schedule.NO_TIME) ? "DS-text-disabled" : "DS-text-gray" %>">-</span>
								<span class="DS-text-fixed-small <%= minValue.equals(Schedule.NO_TIME) ? "DS-text-disabled" : "DS-text-black" %>"><%= maxValue %></span>
							</td>
							<td class="DS-padding-lfrg-2px DS-padding-top-4px DS-border-rg" align="center">
								<% if (minValue.equals(Schedule.NO_TIME)) { %>
									<span class="material-icons DS-text-compact DS-text-disabled" title="Free slot">notifications_none</span>
								<% } else { %>
									<span class="material-icons DS-text-compact DS-text-red DS-cursor-pointer" title="Delete slot" onClick="DelSchedule('<%= gidData.getID() %>', '<%= Schedule.DayNameAbbr[i] %>', '<%= minValue %>', '<%= maxValue %>');">remove_circle_outline</span>
								<% } %>
							</td>
						<% } %>
					</tr>
				<% } %>

				<tr id="trDelSchedError" class="DS-hidden DS-border-full">
					<td class="DS-padding-4px" ColSpan="10" align="center">
						<div id="divDelSchedError"></div>
					</td>
				</tr>
				
			</table>

		</div>

<% /* TODO: +++ Check This %>
<div class="DS-text-fixed"><%= gidData.getSchedule().toString(2).replace("\n", "<br>").replace(" ", "&nbsp;") %></div>
<% */ %>

<%
	} catch (Exception e) {
		out.println(e.getMessage());
	}

	if (DB != null)
		DB.destroy();
%>
