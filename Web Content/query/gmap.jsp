<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%!
	private static final String PAGE_Title = AppCfg.getAppName() + " Map";
	private static final String PAGE_Keywords = "";
	private static final String PAGE_Description = AppCfg.getAppAbstract();

	private static final String DRAW_POINTS_DATA_SCRIPT_NAME = "DrawPointsData";
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

		var MapObj = null;
		var MapOptions = null;

		/**
		 * Store current map settings
		 */
		function SaveMapSettings() {

			setCookie('<%= CifpCookie.MAP_ZOOM.getName() %>', MapObj.getZoom());
			setCookie('<%= CifpCookie.MAP_LAT.getName() %>', MapObj.getCenter().lat());
			setCookie('<%= CifpCookie.MAP_LNG.getName() %>', MapObj.getCenter().lng());
		}

		/**
		 * Config & Options Dialog
		 */
		function ShowConfigDialog() {

			$.ajax({

				cache: false,
				type: 'GET',
				url: './_config_map_dlg.jsp',

				success: function(data) {
					ShowDialog_AJAX(data);
				},

				error: function (jqXHR, textStatus, errorThrown) {
					ShowDialog_OK('Config Error', jqXHR.responseText, "OK")
				}
			});
		}

		/**
		 * Create and Launch ShowData Script
		 */
		function ShowDataScript(mapViewType, mapViewStatus) {

			$.ajax({

				cache: false,
				type: 'POST',
				dataType: 'text',
				url: '../servlet/qryMapPoints',

				data: {
					m: 'MapObj',
					t: mapViewType,
					s: mapViewStatus
				},

				beforeSend: function() {
					$('#divAjaxWait').show();
				},

				success: function(data) {

					var DataScript = document.createElement('script');

					DataScript.id = '<%= DRAW_POINTS_DATA_SCRIPT_NAME %>';
					DataScript.type = 'text/javascript';
					DataScript.text = 'function <%= DRAW_POINTS_DATA_SCRIPT_NAME %>(){' + data + '}';

					$('head').append(DataScript);

					<%= DRAW_POINTS_DATA_SCRIPT_NAME %>();
				},

				error: function(jqXHR, textStatus, errorThrown) {
					ShowDialog_ErrorExt(
						'Error retrieving closure data',
						'<b>No country was found in your setup.</b><br>Please have a CIFP administrator verify your configuration.',
						'../query/'
					);
				},

				complete: function(jqXHR, textStatus) {
					$('#divAjaxWait').hide();
				}
			});
		}

	</script>

</head>

<body>

	<jsp:include page="../_common/header.jsp">
		<jsp:param name="Force-Hide" value="Y"/>
	</jsp:include>

	<div id="<%= AppCfg.getGMapDivName() %>" style="position: absolute; top: 0px; left: 0px; width: 100%; height: 100%; z-index: -1;"></div>

	<div id="divAjaxWait" style="position: absolute; width: 100px; margin-left: -50px; height: 80px; margin-top: -40px; top: 50%; left: 50%; padding: 5px; background-color: white; box-shadow: 3px 3px 5px #888888; border-radius: 5px; -moz-border-radius: 5px;">
		<div class="DS-padding-top-8px" align="center">
			<div><img src="../images/ajax-loader.gif"></div>
			<div>Loading...</div>
		</div>
	</div>

	<div style="bottom: 15px; left: 15px; position: absolute;">
		<%= MdcTool.Button.BackTextIcon("Back", "../query/") %>
	</div>

	<div style="top: 60px; right: 15px; position: absolute;">
		<%= MdcTool.Button.Icon(
			"settings",
			"onClick=\"ShowConfigDialog();\"",
			"Configuration &amp; Options"
		) %>
	</div>
<%
	out.flush();

	Database DB = null;

	try {

		DB = new Database();

		//
		// Map View Cookies
		//

		String mapViewType = "";
		String mapViewStatus = "";

		Cookie[] cookies = request.getCookies();

		try {

			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(CifpCookie.MAP_VIEW_TYPE.getName()))
					mapViewType = cookie.getValue();
				if (cookie.getName().equals(CifpCookie.MAP_VIEW_STATUS.getName()))
					mapViewStatus = cookie.getValue();
			}

		} catch (Exception e) {	}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		// SCRIPTS
		//
%>
		<script>

		/**
		 * Map Initialization
		 */
		function initMap() {

			// Getting last Lat/Lon/Zoom from cookies

			var mapDefaultZoom = parseInt('0' + getCookie('<%= CifpCookie.MAP_ZOOM.getName() %>'));
			var mapDefaultCLat = parseFloat('0' + getCookie('<%= CifpCookie.MAP_LAT.getName() %>'));
			var mapDefaultCLng = parseFloat('0' + getCookie('<%= CifpCookie.MAP_LNG.getName() %>'));

			mapDefaultZoom = (mapDefaultZoom == 0 ? <%= AppCfg.getGMapDefZoom() %> : mapDefaultZoom);
			mapDefaultCLat = (mapDefaultCLat == 0 ? <%= AppCfg.getGMapDefCLat() %> : mapDefaultCLat);
			mapDefaultCLng = (mapDefaultCLng == 0 ? <%= AppCfg.getGMapDefCLng() %> : mapDefaultCLng);

			// Setting Map Options

			const MapOptions = {
				mapId: '21c0ef9413a8d208',
				mapTypeId: google.maps.MapTypeId.ROADMAP,
				zoom: mapDefaultZoom,
				fullscreenControl: false,
				scaleControl: false,
				mapTypeControl: false,
				streetViewControl: false,
				panControl: true,
				center: new google.maps.LatLng(mapDefaultCLat, mapDefaultCLng)
			};

			// Creating map object

			MapObj = new google.maps.Map(
				document.getElementById('<%= AppCfg.getGMapDivName() %>'),
				MapOptions
			);

			// Adding a listener to store last map settings

			google.maps.event.addListener(MapObj, 'tilesloaded', function() {
				google.maps.event.addListener(MapObj, 'zoom_changed', SaveMapSettings);
				google.maps.event.addListener(MapObj, 'dragend', SaveMapSettings);
			});

			// Finalize & Draw

			ShowDataScript('<%= mapViewType %>', '<%= mapViewStatus %>');
		}

		</script>

		<script <%= "async defer" %> src="https://maps.googleapis.com/maps/api/js
			?key=<%= AppCfg.getGMapActvKey() %>
			&v=weekly
			&loading=async
			&libraries=marker
			&callback=initMap
		"></script>

		<jsp:include page="../_common/footer.jsp">
			<jsp:param name="RedirectTo" value=""/>
			<jsp:param name="Force-Hide" value="Y"/>
		</jsp:include>
<%
	} catch (Exception e) {
%>
		<script>
			ShowDialog_Error('<%= e.toString().replace("\n", "<br>") %>');
		</script>
<%
	}

	if (DB != null)
		DB.destroy();
%>
</body>
</html>
