////////////////////////////////////////////////////////////////////////////////////////////////////
//
// GetConfig.java
//
// Retrieve a CIFP config in JSON format
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

import net.danisoft.dslib.EnvTool;
import net.danisoft.dslib.FmtTool;

@WebServlet(description = "Retrieve a CIFP config in JSON format", urlPatterns = { "/api/getcfg" })

public class GetConfig extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	private static final String BUG_REPORT_URL = "https://github.com/fmondini/wazetools-cifp/issues";

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String CallBack = EnvTool.getStr(request, "callback", "");

		JSONObject jConfig = new JSONObject();

		try {

			//
			// ABOUT
			//

			JSONObject jAbout = new JSONObject();

			jAbout.put("lblBugsRpt", "Report bugs and enhancement <a href=\"" + BUG_REPORT_URL + "\" target=\"_blank\">here</a>");

			jConfig.put("About", jAbout);

			//
			// MESSAGES
			//

			JSONObject jMessages = new JSONObject();

			jMessages.put("lblApiKeyHead", "Enter your API-KEY");
			jMessages.put("lblGrpSaving", "Saving the Closures Group");
			jMessages.put("lblPleaseWait", "Please wait just a moment...");

			jConfig.put("Messages", jMessages);

			//
			// ERRORS
			//

			JSONObject jErrors = new JSONObject();

			jErrors.put("lblHal9000", "I'm sorry, Dave... I'm afraid I can't do that.");
			jErrors.put("lblNoSelObj", "<b>No segments selected</b><br>Please select one or more segment(s) to close");
			jErrors.put("lblSeeConsole", "See your browser console (F12) for more details (if available)");

			jConfig.put("Errors", jErrors);

			//
			// RESULTS
			//

			JSONObject jResults = new JSONObject();

			jResults.put("lblGrpHeadOK", "Closure group has been saved");
			jResults.put("lblGrpNextOK", "Click on the GroupID to configure closures<br>directly in the Waze.Tools CIFP WebApp");

			jResults.put("lblGrpHeadKO", "Error saving Closure Group");
			
			jConfig.put("Results", jResults);

			//
			// DONE
			//

			jConfig.put("rc", HttpServletResponse.SC_OK);

		} catch (Exception e) {

			jConfig = new JSONObject();
			jConfig.put("rc", HttpServletResponse.SC_NOT_ACCEPTABLE);
			jConfig.put("error", e.toString());
		}

		response.getOutputStream().println(CallBack + "(" + jConfig.toString() + ")");
	}
	
}
