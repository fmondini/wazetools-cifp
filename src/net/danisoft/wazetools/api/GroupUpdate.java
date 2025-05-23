////////////////////////////////////////////////////////////////////////////////////////////////////
//
// GroupUpdate.java
//
// Create a closure group with segments
//
// First Release: Apr/2024 by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Ported to Waze dslib.jar
//                         Changed to @WebServlet style
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import net.danisoft.dslib.Database;
import net.danisoft.dslib.EnvTool;
import net.danisoft.dslib.FmtTool;
import net.danisoft.wtlib.auth.User;
import net.danisoft.wazetools.cifp.Group;
import net.danisoft.wazetools.cifp.Segment;

@WebServlet(description = "Update a closure group with segments", urlPatterns = { "/api/group/update" })

public class GroupUpdate extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		"groupId": "ID_HERE",
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
		Database DB = null;

		JSONObject jResult = new JSONObject();
		
		String Payload = EnvTool.getStr(request, "payload", "{}");
		String CallBack = EnvTool.getStr(request, "callback", "");

		try {

			DB = new Database();
			Group GRP = new Group(DB.getConnection());
			Segment SEG = new Segment(DB.getConnection());

			JSONObject jPayload = new JSONObject(Payload);

			//
			// Check API-KEY
			//
			
			User USR = new User(DB.getConnection());
			User.Data usrData = USR.Read(jPayload.getString("user"));

			if (!jPayload.getString("apiKey").equals(usrData.getWazerConfig().getCifp().getApiKey()))
				throw new Exception(
					"<b>Please install a valid API-KEY to continue</b><br>" +
					"If you don't have one, ask your country<br>" +
					"administrator to create one for you"
				);

			// Clean previous segments

			Group.Data grpData = GRP.Read(jPayload.getString("groupId"));
			SEG.delAll(grpData.getID());

			// Insert new segments

			JSONObject jSegment;
			Segment.Data segData = SEG.new Data();

			for (int i=0; i<jPayload.getJSONArray("data").length(); i++) {

				jSegment = jPayload.getJSONArray("data").getJSONObject(i);

				segData.setGroup(grpData.getID());
				segData.setWmeID(jSegment.getInt("sid"));								// "sid": 178969879
				segData.setStreet(jSegment.getString("street").replace("'", "\\'"));	// "street": "Via Po"
				segData.setPolyline(jSegment.getString("polyline"));					// "polyline": "45.5385354 9.9396473, 45.5382632 9.9396126"

				SEG.Insert(segData);
			}

			// Force group validation

			GRP.setField(grpData.getID(), "GRP_isActive", "N", jPayload.getString("user"));

			//
			// DONE
			//

			jResult.put("status", "OK");
			jResult.put("gid", grpData.getID());

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
}
