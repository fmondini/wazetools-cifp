////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Schedule.java
//
// Class to hold Schedule data
//
// First Release: Mar/2024 by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import net.danisoft.dslib.FmtTool;

/**
 * Class to hold Schedule data
 */
public class Schedule {

	public static final int DAYS_NO = 7;

	public static final String NO_TIME = "--:--";
	public static final String DayNameAbbr[] = { "mon", "tue", "wed", "thu", "fri", "sat", "sun" };
	public static final String DayNameFull[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

	private String[] _Data;

	public String getDataElem(int i) { return this._Data[i]; }
	
	/**
	 * Constructor
	 */
	public Schedule() {
		super();
		this._Data = new String[DAYS_NO];
	}

	/**
	 * Return a JSON Object<br>
	 * <i>Example</i> --&gt; <tt><b>"mon":"09:00-11:00,17:00-21:00"</b></tt>
	 */
	public JSONObject getJson() {

		JSONObject jSchedule = new JSONObject();

		for (int i=0; i<DAYS_NO; i++)
			if (getDataElem(i) != null)
				if (!getDataElem(i).equals(""))
					jSchedule.put(DayNameAbbr[i], getDataElem(i));

		return(jSchedule);
	}

	/**
	 * Get schedule data from the given DayNameAbbr[] name  
	 */
	public static JSONArray getDayData(JSONObject jSchedule, String abbrDayName) {

		JSONObject jDay;
		JSONArray jaResult = new JSONArray();

		try {

			int dayDataArrayLen = 0;
			String dayDataArray[] = null;

			try {
				dayDataArray = jSchedule.getString(abbrDayName).split(",");
				dayDataArrayLen = dayDataArray.length;
			} catch (Exception e) { }

			for (int i=0; i<4; i++) {

				jDay = new JSONObject();

				if (i < dayDataArrayLen) {
					jDay.put("min", (dayDataArray == null ? NO_TIME : dayDataArray[i].split("-")[0]));
					jDay.put("max", (dayDataArray == null ? NO_TIME : dayDataArray[i].split("-")[1]));
				} else {
					jDay.put("min", NO_TIME);
					jDay.put("max", NO_TIME);
				}

				jaResult.put(jDay);
			}

		} catch (Exception e) {
			jaResult = new JSONArray();
			System.out.println("getDayData(): " + e.toString());
		}

		return(jaResult);
	}

	/**
	 * Get slot start and stop dates if this schedule is active NOW
	 * 
	 * @return A Date[] array of 2 elements containing:<ul>
	 * 	<li>If Date/Time match<br>
	 * 		Element 0: Date Start<br>
	 * 		Element 1: Date End</li>
	 * 	<li>If Date/Time DO NOT match<br>
	 * 		Element 0: null<br>
	 * 		Element 1: null</li>
	 * </ul>
	 */
	public static Date[] getActiveSlotPeriod(Group.Data grpData) {

		int startValue, endValue, nowValue;
		String nowDow, closureTime[], closureSpan[], startTime[], endTime[], nowTime[];

		Date[] slotDates = new Date[] { null, null };

		try {

			Date currentDate = new Date();
			// [TEST ONLY] Date currentDate = FmtTool.scnDateTimeSqlStyle("2025-06-12 14:00:00");

			JSONObject jSchedule = grpData.getSchedule();

			nowDow = new SimpleDateFormat("EEE", Locale.ROOT).format(currentDate).toLowerCase();
			nowTime = FmtTool.fmtTimeNoSecs(currentDate).split(":");
			nowValue = (Integer.parseInt(nowTime[0]) * 60) + Integer.parseInt(nowTime[1]);

			// Closures Array

			if (jSchedule.toString().contains(","))
				closureTime = jSchedule.getString(nowDow).split(",");
			else
				closureTime = new String[] { jSchedule.getString(nowDow) };

			// Check if there is a slot that matches the current date and time

			for (String time : closureTime) {

				closureSpan = time.split("-");

				startTime = closureSpan[0].split(":");
				startValue = (Integer.parseInt(startTime[0]) * 60) + Integer.parseInt(startTime[1]);

				endTime = closureSpan[1].split(":");
				endValue = (Integer.parseInt(endTime[0]) * 60) + Integer.parseInt(endTime[1]);

				if (startValue <= nowValue && endValue >= nowValue) {
					slotDates[0] = FmtTool.scnDateTimeSqlStyle(FmtTool.fmtDateSqlStyle(currentDate) + " " + closureSpan[0] + ":00");
					slotDates[1] = FmtTool.scnDateTimeSqlStyle(FmtTool.fmtDateSqlStyle(currentDate) + " " + closureSpan[1] + ":59");
				}
			}

		} catch (Exception e) { }

		return(slotDates);
	}

}
