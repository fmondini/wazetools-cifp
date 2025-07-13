////////////////////////////////////////////////////////////////////////////////////////////////////
//
// SchedClean.java
//
// Clean ALL GID's Schedule Slots
//
// First Release: Jul/2025 by Fulvio Mondini (https://danisoft.software/)
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

@WebServlet(description = "Clean ALL GID Schedule Slots", urlPatterns = { "/servlet/schedClean" })

public class SchedClean extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Database DB = null;
		JSONObject jResult;
		String gid = EnvTool.getStr(request, "gid", SysTool.getEmptyUuidValue());

		try {

			DB = new Database();

			Group GRP = new Group(DB.getConnection());
			Group.Data grpData = GRP.Read(gid);

			if (grpData.getID().equals(SysTool.getEmptyUuidValue()))
				throw new Exception("SchedClean.doPost(): Cannot find GID ID '" + gid + "'");

			// Clean schedule
			GRP.setField(gid, "GRP_Schedule", new JSONObject().toString(), SysTool.getCurrentUser(request));

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
