////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Group.java
//
// DB Interface for the CIFP Groups table
//
// First Release: Mar/2024 by Fulvio Mondini (https://danisoft.software/)
//        Update: Apr/2024 Removed Region / WmeUrl and Created WmeData (json)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.Vector;

import org.json.JSONObject;

import net.danisoft.dslib.FmtTool;
import net.danisoft.dslib.SysTool;

/**
 * DB Interface for the CIFP Groups table
 */
public class Group {

	private final static String TBL_NAME = "CIFP_groups";

	private Connection cn;

	/**
	 * Constructor
	 */
	public Group(Connection conn) {
		this.cn = conn;
	}

	/**
	 * Groups Data Record
	 */
	public class Data {

		// Fields
		private String		_ID;			// `GRP_ID` varchar(36) NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'
		private String		_Source;		// `GRP_Source` varchar(32) NOT NULL DEFAULT ''
		private String		_Type;			// `GRP_Type` varchar(16) NOT NULL DEFAULT 'ROAD_CLOSED'
		private String		_SubType;		// `GRP_SubType` varchar(48) NOT NULL DEFAULT 'ROAD_CLOSED_CONSTRUCTION'
		private boolean		_isUnedited;	// `GRP_isUnedited` enum('Y','N') NOT NULL DEFAULT 'Y'
		private boolean		_isActive;		// `GRP_isActive` enum('Y','N') NOT NULL DEFAULT 'N'
		private String		_Description;	// `GRP_Description` varchar(255) NOT NULL DEFAULT ''
		private String		_MteDescr;		// `GRP_MteDescr` varchar(255) NOT NULL DEFAULT ''
		private JSONObject	_Period;		// `GRP_Period` json NOT NULL
		private JSONObject	_Schedule;		// `GRP_Schedule` json NOT NULL
		private JSONObject	_WmeData;		// `GRP_WmeData` json NOT NULL
		private String		_City;			// `GRP_City` varchar(64) NOT NULL DEFAULT ''
		private String		_State;			// `GRP_State` varchar(64) NOT NULL DEFAULT ''
		private String		_Country;		// `GRP_Country` varchar(64) NOT NULL DEFAULT ''
		private String		_CtryIso;		// `GRP_CtryIso` char(2) NOT NULL DEFAULT ''
		private String		_CreatedBy;		// `GRP_CreatedBy` varchar(32) NOT NULL DEFAULT ''
		private Timestamp	_CreatedAt;		// `GRP_CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
		private String		_UpdatedBy;		// `GRP_UpdatedBy` varchar(32) NOT NULL DEFAULT ''
		private Timestamp	_UpdatedAt;		// `GRP_UpdatedAt` datetime NOT NULL DEFAULT '1900-01-01 00:00:00'
		private boolean		_isDelAfter;	// `GRP_isDelAfter` enum('Y','N') NOT NULL DEFAULT 'N'

		// Getters
		public String		getID()				{ return this._ID;			}
		public String		getSource()			{ return this._Source;		}
		public String		getType()			{ return this._Type;		}
		public String		getSubType()		{ return this._SubType;		}
		public boolean		isUnedited()		{ return this._isUnedited;	}
		public boolean		isActive()			{ return this._isActive;	}
		public String		getDescription()	{ return this._Description;	}
		public String		getMteDescr()		{ return this._MteDescr;	}
		public JSONObject	getPeriod()			{ return this._Period;		}
		public JSONObject	getSchedule()		{ return this._Schedule;	}
		public JSONObject	getWmeData()		{ return this._WmeData;		}
		public String		getCity()			{ return this._City;		}
		public String		getState()			{ return this._State;		}
		public String		getCountry()		{ return this._Country;		}
		public String		getCtryIso()		{ return this._CtryIso;		}
		public String		getCreatedBy()		{ return this._CreatedBy;	}
		public Timestamp	getCreatedAt()		{ return this._CreatedAt;	}
		public String		getUpdatedBy()		{ return this._UpdatedBy;	}
		public Timestamp	getUpdatedAt()		{ return this._UpdatedAt;	}
		public boolean		isDelAfter()		{ return this._isDelAfter;	}

