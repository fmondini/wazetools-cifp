////////////////////////////////////////////////////////////////////////////////////////////////////
//
// CronJobs.java
//
// CRON jobs to manage closures (to be run every night)
//
// First Release: Jun/2024 by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Changed to @WebServlet style
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import net.danisoft.dslib.Database;
import net.danisoft.dslib.EnvTool;
import net.danisoft.dslib.FmtTool;
import net.danisoft.dslib.LogTool;
import net.danisoft.dslib.Mail;
import net.danisoft.dslib.SysTool;
import net.danisoft.wtlib.auth.User;
import net.danisoft.wazetools.AppCfg;
import net.danisoft.wazetools.cifp.Group;
import net.danisoft.wazetools.cifp.Segment;

@WebServlet(description = "CRON jobs to manage closures (to be run every night)", urlPatterns = { "/servlet/cronJobs" })

public class CronJobs extends HttpServlet {

	private static final long serialVersionUID = FmtTool.getSerialVersionUID();

	private static final String SERVLET_USERNAME_FOR_UPDATES = "Admin"; // Must exists in table AUTH_users

	private Database			_Database;
	private Group				_Group;
	private Segment				_Segment;
	private User				_User;
	private Mail				_Mail;
	private boolean				_RealWork;
	private HttpServletRequest	_HttpRequest;

	// Getters
	public Database				getDatabase()		{ return(this._Database);		}
	public Group				getGroup()			{ return(this._Group);			}
	public Segment				getSegment()		{ return(this._Segment);		}
	public User					getUser()			{ return(this._User);			}
	public Mail					getMail()			{ return(this._Mail);			}
	public boolean				isRealWork()		{ return(this._RealWork);		}
	public HttpServletRequest	getHttpRequest()	{ return(this._HttpRequest);	}

	// Setters
	public void setDatabase(Database d)					{ this._Database = d;		}
	public void setGroup(Group g)						{ this._Group = g;			}
	public void setSegment(Segment s)					{ this._Segment = s;		}
	public void setUser(User u)							{ this._User = u;			}
	public void setMail(Mail m)							{ this._Mail = m;			}
	public void setRealWork(boolean b)					{ this._RealWork = b;		}
	public void setHttpRequest(HttpServletRequest r)	{ this._HttpRequest = r;	}

	/**
	 * Process Type
	 */
	private enum ProcessType {
		EXPIRING, EXPIRED, ORPHANS;
	}

	/**
	 * Entry point
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		setHttpRequest(request);
		setRealWork(EnvTool.getStr(request, "realWork", "N").equals("Y"));

		String JobType = EnvTool.getStr(request, "job", "");
		JSONObject jResult = new JSONObject();

		try {

			setDatabase(new Database());
			setGroup(new Group(getDatabase().getConnection()));
			setSegment(new Segment(getDatabase().getConnection()));
			setUser(new User(getDatabase().getConnection()));

			JSONArray jaMail = _process_all_closures(JobType);

			jResult.put("rc", "OK");
			jResult.put("job", JobType);
			jResult.put("real", isRealWork());
			jResult.put("mail", jaMail);

		} catch (Exception e) {

			System.err.println("CronJobs(): " + e.toString());

			jResult = new JSONObject();
			jResult.put("rc", "KO");
			jResult.put("job", JobType);
			jResult.put("real", isRealWork());
			jResult.put("error", e.getMessage());
		}

		if (getDatabase() != null)
			getDatabase().destroy();

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getOutputStream().println(jResult.toString());
	}

	/**
	 * Process all closures
	 * @throws Exception
	 */
	private JSONArray _process_all_closures(String jobType) throws Exception {

		JSONArray jaMail = new JSONArray();

		// Query Filter

		String qryFilter;

		if (jobType.equalsIgnoreCase("ExpiringAlerts"))	{ qryFilter = "GRP_isUnedited = 'N' AND GRP_isActive = 'Y'";	} else
		if (jobType.equalsIgnoreCase("ProcessExpired"))	{ qryFilter = "GRP_isUnedited = 'N' AND GRP_isActive = 'Y'";	} else
		if (jobType.equalsIgnoreCase("CleanOrphans"))	{ qryFilter = "GRP_isUnedited = 'Y'";							} else
			throw new Exception(
				"_process_all_closures(); Bad job type: '" + jobType + "'"
			);

		// Loop

		Date expireDate;
		String nowDateOnly, nowDateTime;
		String expDateOnly, expDateTime;
		String ccdDateTime, tmrDateTime;
		Vector<Group.Data> vecGrpdata = getGroup().getAll(qryFilter, null);

		for (Group.Data grpData : vecGrpdata) {

			expireDate = FmtTool.scnDateTimeSqlStyle(
				grpData.getPeriod().getJSONObject("max").getString("date") + " " +
				grpData.getPeriod().getJSONObject("max").getString("time") + ":00"
			);

			ccdDateTime = FmtTool.fmtDateTimeServletStyle(grpData.getCreatedAt());

			nowDateOnly = FmtTool.fmtDateServletStyle();
			nowDateTime = FmtTool.fmtDateTimeServletStyle();

			expDateOnly = FmtTool.fmtDateServletStyle(expireDate);
			expDateTime = FmtTool.fmtDateTimeServletStyle(expireDate);

			tmrDateTime = FmtTool.fmtDateTimeServletStyle(FmtTool.addDays(new Date(), 1));

			if (SysTool.isWindog()) { // Dev Test
				nowDateOnly = "20250430";
				nowDateTime = "20250430235959";
				tmrDateTime = "20250426235959";
			}

			// Check expire date and process closure

			if (jobType.equalsIgnoreCase("ExpiringAlerts")) {

				if (nowDateOnly.equals(expDateOnly))
					jaMail.put(_process_closure(grpData, expDateOnly, ProcessType.EXPIRING));

			} else if (jobType.equalsIgnoreCase("ProcessExpired")) {

				if (nowDateTime.compareTo(expDateTime) >= 0)
					jaMail.put(_process_closure(grpData, expDateTime, ProcessType.EXPIRED));

			} else if (jobType.equalsIgnoreCase("CleanOrphans")) {

				if (tmrDateTime.compareTo(ccdDateTime) >= 0)
					jaMail.put(_process_closure(grpData, null, ProcessType.ORPHANS));
			}
		}

		return(jaMail);
	}

