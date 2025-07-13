////////////////////////////////////////////////////////////////////////////////////////////////////
//
// SchedMonToOtherDays.java
//
// Duplicate Monday data to other days
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
import net.danisoft.wazetools.cifp.Schedule;

@WebServlet(description = "Duplicate Monday Schedule Slot", urlPatterns = { "/servlet/schedDuplicateMonday" })

public class SchedDuplicateMonday extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Database DB = null;
		JSONObject jSchedule, jResult;

		String gid = EnvTool.getStr(request, "gid", SysTool.getEmptyUuidValue());
		String ext = EnvTool.getStr(request, "ext", "");

		try {

			DB = new Database();

			Group GRP = new Group(DB.getConnection());
			Group.Data grpData = GRP.Read(gid);

			if (grpData.getID().equals(SysTool.getEmptyUuidValue()))
				throw new Exception("SchedDuplicateMonday.doPost(): Cannot find GID ID '" + gid + "'");

			if (grpData.isMondayScheduleEmpty())
				throw new Exception("Cannot duplicate data: Monday scheduler is empty");

			if (!ext.equals("WD") && !ext.equals("AW"))
				throw new Exception("Cannot duplicate data: WD or AW needed, Report to SysOp");

			jSchedule = grpData.getSchedule();
			String mondaySchedule = jSchedule.getString(Schedule.DayNameAbbr[0]);

			final int FRIDAY_DAY_NO = 5;

			// Tue-Fri in every case

			if (ext.equals("WD") || ext.equals("AW")) {
				for (int i=1; i<FRIDAY_DAY_NO; i++)
					jSchedule.put(Schedule.DayNameAbbr[i], mondaySchedule);
			}

			// Sat-Sun only on AW

			if (ext.equals("AW")) {
				for (int i=FRIDAY_DAY_NO; i<Schedule.DAYS_NO; i++)
					jSchedule.put(Schedule.DayNameAbbr[i], mondaySchedule);
			}

			// Update schedule
			GRP.setField(gid, "GRP_Schedule", jSchedule.toString(), SysTool.getCurrentUser(request));

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