		// Setters
		public void setID(String id)					{ this._ID = id;					}
		public void setSource(String source)			{ this._Source = source;			}
		public void setType(String type)				{ this._Type = type;				}
		public void setSubType(String subType)			{ this._SubType = subType;			}
		public void setUnedited(boolean isUnedited)		{ this._isUnedited = isUnedited;	}
		public void setActive(boolean isActive)			{ this._isActive = isActive;		}
		public void setDescription(String description)	{ this._Description = description;	}
		public void setMteDescr(String mteDescr)		{ this._MteDescr = mteDescr;		}
		public void setPeriod(JSONObject period)		{ this._Period = period;			}
		public void setSchedule(JSONObject schedule)	{ this._Schedule = schedule;		}
		public void setWmeData(JSONObject wmeData)		{ this._WmeData = wmeData;			}
		public void setCity(String city)				{ this._City = city;				}
		public void setState(String state)				{ this._State = state;				}
		public void setCountry(String country)			{ this._Country = country;			}
		public void setCtryIso(String ctryIso)			{ this._CtryIso = ctryIso;			}
		public void setCreatedBy(String createdBy)		{ this._CreatedBy = createdBy;		}
		public void setCreatedAt(Timestamp createdAt)	{ this._CreatedAt = createdAt;		}
		public void setUpdatedBy(String updatedBy)		{ this._UpdatedBy = updatedBy;		}
		public void setUpdatedAt(Timestamp updatedAt)	{ this._UpdatedAt = updatedAt;		}
		public void setDelAfter(boolean isDelAfter)		{ this._isDelAfter = isDelAfter;	}

		/**
		 * Constructor
		 */
		public Data() {
			super();

			this._ID			= SysTool.getEmptyUuidValue();
			this._Source		= "";
			this._Type			= CifpType.ROAD_CLOSED.toString();
			this._SubType		= CifpSubType.ROAD_CLOSED_CONSTRUCTION.toString();
			this._isUnedited	= true;
			this._isActive		= false;
			this._Description	= "";
			this._MteDescr		= "";
			this._Period		= new JSONObject();
			this._Schedule		= new JSONObject();
			this._WmeData		= new JSONObject();
			this._City			= "";
			this._State			= "";
			this._Country		= "";
			this._CtryIso		= "";
			this._CreatedBy		= "";
			this._CreatedAt		= FmtTool.DATEZERO;
			this._UpdatedBy		= "";
			this._UpdatedAt		= FmtTool.DATEZERO;
			this._isDelAfter	= false;
		}

		/**
		 * Check if Monday schedule is empty
		 */
		public boolean isMondayScheduleEmpty() {

			boolean rc = false;

			try {
				getSchedule().getString(Schedule.DayNameAbbr[0]);
			} catch (Exception e) {
				System.err.println(e.toString());
				rc = true;
			}

			return(rc);
		}

		/**
		 * Get Schedule for XML feed (with long day names)
		 * @return Vector<String> of Schedule.DayNameFull[i].toLowerCase() + SysTool.getDelimiter() + daySchedule
		 */
		public Vector<String> getScheduleXml() throws Exception {

			String daySchedule;
			Vector<String> vecElements = new Vector<String>();

			for (int i=0; i<Schedule.DAYS_NO; i++) {

				try {
					daySchedule = getSchedule().getString(Schedule.DayNameAbbr[i]);
					vecElements.add(Schedule.DayNameFull[i].toLowerCase() + SysTool.getDelimiter() + daySchedule);
				} catch (Exception e) { }
			}

			return (vecElements);
		}

		/**
		 * Get Schedule for JSON feed (with long day names)
		 * @return JSONObject modified: DayNameAbbr -> DayNameFull
		 */
		public JSONObject getScheduleJson() throws Exception {

			String strSchedule = getSchedule().toString();

			for (int i=0; i<Schedule.DAYS_NO; i++) {

				strSchedule = strSchedule.replace(
					"\"" + Schedule.DayNameAbbr[i] + "\":",
					"\"" + Schedule.DayNameFull[i].toLowerCase() + "\":"
				);
			}

			return (new JSONObject(strSchedule));
		}

	}

	/**
	 * Read a CIFP Group by GRP_ID
	 */
	public Data Read(String GrpID) {
		return(
			_read_obj_by_id(GrpID)
		);
	}

