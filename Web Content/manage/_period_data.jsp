<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%
	final String cellClassLF = "DS-padding-lfrg-4px DS-padding-updn-2px DS-text-black DS-text-bold DS-back-gray DS-border-rg";
	final String cellClassRG = "DS-padding-lfrg-4px DS-padding-updn-2px DS-back-white";

	final String GidUUID = EnvTool.getStr(request, "gid", "");

	Database DB = null;

	try {

		DB = new Database();

		Group GID = new Group(DB.getConnection());
		Group.Data gidData = GID.Read(GidUUID);
%>
		<div class="<%= MdcTool.Elevation.Light() %>">

			<table class="TableSpacing_0px DS-full-width">

				<tr class="DS-border-full DS-back-darkgray">
					<td class="DS-padding-4px" ColSpan="2">
						<div class="DS-text-big">Closure Period</div>
					</td>
					<td class="DS-padding-4px" ColSpan="2" align="right">
						<table class="TableSpacing_0px DS-text-blue DS-cursor-pointer" title="Refresh table from database" onClick="ReadPeriod('<%= gidData.getID() %>');">
							<tr class="DS-padding-top-8px">
								<td class="DS-padding-top-4px" ColSpan="2">
									<span class="material-icons">refresh</span>
								</td>
							</tr>
						</table>
					</td>
				</tr>

				<tr class="DS-border-full">
					<td class="<%= cellClassLF %>">Start Date/Time</td>
					<td class="<%= cellClassRG %> DS-border-rg" align="center">
						<input id="prmPerDateMin" class="DS-input-textbox-compact DS-text-fixed" type="date" value="<%= gidData.getPeriod().getJSONObject("min").getString("date") %>" onChange="SavePeriod('<%= gidData.getID() %>');">
					</td>
					<td class="<%= cellClassRG %> DS-border-rg" align="center">
						<input id="prmPerTimeMin" class="DS-input-textbox-compact DS-text-fixed" type="time" value="<%= gidData.getPeriod().getJSONObject("min").getString("time") %>" onChange="SavePeriod('<%= gidData.getID() %>');">
					</td>
					<td class="<%= cellClassRG %>" align="center">
						<input id="prmPerZoneMin" class="DS-input-textbox-compact DS-text-fixed" readonly value="<%= gidData.getPeriod().getJSONObject("min").getString("zone") %>" size="5" maxlength="5">
					</td>
				</tr>

				<tr class="DS-border-full">
					<td class="<%= cellClassLF %>">End Date/Time</td>
					<td class="<%= cellClassRG %> DS-border-rg" align="center">
						<input id="prmPerDateMax" class="DS-input-textbox-compact DS-text-fixed" type="date" value="<%= gidData.getPeriod().getJSONObject("max").getString("date") %>" onChange="SavePeriod('<%= gidData.getID() %>');">
					</td>
					<td class="<%= cellClassRG %> DS-border-rg" align="center">
						<input id="prmPerTimeMax" class="DS-input-textbox-compact DS-text-fixed" type="time" value="<%= gidData.getPeriod().getJSONObject("max").getString("time") %>" onChange="SavePeriod('<%= gidData.getID() %>');">
					</td>
					<td class="<%= cellClassRG %>" align="center">
						<input id="prmPerZoneMax" class="DS-input-textbox-compact DS-text-fixed" readonly value="<%= gidData.getPeriod().getJSONObject("min").getString("zone") %>" size="5" maxlength="5">
					</td>
				</tr>

			</table>

		</div>

<% /* TODO: +++ Check This %>
<div class="DS-text-fixed"><%= gidData.getPeriod().toString(2).replace("\n", "<br>").replace(" ", "&nbsp;") %></div>
<% */ %>

<%
	} catch (Exception e) {
		out.println(e.getMessage());
	}

	if (DB != null)
		DB.destroy();
%>
