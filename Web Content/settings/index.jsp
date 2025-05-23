<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wtlib.auth.*"
	import="net.danisoft.dslib.cifp.*"
	import="net.danisoft.wazetools.*"
%>
<%!
	private static final String PAGE_Title = AppCfg.getAppName() + " Settings";
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

	String Action = EnvTool.getStr(request, "Action", "");

	try {

		DB = new Database();
		User USR = new User(DB.getConnection());
		GeoIso GEO = new GeoIso(DB.getConnection());

		User.Data usrData = USR.Read(SysTool.getCurrentUser(request));

		WazerConfig wazerConfig = usrData.getWazerConfig();

		if (Action.equals("")) {

			////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			// EDIT
			//

			final String ICON_COLOR = "LightSlateGrey";
			final String TEXT_COLOR = "LightSlateGrey";
%>
			<form>

				<input type="hidden" name="Action" value="update">

				<div class="DS-padding-top-8px DS-padding-bottom-16px">
					<div class="DS-text-title-shadow"><%= PAGE_Title %></div>
				</div>

				<div class="mdc-layout-grid__inner DS-grid-rowgap-8px">
<%
					//
					// LINE #1
					//
%>
					<div class="<%= MdcTool.Layout.Cell(6, 8, 4) %>">
						<div class="mdc-layout-grid__inner DS-grid-colgap-0px DS-grid-rowgap-0px">
							<div class="<%= MdcTool.Layout.Cell(2, 2, 1) %> DS-gradient-gray-up DS-padding-8px" align="center">
								<div><%= IcoTool.Symbol.RndExtn("security", true, "64px", ICON_COLOR, "", "") %></div>
								<div class="DS-text-big DS-text-<%= TEXT_COLOR %> DS-text-bold">AUTH</div>
							</div>
							<div class="<%= MdcTool.Layout.Cell(10, 6, 3) %> DS-gradient-lightgray-up DS-padding-8px">
								<div class="DS-padding-top-0px">
									<div class="DS-text-extra-large">Access rights</div>
								</div>
								<div class="DS-padding-bottom-8px">
									<div class="DS-text-italic DS-text-gray">Summary of your current access level</div>
								</div>
								<div class="DS-padding-bottom-8px">
									<div class="DS-text-italic DS-text-brown">To change your access level, contact your country admin</div>
								</div>
								<div class="DS-padding-bottom-4px">
									<div class="DS-padding-bottom-4px">
										<div class="DS-text-italic DS-text-gray">Your API-KEY</div>
										<div class="DS-text-fixed DS-text-bold"><%= usrData.getWazerConfig().getCifp().getApiKey() %></div>
									</div>
									<div class="DS-padding-bottom-2px">
										<div class="DS-text-italic DS-text-gray">Your Editing Level</div>
										<div class="DS-text-italic"><%= usrData.getWazerConfig().getCifp().isAdmin()
											? "Authorized to handle all closures in enabled countries (ADMIN rights)"
											: "Authorized to create/edit/delete only its own closures"
										%></div>
									</div>
								</div>
							</div>
						</div>
					</div>

					<div class="<%= MdcTool.Layout.Cell(6, 8, 4) %>">
						<div class="mdc-layout-grid__inner DS-grid-colgap-0px DS-grid-rowgap-0px">
							<div class="<%= MdcTool.Layout.Cell(2, 2, 1) %> DS-gradient-gray-up DS-padding-8px" align="center">
								<div><%= IcoTool.Symbol.RndExtn("public", true, "64px", ICON_COLOR, "", "") %></div>
								<div class="DS-text-big DS-text-<%= TEXT_COLOR %> DS-text-bold">GEO</div>
							</div>
							<div class="<%= MdcTool.Layout.Cell(10, 6, 3) %> DS-gradient-lightgray-up DS-padding-8px">
								<div class="DS-padding-top-0px">
									<div class="DS-text-extra-large">Enabled Countries</div>
								</div>
								<div class="DS-padding-bottom-8px">
									<div class="DS-text-italic DS-text-gray">Countries where closures can be managed</div>
								</div>
								<div class="DS-padding-bottom-8px">
									<div class="DS-text-italic DS-text-brown">To change the enabled countries, contact your country admin</div>
								</div>
								<div class="DS-padding-bottom-4px">
									<% for (String ctryCode : usrData.getWazerConfig().getCifp().getActiveCountryCodes()) { %>
									<div class="DS-padding-bottom-2px DS-text-black">
										<span class="DS-text-fixed-compact">[<b><%= ctryCode %></b>]</span>
										<span class="DS-text-italic"><%= GEO.getFullDesc(ctryCode) %></span>
									</div>
									<% } %>
								</div>
							</div>
						</div>
					</div>
<%
					//
					// LINE #2
					//
%>
					<div class="<%= MdcTool.Layout.Cell(6, 8, 4) %>">
						<div class="mdc-layout-grid__inner DS-grid-colgap-0px DS-grid-rowgap-0px">
							<div class="<%= MdcTool.Layout.Cell(2, 2, 1) %> DS-gradient-gray-up DS-padding-8px" align="center">
								<div><%= IcoTool.Symbol.RndExtn("mail", true, "64px", ICON_COLOR, "", "") %></div>
								<div class="DS-text-big DS-text-<%= TEXT_COLOR %> DS-text-bold">MAIL</div>
							</div>
							<div class="<%= MdcTool.Layout.Cell(10, 6, 3) %> DS-gradient-lightgray-up DS-padding-8px">
								<div class="DS-padding-top-4px">
									<div class="DS-text-extra-large">Mail alerts</div>
								</div>
								<div class="DS-padding-bottom-4px">
									<div class="DS-text-italic DS-text-gray">Enabling sending emails during automatic processes</div>
								</div>
								<div class="DS-padding-updn-8px">
									<div class="DS-text-italic DS-text-brown">All mail will be sent to <b><%= usrData.getMail() %></b></div>
								</div>
								<div class="DS-padding-0px">
									<%= MdcTool.Check.Box(
										"CIFP_opt_EXPM",
										"I would like to receive an email <b>before</b> the closure expire <span class=\"DS-text-compact DS-text-darkred\"><b><sup>(*)</sup></b></span>",
										"Y",
										(wazerConfig.getCifp().isExpireMail() ? MdcTool.Check.Status.CHECKED : MdcTool.Check.Status.UNCHECKED),
										""
									) %>
								</div>
								<div class="DS-padding-0px">
									<%= MdcTool.Check.Box(
										"CIFP_opt_EXPMD",
										"I would like to receive an email <b>when</b> the closure expires <span class=\"DS-text-compact DS-text-darkred\"><b><sup>(*)</sup></b></span>",
										"Y",
										(wazerConfig.getCifp().isExpiredMail() ? MdcTool.Check.Status.CHECKED : MdcTool.Check.Status.UNCHECKED),
										""
									) %>
								</div>
								<div class="DS-padding-top-8px DS-text-compact">
									<span class="DS-text-darkred"><b>(*)</b></span>
									<span class="DS-text-italic">Experimental feature, use at your own risk</span>
								</div>
							</div>
						</div>
					</div>

					<div class="<%= MdcTool.Layout.Cell(6, 8, 4) %>">
						<div class="mdc-layout-grid__inner DS-grid-colgap-0px DS-grid-rowgap-0px">
							<div class="<%= MdcTool.Layout.Cell(2, 2, 1) %> DS-gradient-gray-up DS-padding-8px" align="center">
								<div><%= IcoTool.Symbol.RndExtn("chat", true, "64px", ICON_COLOR, "", "") %></div>
								<div class="DS-text-big DS-text-<%= TEXT_COLOR %> DS-text-bold">SLACK</div>
							</div>
							<div class="<%= MdcTool.Layout.Cell(10, 6, 3) %> DS-gradient-lightgray-up DS-padding-8px">
								<div class="DS-padding-top-0px">
									<div class="DS-text-extra-large">Slack alerts</div>
								</div>
								<div class="DS-padding-bottom-4px">
									<div class="DS-text-italic DS-text-gray">Enabling Slack DMs during automated processes</div>
								</div>
								<div class="DS-padding-updn-8px">
									<div class="DS-text-italic DS-text-brown">All DMs will be sent to SlackID <b><%= usrData.getSlackID() %></b></div>
								</div>
								<div class="DS-padding-0px">
									<%= MdcTool.Check.Box(
										"CIFP_opt_EXPS",
										"<span class=\"DS-text-disabled\">I would like to receive a DM <b>before</b> the closure expire <span class=\"DS-text-compact DS-text-darkred\"><b><sup>(*)</sup></b></span></span>",
										"Y",
										MdcTool.Check.Status.UNCHECKED,
										"disabled"
									) %>
								</div>
								<div class="DS-padding-0px">
									<%= MdcTool.Check.Box(
										"CIFP_opt_EXPSD",
										"<span class=\"DS-text-disabled\">I would like to receive a DM <b>when</b> the closure expire <span class=\"DS-text-compact DS-text-darkred\"><b><sup>(*)</sup></b></span></span>",
										"Y",
										MdcTool.Check.Status.UNCHECKED,
										"disabled"
									) %>
								</div>
								<div class="DS-padding-top-8px DS-text-compact">
									<span class="DS-text-darkred"><b>(*)</b></span>
									<span class="DS-text-italic">Not yet available, feature expected in a future release</span>
								</div>
							</div>
						</div>
					</div>

				</div>
<%
				//
				// BOTTOM BUTTONS
				//
%>
				<div class="DS-padding-updn-8px">
					<div class="mdc-layout-grid__inner">
						<div class="<%= MdcTool.Layout.Cell(6, 4, 2) %>" align="left">
							<%= MdcTool.Button.BackTextIcon("Back", "../query/") %>
						</div>
						<div class="<%= MdcTool.Layout.Cell(6, 4, 2) %>" align="right">
							<%= MdcTool.Button.SubmitTextIconClass(
								"save",
								"&nbsp;Save",
								null,
								"DS-text-lime",
								"DS-text-lime",
								"Save preferences"
							) %>
						</div>
					</div>
				</div>

			</form>
<%
		} else if (Action.equals("update")) {
			
			////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			// UPDATE
			//

			wazerConfig.getCifp().setIsExpireMail(EnvTool.getStr(request, "CIFP_opt_EXPM", "N").equals("Y"));
			wazerConfig.getCifp().setIsExpiredMail(EnvTool.getStr(request, "CIFP_opt_EXPMD", "N").equals("Y"));
			wazerConfig.getCifp().setIsExpireSlack(EnvTool.getStr(request, "CIFP_opt_EXPS", "N").equals("Y"));
			wazerConfig.getCifp().setIsExpiredSlack(EnvTool.getStr(request, "CIFP_opt_EXPSD", "N").equals("Y"));

			usrData.setWazerConfig(wazerConfig);
			USR.Update(SysTool.getCurrentUser(request), usrData);

			MSG.setSnackText("Preferences saved");
			RedirectTo = "../query/";

		} else
			throw new Exception("Bad Action: '" + Action + "'");

	} catch (Exception e) {
		MSG.setSlideText("Fatal Error", e.toString());
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
