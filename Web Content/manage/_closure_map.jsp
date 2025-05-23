<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*"
	import="java.nio.file.*"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%!
	class ClosureCoor {
		double lat;
		double lng;
	}

	class ClosureLine {
		String grpId;
		String segId;
		CifpSegDir segDir;
		Vector<ClosureCoor> vecCoors;
	}

	class ClosureSegs {
		String grpId;
		String segId;
		String segName;
		String segLine;
		CifpSegDir segDir;
	}

	/**
	 * Clean ZIP files older than <DaysOld> day(s)
	 */
	private static void CleanObsoleteZIPs() {

		int DaysOld = 1;
		String completeZipFilename = "";
		Vector<Path> vecZipFiles = FilTool.getAll(AppCfg.getZipDestPath(), "", ".zip");

		for (Path path : vecZipFiles) {

			completeZipFilename = path.getParent().toString().replace("\\", "/") + "/" + path.getFileName().toString();
			java.util.Date lmDate = FilTool.getLastModified(completeZipFilename);

			if (lmDate.getTime() < FmtTool.addDays(new java.util.Date(), -DaysOld).getTime()) {
				FilTool.DeleteFile(completeZipFilename);
				System.err.println("CleanObsoleteZIPs(): Old file'" + completeZipFilename + "' dated '" + FmtTool.fmtDateTime(lmDate) + "' has beed deleted");
			}
		}
	}
