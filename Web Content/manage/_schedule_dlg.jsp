<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%
	final String GidUUID = EnvTool.getStr(request, "gid", "");
	final String abbrDay = EnvTool.getStr(request, "day", "");
%>
	<form>

		<input type="hidden" name="Action" value="addSchedule">
		<input type="hidden" name="gid" value="<%= GidUUID %>">
		<input type="hidden" name="prmSchedTimeDay" value="<%= abbrDay %>">

		<div class="DS-padding-lfrg-8px DS-padding-updn-4px DS-back-gray">
			<div class="DS-text-big DS-text-black">Add a new schedule time</div>
		</div>

		<table class="TableSpacing_0px DS-full-width">

			<tr class="DS-border-full DS-back-lightgray">
				<td class="DS-padding-8px DS-text-black DS-text-italic DS-border-rg" align="center">Weekday</td>
				<td class="DS-padding-8px DS-text-black DS-text-italic DS-border-rg" align="center">Start Time</td>
				<td class="DS-padding-8px DS-text-black DS-text-italic" align="center">End Time</td>
			</tr>

			<tr class="DS-border-full DS-back-white">
				<td class="DS-padding-8px DS-border-rg">
					<% for (int i=0; i<Schedule.DAYS_NO; i++) { %>
						<% if (abbrDay.equals(Schedule.DayNameAbbr[i])) { %>
							<div class="DS-text-bold" align="center"><%= Schedule.DayNameFull[i] %></div>
						<% } %>
					<% } %>
				</td>
				<td class="DS-padding-8px DS-border-rg">
					<input name="prmSchedTimeMin" class="DS-input-textbox-compact DS-text-fixed" type="time" value="00:00">
				</td>
				<td class="DS-padding-8px">
					<input name="prmSchedTimeMax" class="DS-input-textbox-compact DS-text-fixed" type="time" value="23:59">
				</td>
			</tr>

			<tr class="DS-border-full DS-back-gray">
				<td class="DS-padding-8px" align="left">
					<%= MdcTool.Dialog.BtnDismiss(
						"btnDismiss",
						"Cancel",
						true,
						"",
						""
					) %>
				</td>
				<td class="DS-padding-8px" ColSpan="2" align="right">
					<%= MdcTool.Button.SubmitTextIconOutlinedClass(
						"add_circle_outline",
						"&nbsp;Add",
						null,
						"DS-text-green",
						"DS-text-green",
						"Save this schedule"
					) %>
				</td>
			</tr>

		</table>

	</form>