	/**
	 * Insert a new Group
	 * @return Inserted new GID
	 * @throws Exception
	 */
	public String Insert(Data data) throws Exception {

		data.setID(UUID.randomUUID().toString());

		Statement st = this.cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = st.executeQuery("SELECT * FROM " + TBL_NAME + " LIMIT 1");

		rs.moveToInsertRow();
		_update_rs_from_obj(rs, data);
		rs.insertRow();

		rs.close();
		st.close();
		
		return(data.getID());
	}

	/**
	 * Delete a group
	 * @throws Exception
	 */
	public void Delete(String groupUuid) throws Exception {

		Statement st = this.cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		st.executeUpdate(
			"DELETE FROM " + TBL_NAME + " WHERE GRP_ID = '" + groupUuid + "';"
		);

		st.close();
	}

	/**
	 * Read CIFP Groups by SQL
	 * @param WhereStatement A "WHERE" statement, i.e. <tt>"X = 'X' OR Y = 'Y'"</tt><br>
	 * 	Not used if set to null
	 * @param OrderBySqlField SQL Field Name for the "ORDER BY" part of the query<br>
	 * 	No sort order if set to null
	 */
	public Vector<Data> getAll(String WhereStatement, String OrderBySqlField) {

		String sqlQuery =
			"SELECT * FROM " + TBL_NAME + (WhereStatement == null
				? ""
				: (WhereStatement.trim().equals("")
					? ""
					: " WHERE " + WhereStatement
				)
			) + (OrderBySqlField == null
				? ""
				: (OrderBySqlField.trim().equals("")
					? ""
					: " ORDER BY " + OrderBySqlField
				)
			) +
			";"
		;

		return(
			_fill_grp_vector(sqlQuery)
		);
	}

	/**
	 * Check group data syntax
	 * @throws Exception on syntax error(s)
	 */
	public void chkDataSyntax(String groupUuid, String userName) throws Exception {

		String errText = "";
		boolean isGood = true;
		Data grpdata = Read(groupUuid);

		if (grpdata.getDescription().trim().equals("")) {
			isGood = false;
			errText = "Bad Closure Description";
		}

		if (!grpdata.getSubType().startsWith(grpdata.getType())) {
			isGood = false;
			errText = "Invalid Type / SubType";
		}

		if (!isGood) {
			setField(groupUuid, "GRP_isActive", "N", userName);
			throw new Exception("<b>Closure Syntax Check</b><br>" + errText);
		}
	}

	/**
	 * Set a group field
	 * @throws Exception
	 */
	public void setField(String groupUuid, String fieldName, String fieldValue, String userName) throws Exception {

		String Query =
			"UPDATE " + TBL_NAME + " SET " +
				fieldName + " = '" + fieldValue.replace("'", "\\'") + "', " +
				"GRP_UpdatedBy = '" + userName + "', " +
				"GRP_UpdatedAt = '" + FmtTool.fmtDateTimeSqlStyle() + "', " +
				"GRP_isUnedited = 'N' " +
			"WHERE GRP_ID = '" + groupUuid + "';"
		;

		Statement st = this.cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		st.executeUpdate(Query);
		st.close();
	}

	/**
	 * Append a new schedule data to the given DayNameAbbr[] name
	 * @throws Exception
	 */
	public JSONObject addSlot(Data data, String abbrDayName, String timeMin, String timeMax, String userName) throws Exception {

		String schedDays = "";
		JSONObject jSchedule = data.getSchedule();

		try {
			schedDays = jSchedule.getString(abbrDayName);
		} catch (Exception e) { } // No existing slot, to be created

		if (timeMin.equals(""))
			timeMin = "00:00";

		if (timeMax.equals(""))
			timeMax = "23:59";

		schedDays += (schedDays.equals("") ? "" : ",") + (timeMin + "-" + timeMax);
		jSchedule.put(abbrDayName, schedDays);
		data.setSchedule(jSchedule);

		setField(data.getID(), "GRP_Schedule", jSchedule.toString(), userName);

		return(jSchedule);
	}

