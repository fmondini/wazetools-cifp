////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Period.java
//
// Class to hold Period data
//
// First Release: Mar/2024 by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

import java.time.YearMonth;
import java.util.Date;

import org.json.JSONObject;

import net.danisoft.dslib.FmtTool;

/**
 * Class to hold Period data
 */
public class Period {

	// Fields
	private String _MinDate;
	private String _MinTime;
	private String _MinZone;
	private String _MaxDate;
	private String _MaxTime;
	private String _MaxZone;

	// Getters
	public String getMinDate() { return this._MinDate; }
	public String getMinTime() { return this._MinTime; }
	public String getMinZone() { return this._MinZone; }
	public String getMaxDate() { return this._MaxDate; }
	public String getMaxTime() { return this._MaxTime; }
	public String getMaxZone() { return this._MaxZone; }

	// Setters
	public void setMinDate(String minDate) { this._MinDate = minDate; }
	public void setMinTime(String minTime) { this._MinTime = minTime; }
	public void setMinZone(String minZone) { this._MinZone = minZone; }
	public void setMaxDate(String maxDate) { this._MaxDate = maxDate; }
	public void setMaxTime(String maxTime) { this._MaxTime = maxTime; }
	public void setMaxZone(String maxZone) { this._MaxZone = maxZone; }

	/**
	 * Constructor
	 */
	public Period() {
		super();

		Date minDate = FmtTool.scnDateTimeSqlStyle(YearMonth.now().toString() + "-01 00:00:00");
		Date maxDate = FmtTool.scnDateTimeSqlStyle(YearMonth.now().toString() + "-01 23:59:59");
		maxDate = FmtTool.addMonths(maxDate, 1);
		maxDate = FmtTool.addDays(maxDate, -1);

		this._MinDate = FmtTool.fmtWazePeriodDate(minDate);
		this._MinTime = FmtTool.fmtWazePeriodTime(minDate);
		this._MinZone = "+00:00";
		this._MaxDate = FmtTool.fmtWazePeriodDate(maxDate);
		this._MaxTime = FmtTool.fmtWazePeriodTime(maxDate);
		this._MaxZone = "+00:00";
	}

	/**
	 * Return a JSON Object
	 */
	public JSONObject getJson() {

		JSONObject jPeriodMin = new JSONObject();
		jPeriodMin.put("date", getMinDate());
		jPeriodMin.put("time", getMinTime());
		jPeriodMin.put("zone", getMinZone());

		JSONObject jPeriodMax = new JSONObject();
		jPeriodMax.put("date", getMaxDate());
		jPeriodMax.put("time", getMaxTime());
		jPeriodMax.put("zone", getMaxZone());

		JSONObject jPeriod = new JSONObject();
		jPeriod.put("min", jPeriodMin);
		jPeriod.put("max", jPeriodMax);

		return(jPeriod);
	}

}
