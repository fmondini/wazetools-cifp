////////////////////////////////////////////////////////////////////////////////////////////////////
//
// UpdateFieldGRP.java
//
// Update a GID field
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

@WebServlet(description = "Update a GID field", urlPatterns = { "/servlet/grpUpdFld" })

public class UpdateFieldGRP extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Database DB = null;
		JSONObject jResult;

		String gid = EnvTool.getStr(request, "gid", SysTool.getEmptyUuidValue());
		String fld = EnvTool.getStr(request, "fld", "");
		String val = EnvTool.getStr(request, "val", "");

		try {

			DB = new Database();
			Group GRP = new Group(DB.getConnection());

			// Update Field
			GRP.setField(gid, fld, val, SysTool.getCurrentUser(request));

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