	/**
	 * Delete an existing schedule slot in the given DayNameAbbr[] name
	 * @throws Exception
	 */
	public void delSlot(Data data, String abbrDayName, String timeMin, String timeMax, String userName) throws Exception {

		JSONObject jSchedule = data.getSchedule();
		String dayData = "", slotData = timeMin + "-" + timeMax;

		try {
			dayData = jSchedule.getString(abbrDayName);
		} catch (Exception e) { }

		if (dayData.contains("," + slotData)) {
			dayData = dayData.replace("," + slotData, "");
		} else if (dayData.contains(slotData + ",")) {
			dayData = dayData.replace(slotData + ",", "");
		} else if (dayData.contains(slotData)) {
			dayData = dayData.replace(slotData, "");
		} else {
			throw new Exception("No slot '" + slotData + "' found in '" + abbrDayName + "'");
		}

		if (dayData.trim().equals(""))
			jSchedule.remove(abbrDayName);
		else
			jSchedule.put(abbrDayName, dayData);

		data.setSchedule(jSchedule);

		setField(data.getID(), "GRP_Schedule", jSchedule.toString(), userName);
	}

	/**
	 * Read (distinct) all editors in GRP_CreatedBy and GRP_UpdatedBy
	 */
	public Vector<String> getEditors(Vector<String> vecActiveCountries) {

		Vector<String> vecAllEditors = new Vector<String>();

		try {

			Statement st = this.cn.createStatement();
			ResultSet rs = null;
			String sqlQuery = "";

			// Filters

			String actCtryFilter = "";

			for (String activeCountries : vecActiveCountries) {
				actCtryFilter += (
					(actCtryFilter.equals("") ? "" : " OR ") +
					"GRP_CtryIso = '" + activeCountries + "'"
				);
			}

			String createdWhere = "GRP_CreatedBy != ''" + (actCtryFilter.equals("") ? "" : " AND (" + actCtryFilter + ")");
			String updatedWhere = "GRP_UpdatedBy != ''" + (actCtryFilter.equals("") ? "" : " AND (" + actCtryFilter + ")");

			// Created By

			sqlQuery = "SELECT DISTINCT GRP_CreatedBy FROM " + TBL_NAME + " WHERE " + createdWhere + ";";

			rs = st.executeQuery(sqlQuery);

			while (rs.next())
				vecAllEditors.add(rs.getString("GRP_CreatedBy"));

			rs.close();

			// Updated By

			sqlQuery = "SELECT DISTINCT GRP_UpdatedBy FROM " + TBL_NAME + " WHERE " + updatedWhere + ";";

			rs = st.executeQuery(sqlQuery);

			while (rs.next())
				vecAllEditors.add(rs.getString("GRP_UpdatedBy"));

			rs.close();
			st.close();

		} catch (Exception e) { }

		// Sort & Merge

		LinkedHashSet<String> hashSet = new LinkedHashSet<String>(vecAllEditors);

		Vector<String> vecResult = new Vector<String>();
		vecResult.addAll(hashSet);
		Collections.sort(vecResult);

		return(vecResult);
	}

	/**
	 * Read (distinct) all countries in GRP_CtryIso
	 */
	public Vector<String> getAllCtryIso() {

		Vector<String> vecAllCtryIso = new Vector<String>();

		try {

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery("SELECT DISTINCT GRP_CtryIso FROM " + TBL_NAME + " ORDER BY GRP_CtryIso;");

			while (rs.next())
				vecAllCtryIso.add(rs.getString("GRP_CtryIso"));

			rs.close();
			st.close();

		} catch (Exception e) {
			System.err.println("Group.getAllCtryIso(): " + e.toString());
		}

		return(vecAllCtryIso);
	}

