<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*"
	import="java.time.*"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wtlib.auth.*"
	import="net.danisoft.wazetools.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%!
	private static final String PAGE_Title = AppCfg.getAppName() + " Queries";
	private static final String PAGE_Keywords = "";
	private static final String PAGE_Description = AppCfg.getAppAbstract();

	/**
	 * Draw a box around a string (red, integer)
	 */
	private static String _boxedKo(int value) {
		return(
			"<b class=\"DS-text-white DS-back-Crimson DS-padding-lfrg-4px DS-padding-updn-2px DS-border-full DS-border-round\">" +
				value +
			"</b>"
		);
	}

	/**
	 * Clean quotes
	 */
	String cleanQuotes(String text) {
		return(text
			.replace("'", "&lsquo;")
			.replace("\"", "&ldquo;")
			.trim()
		);
	}

	/**
	 * Sort Order
	 */
	String getSortOrder(QryFilterOrder qryFilterOrder) {

		String Result = "";

		if (qryFilterOrder.equals(QryFilterOrder.LOCATION)) { Result = "GRP_Country, GRP_State, GRP_City, GRP_Description";	} else
		if (qryFilterOrder.equals(QryFilterOrder.FRST_UPD)) { Result = "GRP_CreatedAt, GRP_CreatedBy, GRP_Description";		} else
		if (qryFilterOrder.equals(QryFilterOrder.LAST_UPD)) { Result = "GRP_UpdatedAt, GRP_UpdatedBy, GRP_Description";		} else
		if (qryFilterOrder.equals(QryFilterOrder.LAST_EDT)) { Result = "GRP_UpdatedBy, GRP_UpdatedAt, GRP_Description";		}

		return(Result);
	}

	/**
	 * Header fields
	 * Description starts with a "*" if the field must be set to "nowrap"
	 */
	String[] getColHead(QryFilterOrder qryFilterOrder) {

		String Result[] = null;

		// "*" -> nowrap
		String Location		= "<div class=\"DS-text-centered\">*Location</div>";
		String SegStr		= "<div class=\"DS-text-centered\">*Seg/Str</div>";
		String Description	= "<div class=\"DS-text-centered\">Description</div>";
		String MteDescr		= "<div class=\"DS-text-centered\">MTE</div>";
		String PeriodMin	= "<div class=\"DS-text-centered\">Closure Start</div>";
		String PeriodMax	= "<div class=\"DS-text-centered\">Closure Stop</div>";
		String DelAfterExp	= "<div class=\"DS-text-centered\">On Expire</div>";
		String CreatedBy	= "<div class=\"DS-text-centered\">*Created</div>";
		String UpdatedBy	= "<div class=\"DS-text-centered\">*Last Edit</div>";

		if (qryFilterOrder.equals(QryFilterOrder.LOCATION)) { Result = new String[] { Location, SegStr, Description, MteDescr, PeriodMin, PeriodMax, DelAfterExp, CreatedBy, UpdatedBy }; } else
		if (qryFilterOrder.equals(QryFilterOrder.FRST_UPD)) { Result = new String[] { CreatedBy, Description, MteDescr, Location, SegStr, PeriodMin, PeriodMax, DelAfterExp, UpdatedBy }; } else
		if (qryFilterOrder.equals(QryFilterOrder.LAST_UPD)) { Result = new String[] { UpdatedBy, Description, MteDescr, Location, SegStr, PeriodMin, PeriodMax, DelAfterExp, CreatedBy }; } else
		if (qryFilterOrder.equals(QryFilterOrder.LAST_EDT)) { Result = new String[] { UpdatedBy, CreatedBy, Description, MteDescr, Location, SegStr, PeriodMin, PeriodMax, DelAfterExp }; }

		return(Result);
	}

	/**
	 * Details fields
	 */
	String[] getColBody(Group.Data grpData, int SegNo, int StrNo, Vector<String> vecStreets, QryFilterOrder qryFilterOrder, String Caller) {

		String streetList = "";
		boolean SomeStreetNamesMissing = false;

		for (String streetName : vecStreets) {

			streetList +=
				"&nbsp;&#9205;&nbsp;" +
				"[" + (grpData.getCity().trim().equals("") ? "<i>no city</i>" : cleanQuotes(grpData.getCity())) + "]" +
				"&nbsp;" +
				(streetName.trim().equals("") ? "<i>[no street name]</i>" : cleanQuotes(streetName)) +
				"<br>"
			;

			if (streetName.trim().equals(""))
				SomeStreetNamesMissing = true;
		}

		String streetListDialog = "onClick=\"ShowDialog_OK('" + cleanQuotes(grpData.getDescription()) + "&nbsp;', '" +
			"<div>Total Segments: <b>" + SegNo + "</b></div><br>" +
			"<div>Total Streets: <b>" + StrNo + "</b>" + (SomeStreetNamesMissing ? "&nbsp;&#129054;&nbsp;&#128680;&nbsp;<small><i>[Street Name Warning]</i></small>" : "") + "</div>" +
			"<div>" + streetList + "</div>" +
		"', 'OK');\"";

		String Location = cleanQuotes(grpData.getCountry()) +
			(grpData.getState().trim().equals("") ? "" : " / " + cleanQuotes(grpData.getState())) +
			(grpData.getCity().trim().equals("") ? "" : " / " + cleanQuotes(grpData.getCity()))
		;

		String SegStr =
			"<div class=\"DS-text-fixed-compact DS-text-centered DS-cursor-help\"" + streetListDialog + " title=\"" + (SomeStreetNamesMissing ? "WARNING: Some streets do not have street names" : "Click for road details") + "\">" +
				SegNo + "&nbsp;/&nbsp;" + 
				(SomeStreetNamesMissing
					? _boxedKo(StrNo)
					: StrNo
				) +
			"</div>"
		;

		String PeriodMin =
			"<div class=\"DS-text-fixed-compact DS-text-centered\">" +
				FmtTool.fmtDate(FmtTool.scnDateSqlStyle(grpData.getPeriod().getJSONObject("min").getString("date"))) + "<br>" +
				grpData.getPeriod().getJSONObject("min").getString("time") +
			"</div>"
		;

		String PeriodMax =
			"<div class=\"DS-text-fixed-compact DS-text-centered\">" +
				FmtTool.fmtDate(FmtTool.scnDateSqlStyle(grpData.getPeriod().getJSONObject("max").getString("date"))) + "<br>" +
				grpData.getPeriod().getJSONObject("max").getString("time") +
			"</div>"
		;

		String DelAfterExp =
			"<div class=\"DS-text-centered\">" +
				"<span class=\"" + (grpData.isDelAfter() ? "DS-back-PapayaWhip" : "DS-back-gray") + " DS-padding-updn-1px DS-padding-lfrg-8px DS-border-full DS-border-round\">" +
					"<span class=\"DS-text-bold " + (grpData.isDelAfter() ? "DS-text-Crimson" : "DS-text-gray") + " DS-text-fixed-compact\">" +
						(grpData.isDelAfter() ? "DELETE" : "PAUSE") +
					"</span>" +
				"</span>" +
			"</div>"
		;

		String Description =
			"<a href=\"../manage/gid_edit.jsp?caller=" + Caller + "&gid=" + grpData.getID() + "\">" +
				(grpData.getDescription().trim().equals("")
					? "<span class=\"DS-text-disabled DS-text-italic\">[No Data]</span>"
					: cleanQuotes(grpData.getDescription())
				) +
			"</a>"
		;

		String MteDescr =
			(grpData.getMteDescr().equals("")
				? "<span class=\"DS-text-disabled DS-text-italic\">[No Data]</span>"
				: cleanQuotes(grpData.getMteDescr())
			)
		;

		String CreatedBy =
			"<div class=\"DS-text-fixed-compact DS-text-centered\">" +
				(grpData.getCreatedAt().equals(FmtTool.DATEZERO)
					? ""
					: FmtTool.fmtDateTimeNoSecs(grpData.getCreatedAt())
				) +
			"</div>" +
			"<div class=\"DS-text-gray DS-text-centered\">" +
				(grpData.getCreatedBy().trim().equals("")
					? ""
					: "by " + cleanQuotes(grpData.getCreatedBy())
				) +
			"</div>"
		;

		String UpdatedBy =
			"<div class=\"DS-text-fixed-compact DS-text-centered\">" +
				(grpData.getUpdatedAt().equals(FmtTool.DATEZERO)
					? ""
					: FmtTool.fmtDateTimeNoSecs(grpData.getUpdatedAt())
				) +
			"</div>" +
			"<div class=\"DS-text-gray DS-text-centered\">" +
				(grpData.getUpdatedBy().trim().equals("")
					? ""
					: "by " + cleanQuotes(grpData.getUpdatedBy())
				) +
			"</div>"
		;

		//
		// Compose
		//

		String Result[] = null;

		if (qryFilterOrder.equals(QryFilterOrder.LOCATION)) { Result = new String[] { Location, SegStr, Description, MteDescr, PeriodMin, PeriodMax, DelAfterExp, CreatedBy, UpdatedBy }; } else
		if (qryFilterOrder.equals(QryFilterOrder.FRST_UPD)) { Result = new String[] { CreatedBy, Description, MteDescr, Location, SegStr, PeriodMin, PeriodMax, DelAfterExp, UpdatedBy }; } else
		if (qryFilterOrder.equals(QryFilterOrder.LAST_UPD)) { Result = new String[] { UpdatedBy, Description, MteDescr, Location, SegStr, PeriodMin, PeriodMax, DelAfterExp, CreatedBy }; } else
		if (qryFilterOrder.equals(QryFilterOrder.LAST_EDT)) { Result = new String[] { UpdatedBy, CreatedBy, Description, MteDescr, Location, SegStr, PeriodMin, PeriodMax, DelAfterExp }; }

		return(Result);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// DATE / TIME RANGE
	//

	private static String getLastYearMin()	{ return(Year.now().minusYears(1).toString() +	"-01-01 00:00:00"); }
	private static String getLastYearMax()	{ return(Year.now().minusYears(1).toString() +	"-12-31 23:59:59"); }
	private static String getThisYearMin()	{ return(Year.now().toString() +				"-01-01 00:00:00"); }
	private static String getThisYearMax()	{ return(Year.now().toString() +				"-12-31 23:59:59"); }
	private static String getNextYearMin()	{ return(Year.now().plusYears(1).toString() +	"-01-01 00:00:00"); }
	private static String getNextYearMax()	{ return(Year.now().plusYears(1).toString() +	"-12-31 23:59:59"); }

	private static String getLastMonthMin()	{ return(YearMonth.now().minusMonths(1).atDay(1).toString() +		" 00:00:00"); }
	private static String getLastMonthMax()	{ return(YearMonth.now().minusMonths(1).atEndOfMonth().toString() +	" 23:59:59"); }
	private static String getThisMonthMin()	{ return(YearMonth.now().atDay(1).toString() +						" 00:00:00"); }
	private static String getThisMonthMax()	{ return(YearMonth.now().atEndOfMonth().toString() +				" 23:59:59"); }
	private static String getNextMonthMin()	{ return(YearMonth.now().plusMonths(1).atDay(1).toString() +		" 00:00:00"); }
	private static String getNextMonthMax()	{ return(YearMonth.now().plusMonths(1).atEndOfMonth().toString() +	" 23:59:59"); }
%>
<!DOCTYPE html>
<html>
<head>

	<jsp:include page="../_common/head.jsp">
		<jsp:param name="PAGE_Title" value="<%= PAGE_Title %>"/>
		<jsp:param name="PAGE_Keywords" value="<%= PAGE_Keywords %>"/>
		<jsp:param name="PAGE_Description" value="<%= PAGE_Description %>"/>
	</jsp:include>

	<script>

		/**
		 * Show Config Dialog
		 */
		function showConfig(countryList) {

			$.ajax({

				cache: false,
				type: 'GET',
				url: './_config_tbl_dlg.jsp',

				data: {
					cl: countryList
				},

				success: function(data) {
					ShowDialog_AJAX(data);
				},

				error: function (jqXHR, textStatus, errorThrown) {
					ShowDialog_OK('Config Error', jqXHR.responseText, "OK")
				}
			});
		}

	</script>

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

	CifpStatus cifpStatus = CifpStatus.getEnum(EnvTool.getStr(request, "type", ""));

	try {

		DB = new Database();
		Group GRP = new Group(DB.getConnection());
		Segment SEG = new Segment(DB.getConnection());
		User USR = new User(DB.getConnection());
		QryFilter QRF = new QryFilter();

		QryFilterOrder qryFilterOrder = QRF.getOrderFromCookie(request.getCookies());
		QryFilterPeriod qryFilterPeriod = QRF.getPeriodFromCookie(request.getCookies());
		QryFilterEditor qryFilterEditor = QRF.getEditorFromCookie(request.getCookies());
		String qryEditorName = QRF.getEditorNameFromCookie(request.getCookies());

		// Countries Filter

		String countryFilter = "";
		String scriptCountryList = "";

		User.Data usrData = USR.Read(SysTool.getCurrentUser(request));
		Vector<String> vecCountries = usrData.getWazerConfig().getCifp().getActiveCountryCodes();

		for (String usrCountry : vecCountries) {
			countryFilter += (countryFilter.equals("") ? "" : " OR ") + "GRP_CtryIso = '" + usrCountry + "'";
			scriptCountryList += (scriptCountryList.equals("") ? "" : SysTool.getDelimiter()) + usrCountry;
		}
%>
		<div class="DS-padding-updn-8px">
			<div class="mdc-layout-grid__inner">
				<div class="<%= MdcTool.Layout.Cell(10, 6, 3) %> DS-grid-middle-left">
					<span class="DS-text-title-shadow"><%= PAGE_Title %></span>
					<span class="DS-padding-top-8px DS-padding-lf48px DS-text-compact DS-text-italic DS-text-gray">
						Type: <b><%= cifpStatus.getDescr() %></b> -
						Order: <b><%= qryFilterOrder.getDesc() %></b> -
						Period: <b><%= qryFilterPeriod.getDesc() %></b> -
						Editor: <b><%= qryFilterEditor.equals(QryFilterEditor.SEL_EDIT) ? qryEditorName : qryFilterEditor.getDesc() %></b>
					</span>
				</div>
				<div class="<%= MdcTool.Layout.Cell(2, 2, 1) %> DS-grid-middle-right">
					<%= MdcTool.Button.IconOutlined(
						"settings",
						"onClick=\"showConfig('" + scriptCountryList  + "');\"",
						"Show options"
					) %>
				</div>
			</div>
		</div>
<%
		//
		// HEADER
		//

		String colHead[] = getColHead(qryFilterOrder);
%>
		<table class="TableSpacing_0px DS-full-width">

		<tr class="DS-back-gray DS-text-black">
			<% for (int i=0; i<colHead.length; i++) { %>
				<td class="DS-padding-lfrg-4px DS-padding-updn-2px DS-border-full"><%= colHead[i].replace("*", "") %></td>
			<% } %>
		</tr>
<%
		//
		// BODY
		//

		String colBody[] = null;

		// Status Filters
		
		String statusFilter = "";

		if (cifpStatus.equals(CifpStatus.ACTIVE)) { statusFilter = "GRP_isUnedited = 'N' AND GRP_isActive = 'Y' AND (" + countryFilter + ")";	} else
		if (cifpStatus.equals(CifpStatus.PAUSED)) { statusFilter = "GRP_isUnedited = 'N' AND GRP_isActive = 'N' AND (" + countryFilter + ")";	} else
		if (cifpStatus.equals(CifpStatus.ORPHAN)) { statusFilter = "GRP_isUnedited = 'Y' AND (" + countryFilter + ")";							} else
		{
			throw new Exception("Bad Report Type: '" + EnvTool.getStr(request, "type", "") + "'");
		}

		// Period Filters

		String periodFilterMin, periodFilterMax;

		if (qryFilterPeriod.equals(QryFilterPeriod.LAST_Y)) { periodFilterMin = getLastYearMin();  periodFilterMax = getLastYearMax();  } else
		if (qryFilterPeriod.equals(QryFilterPeriod.THIS_Y)) { periodFilterMin = getThisYearMin();  periodFilterMax = getThisYearMax();  } else
		if (qryFilterPeriod.equals(QryFilterPeriod.NEXT_Y)) { periodFilterMin = getNextYearMin();  periodFilterMax = getNextYearMax();  } else
		if (qryFilterPeriod.equals(QryFilterPeriod.LAST_M)) { periodFilterMin = getLastMonthMin(); periodFilterMax = getLastMonthMax(); } else
		if (qryFilterPeriod.equals(QryFilterPeriod.THIS_M)) { periodFilterMin = getThisMonthMin(); periodFilterMax = getThisMonthMax(); } else
		if (qryFilterPeriod.equals(QryFilterPeriod.NEXT_M)) { periodFilterMin = getNextMonthMin(); periodFilterMax = getNextMonthMax(); } else
		{
			periodFilterMin = FmtTool.fmtDateTimeSqlStyle(FmtTool.DATEZERO);
			periodFilterMax = FmtTool.fmtDateTimeSqlStyle(FmtTool.DATEMAXV);
		}

		// Editor Filters

		if (qryFilterEditor.equals(QryFilterEditor.SEL_EDIT))
			statusFilter += " AND (GRP_CreatedBy = '" + qryEditorName + "' OR GRP_UpdatedBy = '" + qryEditorName + "')";

		// Closures loop

		String trClass, trTitle;
		int SegNo = 0, StrNo = 0, closuresCount = 0;
		Vector<String> vecStreets = new Vector<String>();
		Vector<Group.Data> vecGrpData = GRP.getAll(statusFilter, getSortOrder(qryFilterOrder));

		for (Group.Data grpData : vecGrpData) {

			String PeriodMin = grpData.getPeriod().getJSONObject("min").getString("date") + " " + grpData.getPeriod().getJSONObject("min").getString("time") + ":00";
			String PeriodMax = grpData.getPeriod().getJSONObject("max").getString("date") + " " + grpData.getPeriod().getJSONObject("max").getString("time") + ":59";

			if (PeriodMin.compareTo(periodFilterMin) >= 0 && PeriodMax.compareTo(periodFilterMax) <= 0) {

				SegNo = SEG.countSegments(grpData.getID());
				StrNo = SEG.countStreets(grpData.getID());
				vecStreets = SEG.getStreets(grpData.getID());
				trClass = ((SegNo == 0 || StrNo == 0) ? "DS-text-darkred DS-back-pastel-red" : "DS-back-white");
				trTitle = ((SegNo == 0 || StrNo == 0) ? "No Segments/Streets found, closure cannot be processed. Delete it." : "");

				colBody = getColBody(grpData, SegNo, StrNo, vecStreets, qryFilterOrder, cifpStatus.toString());
%>
				<tr class="DS-text-compact <%= trClass %>" title="<%= trTitle %>">
					<% for (int i=0; i<colHead.length; i++) { %>
						<td <%= colHead[i].contains("*") ? "nowrap" : "" %> class="DS-padding-lfrg-4px DS-padding-updn-2px DS-border-full">
							<%= colBody[i] %>
						</td>
					<% } %>
				</tr>
<%
				closuresCount++;
			}
		}
		
		if (closuresCount == 0) {
%>
		<tr class="DS-back-white DS-text-compact">
			<td class="DS-padding-16px DS-border-full" ColSpan="<%= colHead.length %>">
				<div class="DS-text-exception" align="center">Sorry, no entries found</div>
			</td>
		</tr>
<%
		}

		//
		// FOOTER
		//
%>
		<tr class="DS-back-gray">
			<td class="DS-padding-lfrg-4px DS-padding-updn-2px DS-border-full" ColSpan="<%= colHead.length %>" align="center">
				<div class="DS-text-large DS-text-italic">A total of <b><%= closuresCount %></b> <%= cifpStatus.getDescr() %> Closures were found</div>
			</td>
		</tr>

		</table>
<%
		//
		// BOTTOM BUTTONS
		//
%>
		<div class="DS-card-foot">
			<%= MdcTool.Button.BackTextIcon("Back", "../query/") %>
		</div>
<%
	} catch (Exception e) {
		MSG.setSlideText("Query Fatal Error", e.toString());
		RedirectTo = "../query/";
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