	/**
	 * Process closure
	 * @throws Exception
	 */
	private JSONObject _process_closure(Group.Data grpData, String expDateRaw, ProcessType processType) throws Exception {

		// Expire Date

		String ExpireDate = "N/A";

		if (processType.equals(ProcessType.EXPIRING))
			ExpireDate = FmtTool.fmtDate(FmtTool.scnDateServletStyle(expDateRaw));
		else if (processType.equals(ProcessType.EXPIRED))
			ExpireDate = FmtTool.fmtDateTimeNoSecs(FmtTool.scnDateTimeServletStyle(expDateRaw));

		// Process closure

		JSONObject jResult = _send_mail(grpData, ExpireDate, grpData.getCreatedBy(), processType);

		if (processType.equals(ProcessType.EXPIRING)) {

		} else if (processType.equals(ProcessType.EXPIRED)) {

			// Pause or Delete closure

			if (isRealWork()) {

				jResult.put("actn", "Closure " + (grpData.isDelAfter() ? "Deleted" : "Paused"));

				if (grpData.isDelAfter()) {
					getGroup().Delete(grpData.getID());
					getSegment().delAll(grpData.getID());
				} else {
					getGroup().setField(grpData.getID(), "GRP_isActive", "N", SERVLET_USERNAME_FOR_UPDATES);
				}

			} else
				jResult.put("actn", "[SIMUL] Closure " + (grpData.isDelAfter() ? "Deleted" : "Paused"));

		} else if (processType.equals(ProcessType.ORPHANS)) {

			if (isRealWork()) {
				getGroup().Delete(grpData.getID());
				getSegment().delAll(grpData.getID());
			} else
				jResult.put("actn", "[SIMUL] Closure Deleted");
		}

		return(jResult);
	}

