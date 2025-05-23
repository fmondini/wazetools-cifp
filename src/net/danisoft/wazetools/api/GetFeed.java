////////////////////////////////////////////////////////////////////////////////////////////////////
//
// GetFeed.java
//
// Publish a CIFP feed in JSON format
//
// First Release: Apr/2024 by Fulvio Mondini (https://danisoft.software/)
//        Update: May/2024 Segment Direction implemented
//        Update: Jun/2024 XML feed added
//       Revised: Mar/2025 Ported to Waze dslib.jar
//                         Changed to @WebServlet style
//       Revised: Apr/2025 Extended output added
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.api;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.danisoft.dslib.Database;
import net.danisoft.dslib.EnvTool;
import net.danisoft.dslib.FmtTool;
import net.danisoft.dslib.SysTool;
import net.danisoft.wtlib.auth.GeoIso;
import net.danisoft.wazetools.AppCfg;
import net.danisoft.wazetools.cifp.CifpSegDir;
import net.danisoft.wazetools.cifp.FeedLastCheck;
import net.danisoft.wazetools.cifp.Group;
import net.danisoft.wazetools.cifp.Segment;

@WebServlet(description = "Publish a CIFP feed in JSON format", urlPatterns = { "/feed/get" })

public class GetFeed extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	private Document DOC = null;

	public enum FeedFormat {
		XML, JSON;
	}

	public enum FeedType {
		S, X;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Database DB = null;
		ServletOutputStream out = response.getOutputStream();

		JSONObject jFeed = new JSONObject();
		String feedFormat = EnvTool.getStr(request, "f", "").toUpperCase();
		String feedCountry = EnvTool.getStr(request, "c", "").toUpperCase();
		String feedType = EnvTool.getStr(request, "t", "").toUpperCase();

		// Update date, user and address of the last feed read
		FeedLastCheck.Update(SysTool.getCurrentUser(request), request.getRemoteAddr());

		try {

			if (!feedFormat.equals(FeedFormat.XML.toString()) && !feedFormat.equals(FeedFormat.JSON.toString()))
				throw new Exception("Bad Format");

			if (!feedType.equals(FeedType.S.toString()) && !feedType.equals(FeedType.X.toString()))
				throw new Exception("Bad Type");

			if (feedCountry.equals(""))
				throw new Exception("Bad Country");

			DB = new Database();

			Group GRP = new Group(DB.getConnection());
			Segment SEG = new Segment(DB.getConnection());
			GeoIso GEO = new GeoIso(DB.getConnection());

			// Check country

			GeoIso.Data geoData = GEO.Read(feedCountry);

			if (!geoData.getIso3().equals(feedCountry))
				throw new Exception("Unknown country: '" + feedCountry + "'");

			// Prepare JSON

			JSONObject jIncident;
			JSONArray jaIncidents = new JSONArray();

			// Prepare XML

			this.DOC = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			Element elem_incidents = _new_element("incidents");
			elem_incidents.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			elem_incidents.setAttribute("xsi:noNamespaceSchemaLocation", "http://www.gstatic.com/road-incidents/cifsv2.xsd");

			// Read data

			String feed_id, feed_type, feed_subtype, feed_street, feed_description,
				feed_start_time, feed_end_time, feed_creation_time, feed_update_time,
				feed_name, feed_url,
				feed_polyLine, feed_segmentDir
			;

			for (Group.Data grpData : GRP.getAll("GRP_CtryIso = '" + feedCountry + "'", null)) {

				if (grpData.isActive()) {

					for (Segment.Data segData : SEG.getAll(grpData.getID())) {

						//
						// Feed Fields
						//

						Date datePeriodMin, datePeriodMax;
						datePeriodMin = FmtTool.scnDateTimeSqlStyle(grpData.getPeriod().getJSONObject("min").getString("date") + " " + grpData.getPeriod().getJSONObject("min").getString("time") + ":00");
						datePeriodMax = FmtTool.scnDateTimeSqlStyle(grpData.getPeriod().getJSONObject("max").getString("date") + " " + grpData.getPeriod().getJSONObject("max").getString("time") + ":59");

						feed_id = segData.getID();
						feed_type = grpData.getType();
						feed_subtype = grpData.getSubType();
						feed_street = segData.getStreet();
						feed_description = grpData.getDescription();
						feed_start_time = FmtTool.fmtWazeCifpFeedDateTime(datePeriodMin);
						feed_end_time = FmtTool.fmtWazeCifpFeedDateTime(datePeriodMax);
						feed_creation_time = FmtTool.fmtWazeCifpFeedDateTime(grpData.getCreatedAt());
						feed_update_time = FmtTool.fmtWazeCifpFeedDateTime(grpData.getUpdatedAt());
						feed_name = "Generated by " + AppCfg.getAppName() + " " + AppCfg.getAppVersion();
						feed_url = AppCfg.getServerHomeUrl();

						feed_polyLine = // Use "getPolylineText()" for reverse or "getPolylineFeed()" for all other
							(segData.getDirection().equals(CifpSegDir.FWD)
								? segData.getPolylineFeed()
								: (segData.getDirection().equals(CifpSegDir.BCK)
									? _reverse_polyline(segData.getPolylineText())
									: segData.getPolylineFeed()
								)
							)
						;

						feed_segmentDir =
							(segData.getDirection().equals(CifpSegDir.FWD)
								? "ONE_DIRECTION"
								: (segData.getDirection().equals(CifpSegDir.BCK)
									? "ONE_DIRECTION"
									: "BOTH_DIRECTIONS"
								)
							)
						;

						//
						// Create feed
						//

						if (feedFormat.equals(FeedFormat.XML.toString())) {

							Element elem_incident = _new_element("incident");

							elem_incident.setAttribute("id", feed_id);

							// Standard Data

							elem_incident.appendChild(_new_element("type", feed_type));
							elem_incident.appendChild(_new_element("subtype", feed_subtype));
							elem_incident.appendChild(_new_element("polyline", feed_polyLine));
							elem_incident.appendChild(_new_element("direction", feed_segmentDir));
							elem_incident.appendChild(_new_element("starttime", feed_start_time));
							elem_incident.appendChild(_new_element("endtime", feed_end_time));

							// Standard CData

							elem_incident.appendChild(_new_element_cdata("street", feed_street));
							elem_incident.appendChild(_new_element_cdata("description", feed_description));

							// Extended Data

							if (feedType.equals(FeedType.X.toString())) {
								elem_incident.appendChild(_new_element("creationtime", feed_creation_time));
								elem_incident.appendChild(_new_element("updatetime", feed_update_time));
								elem_incident.appendChild(_new_element("name", feed_name));
								elem_incident.appendChild(_new_element("url", feed_url));
								elem_incident.appendChild(_new_element("reference", feed_id));
							}

							// Schedule

							Vector<String> vecElements = grpData.getScheduleXml();

							if (!vecElements.isEmpty()) {
								
								Element elem_schedule = _new_element("schedule");

								for (String schedLine : vecElements) {
									elem_schedule.appendChild(
										_new_element(
											schedLine.split(SysTool.getDelimiter())[0],
											schedLine.split(SysTool.getDelimiter())[1]
										)
									);
								}
								
								elem_incident.appendChild(elem_schedule);
							}

							elem_incidents.appendChild(elem_incident);

						} else {

							jIncident = new JSONObject();

							// Standard Data

							jIncident.put("id", feed_id);
							jIncident.put("type", feed_type);
							jIncident.put("subtype", feed_subtype);
							jIncident.put("polyline", feed_polyLine);
							jIncident.put("direction", feed_segmentDir);
							jIncident.put("street", feed_street);
							jIncident.put("starttime", feed_start_time);
							jIncident.put("endtime", feed_end_time);
							jIncident.put("description", feed_description);

							// Extended Data

							if (feedType.equals(FeedType.X.toString())) {
								jIncident.put("creationtime", feed_creation_time);
								jIncident.put("updatetime", feed_update_time);
								jIncident.put("name", feed_name);
								jIncident.put("url", feed_url);
								jIncident.put("reference", feed_id);
							}

							// Schedule

							if (!grpData.getScheduleJson().isEmpty())
								jIncident.put("schedule", grpData.getScheduleJson());

							// Add to array

							jaIncidents.put(jIncident);
						}
					}
				}
			}

			if (feedFormat.equals(FeedFormat.XML.toString())) {
				this.DOC.appendChild(elem_incidents);
			} else {
				jFeed.put("incidents", jaIncidents);
			}

		} catch (Exception e) {

			String errMsg = "GetFeed('" + feedFormat + "', '" + feedCountry + "'): " + e.toString();
			System.err.println(errMsg);

			String errorHint = "Ask fmondini what the hell happened - https://www.waze.com/discuss/u/fmondini/summary";

			if (feedFormat.equals(FeedFormat.XML.toString())) {

				try {

					this.DOC = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

					Element elem_err = _new_element("InternalError");
					Element elem_err_cifp = _new_element("Generator", AppCfg.getAppName() + " " + AppCfg.getAppVersion()); elem_err.appendChild(elem_err_cifp);
					Element elem_err_data = _new_element("ErrorThrown", e.toString()); elem_err.appendChild(elem_err_data);
					Element elem_err_hint = _new_element("Comments", errorHint); elem_err.appendChild(elem_err_hint);

					this.DOC.appendChild(elem_err);

				} catch (Exception ee) { }

			} else {

				jFeed = new JSONObject();
				JSONObject jError = new JSONObject();
				jError.put("Generator", AppCfg.getAppName() + " " + AppCfg.getAppVersion());
				jError.put("ErrorThrown", e.toString());
				jError.put("Comments", errorHint);
				jFeed.put("InternalError", jError);
			}
		}

		// Publish

		if (feedFormat.equals(FeedFormat.XML.toString())) {
			
			//
			// Generate and send to customer browser
			//

			try {
				
				DOMSource domSource = new DOMSource(this.DOC);
				StreamResult streamResult = new StreamResult(out);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();

				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // or ISO-8859-1
				transformer.setOutputProperty(OutputKeys.INDENT, "no");
				transformer.transform(domSource, streamResult);

			} catch (Exception e) {

				String errMsg = "GetFeed('" + feedFormat + "', '" + feedCountry + "'): " + e.toString();
				System.err.println(errMsg);
			}

		} else {
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.println(jFeed.toString(3));
			out.flush();
		}

		if (DB != null)
			DB.destroy();
	}

	/**
	 * Reverse a given polyline
	 */
	private static String _reverse_polyline(String lineString) throws Exception {

		String points[] = lineString.replace("LINESTRING(", "").replace(")", "").split(",");
		
		String polyLine = "";

		for (int i=points.length - 1; i>=0; i--) {

			polyLine += points[i];

			if (i > 0)
				polyLine += " ";
		}

		return(polyLine);
	}

	/**
	 * Create an XML element
	 */
	private Element _new_element(String name) {

		return(this.DOC.createElement(name));
	}

	/**
	 * Create an XML element with a value
	 */
	private Element _new_element(String name, String value) {

		Element elem = this.DOC.createElement(name);
		elem.appendChild(this.DOC.createTextNode(value));
		return(elem);
	}

	/**
	 * Create an XML element with a CDATA value
	 */
	private Element _new_element_cdata(String name, String value) {

		Element elem = this.DOC.createElement(name);
		elem.appendChild(this.DOC.createCDATASection(value));
		return(elem);
	}
}
