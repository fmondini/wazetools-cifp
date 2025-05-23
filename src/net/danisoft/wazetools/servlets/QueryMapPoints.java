////////////////////////////////////////////////////////////////////////////////////////////////////
//
// QueryMapPoints.java
//
// Create script to draw closure map points
//
// First Release: Apr/2024 by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Changed to @WebServlet style
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.servlets;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.danisoft.dslib.Database;
import net.danisoft.dslib.EnvTool;
import net.danisoft.dslib.FmtTool;
import net.danisoft.dslib.SysTool;
import net.danisoft.wtlib.auth.User;
import net.danisoft.wazetools.cifp.CifpStatus;
import net.danisoft.wazetools.cifp.CifpType;
import net.danisoft.wazetools.cifp.Group;
import net.danisoft.wazetools.cifp.Segment;

@WebServlet(description = "Create script to draw closure map points", urlPatterns = { "/servlet/qryMapPoints" })

public class QueryMapPoints extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	public static final String GMAP_CALLER = "GMAP";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Database DB = null;
		
		String AllMarkers = (
			"var mrkIcon, mrkMark;"
		);

		try {

			DB = new Database();
			Group GRP = new Group(DB.getConnection());
			Segment SEG = new Segment(DB.getConnection());
			User USR = new User(DB.getConnection());

			String reqMapObj = EnvTool.getStr(request, "m", "");
			String viewFilterType = EnvTool.getStr(request, "t", CifpType.ALL.toString());
			String viewFilterStatus = EnvTool.getStr(request, "s", CifpStatus.ALL.toString());

			if (reqMapObj.trim().equals(""))
				throw new Exception("No mapObj found");

			User.Data usrData = USR.Read(SysTool.getCurrentUser(request));
			Vector<String> vecCountries = usrData.getWazerConfig().getCifp().getActiveCountryCodes();

			if (vecCountries.isEmpty())
				throw new Exception("No country found in configuration");

			String MarkerIcon, MarkerTitle, Points[];
			double pointLat, pointLng, latMin, latMax, lngMin, lngMax, centerLat, centerLng;

			// Filter

			String selectionQuery = "";
			
			for (String usrCountry : vecCountries) {
				selectionQuery += (selectionQuery.equals("") ? "" : " OR ") + "GRP_CtryIso = '" + usrCountry + "'";
			}
			
			selectionQuery = "(" + selectionQuery + ") ";

			if (!viewFilterType.equals(CifpType.ALL.toString()))
				selectionQuery += "AND GRP_Type = '" + viewFilterType + "'";

			if (!viewFilterStatus.equals(CifpStatus.ALL.toString()))
				selectionQuery += (
					" AND " + 
					(viewFilterStatus.equals(CifpStatus.ORPHAN.toString())
						? "GRP_isUnedited = 'Y'"
						: (viewFilterStatus.equals(CifpStatus.ACTIVE.toString())
							? "GRP_isActive = 'Y'"
							: "GRP_isActive = 'N'"
						)
					)
				);

			// Query

			Vector<Group.Data> vecGrpData = GRP.getAll(selectionQuery, null);

			for (Group.Data grpData : vecGrpData) {

				Vector<Segment.Data> vecSegData = SEG.getAll(grpData.getID());

				latMin = Double.MAX_VALUE; latMax = Double.MIN_VALUE; lngMin = Double.MAX_VALUE; lngMax = Double.MIN_VALUE;

				for (Segment.Data segData : vecSegData) {

					// Find map center

					Points = segData.getPolylineText().replace("LINESTRING(", "").replace(")", "").split(",");

					for (int i=0; i<Points.length; i++) {

						pointLat = Double.parseDouble(Points[i].split(" ")[0]);
						pointLng = Double.parseDouble(Points[i].split(" ")[1]);

						if (latMin > pointLat) latMin = pointLat;
						if (latMax < pointLat) latMax = pointLat;
						if (lngMin > pointLng) lngMin = pointLng;
						if (lngMax < pointLng) lngMax = pointLng;
					}
				}

				if (latMin != Double.MAX_VALUE && latMax != Double.MIN_VALUE && lngMin != Double.MAX_VALUE && lngMax != Double.MIN_VALUE) {

					centerLat = FmtTool.Round((latMax + latMin) / 2.0D, 5);
					centerLng = FmtTool.Round((lngMax + lngMin) / 2.0D, 5);

					MarkerIcon = CifpType.getIconSrc(grpData);

					MarkerTitle = "[" + (grpData.isUnedited()
						? "ORPHAN"
						: (grpData.isActive()
							? "ACTIVE"
							: "PAUSED"
						)
					) + "]";

					AllMarkers += (
						// Icon
						"mrkIcon = document.createElement('img');mrkIcon.src='" + MarkerIcon + "';" +
						// Marker
						"mrkMark = new google.maps.marker.AdvancedMarkerElement({" +
							"map:" + reqMapObj + "," +
							"title:'" + MarkerTitle + " " + (grpData.getDescription().trim().equals("") ? "[no description]" : grpData.getDescription().replace("'", "\\'")) + "'," +
							"position:{lat:" + centerLat + ",lng:" + centerLng + "}," +
							"content:mrkIcon" +
						"});" +
						// Events
						"mrkMark.addListener('click', () => {" +
							"window.location.href='../manage/gid_edit.jsp?caller=" + GMAP_CALLER + "&gid=" + grpData.getID() + "'" +
						"});"
					);
				}
			}

		} catch (Exception e) {

			AllMarkers = "";
			System.err.println("QueryMapPoints(): [" + SysTool.getCurrentUser(request) + "] " + e.toString());
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
		}

		if (DB != null)
			DB.destroy();

		response.setContentType("text/javascript; charset=UTF-8");
		response.getWriter().print(AllMarkers);
	}

}