	/**
	 * Count closures by ISO country code
	 */
	public int countByCountry(String isoCode, CifpStatus cifpStatus) {

		int rc = 0;

		try {

			Statement st = this.cn.createStatement();

			ResultSet rs = st.executeQuery(
				"SELECT COUNT(*) AS RecCount " +
				"FROM " + TBL_NAME + " " +
				"WHERE (" +
					"GRP_CtryIso = '" + isoCode + "'" +
					(cifpStatus.equals(CifpStatus.ORPHAN)
						? " AND GRP_isUnedited = 'Y'"
						: (cifpStatus.equals(CifpStatus.PAUSED)
							? " AND GRP_isUnedited = 'N' AND GRP_isActive = 'N'"
							: (cifpStatus.equals(CifpStatus.ACTIVE)
								? " AND GRP_isUnedited = 'N' AND GRP_isActive = 'Y'"
								: ""
							)
						)
					) +
				");"
			);

			if (rs.next())
				rc = rs.getInt("RecCount");

			rs.close();
			st.close();

		} catch (Exception e) {
			System.err.println("Group.countByCountry(): " + e.toString());
		}

		return(rc);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// +++ PRIVATE +++
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parse a given ResultSet into a Groups.Data object
	 * @return <Groups.Data> result 
	 */
	private Data _parse_obj_from_rs(ResultSet rs) {

		Data data = new Data();

		try {

			data.setID(rs.getString("GRP_ID"));
			data.setSource(rs.getString("GRP_Source"));
			data.setType(rs.getString("GRP_Type"));
			data.setSubType(rs.getString("GRP_SubType"));
			data.setUnedited(rs.getString("GRP_isUnedited").equals("Y"));
			data.setActive(rs.getString("GRP_isActive").equals("Y"));
			data.setDescription(rs.getString("GRP_Description"));
			data.setMteDescr(rs.getString("GRP_MteDescr"));
			data.setPeriod(new JSONObject(rs.getString("GRP_Period")));
			data.setSchedule(new JSONObject(rs.getString("GRP_Schedule")));
			data.setWmeData(new JSONObject(rs.getString("GRP_WmeData")));
			data.setCity(rs.getString("GRP_City"));
			data.setState(rs.getString("GRP_State"));
			data.setCountry(rs.getString("GRP_Country"));
			data.setCtryIso(rs.getString("GRP_CtryIso"));
			data.setCreatedBy(rs.getString("GRP_CreatedBy"));
			data.setUpdatedBy(rs.getString("GRP_UpdatedBy"));
			data.setDelAfter(rs.getString("GRP_isDelAfter").equals("Y"));

			// Special handling for dates
			try { data.setCreatedAt(rs.getTimestamp("GRP_CreatedAt")); } catch (Exception e) { }
			try { data.setUpdatedAt(rs.getTimestamp("GRP_UpdatedAt")); } catch (Exception e) { }

		} catch (Exception e) { }

		return(data);
	}

	/**
	 * Update a given ResultSet from a given Groups.Data object
	 */
	private static void _update_rs_from_obj(ResultSet rs, Data data) {

		try {

			rs.updateString("GRP_ID", data.getID());
			rs.updateString("GRP_Source", data.getSource());
			rs.updateString("GRP_Type", data.getType());
			rs.updateString("GRP_SubType", data.getSubType());
			rs.updateString("GRP_isUnedited", data.isUnedited() ? "Y" : "N");
			rs.updateString("GRP_isActive", data.isActive() ? "Y" : "N");
			rs.updateString("GRP_Description", data.getDescription());
			rs.updateString("GRP_MteDescr", data.getMteDescr());
			rs.updateString("GRP_Period", data.getPeriod().toString());
			rs.updateString("GRP_Schedule", data.getSchedule().toString());
			rs.updateString("GRP_WmeData", data.getWmeData().toString());
			rs.updateString("GRP_City", data.getCity());
			rs.updateString("GRP_State", data.getState());
			rs.updateString("GRP_Country", data.getCountry());
			rs.updateString("GRP_CtryIso", data.getCtryIso());
			rs.updateString("GRP_CreatedBy", data.getCreatedBy());
			rs.updateTimestamp("GRP_CreatedAt", data.getCreatedAt());
			rs.updateString("GRP_UpdatedBy", data.getUpdatedBy());
			rs.updateTimestamp("GRP_UpdatedAt", data.getUpdatedAt());
			rs.updateString("GRP_isDelAfter", data.isDelAfter() ? "Y" : "N");

		} catch (Exception e) { }
	}

	/**
	 * Read GRP Record based on given GrpID
	 * @return <Groups.Data> result 
	 */
	private Data _read_obj_by_id(String GrpID) {

		Data data = new Data();

		try {

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM " + TBL_NAME + " WHERE GRP_ID = '" + GrpID + "';");

			if (rs.next())
				data = _parse_obj_from_rs(rs);

			rs.close();
			st.close();

		} catch (Exception e) { }

		return(data);
	}

	/**
	 * Read all GRP Records
	 * @return Vector<Group.Data> of results 
	 */
	private Vector<Data> _fill_grp_vector(String query) {

		Vector<Data> vecData = new Vector<Data>();

		try {

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery(query);

			while (rs.next())
				vecData.add(_parse_obj_from_rs(rs));

			rs.close();
			st.close();

		} catch (Exception e) { }

		return(vecData);
	}

}
