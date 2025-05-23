////////////////////////////////////////////////////////////////////////////////////////////////////
//
// SchedDelSlot.java
//
// Delete a GID Schedule Slot
//
// First Release: Mar/2024 by Fulvio Mondini (https://danisoft.software/)
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
import net.danisoft.wazetools.cifp.Group;

@WebServlet(description = "Delete a GID Schedule Slot", urlPatterns = { "/servlet/schedDelSlot" })

public class SchedDelSlot extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Database DB = null;

		JSONObject jResult;

		String gid = EnvTool.getStr(request, "gid", SysTool.getEmptyUuidValue());
		String day = EnvTool.getStr(request, "day", "");
		String min = EnvTool.getStr(request, "min", "");
		String max = EnvTool.getStr(request, "max", "");

		try {

			DB = new Database();

			Group GRP = new Group(DB.getConnection());
			Group.Data grpData = GRP.Read(gid);

			if (grpData.getID().equals(SysTool.getEmptyUuidValue()))
				throw new Exception("SchedDelSlot.doPost(): Cannot find GID ID '" + gid + "'");

			GRP.delSlot(grpData, day, min, max, SysTool.getCurrentUser(request));

			// Force validation
			GRP.setField(gid, "GRP_isActive", "N", SysTool.getCurrentUser(request));

			jResult = new JSONObject();
			jResult.put("status", "OK");

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
		response.getOutputStream().println(jResult.toString());
	}

}
