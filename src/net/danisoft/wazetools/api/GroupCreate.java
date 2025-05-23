////////////////////////////////////////////////////////////////////////////////////////////////////
//
// GroupCreate.java
//
// Create a closure group with segments
//
// First Release: Mar/2024 by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Ported to Waze dslib.jar
//                         Changed to @WebServlet style
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import net.danisoft.dslib.Database;
import net.danisoft.dslib.EnvTool;
import net.danisoft.dslib.FmtTool;
import net.danisoft.wtlib.auth.GeoIso;
import net.danisoft.wtlib.auth.User;
import net.danisoft.wazetools.AppCfg;
import net.danisoft.wazetools.cifp.CifpSubType;
import net.danisoft.wazetools.cifp.CifpType;
import net.danisoft.wazetools.cifp.Group;
import net.danisoft.wazetools.cifp.Period;
import net.danisoft.wazetools.cifp.Schedule;
import net.danisoft.wazetools.cifp.Segment;

@WebServlet(description = "Create a closure group with segments", urlPatterns = { "/api/group/create" })

public class GroupCreate extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	private static final CifpType DEFAULT_TYPE = CifpType.ROAD_CLOSED;
	private static final CifpSubType DEFAULT_SUBTYPE = CifpSubType.ROAD_CLOSED_CONSTRUCTION;
