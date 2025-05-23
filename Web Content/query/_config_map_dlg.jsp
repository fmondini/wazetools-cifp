<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%!
	private static final String COOKIE_NAME_TYPE = CifpCookie.MAP_VIEW_TYPE.getName();
	private static final String COOKIE_NAME_STATUS = CifpCookie.MAP_VIEW_STATUS.getName();

	private static final String colLf = "DS-padding-lfrg-4px DS-padding-updn-2px DS-text-black DS-text-bold DS-text-centered DS-back-lightgray DS-border-rg";
	private static final String colRg = "DS-padding-lfrg-4px DS-padding-updn-2px DS-back-white";
%>
	<script>
	
		/**
		 * Save selected view options
		 */
		function SaveViewOptions() {

			const comboType = document.getElementById('selType');
			const comboStatus = document.getElementById('selStatus');
			
			const comboTypeValue = comboType.options[comboType.selectedIndex].value;
			const comboStatusValue = comboStatus.options[comboStatus.selectedIndex].value;

			console.log('comboTypeValue..: %o', comboTypeValue);
			console.log('comboStatusValue: %o', comboStatusValue);

			setCookie('<%= COOKIE_NAME_TYPE %>', comboTypeValue);
			setCookie('<%= COOKIE_NAME_STATUS %>', comboStatusValue);

			window.location.reload();
		}
	</script>
<%
	String defaultType = "";
	String defaultStatus = "";

	Cookie[] cookies = request.getCookies();

	try {

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(COOKIE_NAME_TYPE))
				defaultType = cookie.getValue();
			if (cookie.getName().equals(COOKIE_NAME_STATUS))
				defaultStatus = cookie.getValue();
		}

	} catch (Exception e) {	}
%>
	<div class="DS-padding-lfrg-8px DS-padding-updn-4px DS-back-gray">
		<div class="DS-text-big DS-text-black">Map Filter</div>
	</div>

	<table class="TableSpacing_0px DS-full-width">

		<tr class="DS-border-full">
			<td class="<%= colLf %>">Closures Type</td>
			<td class="<%= colRg %>"><select id="selType" name="selType" class="DS-input-textbox">
				<%= CifpType.getCombo(defaultType, true) %>
			</select></td>
		</tr>

		<tr class="DS-border-full">
			<td class="<%= colLf %>">Closures Status</td>
			<td class="<%= colRg %>"><select id="selStatus" name="selStatus" class="DS-input-textbox">
				<%= CifpStatus.getCombo(defaultStatus, true) %>
			</select></td>
		</tr>

		<tr class="DS-border-full DS-back-gray">
			<td class="DS-padding-8px" align="left">
				<%= MdcTool.Dialog.BtnDismiss(
					"btnDismiss",
					"Cancel",
					true,
					"",
					"Don\'t save and exit"
				) %>
			</td>
			<td class="DS-padding-8px" align="right">
				<%= MdcTool.Dialog.BtnConfirm(
					"btnConfirm",
					"Save",
					true,
					"onClick=\"SaveViewOptions();\"", // additionalData,
					"Save Selected Options"
				) %>
			</td>
		</tr>

	</table>
