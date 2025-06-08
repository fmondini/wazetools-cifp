////////////////////////////////////////////////////////////////////////////////////////////////////
//
// FeedLastCheck.java
//
// Read/Write the LastCheck JSON File
//
// First Release: May/2025 by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import net.danisoft.dslib.FmtTool;
import net.danisoft.wazetools.AppCfg;

/**
 * Read/Write the LastCheck JSON File
 */
public class FeedLastCheck {

	private static final String LAST_REFRESH_FILE = AppCfg.getServerRootPath() + "/LastRefresh.json";

	/**
	 * Set the last refresh date to now
	 */
	public static void Update(String currentUser, String remoteAddress) {

		try {

			JSONObject jLastChk = new JSONObject();

			jLastChk.put("date", FmtTool.fmtDateTimeSqlStyle());
			jLastChk.put("addr", remoteAddress);
			jLastChk.put("user", currentUser);

			PrintWriter jsonFile = new PrintWriter(new FileWriter(LAST_REFRESH_FILE, false));
			jsonFile.println(jLastChk.toString());
			jsonFile.flush();
			jsonFile.close();

		} catch (Exception e) {
			System.err.println("FeedLastCheck.Update(): " + e.toString());
		}
	}

	/**
	 * Time since last get
	 */
	public static String getTime() {

		String Result = "No Date/Time Set";

		try {
			Result = FmtTool.TimeBetween(
				FmtTool.scnDateTimeSqlStyle(_get_refresh_file_content().getString("date")),
				new Date(),
				Locale.ITALY
			);
		} catch (Exception e) {
			System.err.println("FeedLastCheck.getDate(): " + e.toString());
		}

		return(Result);
	}

	/**
	 * Get the last refresh date
	 */
	public static String getDate() {

		String Result = "No Date/Time Set";

		try {
			Result = FmtTool.fmtDateTime(FmtTool.scnDateTimeSqlStyle(_get_refresh_file_content().getString("date")));
		} catch (Exception e) {
			System.err.println("FeedLastCheck.getDate(): " + e.toString());
		}

		return(Result);
	}

	/**
	 * Get the last refresh user
	 */
	public static String getUser() {

		String Result = "UNKNOWN";

		try {
			Result = _get_refresh_file_content().getString("user");
		} catch (Exception e) {
			System.err.println("FeedLastCheck.getUser(): " + e.toString());
		}

		return(Result);
	}

	/**
	 * Get the last refresh address
	 */
	public static String getAddr() {

		String Result = "0.0.0.0";

		try {
			Result = _get_refresh_file_content().getString("addr");
		} catch (Exception e) {
			System.err.println("FeedLastCheck.getAddr(): " + e.toString());
		}

		return(Result);
	}

	/**
	 * Read JSON file in a JSONObject
	 */
	private static JSONObject _get_refresh_file_content() throws Exception {

		return(
			new JSONObject(
				new String(
					Files.readAllBytes(
						Paths.get(LAST_REFRESH_FILE)
					)
				)
			)
		);
	}

}
