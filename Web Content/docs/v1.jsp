<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.*"
%>
<%!
	private static final String API_VERSION = "0.3.beta";

	private static final String PAGE_Title = AppCfg.getAppTag() + " API " + API_VERSION + " Reference";
	private static final String PAGE_Keywords = "";
	private static final String PAGE_Description = AppCfg.getAppAbstract();

	private static String htmlSection(String sectionText) {

		String sectionClass = (sectionText.contains(" OK")
			? "DS-text-green"
			: (sectionText.contains(" KO")
				? "DS-text-exception"
				: "DS-text-blue"
			)
		);

		return(
			"<div class=\"DS-padding-updn-8px\">" +
				"<div class=\"DS-text-huge " + sectionClass + "\">" + sectionText + "</div>" +
			"</div>"
		);
	}

	private static String htmlEndpoint(String method, String endpoint) {

		String methodClass = (method.equals("POST")
			? "DS-text-purple"
			: (method.equals("GET")
				? "DS-text-green"
				: "DS-text-red"
			)
		);

		return(
			"<div class=\"DS-padding-updn-8px\">" +
				"<div class=\"DS-padding-updn-8px\">" +
					"<span class=\"DS-text-fixed DS-padding-updn-4px DS-padding-lfrg-8px DS-back-gray DS-border-full\">" +
						"<b>[<span class=\"" + methodClass + "\">" + method + "</span>]</b> https://cifp.waze.tools<b>" + endpoint + "</b>" +
					"</span>" +
				"</div>" +
			"</div>"
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

		<div class="DS-padding-updn-8px">
			<div class="DS-text-title-shadow">API Reference - Version <%= API_VERSION %> -- <span class="DS-text-small DS-text-Crimson">WARNING: This document is DEPRECATED</span></div>
		</div>

		<div class="DS-card-body">
			<div class="mdc-tab-bar DS-back-gray" role="tablist">
				<div class="mdc-tab-scroller">
					<div class="mdc-tab-scroller__scroll-area">
						<div class="mdc-tab-scroller__scroll-content">
							<%= MdcTool.Tab.ElementIconText(0, "account_tree", "Flow Recap", "DS-text-large", true) %>
							<%= MdcTool.Tab.ElementIconText(1, "settings", "Config", "DS-text-large", false) %>
							<%= MdcTool.Tab.ElementIconText(2, "add_circle_outline", "Create", "DS-text-large", false) %>
							<%= MdcTool.Tab.ElementIconText(3, "published_with_changes", "Update", "DS-text-large", false) %>
							<%= MdcTool.Tab.ElementIconText(4, "stream", "JSON Feed", "DS-text-large", false) %>
							<%= MdcTool.Tab.ElementIconText(5, "local_library", "Reference", "DS-text-large", false) %>
						</div>
					</div>
				</div>
			</div>
		</div>
<%
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// TAB #0 - FLOW
		//
%>
		<div class="DS-tab-panel DS-tab-panel-active">
			<div class="DS-card-body" align="center">
				<div class="DS-text-title-shadow">Data Flow Example</div>
			</div>
			<div class="DS-card-body" align="center">
				<img class="<%= MdcTool.Elevation.Normal() %>" src="./data-flow.png">
			</div>
		</div>
<%
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// TAB #1 - CONFIG
		//
%>
		<div class="DS-tab-panel">

			<div class="DS-card-body">
				<div class="DS-text-title-shadow">Script Configuration</div>
			</div>

			<div>At this stage the script and WebApp user interfaces are only available in English.</div>
			<div>In a future expansion, messages will be localized and passed (already translated) to the script via this API.</div>
			<div>Maybe. :-)</div>

			<%= htmlSection("<b>Endpoint</b>") %>
			<%= htmlEndpoint("GET", "/api/getcfg") %>

			<%= htmlSection("<b>Response OK</b> (API Server → Script WME)") %>
			<pre class="DS-text-compact DS-padding-8px DS-back-lightgray DS-border-full">
{
  "rc": 200,
  "Icon": "data:image/png;base64,[BASE64_PNG_ICON_HERE]",
  "About": {
    "lblAppName": "Closure and Incidents Feed",
    "lblAuthors": "fmondini",
    "lblAuthUrl": "https://www.waze.com/en/user/editor/fmondini",
    "lblBugsRpt": "Report bugs and enhancement &lt;a href="BUG_REPORTS_URL" target="_blank"&gt;here&lt;/a&gt;"
  },
  "Messages": {
    "lblCreating": "Creating new Closures Group",
    "lblPleaseWait": "Please wait just a moment..."
  },
  "Results": {
    "lblNewHeadOK": "A new closure group has been created",
    "lblNewHeadKO": "New group NOT Created",
    "lblNewBodyOK": "Your new closures group has been created successfully",
    "lblNewBodyKO": "Error creating the new closure group",
    "lblNewNextOK": "Click on the new GroupID to configure closures&lt;br&gt;directly in the Waze.Tools CIFP Web App"
  },
  "Errors": {
    "lblHal9000": "I'm sorry, Dave... I'm afraid I can't do that.",
    "lblNoSelObj": "No segments selected&lt;br&gt;Please select one or more segment(s) to unlock",
    "lblSeeConsole": "See your browser console (F12) for more details (if available)"
  }
}</pre>
			<%= htmlSection("<b>Response KO</b> (API Server → Script WME)") %>
			<pre class="DS-text-compact DS-padding-8px DS-back-lightgray DS-border-full">
{
  "status": "KO",
  "error": "Custom Error Message Here"
}</pre>
		</div>
<%
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// TAB #2 - CREATE
		//
%>
		<div class="DS-tab-panel">

			<div class="DS-card-body">
				<div class="DS-text-title-shadow">New closure creation</div>
			</div>

			<%= htmlSection("<b>Endpoint</b>") %>
			<%= htmlEndpoint("GET", "/api/group/create") %>
			<div class="DS-padding-bottom-8px DS-text-italic"><b>NOTE</b>: This version uses GET to avoid AJAX JSONP limitations</div>

			<%= htmlSection("<b>Payload</b> (Script WME → API Server)") %>
			<pre class="DS-text-compact DS-padding-8px DS-back-lightgray DS-border-full">
{
  "source": "x.y.z.ga",
  "apiKey": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee", <span class="DS-text-serif DS-text-orange DS-text-italic">(Request a valid API-KEY from your country coordinator)</span>
  "user": "fmondini",
  "city": "",
  "state": "Lombardia",
  "country": "Italy",
  "ctryIso": "IT",
  "wmeEnv": "row",
  "wmeLat": 45.55656,
  "wmeLng": 9.9337,
  "wmeZoom": 16,
  "groupId": "", <span class="DS-text-serif DS-text-orange DS-text-italic">Always "" (empty) &rarr; insert new group id</span>
  "data": [
    {
      "street": "Via Vincenzo Gioberti",
      "polyline": "45.53802 9.93679, 45.53779 9.93676",
      "sid": 83344301
    }, {
      "street": "Via della Battaglia",
      "polyline": "45.53722 9.94097, 45.53726 9.94101, 45.53773 9.94126, 45.53782 9.94133, 45.53787 9.94142, 45.5379 9.9415, 45.5379 9.9421",
      "sid": 253320299
    }, {
      "street": "Via della Battaglia",
      "polyline": "45.53812 9.94304, 45.53818 9.94349",
      "sid": 397809899
    },
    {
       <span class="DS-text-serif DS-text-orange DS-text-italic">... repeats "n" times</span>
    }
  ]
}</pre>
			<%= htmlSection("<b>Response OK</b> (API Server → Script WME)") %>
			<pre class="DS-text-compact DS-padding-8px DS-back-lightgray DS-border-full">
{
  "status": "OK",
  "gid": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee", <span class="DS-text-serif DS-text-orange DS-text-italic">(New Group UUID)</span>
}</pre>
			<%= htmlSection("<b>Response KO</b> (API Server → Script WME)") %>
			<pre class="DS-text-compact DS-padding-8px DS-back-lightgray DS-border-full">
{
  "status": "KO",
  "error": "Custom Error Message Here"
}</pre>
		</div>
<%
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// TAB #3 - UPDATE
		//
%>
		<div class="DS-tab-panel">

			<div class="DS-card-body">
				<div class="DS-text-title-shadow">Existing closure update</div>
			</div>

			<%= htmlSection("<b>Endpoint</b>") %>
			<%= htmlEndpoint("GET", "/api/group/update") %>
			<div class="DS-padding-bottom-8px DS-text-italic"><b>NOTE</b>: This version uses GET to avoid AJAX JSONP limitations</div>

			<%= htmlSection("<b>Payload</b> (Script WME → API Server)") %>
			<pre class="DS-text-compact DS-padding-8px DS-back-lightgray DS-border-full">
{
  "source": "x.y.z.ga",
  "apiKey": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee", <span class="DS-text-serif DS-text-orange DS-text-italic">(Request a valid API-KEY from your country coordinator)</span>
  "user": "fmondini",
  "city": "",
  "state": "Lombardia",
  "country": "Italy",
  "ctryIso": "IT",
  "wmeEnv": "row",
  "wmeLat": 45.55656,
  "wmeLng": 9.9337,
  "wmeZoom": 16,
  "groupId": "xxxxxxxx-yyyy-zzzz-jjjj-kkkkkkkkkkkk", <span class="DS-text-serif DS-text-orange DS-text-italic">UUID of the group to change</span>
  "data": [
    {
      "street": "Via Vincenzo Gioberti",
      "polyline": "45.53802 9.93679, 45.53779 9.93676",
      "sid": 83344301
    }, {
      "street": "Via della Battaglia",
      "polyline": "45.53722 9.94097, 45.53726 9.94101, 45.53773 9.94126, 45.53782 9.94133, 45.53787 9.94142, 45.5379 9.9415, 45.5379 9.9421",
      "sid": 253320299
    }, {
      "street": "Via della Battaglia",
      "polyline": "45.53812 9.94304, 45.53818 9.94349",
      "sid": 397809899
    },
    {
       <span class="DS-text-serif DS-text-orange DS-text-italic">... repeats "n" times</span>
    }
  ]
}</pre>
			<%= htmlSection("<b>Response OK</b> (API Server → Script WME)") %>
			<pre class="DS-text-compact DS-padding-8px DS-back-lightgray DS-border-full">
{
  "status": "OK",
  "gid": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee" <span class="DS-text-serif DS-text-orange DS-text-italic">(Updated Group UUID)</span>
}</pre>
			<%= htmlSection("<b>Response KO</b> (API Server → Script WME)") %>
			<pre class="DS-text-compact DS-padding-8px DS-back-lightgray DS-border-full">
{
  "status": "KO",
  "error": "Custom Error Message Here"
}</pre>

		</div>

<%
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// TAB #4 - FEED
		//
%>
		<div class="DS-tab-panel">

			<div class="DS-card-body">
				<div class="DS-text-title-shadow">JSON Feed</div>
			</div>

			<%= htmlSection("<b>Endpoint</b>") %>
			<%= htmlEndpoint("GET", "/feed/get?c=<span class=\"DS-text-purple\">{COUNTRY_CODE}</span>") %>
			<div class="DS-padding-bottom-8px">Where <span class="DS-text-fixed DS-text-bold DS-text-purple">{COUNTRY_CODE}</span> is a
				<a href="https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3" target="_blank"><b>ISO 3166-1 alpha-3 code</b></a>.
			</div>

			<%= htmlSection("<b>Payload</b> (Waze → API Server)") %>
			<pre class="DS-text-compact DS-padding-8px DS-back-lightgray DS-border-full">NONE</pre>

			<%= htmlSection("<b>Response OK</b> (API Server → Waze)") %>
			<pre class="DS-text-compact DS-padding-8px DS-back-lightgray DS-border-full">
{
"incidents": [
  {
    "id": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
    "type": "ROAD_CLOSED",
    "subtype": "ROAD_CLOSED_CONSTRUCTION",
    "polyline": "45.56296 9.93761 45.56264 9.93756 45.56179 9.93752",
    "direction": "BOTH_DIRECTIONS",
    "street": "Via Sala",
    "starttime": "2024-04-01T00:00:00+0200",
    "endtime": "2024-04-30T23:59:59+0200",
    "description": "Lavori di ripristino su Via Sala",
    "creationtime": "2024-04-09T10:09:58+0200",
    "updatetime": "2024-04-09T17:57:44+0200",
    "reference": "Waze.Tools CIFP v.x.x - GID: xxxxxxxx-yyyy-zzzz-jjjj-kkkkkkkkkkkk",
    "schedule": { <span class="DS-text-serif DS-text-hotpink DS-text-italic">The "schedule" element only exists if a schedule has been set</span>
      "saturday": "00:00-23:59",
      "tuesday": "09:00-12:00",
      "thursday": "09:00-11:00,12:00-14:00"
    }
  },
  {
     <span class="DS-text-serif DS-text-orange DS-text-italic">... repeats "n" times</span>
  }
}</pre>
			<%= htmlSection("<b>Response KO</b> (API Server → Waze)") %>
			<pre class="DS-text-compact DS-padding-8px DS-back-lightgray DS-border-full">
{
  "error": "Custom Error Message Here"
}</pre>

		</div>

<%
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// TAB #5 - REFERENCE
		//
%>
		<div class="DS-tab-panel">
			<div class="DS-card-body">
				<div class="DS-text-title-shadow">Reference</div>
			</div>
			<div class="DS-card-body">
				For more information about the Waze CIFP Feed protocol, see the
				<a href="https://developers.google.com/waze/data-feed/overview" target="_blank">Waze CIFS Feed Reference</a>.
			</div>
		</div>


	</div>
	</div>
	</div>

	<jsp:include page="../_common/footer.jsp">
		<jsp:param name="RedirectTo" value=""/>
	</jsp:include>

</body>
</html>