%>
<%
	final String GidUUID = EnvTool.getStr(request, "gid", "");

	final String STROKE_WEIGHT_ONE_WAY = "3";
	final String STROKE_WEIGHT_TWO_WAY = "7";

	Database DB = null;

	try {

		DB = new Database();

		Segment SEG = new Segment(DB.getConnection());
		Vector<Segment.Data> vecSegObj = SEG.getAll(GidUUID);

		// Clean ZIP folder

		CleanObsoleteZIPs();

		// Prepare Segments Data

		ClosureLine closureLine;
		ClosureCoor closureCoor;
		Vector<ClosureLine> vecClosureLine = new Vector<ClosureLine>();
		Vector<ClosureCoor> vecClosureCoor = new Vector<ClosureCoor>();
		Vector<ClosureSegs> vecClosureSegs = new Vector<ClosureSegs>();

		int lineNo = 0;
		String pointsArray[], segLine, jsCoords, jsPolylines;
		double centerLat, centerLng, latMin = Double.MAX_VALUE, latMax = Double.MIN_VALUE, lngMin = Double.MAX_VALUE, lngMax = Double.MIN_VALUE;

		for (Segment.Data segData : vecSegObj) {

			pointsArray = segData.getPolylineText().replace("LINESTRING(", "").replace(")", "").split(",");
			vecClosureCoor = new Vector<ClosureCoor>();

			for (int p=0; p<pointsArray.length; p++) {

				closureCoor = new ClosureCoor();
				closureCoor.lat = Double.parseDouble(pointsArray[p].split(" ")[0]);
				closureCoor.lng = Double.parseDouble(pointsArray[p].split(" ")[1]);
				vecClosureCoor.add(closureCoor);
			}

			closureLine = new ClosureLine();
			closureLine.grpId = segData.getGroup();
			closureLine.segId = segData.getID();
			closureLine.segDir = segData.getDirection();
			closureLine.vecCoors = vecClosureCoor;
			vecClosureLine.add(closureLine);
		}

		for (ClosureLine cLine : vecClosureLine) {

			lineNo++;
			segLine = "";
			ClosureSegs closureSegs = new ClosureSegs();

			for (ClosureCoor cCoor : cLine.vecCoors) {

				segLine += "{ lat: " + cCoor.lat + ", lng: " + cCoor.lng + " }, ";

				// Find map center

				if (latMin > cCoor.lat) latMin = cCoor.lat;
				if (latMax < cCoor.lat) latMax = cCoor.lat;
				if (lngMin > cCoor.lng) lngMin = cCoor.lng;
				if (lngMax < cCoor.lng) lngMax = cCoor.lng;
			}

			closureSegs.grpId = cLine.grpId;
			closureSegs.segId = cLine.segId;
			closureSegs.segDir = cLine.segDir;
			closureSegs.segName = "PolyLine_" + FmtTool.fmtZeroPad(lineNo, 3);
			closureSegs.segLine = segLine;
			vecClosureSegs.add(closureSegs);
		}

		centerLat = FmtTool.Round((latMax + latMin) / 2.0D, 5);
		centerLng = FmtTool.Round((lngMax + lngMin) / 2.0D, 5);

		// JS data

		jsCoords = "";
		jsPolylines = "";

		for (ClosureSegs closureSegs : vecClosureSegs) {

			// Points definition

			jsCoords += ("\n" +
				"const " + closureSegs.segName + " = [" +
					closureSegs.segLine +
				"];"
			);

			// Polyline creation

			jsPolylines += ("\n" +
				"const path" + closureSegs.segName + " = new google.maps.Polyline({" +
					"id: '" + closureSegs.segId + "', " +
					"group: '" + closureSegs.grpId + "', " +
					"cifpDir: '" + closureSegs.segDir.toString() + "', " +
					"geodesic: true, " +
					"strokeColor: 'Red', " +
					"strokeOpacity: 1.0, " +
					"strokeWeight: " + (closureSegs.segDir.equals(CifpSegDir.ALL) ? STROKE_WEIGHT_TWO_WAY : STROKE_WEIGHT_ONE_WAY) + ", " +
					"icons: [{" + (
						closureSegs.segDir.equals(CifpSegDir.FWD)
							? "icon: oneWayArrowFwd, repeat: '20px', offset: '0'"
							: (closureSegs.segDir.equals(CifpSegDir.BCK)
								? "icon: oneWayArrowBck, repeat: '20px', offset: '0'"
								: ""
							)
						) +
					"}]," +
					"path: " + closureSegs.segName +
				"});" +
				"path" + closureSegs.segName + ".setMap(MapObj);" +
				"google.maps.event.addListener(path" + closureSegs.segName + ", 'click', function(e) {" +
					"ToggleDirection(this);" +
				"});"
			);
		}
%>
	<div class="<%= MdcTool.Elevation.Light() %>">
		<div id="<%= AppCfg.getGMapDivName() %>" style="position: relative; top: 0px; left: 0px; width: 100%; height: 357px;"></div>
	</div>

	<script>

		const mapDefaultZoom = 14;
		const mapDefaultCLat = <%= centerLat %>;
		const mapDefaultCLng = <%= centerLng %>;
		const currentGroupID = '<%= GidUUID %>';

		var MapObj = null;
		var oneWayArrowFwd = {}; // Filled in initMap()
		var oneWayArrowBck = {}; // Filled in initMap()

		/**
		 * Map Initialization
		 */
		function initMap() {

			// Arrows

			oneWayArrowFwd = {
				path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
				fillOpacity: 1,
				fillColor: 'White',
				strokeColor: 'DarkRed',
				strokeWeight: 0.5,
				scale: 2
			};

			oneWayArrowBck = {
				path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
				fillOpacity: 1,
				fillColor: 'White',
				strokeColor: 'DarkRed',
				strokeWeight: 0.5,
				scale: 2
			};

			// Map

			const mapStyles = {
				default: [],
				hidePoiAndLabels: [
					{
						featureType: 'poi',
						stylers: [{ visibility: 'off' }],
					}, {
						featureType: 'transit',
						elementType: 'labels.icon',
						stylers: [{ visibility: 'off' }],
					},
				],
			};

			const mapOptions = {
				mapTypeId: google.maps.MapTypeId.TERRAIN,
				zoom: mapDefaultZoom,
				fullscreenControl: true,
				scaleControl: false,
				mapTypeControl: false,
				streetViewControl: false,
				styles: mapStyles['hidePoiAndLabels'],
				center: new google.maps.LatLng(mapDefaultCLat, mapDefaultCLng)
			};

			MapObj = new google.maps.Map(
				document.getElementById('<%= AppCfg.getGMapDivName() %>'),
				mapOptions
			);

			// Segments

			<%= jsCoords %>
			<%= jsPolylines %>

			// Export buttons

			const divGMapExportButtons = document.createElement("div");
			
			divGMapExportButtons.appendChild(createExportButton('JSON', 'Export segments in JSON', ExportJSON));
			divGMapExportButtons.appendChild(createExportButton('WKT', 'Export segments in WKT', ExportWKT));
			divGMapExportButtons.appendChild(createExportButton('KML', 'Export segments in KML', ExportKML));
			divGMapExportButtons.appendChild(createExportButton('CSV', 'Export segments in CSV', ExportCSV));

			MapObj.controls[google.maps.ControlPosition.TOP_LEFT].push(
				divGMapExportButtons
			);
		}

		/**
		 * Toggle Segment Direction
		 */
		function ToggleDirection(segment) {

			const noIconValue = '{}';
			const offsetValue = '0';
			const repeatValue = '20px';

			$.ajax({

				cache: false,
				type: 'GET',
				url: '../servlet/toggleSegmentDir',

				data: {
					segId: segment.id
				},

				success: function(data) {

					if (data.status == 'OK') {

						segment.setMap(null);

						segment.setOptions({
							strokeWeight: (data.segDir == '<%= CifpSegDir.ALL.toString() %>'
								? '<%= STROKE_WEIGHT_TWO_WAY %>'
								: '<%= STROKE_WEIGHT_ONE_WAY %>'
							),
							icons: [
								(data.segDir == '<%= CifpSegDir.FWD.toString() %>'
									? { icon: oneWayArrowFwd, repeat: repeatValue, offset: offsetValue }
									: (data.segDir == '<%= CifpSegDir.BCK.toString() %>'
										? { icon: oneWayArrowBck, repeat: repeatValue, offset: offsetValue }
										: noIconValue
									)
								)
							]
						});

						segment.setMap(MapObj);

						ReadStatus(segment.group);

					} else
						ShowDialog_Error(data.msg);
				},

				error: function (jqXHR, textStatus, errorThrown) {
					ShowDialog_Error('ToggleDirection() Error ' + jqXHR.status);
				}
			});
		}

		/**
		 * Creates an export button
		 */
		function createExportButton(btnLabel, bntTitle, fnClick) {

			const btnExport = document.createElement('button');

			btnExport.type					= 'button';
			btnExport.textContent			= btnLabel;
			btnExport.title					= bntTitle;
			btnExport.style.color			= 'var(--font-color)';
			btnExport.style.fontFamily		= 'var(--font-family-cond)';
			btnExport.style.fontSize		= 'var(--font-size)';
			btnExport.style.textAlign		= 'center';
			btnExport.style.backgroundColor	= 'white';
			btnExport.style.padding			= '2px 8px 2px 8px'; // top, right, bottom, left
			btnExport.style.margin			= '8px 0px 0px 8px'; // top, right, bottom, left
			btnExport.style.border			= '1px solid gray';  // width, style (required), color
			btnExport.style.borderRadius	= '3px';
			btnExport.style.boxShadow		= '2px 2px 5px rgba(0,0,0,0.3)';
			btnExport.style.cursor			= 'pointer';

			btnExport.addEventListener("click", () => {
				fnClick();
			});

			return btnExport;
		}

		/**
		 * Export segments in JSON format
		 */
		function ExportJSON() {
			window.location.href = '../servlet/exportSegs?gid=' + currentGroupID + '&type=JSON';
		}

		/**
		 * Export segments in WKT format
		 */
		function ExportWKT() {
			window.location.href = '../servlet/exportSegs?gid=' + currentGroupID + '&type=WKT';
		}

		/**
		 * Export segments in KML format
		 */
		function ExportKML() {
			window.location.href = '../servlet/exportSegs?gid=' + currentGroupID + '&type=KML';
		}

		/**
		 * Export segments in CSV format
		 */
		function ExportCSV() {
			window.location.href = '../servlet/exportSegs?gid=' + currentGroupID + '&type=CSV';
		}

	</script>

	<script <%= "async" %> src="https://maps.googleapis.com/maps/api/js
		?key=<%= AppCfg.getGMapActvKey() %>
		&v=weekly
		&loading=async
		&libraries=marker
		&callback=initMap
	"></script>

<%
	} catch (Exception e) {
		out.println(e.getMessage());
	}

	if (DB != null)
		DB.destroy();
%>
