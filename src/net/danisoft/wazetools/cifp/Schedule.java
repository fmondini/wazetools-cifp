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

import org.json.JSONArray;
import org.json.JSONObject;

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

}
