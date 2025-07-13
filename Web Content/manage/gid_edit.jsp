<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wtlib.auth.*"
	import="net.danisoft.wazetools.*"
	import="net.danisoft.wazetools.cifp.*"
	import="net.danisoft.wazetools.servlets.*"
%>
<%!
	private static final String PAGE_Title = "Editing a Closures Group";
	private static final String PAGE_Keywords = "";
	private static final String PAGE_Description = AppCfg.getAppAbstract();

	private static final String cellClassLF = "DS-padding-lfrg-4px DS-padding-updn-2px DS-text-black DS-text-bold DS-back-gray DS-border-rg";
	private static final String cellClassRG = "DS-padding-lfrg-4px DS-padding-updn-2px DS-back-white";
	private static final String cellClassST = "DS-padding-top-8px DS-text-big";
	private static final String cellClassBD = "DS-padding-top-4px DS-padding-bottom-8px";

	private static final String WME_START_URL = "https://www.waze.com/editor";
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

		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// +++ CLOSURE +++
		//

		/**
		 * Read Status Box
		 */
		function ReadStatus(GidID) {

			$.ajax({

				cache: false,
				type: 'GET',
				url: './_closure_status.jsp',
				data: { gid: GidID },

				beforeSend: function(XMLHttpRequest) {
					$('#divSummaryStatus').html('<img border="0" src="../images/ajax-loader.gif">');
				},

				success: function(data) {
					$('#divSummaryStatus').html(data);
				},

				error: function (jqXHR, textStatus, errorThrown) {
					$('#divSummaryStatus').html('Error');
				}
			});
		}

		/**
		 * Save Closure Field
		 */
		function SaveClosureField(obj, GidID) {

			const newObjValue = (obj.type == 'checkbox'
				? (obj.checked
					? 'Y'
					: 'N'
				)
				: obj.value
			);

			$.ajax({

				cache: false,
				type: 'POST',
				dataType: 'json',
				url: '../servlet/grpUpdFld',

				data: {
					gid: GidID,
					fld: obj.id,
					val: newObjValue
				},

				beforeSend: function(XMLHttpRequest) {
					$('#divSaveDataResultContent').html('');
					$('#divSaveDataResult').hide();
				},

				success: function(data) {

					const spanStyle = 'style="box-shadow: 3px 3px 7px #777777; border-radius: 5px; -moz-border-radius: 5px;"';

					const spanClass = 'class="DS-padding-updn-4px DS-padding-lfrg-8px ' + (data.status == 'OK'
						? 'DS-text-PaleGreen DS-back-DarkGreen'
						: 'DS-text-FireBrick DS-back-pastel-red'
					) + '"';

					const spanText = '' + (data.status == 'OK'
						? 'Field updated'
						: 'Error updating field: ' + data.error
					) + '';

					$('#divSaveDataResultContent').html('<span ' + spanClass + ' ' + spanStyle + '>' + spanText + '</span>');
				},

				error: function (jqXHR, textStatus, errorThrown) {

					const spanStyle = 'style="box-shadow: 3px 3px 7px #777777; border-radius: 5px; -moz-border-radius: 5px;"';
					const spanClass = 'class="DS-padding-updn-4px DS-padding-lfrg-8px DS-text-FireBrick DS-back-pastel-red';
					const spanText = 'Error ' + jqXHR.status + ' updating data: ' + textStatus;

					$('#divSaveDataResultContent').html('<span ' + spanClass + ' ' + spanStyle + '>' + spanText + '</span>');
				},

				complete: function(jqXHR, textStatus) {
					$('#divSaveDataResult').show().delay(1000).fadeOut(500);
					ReadStatus(GidID);
				}
			});
		}

		/**
		 * Check and Activate closure
		 */
		function ActivateClosure(GidID, EmptySegmentsCount) {

			const activateUrl = encodeURI('?Action=activateClosure&gid=' + GidID);

			if (EmptySegmentsCount > 0) {

				ShowDialog_YesNo(
					'Save &amp; Activate',
					'<div class="DS-padding-updn-4px DS-text-centered">' +
						'<div class="DS-text-big DS-text-bold DS-text-Crimson">WARNING</div>' +
						'<div class="DS-text-Crimson DS-padding-bottom-8px">This closure contains <b>' + EmptySegmentsCount + '</b> unnamed segment(s)</div>' +
						'<div class="DS-text-italic">To be accepted, each segment <b>must have</b> a street name</div>' +
						'<div class="DS-text-italic DS-padding-bottom-8px">because segments without a name are <b>not sent</b> to WME</div>' +
						'<div class="DS-text-large DS-text-bold DS-text-RoyalBlue">Should I go ahead and save anyway?</div>' +
					'</div>',
					'Yes, Activate!', activateUrl,
					'No, Stay here', ''
				);

			} else
				window.location.href = activateUrl;
		}

		/**
		 * Bookmark closure
		 */
		function SetBookmark(GidID, returnUrl) {
			ShowDialog_YesNo(
				'Bookmark This Closure',
				'<div class="DS-padding-updn-4px">Add a bookmark for this closure?</div>',
				'Yes', encodeURI('?Action=setBookmark&gid=' + GidID),
				'No', ''
			);
		}

		/**
		 * Delete closure
		 */
		function DelClosure(GidID, returnUrl) {
			ShowDialog_YesNo(
				'Delete This Closure',
				'<div class="DS-padding-updn-4px">Clicking the &quot;Yes&quot; button will <span class="DS-text-exception"><b>delete</b></span> this closure, and this action <b>cannot be undone</b>.</div>' +
				'<div class="DS-padding-updn-4px">Are you sure you want to continue?</div>',
				'Yes, delete it', encodeURI('?Action=delClosure&gid=' + GidID + '&NextUrl=' + returnUrl),
				'No, Keep it', ''
			);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// +++ SEGMENTS +++
		//

		/**
		 * Call WME with current segments
		 */
		function EditSegmentsInWME(GidID) {
			ShowDialog_YesNo(
				'Change / Replace Segments',
				'<div class="DS-padding-updn-4px">Clicking the &quot;Yes&quot; button will launch WME with the segments you want to edit selected.<br>Once the corrections are finished, save the new segments to update this closure.</div>' +
				'<div class="DS-padding-updn-4px">Are you sure you want to continue?</div>',
				'Yes, Take me to WME', encodeURI('?Action=editSegmentsInWME&gid=' + GidID),
				'No, Stay here', ''
			);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// +++ SUMMARY +++
		//

		/**
		 * Read Summary Data
		 */
		function ReadSummary(GidID) {

			$.ajax({

				cache: false,
				type: 'GET',
				url: './_summary_data.jsp',
				data: { gid: GidID },

				beforeSend: function(XMLHttpRequest) {
					$('#divSummaryData').html('<img border="0" src="../images/ajax-loader.gif">');
				},

				success: function(data) {
					$('#divSummaryData').html(data);
				},

				error: function (jqXHR, textStatus, errorThrown) {
					$('#divSummaryData').html('Error <b>' + jqXHR.status + '</b> refreshing period data - ' + errorThrown);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// +++ PERIOD +++
		//

		/**
		 * Read Period Data
		 */
		function ReadPeriod(GidID) {

			$.ajax({

				cache: false,
				type: 'GET',
				url: './_period_data.jsp',
				data: { gid: GidID },

				beforeSend: function(XMLHttpRequest) {
					$('#divPeriodData').html('<img border="0" src="../images/ajax-loader.gif">');
				},

				success: function(data) {
					$('#divPeriodData').html(data);
				},

				error: function (jqXHR, textStatus, errorThrown) {
					$('#divPeriodData').html('Error <b>' + jqXHR.status + '</b> refreshing period data - ' + errorThrown);
				}
			});
		}

		/**
		 * Save Period Data
		 */
		function SavePeriod(GidID) {

			const jPeriod = {
				'min': {
					'date': $('#prmPerDateMin').val(),
					'time': $('#prmPerTimeMin').val(),
					'zone': $('#prmPerZoneMin').val()
				},
				'max': {
					'date': $('#prmPerDateMax').val(),
					'time': $('#prmPerTimeMax').val(),
					'zone': $('#prmPerZoneMax').val()
				}
			};

			SaveClosureField(
				{
					'id': 'GRP_Period',
					'value': JSON.stringify(jPeriod)
				},
				GidID
			);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// +++ SCHEDULE +++
		//

		/**
		 * Read Schedule Data
		 */
		function ReadSchedule(GidID) {

			$.ajax({

				cache: false,
				type: 'GET',
				url: './_schedule_data.jsp',
				data: { gid: GidID },

				beforeSend: function(XMLHttpRequest) {
					$('#divScheduleData').html('<img border="0" src="../images/ajax-loader.gif">');
				},

				success: function(data) {
					$('#divScheduleData').html(data);
				},

				error: function (jqXHR, textStatus, errorThrown) {
					$('#divScheduleData').html('Error <b>' + jqXHR.status + '</b> refreshing schedule data - ' + errorThrown);
				}
			});
		}

		/**
		 * Add Schedule Data
		 */
		function AddSchedule(GidID, abbrDay) {

			$.ajax({

				cache: false,
				type: 'GET',
				url: './_schedule_dlg.jsp',
				data: {
					gid: GidID,
					day: abbrDay
				},

				success: function(data) {
					ShowDialog_AJAX(data);
				},

				error: function (jqXHR, textStatus, errorThrown) {
					console.error('Error ' + jqXHR.status + ' opening schedule dialog - ' + errorThrown);
				},

				complete: function(jqXHR, textStatus) {
					ReadStatus(GidID);
				}
			});
		}

		/**
		 * Delete a Schedule Entry
		 */
		function DelSchedule(GidID, abbrDay, timeMin, timeMax) {

			$.ajax({

				cache: false,
				type: 'POST',
				url: '../servlet/schedDelSlot',

				data: {
					gid: GidID,
					day: abbrDay,
					min: timeMin,
					max: timeMax
				},

				success: function(data) {
					if (data.status == 'OK') {
						ReadSchedule(GidID);
					} else {
						$('#divDelSchedError').css('color', 'FireBrick');
						$('#divDelSchedError').html(data.error);
						$('#trDelSchedError').show().delay(5000).fadeOut(1000);
					}
				},

				error: function (jqXHR, textStatus, errorThrown) {
					var errText = 'Error ' + jqXHR.status + ' deleting slot [' + abbrDay + '] ' + timeMin + '-' + timeMax + ' - Report to SysOp';
					console.error(errText + ' - ' + errorThrown);
					$('#divDelSchedError').html(errText);
					$('#trDelSchedError').show().delay(3000).fadeOut(1000);
				},

				complete: function(jqXHR, textStatus) {
					ReadStatus(GidID);
				}
			});
		}

		/**
		 * Clean All Schedule Data
		 */
		function CleanSchedule(GidID) {

			$.ajax({

				cache: false,
				type: 'POST',
				url: '../servlet/schedClean',

				data: {
					gid: GidID
				},

				success: function(data) {
					if (data.status == 'OK') {
						ReadSchedule(GidID);
					} else {
						$('#divDelSchedError').css('color', 'FireBrick');
						$('#divDelSchedError').html(data.error);
						$('#trDelSchedError').show().delay(5000).fadeOut(1000);
					}
				},

				error: function (jqXHR, textStatus, errorThrown) {
					var errText = 'Error ' + jqXHR.status + ' deleting schedule - Report to SysOp';
					console.error(errText + ' - ' + errorThrown);
					$('#divDelSchedError').css('color', 'FireBrick');
					$('#divDelSchedError').html(errText);
					$('#trDelSchedError').show().delay(3000).fadeOut(1000);
				},

				complete: function(jqXHR, textStatus) {
					ReadStatus(GidID);
				}
			});
		}

		/**
		 * Monday Schedule Data to WorkDays
		 */
		function MondayToWorkDays(GidID) {

			$.ajax({

				cache: false,
				type: 'POST',
				url: '../servlet/schedDuplicateMonday',

				data: {
					gid: GidID,
					ext: 'WD' // WorkDays only
				},

				success: function(data) {
					if (data.status == 'OK') {
						ReadSchedule(GidID);
					} else {
						$('#divDelSchedError').css('color', 'FireBrick');
						$('#divDelSchedError').html(data.error);
						$('#trDelSchedError').show().delay(5000).fadeOut(1000);
					}
				},

				error: function (jqXHR, textStatus, errorThrown) {
					var errText = 'Error ' + jqXHR.status + ' copying schedule - Report to SysOp';
					console.error(errText + ' - ' + errorThrown);
					$('#divDelSchedError').css('color', 'FireBrick');
					$('#divDelSchedError').html(errText);
					$('#trDelSchedError').show().delay(3000).fadeOut(1000);
				},

				complete: function(jqXHR, textStatus) {
					ReadStatus(GidID);
				}
			});
		}

		/**
		 * Monday Schedule Data to WeekDays
		 */
		function MondayToWeekDays(GidID) {

			$.ajax({

				cache: false,
				type: 'POST',
				url: '../servlet/schedDuplicateMonday',

				data: {
					gid: GidID,
					ext: 'AW' // All Weekdays
				},

				success: function(data) {
					if (data.status == 'OK') {
						ReadSchedule(GidID);
					} else {
						$('#divDelSchedError').css('color', 'FireBrick');
						$('#divDelSchedError').html(data.error);
						$('#trDelSchedError').show().delay(5000).fadeOut(1000);
					}
				},

				error: function (jqXHR, textStatus, errorThrown) {
					var errText = 'Error ' + jqXHR.status + ' copying schedule - Report to SysOp';
					console.error(errText + ' - ' + errorThrown);
					$('#divDelSchedError').css('color', 'FireBrick');
					$('#divDelSchedError').html(errText);
					$('#trDelSchedError').show().delay(3000).fadeOut(1000);
				},

				complete: function(jqXHR, textStatus) {
					ReadStatus(GidID);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// +++ GMAP +++
		//

		/**
		 * Draw GMap
		 */
		function DrawGMap(GidID) {

			$.ajax({

				cache: false,
				type: 'GET',
				url: './_closure_map.jsp',
				data: { gid: GidID },

				beforeSend: function(XMLHttpRequest) {
					$('#divClosureMap').html('<img border="0" src="../images/ajax-loader.gif">');
				},

				success: function(data) {
					$('#divClosureMap').html(data);
				},

				error: function (jqXHR, textStatus, errorThrown) {
					$('#divClosureMap').html('Error <b>' + jqXHR.status + '</b> refreshing map data - ' + errorThrown);
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
	MsgTool msgTool = new MsgTool(session);

	String Action = EnvTool.getStr(request, "Action", "");
	String GidUUID = EnvTool.getStr(request, "gid", "");
	String Caller = EnvTool.getStr(request, "caller", "");

	try {

		DB = new Database();
		User USR = new User(DB.getConnection());
		Group GRP = new Group(DB.getConnection());
		MsgTool MSG = new MsgTool(session);

		User.Data usrData = USR.Read(SysTool.getCurrentUser(request));
		Group.Data grpData = GRP.Read(GidUUID);

		String ReturnUrl = (Caller.equals(CifpStatus.ACTIVE.toString())
			? "../query/results.jsp?type=" + CifpStatus.ACTIVE.toString()
			: (Caller.equals(CifpStatus.PAUSED.toString())
				? "../query/results.jsp?type=" + CifpStatus.PAUSED.toString()
				: (Caller.equals(CifpStatus.ORPHAN.toString())
					? "../query/results.jsp?type=" + CifpStatus.ORPHAN.toString()
					: (Caller.equals(QueryMapPoints.GMAP_CALLER)
						? "../query/gmap.jsp"
						: "../query/"
					)
				)
			)
		);

		// Check edit rights

		boolean editEnabled = false;

		if (usrData.getWazerConfig().getCifp().isAdmin())
			editEnabled = true;
		else
			if (grpData.getCreatedBy().equals(SysTool.getCurrentUser(request)))
				editEnabled = true;

		if (!editEnabled) {
			RedirectTo = ReturnUrl;
			throw new Exception(SysTool.getCurrentUser(request) + ", you do not have sufficient rights to manage this closure, only " + grpData.getCreatedBy() + " (or a CIFP Country Admin) can do it");
		}

		// MAIN

		if (Action.equals("")) {

			////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			// SHOW CLOSURE (DEFAULT)
			//
			////////////////////////////////////////////////////////////////////////////////////////////////////

			if (SysTool.isEmptyUuidValue(grpData.getID())) {
				RedirectTo = ReturnUrl;
				throw new Exception("The requested closure do not exists: <span class=\"DS-text-fixed-compact DS-text-bold\">'" + GidUUID + "'</span>");
			}
			
			// Check empty  street names

			int EmptySegmentsCount = 0;

			Segment SEG = new Segment(DB.getConnection());
			Vector<Segment.Data> vecSegObj = SEG.getAll(grpData.getID());

			for (Segment.Data segData : vecSegObj)
				if (segData.getStreet().trim().equals(""))
					EmptySegmentsCount++;
%>
			<div class="DS-padding-updn-8px">
				<div class="mdc-layout-grid__inner">
					<div class="<%= MdcTool.Layout.Cell(8, 6, 4) %> DS-grid-middle-left">
						<div class="DS-text-title-shadow"><%= PAGE_Title %></div>
					</div>
					<div class="<%= MdcTool.Layout.Cell(2, 1, 2) %> DS-grid-middle-left">
						&nbsp;
					</div>
					<div class="<%= MdcTool.Layout.Cell(2, 1, 2) %> DS-grid-middle-right">
						&nbsp;
					</div>
				</div>
			</div>
<%
			//
			// HEADER
			//
%>
			<div class="mdc-layout-grid__inner">
<%
				//
				// Description & Type
				//
%>
				<div class="<%= MdcTool.Layout.Cell(6, 6, 4) %>">

					<div class="mdc-layout-grid__inner">

						<div class="<%= MdcTool.Layout.Cell(6, 8, 4) %>">
							<div class="DS-padding-top-8px">
								<%= MdcTool.Text.Box(
									"GRP_Description",
									grpData.getDescription(),
									MdcTool.Text.Type.TEXT,
									MdcTool.Text.Width.FULL,
									"Closure Description",
									"maxlength=\"36\" onBlur=\"SaveClosureField(this, '" + grpData.getID() + "');\""
								) %>
							</div>
						</div>
						<div class="<%= MdcTool.Layout.Cell(6, 8, 4) %>">
							<div class="DS-padding-top-8px">
								<%= MdcTool.Text.Box(
									"GRP_MteDescr",
									grpData.getMteDescr(),
									MdcTool.Text.Type.TEXT,
									MdcTool.Text.Width.FULL,
									"Major Traffic Event Name [optional]",
									"maxlength=\"36\" onBlur=\"SaveClosureField(this, '" + grpData.getID() + "');\""
								) %>
							</div>
						</div>

						<div class="<%= MdcTool.Layout.Cell(3, 2, 4) %>">
							<div class="DS-padding-top-8px">
								<%= MdcTool.Select.Box(
									"GRP_Type",
									MdcTool.Select.Width.FULL,
									"Type",
									CifpType.getCombo(grpData.getType(), false),
									"onChange=\"SaveClosureField(this, '" + grpData.getID() + "'); window.location.href='?gid=" + grpData.getID() + "';\""
								) %>
							</div>
						</div>
						<div class="<%= MdcTool.Layout.Cell(5, 3, 4) %>">
							<div class="DS-padding-top-8px">
								<%= MdcTool.Select.Box(
									"GRP_SubType",
									MdcTool.Select.Width.FULL,
									"SubType",
									CifpSubType.getCombo(grpData.getType(), grpData.getSubType()),
									"onChange=\"SaveClosureField(this, '" + grpData.getID() + "');\""
								) %>
							</div>
						</div>
						<div class="<%= MdcTool.Layout.Cell(4, 3, 4) %>">
							<div class="DS-padding-top-16px">
								<%= MdcTool.Check.Box(
									"GRP_isDelAfter",
									"<span class=\"" + (grpData.isDelAfter() ? "DS-text-Crimson DS-text-bold" : "") + "\">Delete after expiration</span>",
									"Y",
									grpData.isDelAfter() ? MdcTool.Check.Status.CHECKED : MdcTool.Check.Status.UNCHECKED,
									"onClick=\"SaveClosureField(this, '" + grpData.getID() + "');\""
								) %>
							</div>
						</div>

					</div>

				</div>
<%
				//
				// Status Box
				//
%>
				<div class="<%= MdcTool.Layout.Cell(2, 2, 4) %> DS-grid-middle-center">
					<div id="divSummaryStatus"></div>
				</div>
<%
				//
				// Summary Data
				//
%>
				<div class="<%= MdcTool.Layout.Cell(4, 8, 4) %>">
					<div id="divSummaryData" align="right"></div>
				</div>

			</div>
<%
			//
			// Period, Schedule & GMAP
			//
%>
			<div class="mdc-layout-grid__inner">

				<div class="<%= MdcTool.Layout.Cell(6, 4, 4) %>">
					<div class="DS-padding-top-16px" id="divPeriodData"></div>
					<div class="DS-padding-top-16px" id="divScheduleData"></div>
				</div>

				<div class="<%= MdcTool.Layout.Cell(6, 4, 4) %> DS-shadow">
					<div class="DS-padding-top-16px" id="divClosureMap"></div>
				</div>

			</div>
<%
			//
			// FOOTER (Action Buttons)
			//
%>
			<div class="mdc-layout-grid__inner">
				<div class="<%= MdcTool.Layout.Cell(12, 8, 4) %>">
					<div class="DS-padding-top-16px" align="center">
						<table class="TableSpacing_0px">
							<tr>
								<td class="DS-padding-lfrg16px">
									<%= MdcTool.Button.TextIconClass(
										"arrow_back",
										"&nbsp;Go Back",
										null,
										"DS-text-lightgray",
										"DS-text-lightgray",
										"onClick=\"window.location.href='" + ReturnUrl + "';\"",
										"Go back to the home page"
									) %>
								</td>
								<td class="DS-padding-lfrg16px">
									<%= MdcTool.Button.TextIconClass(
										"bookmarks",
										"&nbsp;Bookmark This",
										null,
										"DS-text-gold",
										"DS-text-gold",
										"onClick=\"SetBookmark('" + grpData.getID() + "', '" + ReturnUrl + "');\"",
										"Bookmark this closure"
									) %>
								</td>
								<td class="DS-padding-lfrg16px">
									<%= MdcTool.Button.TextIconClass(
										"border_color",
										"&nbsp;Edit Segments&nbsp;",
										null,
										"DS-text-lightblue",
										"DS-text-lightblue",
										"onClick=\"EditSegmentsInWME('" + grpData.getID() + "');\"",
										"Edit the segments of this closure in WME"
									) %>
								</td>
								<td class="DS-padding-lfrg16px">
									<%= MdcTool.Button.TextIconClass(
										"delete_forever",
										"&nbsp;Delete Forever",
										null,
										"DS-text-lightred",
										"DS-text-lightred",
										"onClick=\"DelClosure('" + grpData.getID() + "', '" + ReturnUrl + "');\"",
										"Permanently eliminate this closure"
									) %>
								</td>
								<td class="DS-padding-lfrg16px">
									<%= MdcTool.Button.TextIconClass(
										"verified",
										"&nbsp;Save &amp; Activate",
										null,
										"DS-text-lime",
										"DS-text-lime",
										"onClick=\"ActivateClosure('" + grpData.getID() + "', " + EmptySegmentsCount + ");\"",
										"Save and Activate this closure"
									) %>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
<%
		} else if (Action.equals("addSchedule")) {

			////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			// ADD NEW SCHEDULE SLOT
			//
			////////////////////////////////////////////////////////////////////////////////////////////////////

			String prmSchedTimeDay = EnvTool.getStr(request, "prmSchedTimeDay", "");
			String prmSchedTimeMin = EnvTool.getStr(request, "prmSchedTimeMin", Schedule.NO_TIME);
			String prmSchedTimeMax = EnvTool.getStr(request, "prmSchedTimeMax", Schedule.NO_TIME);

			GRP.addSlot(grpData, prmSchedTimeDay, prmSchedTimeMin, prmSchedTimeMax, SysTool.getCurrentUser(request));

			// Force validation
			GRP.setField(grpData.getID(), "GRP_isActive", "N", SysTool.getCurrentUser(request));

			RedirectTo = "?gid=" + grpData.getID();

		} else if (Action.equals("editSegmentsInWME")) {

			////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			// EDIT SEGMENTS IN WME
			//
			////////////////////////////////////////////////////////////////////////////////////////////////////

			String segList = "";
			String wmeUrl = WME_START_URL;
			Segment SEG = new Segment(DB.getConnection());

			wmeUrl += "?env=" + grpData.getWmeData().getString("wmeEnv");
			wmeUrl += "&lat=" + grpData.getWmeData().getDouble("wmeLat");
			wmeUrl += "&lon=" + grpData.getWmeData().getDouble("wmeLng");
			wmeUrl += "&zoomLevel=" + grpData.getWmeData().getInt("wmeZoom");

			Vector<Segment.Data> vecSegObj = SEG.getAll(grpData.getID());

			for (Segment.Data segData : vecSegObj)
				segList += (segList.equals("") ? "" : ",") + segData.getWmeID();

			wmeUrl += "&segments=" + segList;

			RedirectTo = wmeUrl + "&CIFP_GRPID=" + grpData.getID(); // CIFP_GRPID param name must be THE SAME as the one in the script

		} else if (Action.equals("activateClosure")) {

			////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			// CHECK AND ACTIVATE A CLOSURE
			//
			////////////////////////////////////////////////////////////////////////////////////////////////////

			GRP.chkDataSyntax(GidUUID, SysTool.getCurrentUser(request));
			GRP.setField(GidUUID, "GRP_isActive", "Y", SysTool.getCurrentUser(request));

			MSG.setSnackText("Closure Activated");

			RedirectTo = "?gid=" + GidUUID;

		} else if (Action.equals("setBookmark")) {

			////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			// BOOKMARK A CLOSURE
			//
			////////////////////////////////////////////////////////////////////////////////////////////////////

			Bookmark UBK = new Bookmark(DB.getConnection());

			try {
				UBK.Add(SysTool.getCurrentUser(request), GidUUID);
				MSG.setSnackText("Bookmark added");
			} catch (Exception e) {
				System.err.println("gid_edit.jsp: setBookmark: " + e.toString());
				MSG.setSlideText("Cannot add bookmark", e.getMessage());
			}

			RedirectTo = "?gid=" + GidUUID;

		} else if (Action.equals("delClosure")) {

			////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			// DELETE A CLOSURE
			//
			////////////////////////////////////////////////////////////////////////////////////////////////////

			String NextUrl = EnvTool.getStr(request, "NextUrl", ReturnUrl);
			Segment SEG = new Segment(DB.getConnection());

			SEG.delAll(GidUUID);
			GRP.Delete(GidUUID);

			RedirectTo = NextUrl;

		} else {

			////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			// BAD ACTION
			//
			////////////////////////////////////////////////////////////////////////////////////////////////////

			throw new Exception("Unknown Action: '" + Action + "'");
		}

	} catch (Exception e) {

		System.err.println(e.toString());
		msgTool.setSlideText(PAGE_Title, e.getMessage());

		if (RedirectTo.equals(""))
			RedirectTo = "?gid=" + GidUUID;
	}

	if (DB != null)
		DB.destroy();
%>
	</div>
	</div>
	</div>

	<script>
		$(document).ready(function() {
			ReadStatus('<%= GidUUID %>');
			ReadSummary('<%= GidUUID %>');
			ReadPeriod('<%= GidUUID %>');
			ReadSchedule('<%= GidUUID %>');
			DrawGMap('<%= GidUUID %>');
		});
	</script>

	<jsp:include page="../_common/footer.jsp">
		<jsp:param name="RedirectTo" value="<%= RedirectTo %>"/>
	</jsp:include>
<%
	// Field updated DIV
%>
	<div id="divSaveDataResult" style="position: absolute; width: 800px; margin-left: -400px; height: 40px; margin-top: -20px; top: 90px; left: 50%;" align="center">
		<span id="divSaveDataResultContent"></span>
	</div>

	<script>
		$('#divSaveDataResult').hide();
	</script>

</body>
</html>