	/**
	 * Send mail
	 */
	private JSONObject _send_mail(Group.Data grpData, String expireDate, String userName, ProcessType processType) {

		JSONObject jResult = new JSONObject();

		boolean
			isSendToRecipient = false,
			isSendToCarbonCopy = false;

		String
			expireMsg = "",
			mailSubject = "",
			mailTitle = "",
			mailDescr = "",
			mailClsId = "",
			mailClsDes = "",
			mailExpDate = "",
			mailAction = "",
			mailLink = "",
			mailRecipient = "",
			mailCarbonCopy = "",
			mailSimul = "";

		try {

			User.Data usrDataRec = null;
			User.Data usrDataCpy = null;

			// Recipient

			usrDataRec = getUser().Read(userName);

			mailRecipient = usrDataRec.getMail();

			// Carbon Copy

			mailCarbonCopy = "";

			if (!grpData.getCreatedBy().equals(grpData.getUpdatedBy()) && !grpData.getUpdatedBy().equals("")) {
				usrDataCpy = getUser().Read(grpData.getUpdatedBy());
				mailCarbonCopy = usrDataCpy.getMail();
			}

			// Set mail data

			if (processType.equals(ProcessType.EXPIRING)) {

				expireMsg = (grpData.isDelAfter()
					? "<span style=\"color:red;\">The closure will be deleted</span>"
					: "<span style=\"color:maroon;\">The closure will be paused</span>"
				);

				mailSubject	= "A closure will expire today: " + grpData.getDescription();
				mailTitle	= "This closure is about to expire";
				mailDescr	= "<div>You received this email because one of your CIFP closures is about to expire.</div>";
				mailClsId	= "Closure ID.: <b>" + grpData.getID() + "</b>";
				mailClsDes	= "Description: <b>" + grpData.getDescription() + "</b>";
				mailExpDate	= "Expire Date: <b>" + expireDate + "</b>";
				mailAction	= "<p>After expiration date: <b>" + expireMsg + "</b></p>";
				mailLink	= "<p><a href=\"" + AppCfg.getServerHomeUrl() + "/manage/gid_edit.jsp?gid=" + grpData.getID() + "\">View this closure in CIFP</a> (login required)</p>";

				if (usrDataRec.getWazerConfig().getCifp().isExpireMail())
					isSendToRecipient = true;

				if (usrDataCpy != null)
					if (usrDataCpy.getWazerConfig().getCifp().isExpireMail())
						isSendToCarbonCopy = true;

			} else if (processType.equals(ProcessType.EXPIRED)) {

				expireMsg = (grpData.isDelAfter()
					? "<span style=\"color:red;\">The closure has been deleted</span>"
					: "<span style=\"color:maroon;\">The closure has been paused</span>"
				);

				mailSubject	= "A closure has just expired: " + grpData.getDescription();
				mailTitle	= "This closure just expired";
				mailDescr	= "<div>You received this email because one of your CIFP closures is just expired.</div>";
				mailClsId	= "Closure ID.: <b>" + grpData.getID() + "</b>";
				mailClsDes	= "Description: <b>" + grpData.getDescription() + "</b>";
				mailExpDate	= "Expired on : <b>" + expireDate + "</b>";
				mailAction	= "<p>Action taken: <b>" + expireMsg + "</b></p>";

				if (!isRealWork())
					mailSimul = "<p style=\"color:red;\">NOTE: <b>THIS IS A SIMULATION ONLY</b> - No data has been updated in the database</p>";

				if (!grpData.isDelAfter())
					mailLink = "<p><a href=\"" + AppCfg.getServerHomeUrl() + "/manage/gid_edit.jsp?gid=" + grpData.getID() + "\">View this closure in CIFP</a> (login required)</p>";

				if (usrDataRec.getWazerConfig().getCifp().isExpireMail())
					isSendToRecipient = true;

				if (usrDataCpy != null)
					if (usrDataCpy.getWazerConfig().getCifp().isExpireMail())
						isSendToCarbonCopy = true;
			}

			setMail(new Mail(getHttpRequest()));

			getMail().setSubject(mailSubject);
			getMail().setHtmlTitle(mailTitle);
			getMail().addHtmlBody(mailDescr);
			getMail().addHtmlBody("<pre>");
			getMail().addHtmlBody(mailClsId);
			getMail().addHtmlBody(mailClsDes);
			getMail().addHtmlBody(mailExpDate);
			getMail().addHtmlBody("</pre>");
			getMail().addHtmlBody(mailAction);

			if (!mailSimul.equals(""))
				getMail().addHtmlBody(mailSimul);

			if (!mailLink.equals(""))
				getMail().addHtmlBody(mailLink);

			LogTool LOG = new LogTool(getDatabase().getConnection());

			if (isSendToRecipient) {

				getMail().setRecipient(mailRecipient);

				if (getMail().Send()) {
					LOG.Info(getHttpRequest(), LogTool.Category.MAIL, "Mail sent to '" + getMail().getRecipient() + "' with subject '" + getMail().getSubject() + "'");
				} else {
					LOG.Error(getHttpRequest(), LogTool.Category.MAIL, "Error sending mail to '" + getMail().getRecipient() + "' with subject '" + getMail().getSubject() + "': " + getMail().getLastError());
				}
			}
			
			if (isSendToCarbonCopy) {

				getMail().setRecipient(mailCarbonCopy);

				if (getMail().Send()) {
					LOG.Info(getHttpRequest(), LogTool.Category.MAIL, "Mail sent to '" + getMail().getRecipient() + "' with subject '" + getMail().getSubject() + "'");
				} else {
					LOG.Error(getHttpRequest(), LogTool.Category.MAIL, "Error sending mail to '" + getMail().getRecipient() + "' with subject '" + getMail().getSubject() + "': " + getMail().getLastError());
				}
			}

			jResult.put("rc", "OK");
			jResult.put("id", grpData.getID());
			jResult.put("type", processType.toString());
			jResult.put("expd", expireDate);
			jResult.put("user", userName);
			jResult.put("mail", (isSendToRecipient ? mailRecipient : "NO"));
			jResult.put("ccpy", (isSendToCarbonCopy ? mailCarbonCopy : "NO"));
			jResult.put("desc", (grpData.getDescription().trim().equals("") ? "N/A" : grpData.getDescription()));

		} catch (Exception e) {

			jResult = new JSONObject();
			jResult.put("rc", "KO");
			jResult.put("id", grpData.getID());
			jResult.put("err", e.toString());
		}

		return(jResult);
	}

}
