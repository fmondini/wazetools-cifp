////////////////////////////////////////////////////////////////////////////////////////////////////
//
// ToggleSegmentDir.java
//
// Toggle Segment direction
//
// First Release: May/2024 by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Changed to @WebServlet style
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.servlets;

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
import net.danisoft.dslib.SysTool;
import net.danisoft.wazetools.cifp.CifpSegDir;
import net.danisoft.wazetools.cifp.Group;
import net.danisoft.wazetools.cifp.Segment;

@WebServlet(description = "Toggle Segment direction", urlPatterns = { "/servlet/toggleSegmentDir" })

public class ToggleSegmentDir extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Database DB = null;

		JSONObject jResult;

		String segId = EnvTool.getStr(request, "segId", SysTool.getEmptyUuidValue());

		try {

			DB = new Database();

			Segment SEG = new Segment(DB.getConnection());
			Segment.Data segData = SEG.Read(segId);

			if (segData.getID().equals(SysTool.getEmptyUuidValue()))
				throw new Exception("Cannot find SID '" + segId + "'");

			CifpSegDir cifpSegDir = segData.getDirection();

			if (cifpSegDir.equals(CifpSegDir.ALL)) { cifpSegDir = CifpSegDir.FWD; } else
			if (cifpSegDir.equals(CifpSegDir.FWD)) { cifpSegDir = CifpSegDir.BCK; } else
			if (cifpSegDir.equals(CifpSegDir.BCK)) { cifpSegDir = CifpSegDir.ALL; } else
			{
				throw new Exception("Bad segData.getDirection(): '" + segData.getDirection().toString() + "'");
			}

			SEG.updateDirection(segId, cifpSegDir);

			// Force entire group validation

			Group GRP = new Group(DB.getConnection());
			GRP.setField(segData.getGroup(), "GRP_isActive", "N", SysTool.getCurrentUser(request));

			// Result

			jResult = new JSONObject();

			jResult.put("status", "OK");
			jResult.put("segId", segData.getID());
			jResult.put("segDir", cifpSegDir.toString());

		} catch (Exception e) {

			System.err.println("ReverseSegment.doGet(): " + e.toString());

			jResult = new JSONObject();
			jResult.put("rc", "KO");
			jResult.put("msg", "ReverseSegment.doGet(): " + e.toString());
		}

		if (DB != null)
			DB.destroy();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getOutputStream().println(jResult.toString(3));
	}

}