/*
	jPayload: {

		"source": "0.0.4.alpha",
		"apiKey": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
		"user": "fmondini",
		"city": "",
		"state": "Lombardia",
		"country": "Italy",
		"ctryIso": "IT",
		"wmeEnv": "row",
		"wmeLat": 45.55656,
		"wmeLng": 9.9337,
		"wmeZoom": 16,
		"groupId": "",
		"data": [
			{
				"street": "Via Vincenzo Gioberti",
				"polyline": "45.53802 9.93679, 45.53779 9.93676",
				"sid": 83344301
			}, {
				"street": "Via della Battaglia",
				"polyline": "45.53722 9.94097, 45.53726 9.94101, 45.53773 9.94126, 45.53782 9.94133, 45.53787 9.94142, 45.53791 9.94153, 45.53796 9.94212",
				"sid": 253320299
			}, {
				"street": "Via della Battaglia",
				"polyline": "45.53812 9.94304, 45.53818 9.94349",
				"sid": 397809899
			}
		]
	}
*/
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Database DB = null;

		JSONObject jResult = new JSONObject();

		String Payload = EnvTool.getStr(request, "payload", "{}");
		String CallBack = EnvTool.getStr(request, "callback", "");

		try {

			DB = new Database();

			JSONObject jPayload = new JSONObject(Payload);
			String iso3code = new GeoIso(DB.getConnection()).ReadByIso2(jPayload.getString("ctryIso")).getIso3();

			//
			// Check Script Version
			//

			_check_script_version(
				DB.getConnection(),
				jPayload.getString("source")
			);

			//
			// Check API-KEY
			//
			
			User USR = new User(DB.getConnection());
			User.Data usrData = USR.Read(jPayload.getString("user"));

			if (!jPayload.getString("apiKey").equals(usrData.getWazerConfig().getCifp().getApiKey())) {

				throw new Exception(
					"<b>Please install a valid API-KEY to continue</b><br>" +
					"If you don't have one, ask your country<br>" +
					"administrator to create one for you"
				);
			}

			//
			// Create a new WmeData
			//

			JSONObject jWmeData = new JSONObject();

			jWmeData.put("wmeEnv", jPayload.getString("wmeEnv"));
			jWmeData.put("wmeLat", jPayload.getDouble("wmeLat"));
			jWmeData.put("wmeLng", jPayload.getDouble("wmeLng"));
			jWmeData.put("wmeZoom", jPayload.getInt("wmeZoom"));

			//
			// Create a new GRP
			//

			Group GRP = new Group(DB.getConnection());
			Group.Data gidData = GRP.new Data();

			gidData.setSource(jPayload.getString("source"));
			gidData.setType(DEFAULT_TYPE.toString());
			gidData.setSubType(DEFAULT_SUBTYPE.toString());
			gidData.setActive(false);
			gidData.setDescription("");
			gidData.setPeriod(new Period().getJson());
			gidData.setSchedule(new Schedule().getJson());
			gidData.setWmeData(jWmeData);
			gidData.setCity(jPayload.getString("city"));
			gidData.setState(jPayload.getString("state"));
			gidData.setCountry(jPayload.getString("country"));
			gidData.setCtryIso(iso3code);
			gidData.setCreatedBy(jPayload.getString("user"));
			gidData.setCreatedAt(FmtTool.getCurrentTimestamp());
			gidData.setUpdatedBy("");
			gidData.setUpdatedAt(FmtTool.DATEZERO);

			String newGid = GRP.Insert(gidData);

			//
			// Create SIDs
			//

			Segment SID = new Segment(DB.getConnection());
			Segment.Data sidData = SID.new Data();

			JSONObject jSegment;

			for (int i=0; i<jPayload.getJSONArray("data").length(); i++) {

				jSegment = jPayload.getJSONArray("data").getJSONObject(i);

				sidData.setGroup(newGid);
				sidData.setWmeID(jSegment.getInt("sid"));								// "sid": 178969879
				sidData.setStreet(jSegment.getString("street").replace("'", "\\'"));	// "street": "Via Po"
				sidData.setPolyline(jSegment.getString("polyline"));					// "polyline": "45.5385354 9.9396473, 45.5382632 9.9396126"

				SID.Insert(sidData);
			}

			//
			// DONE
			//

			jResult.put("status", "OK");
			jResult.put("gid", newGid);

		} catch (Exception e) {

			System.err.println(e.toString());

			jResult = new JSONObject();
			jResult.put("status", "KO");
			jResult.put("error", e.getMessage());
		}

		if (DB != null)
			DB.destroy();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getOutputStream().println(CallBack + "(" + jResult.toString() + ")");
	}

	/**
	 * Check script version
	 * @throws an exception if different
	 */
	private static void _check_script_version(Connection cn, String currentVersion) throws Exception {

		final String CODE_TBL_NAME = "CODE_scripts";
		final String CODE_HOMEPAGE = "https://code.waze.tools/home/browse.jsp?ShowSID=" + AppCfg.getCifpScriptId();

		Statement st = cn.createStatement();

		ResultSet rs = st.executeQuery(
			"SELECT SCR_ID, SCR_Title, SCR_Major, SCR_Minor, SCR_Build " +
			"FROM " + CODE_TBL_NAME + " " +
			"WHERE SCR_ID = '" + AppCfg.getCifpScriptId() + "'"
		);

		if (!rs.next())
			throw new Exception("<b>INTERNAL ERROR</b><br>ScriptID " + AppCfg.getCifpScriptId() + "<br>not found in " + CODE_TBL_NAME + " table");

		String SCR_Major = rs.getString("SCR_Major");
		String SCR_Minor = rs.getString("SCR_Minor");
		String SCR_Build = rs.getString("SCR_Build");

		rs.close();
		st.close();

		String[] CurrVers = currentVersion.split("\\.");

		String zpCodeVers = String.format("%05d%05d%05d", Integer.parseInt(SCR_Major), Integer.parseInt(SCR_Minor), Integer.parseInt(SCR_Build));
		String zpUserVers = String.format("%05d%05d%05d", Integer.parseInt(CurrVers[0]), Integer.parseInt(CurrVers[1]), Integer.parseInt(CurrVers[2]));

		if (Long.parseLong(zpCodeVers) > Long.parseLong(zpUserVers)) {
			throw new Exception(
				"<b>SCRIPT VERSION " + currentVersion + " IS OUTDATED</b><br>" +
				"<br>" +
				"<div style=\"font-size: 14px\">" +
					"Update this script to the latest version <b>" + SCR_Major + "." + SCR_Minor + "." + SCR_Build + "</b> by<br>" +
					"downloading it from the <a href=\"" + CODE_HOMEPAGE + "\" target=\"_blank\">Waze.Tools CODE</a> official repository.<br>" +
				"</div>" +
				"<br>" +
				"<div style=\"font-size: 16px\">" +
					"<i style=\"color:red\">Please note that this closure was <b>not accepted</b>.<br>" +
					"Once the correct script is installed, you will need to resubmit it</i>" +
				"</div>"
			);
		}
	}

}
