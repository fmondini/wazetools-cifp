<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wtlib.auth.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%!
	private static final String colLf = "DS-padding-lfrg-4px DS-padding-updn-2px DS-text-black DS-text-bold DS-text-centered DS-back-lightgray DS-border-rg";
	private static final String colRg = "DS-padding-lfrg-4px DS-padding-updn-2px DS-back-white";
%>
	<script>

		/**
		 * Show/Hide Editor Name DIV
		 */
		function RefreshEditorNameDiv() {

			const comboEditor = document.getElementById('selEditor');
			const SelectedValue = comboEditor.options[comboEditor.selectedIndex].value;
			console.log('Value: %o', SelectedValue);

			if (SelectedValue == '<%= QryFilterEditor.SEL_EDIT %>')
				$(spanEditorName).show();
			else
				$(spanEditorName).hide();
		}

		/**
		 * Save selected options
		 */
		function SaveOptions() {

			const comboOrder = document.getElementById('selOrder');
			const comboPeriod = document.getElementById('selPeriod');
			const comboEditor = document.getElementById('selEditor');
			const comboEditorName = document.getElementById('selEditorName');
			
			const comboOrderValue = comboOrder.options[comboOrder.selectedIndex].value;
			const comboPeriodValue = comboPeriod.options[comboPeriod.selectedIndex].value;
			const comboEditorValue = comboEditor.options[comboEditor.selectedIndex].value;

			var comboEditorNameValue = '';

			if (comboEditorValue == '<%= QryFilterEditor.SEL_EDIT %>')
				comboEditorNameValue = comboEditorName.options[comboEditorName.selectedIndex].value;

			console.log('comboOrderValue.....: %o', comboOrderValue);
			console.log('comboPeriodValue....: %o', comboPeriodValue);
			console.log('comboEditorValue....: %o', comboEditorValue);
			console.log('comboEditorNameValue: %o', comboEditorNameValue);

			setCookie('<%= CifpCookie.QRY_ORDER.getName() %>', comboOrderValue);
			setCookie('<%= CifpCookie.QRY_PERIOD.getName() %>', comboPeriodValue);
			setCookie('<%= CifpCookie.QRY_EDITOR.getName() %>', comboEditorValue);
			setCookie('<%= CifpCookie.QRY_ED_NAME.getName() %>', comboEditorNameValue);

			window.location.reload();
		}

	</script>
<%
	Database DB = null;

	try {

		String userCountryList[] = EnvTool.getStr(request, "cl", "").split(SysTool.getDelimiter());

		DB = new Database();
		User USR = new User(DB.getConnection());
		GeoIso GEO = new GeoIso(DB.getConnection());
		QryFilter QRF = new QryFilter();

		User.Data usrData = USR.Read(SysTool.getCurrentUser(request));

		QryFilterOrder qryFilterOrder = QRF.getOrderFromCookie(request.getCookies());
		QryFilterPeriod qryFilterPeriod = QRF.getPeriodFromCookie(request.getCookies());
		QryFilterEditor qryFilterEditor = QRF.getEditorFromCookie(request.getCookies());
		String qryEditorName = QRF.getEditorNameFromCookie(request.getCookies());
%>
		<div class="DS-padding-lfrg-8px DS-padding-updn-4px DS-back-gray">
			<div class="DS-text-big DS-text-black">Query Filter</div>
		</div>

		<table class="TableSpacing_0px DS-full-width">

			<tr class="DS-border-full">
				<td class="<%= colLf %>">Countries</td>
				<td class="<%= colRg %>">
					<% for (int i=0; i<userCountryList.length; i++) { %>
					<div>
						<span class="DS-text-fixed-compact DS-text-bold">[<%= userCountryList[i] %>]</span>
						<span class="DS-text-compact"><%= GEO.getFullDesc(userCountryList[i]) %></span>
					</div>
					<% } %>
				</td>
			</tr>

			<tr class="DS-border-full">
				<td class="<%= colLf %>">Order</td>
				<td class="<%= colRg %>"><select id="selOrder" name="selOrder" class="DS-input-textbox">
					<%= QryFilterOrder.getCombo(qryFilterOrder.toString()) %>
				</select></td>
			</tr>

			<tr class="DS-border-full">
				<td class="<%= colLf %>">Period</td>
				<td class="<%= colRg %>"><select id="selPeriod" name="selPeriod" class="DS-input-textbox">
					<%= QryFilterPeriod.getCombo(qryFilterPeriod.toString()) %>
				</select></td>
			</tr>

			<tr class="DS-border-full">
				<td class="<%= colLf %>">Editor</td>
				<td class="<%= colRg %>">
					<select id="selEditor" name="selEditor" class="DS-input-textbox" onChange="RefreshEditorNameDiv();">
						<%= QryFilterEditor.getCombo(qryFilterEditor.toString()) %>
					</select>
					<span id="spanEditorName" class="DS-hidden">
						<select id="selEditorName" name="selEditorName" class="DS-input-textbox">
							<%= QRF.getEditorsCombo(qryEditorName, usrData.getWazerConfig().getCifp().getActiveCountryCodes()) %>
						</select>
					</span>
				</td>
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
						"onClick=\"SaveOptions();\"", // additionalData,
						"Save Selected Options"
					) %>
				</td>
			</tr>

		</table>

		<script>
			RefreshEditorNameDiv();
		</script>
<%
	} catch (Exception e) {
		
	}

	if (DB != null)
		DB.destroy();
%>
