////////////////////////////////////////////////////////////////////////////////////////////////////
//
// ExportSegs.java
//
// Export segments in the given format
//
// First Release: Mar/2024 by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Changed to @WebServlet style
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.opencsv.CSVWriter;

import net.danisoft.dslib.Database;
import net.danisoft.dslib.EnvTool;
import net.danisoft.dslib.FmtTool;
import net.danisoft.dslib.SysTool;
import net.danisoft.dslib.ZipTool;
import net.danisoft.wazetools.AppCfg;
import net.danisoft.wazetools.cifp.Group;
import net.danisoft.wazetools.cifp.Segment;

@WebServlet(description = "Export segments in the given format", urlPatterns = { "/servlet/exportSegs" })

public class ExportSegs extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Database DB = null;

		String gid = EnvTool.getStr(request, "gid", SysTool.getEmptyUuidValue());
		String DataType = EnvTool.getStr(request, "type", "");

		int bytes;
		String Result;

		try {

			DB = new Database();

			Group GRP = new Group(DB.getConnection());
			Group.Data grpData = GRP.Read(gid);

			Segment SEG = new Segment(DB.getConnection());
			Vector<Segment.Data> vecSegObj = SEG.getAll(gid);

			if (vecSegObj.isEmpty())
				throw new Exception("No segments found for this GRP");

			if (DataType.equals("JSON"))	{ Result = _export_data_json(vecSegObj);			} else
			if (DataType.equals("WKT"))		{ Result = _export_data_wkt(vecSegObj);				} else
			if (DataType.equals("KML"))		{ Result = _export_data_kml(grpData, vecSegObj);	} else
			if (DataType.equals("CSV"))		{ Result = _export_data_csv(grpData, vecSegObj);	} else
			{
				throw new Exception("Unknown dataType '" + DataType + "'");
			}

			// Create and Compress file

		    ZipTool ZIP = new ZipTool("CIFP-" + DataType, "zip");

		    ZIP.CreateFromString(
		    	Result,
		    	grpData.getID() + "." + DataType.toLowerCase(),
		    	grpData.getID()
		    );

		    // Done

			response.setContentType("application/zip");
			response.setHeader("Content-Disposition", "attachment;filename=\"" + ZIP.getFinalZipName() + "\"");
			OutputStream out = response.getOutputStream();

			FileInputStream fis = new FileInputStream(ZIP.getFinalZipNameFull());

	        while ((bytes = fis.read()) != -1)
	        	out.write(bytes);

	        fis.close();
	        out.flush();

		} catch (Exception e) {

			System.err.println(e.toString());
			response.getOutputStream().println(e.toString());
		}

		if (DB != null)
			DB.destroy();

		response.flushBuffer();
	}

	/**
	 * Export Data (JSON worker)
	 * @throws Exception
	 */
	private static String _export_data_json(Vector<Segment.Data> vecSegObj) throws Exception {

		String saCoord[];
		JSONObject jCoord;
		JSONObject jGColl = new JSONObject();
		JSONArray jaLines = new JSONArray();
		JSONArray jaGColl = new JSONArray();

		for (Segment.Data segData : vecSegObj) {

			jaLines = new JSONArray();
			saCoord = segData.getPolylineText().replace("LINESTRING(", "").replace(")", "").split(",");

			for (int i=0; i<saCoord.length; i++) {

				jCoord = new JSONObject();
				jCoord.put("lat", saCoord[i].split(" ").clone()[0]);
				jCoord.put("lon", saCoord[i].split(" ").clone()[1]);
				jaLines.put(jCoord);
			}
			
			jaGColl.put(jaLines);
		}

		jGColl.put("geometry", jaGColl);

		return(jGColl.toString());
	}

	/**
	 * Export Data (WKT worker)
	 * @throws Exception
	 */
	private static String _export_data_wkt(Vector<Segment.Data> vecSegObj) throws Exception {

		String wktData = "GEOMETRYCOLLECTION(";

		for (Segment.Data segData : vecSegObj) {

			wktData += (wktData.endsWith(")") ? "," : "") + "LINESTRING(";
			String saCoord[] = segData.getPolylineText().replace("LINESTRING(", "").replace(")", "").split(",");

			for (int i=0; i<saCoord.length; i++)
				wktData +=
					(wktData.endsWith("(") ? "" : ",") +
					(saCoord[i].split(" ").clone()[1]) +
					" " +
					(saCoord[i].split(" ").clone()[0])
				;

			wktData += ")";
		}

		wktData += ")";

		return(wktData);
	}

	/**
	 * Export Data (KML worker)
	 * @throws Exception
	 */
	private static String _export_data_kml(Group.Data grpData, Vector<Segment.Data> vecSegObj) throws Exception {

		final String LineStyleID = "CIFPDefaultLineStyle";
		final String LineStyleColor = "ff0000ff";
		final String LineStyleOuterColor = "ffffffff";
		final String LineStyleOuterWidth = "0.30";
		final String LineStylePhysicalWidth = "7.00"; // In meters

		String kmlData = "Error in KML Data";

		try {

			Document DOC = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			// KML
			Element elem_Kml = DOC.createElement("kml");
			elem_Kml.setAttribute("xmlns", "http://www.opengis.net/kml/2.2");
			elem_Kml.setAttribute("xmlns:gx", "http://www.google.com/kml/ext/2.2");
			elem_Kml.setAttribute("xmlns:kml", "http://www.opengis.net/kml/2.2");
			elem_Kml.setAttribute("xmlns:atom", "http://www.w3.org/2005/Atom");
			DOC.appendChild(elem_Kml);

			// Document
			Element elem_Document = DOC.createElement("Document");
			elem_Kml.appendChild(elem_Document);

			// Document_name
			Element elem_Document_name = DOC.createElement("name");
			elem_Document_name.appendChild(DOC.createTextNode(grpData.getDescription()));
			elem_Document.appendChild(elem_Document_name);

			// Document_open
			Element elem_Document_open = DOC.createElement("open");
			elem_Document_open.appendChild(DOC.createTextNode("1"));
			elem_Document.appendChild(elem_Document_open);

			// Document_description
			Element elem_Document_description = DOC.createElement("description");
			elem_Document_description.appendChild(DOC.createTextNode("Generated by " + AppCfg.getAppName() + " " + AppCfg.getAppVersion() + " build " + AppCfg.getAppRelDate()));
			elem_Document.appendChild(elem_Document_description);

			// Document_Style
			Element elem_Document_Style = DOC.createElement("Style");
			elem_Document_Style.setAttribute("id", LineStyleID);
			elem_Document.appendChild(elem_Document_Style);

			// Document_Style_LineStyle
			Element elem_Document_Style_LineStyle = DOC.createElement("LineStyle");
			elem_Document_Style.appendChild(elem_Document_Style_LineStyle);

			// Document_Style_LineStyle_color
			Element elem_Document_Style_LineStyle_color = DOC.createElement("color");
			elem_Document_Style_LineStyle_color.appendChild(DOC.createTextNode(LineStyleColor));
			elem_Document_Style_LineStyle.appendChild(elem_Document_Style_LineStyle_color);

			// Document_Style_LineStyle_colorMode
			Element elem_Document_Style_LineStyle_colorMode = DOC.createElement("colorMode");
			elem_Document_Style_LineStyle_colorMode.appendChild(DOC.createTextNode("normal"));
			elem_Document_Style_LineStyle.appendChild(elem_Document_Style_LineStyle_colorMode);

			// Document_Style_LineStyle_gx:outerColor
			Element elem_Document_Style_LineStyle_gx_outerColor = DOC.createElement("gx:outerColor");
			elem_Document_Style_LineStyle_gx_outerColor.appendChild(DOC.createTextNode(LineStyleOuterColor));
			elem_Document_Style_LineStyle.appendChild(elem_Document_Style_LineStyle_gx_outerColor);

			// Document_Style_LineStyle_gx:outerWidth
			Element elem_Document_Style_LineStyle_gx_outerWidth = DOC.createElement("gx:outerWidth");
			elem_Document_Style_LineStyle_gx_outerWidth.appendChild(DOC.createTextNode(LineStyleOuterWidth));
			elem_Document_Style_LineStyle.appendChild(elem_Document_Style_LineStyle_gx_outerWidth);

			// Document_Style_LineStyle_gx:physicalWidth
			Element elem_Document_Style_LineStyle_gx_physicalWidth = DOC.createElement("gx:physicalWidth");
			elem_Document_Style_LineStyle_gx_physicalWidth.appendChild(DOC.createTextNode(LineStylePhysicalWidth));
			elem_Document_Style_LineStyle.appendChild(elem_Document_Style_LineStyle_gx_physicalWidth);

			//
			// Placemarks Loop
			//

			String segmentCoords;

			for (Segment.Data segData : vecSegObj) {

				String saCoord[] = segData.getPolylineText().replace("LINESTRING(", "").replace(")", "").split(",");

				segmentCoords = "";

				for (int i=0; i<saCoord.length; i++)
					segmentCoords += (
						saCoord[i].split(" ").clone()[1] + "," +
						saCoord[i].split(" ").clone()[0] + "," +
						"0 "
					);

				// Document_Placemark
				Element elem_Document_Placemark = DOC.createElement("Placemark");
				elem_Document.appendChild(elem_Document_Placemark);

				// Document_Placemark_name
				Element elem_Document_Placemark_name = DOC.createElement("name");
				elem_Document_Placemark_name.appendChild(DOC.createTextNode(segData.getStreet() + " (Waze ID " + segData.getWmeID() + ")"));
				elem_Document_Placemark.appendChild(elem_Document_Placemark_name);

				// Document_Placemark_description
				Element elem_Document_Placemark_description = DOC.createElement("description");
				elem_Document_Placemark_description.appendChild(DOC.createTextNode("CIFP ID " + segData.getID()));
				elem_Document_Placemark.appendChild(elem_Document_Placemark_description);

				// Document_Placemark_styleUrl
				Element elem_Document_Placemark_styleUrl = DOC.createElement("styleUrl");
				elem_Document_Placemark_styleUrl.appendChild(DOC.createTextNode("#" + LineStyleID));
				elem_Document_Placemark.appendChild(elem_Document_Placemark_styleUrl);

				// Document_Placemark_LineString
				Element elem_Document_Placemark_LineString = DOC.createElement("LineString");
				elem_Document_Placemark.appendChild(elem_Document_Placemark_LineString);

				// Document_Placemark_LineString_extrude
				Element elem_Document_Placemark_LineString_extrude = DOC.createElement("extrude");
				elem_Document_Placemark_LineString_extrude.appendChild(DOC.createTextNode("1"));
				elem_Document_Placemark_LineString.appendChild(elem_Document_Placemark_LineString_extrude);

				// Document_Placemark_LineString_tessellate
				Element elem_Document_Placemark_LineString_tessellate = DOC.createElement("tessellate");
				elem_Document_Placemark_LineString_tessellate.appendChild(DOC.createTextNode("1"));
				elem_Document_Placemark_LineString.appendChild(elem_Document_Placemark_LineString_tessellate);

				// Document_Placemark_LineString_altitudeMode
				Element elem_Document_Placemark_LineString_altitudeMode = DOC.createElement("altitudeMode");
				elem_Document_Placemark_LineString_altitudeMode.appendChild(DOC.createTextNode("clampToGround"));
				elem_Document_Placemark_LineString.appendChild(elem_Document_Placemark_LineString_altitudeMode);

				// Document_Placemark_LineString_coordinates
				Element elem_Document_Placemark_LineString_coordinates = DOC.createElement("coordinates");
				elem_Document_Placemark_LineString_coordinates.appendChild(DOC.createTextNode(segmentCoords));
				elem_Document_Placemark_LineString.appendChild(elem_Document_Placemark_LineString_coordinates);
			}

			//
			// Generate
			//

			StringWriter swOut = new StringWriter();

			TransformerFactory.newInstance().newTransformer().transform(
				new DOMSource(DOC),
				new StreamResult(swOut)
			);

			kmlData = swOut.toString()
			/*
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
			*/
			;

		} catch (Exception e) {
			kmlData = e.toString();
		}

		return(kmlData);
	}

	/**
	 * Export Data (CSV worker)
	 * @throws Exception
	 */
	private static String _export_data_csv(Group.Data grpData, Vector<Segment.Data> vecSegObj) throws Exception {

		String csvData = "";
		List<String[]> csvLines = new ArrayList<>();

		try {

			StringWriter sw = new StringWriter();
			CSVWriter csvWriter = new CSVWriter(sw);

			csvWriter.writeNext(
				new String[] {
					// Group
					"GroupID",
					"Source",
					"Type",
					"SubType",
					"Description",
					"Country",
					"State",
					"City",
					"Period",
					"Schedule",
					"CreatedBy",
					"CreatedAt",
					"UpdatedBy",
					"UpdatedAt",
					// Segment
					"SegmentID",
					"WmeID",
					"Street",
					"Polyline"
				}
			);

			for (Segment.Data segData : vecSegObj) {
				csvLines.add(new String[] {
					// Group
					grpData.getID(),
					grpData.getSource(),
					grpData.getType(),
					grpData.getSubType(),
					grpData.getDescription(),
					grpData.getCountry(),
					grpData.getState(),
					grpData.getCity(),
					grpData.getPeriod().toString(),
					grpData.getSchedule().toString(),
					grpData.getCreatedBy(),
					FmtTool.fmtDateTimeSqlStyle(grpData.getCreatedAt()),
					grpData.getUpdatedBy(),
					FmtTool.fmtDateTimeSqlStyle(grpData.getUpdatedAt()),
					// Segment
					segData.getID(),
					Integer.toString(segData.getWmeID()),
					segData.getStreet(),
					segData.getPolylineText()
				});
			}

			csvWriter.writeAll(csvLines);
			csvWriter.close();
			csvData = sw.toString();

		} catch (Exception e) {
			csvData = e.getMessage();
		}

		return(csvData);
	}

}
