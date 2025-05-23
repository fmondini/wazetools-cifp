////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Bookmark.java
//
// DB Interface for the CIFP Bookmarks table
//
// First Release: May/2024 by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import org.json.JSONObject;

import net.danisoft.dslib.FmtTool;
import net.danisoft.dslib.SysTool;

/**
 * DB Interface for the CIFP Bookmarks table
 */
public class Bookmark {

	private final static String TBL_NAME = "CIFP_bookmarks";

	private Connection cn;

	/**
	 * Constructor
	 */
	public Bookmark(Connection conn) {
		this.cn = conn;
	}

	/**
	 * Bookmark Data Record
	 */
	public class Data {

		// Fields
		private int			_ID;		// `UBK_ID` int NOT NULL AUTO_INCREMENT
		private String		_User;		// `UBK_User` varchar(32) NOT NULL DEFAULT ''
		private String		_Group;		// `UBK_Group` varchar(36) NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'
		private String		_Type;
		private String		_SubType;
		private boolean		_IsUnedited;
		private boolean		_IsActive;
		private String		_Descr;
		private JSONObject	_Period;
		private String		_Location;
		private Timestamp	_UpdatedAt;
		private String		_UpdatedBy;
		// Created after a read
		private Date		_PeriodMin;
		private Date		_PeriodMax;
		private CifpStatus	_Status;

		// Getters
		public int			getID()			{ return this._ID;			}
		public String		getUser()		{ return this._User;		}
		public String		getGroup()		{ return this._Group;		}
		public String		getType()		{ return this._Type;		}
		public String		getSubType()	{ return this._SubType;		}
		public boolean		isUnedited()	{ return this._IsUnedited;	}
		public boolean		isActive()		{ return this._IsActive;	}
		public String		getDescr()		{ return this._Descr;		}
		public JSONObject	getPeriod()		{ return this._Period;		}
		public String		getLocation()	{ return this._Location;	}
		public Timestamp	getUpdatedAt()	{ return this._UpdatedAt;	}
		public String		getUpdatedBy()	{ return this._UpdatedBy;	}
		// Created after a read
		public Date			getPeriodMin()	{ return this._PeriodMin;	}
		public Date			getPeriodMax()	{ return this._PeriodMax;	}
		public CifpStatus	getStatus()		{ return this._Status;		}

		// Setters
		public void setID(int i)				{ this._ID = i;			}
		public void setUser(String s)			{ this._User = s;		}
		public void setGroup(String s)			{ this._Group = s;		}
		public void setType(String s)			{ this._Type = s;		}
		public void setSubType(String s)		{ this._SubType = s;	}
		public void setIsUnedited(boolean b)	{ this._IsUnedited = b;	}
		public void setIsActive(boolean b)		{ this._IsActive = b;	}
		public void setDescr(String s)			{ this._Descr = s;		}
		public void setPeriod(JSONObject j)		{ this._Period = j;		}
		public void setLocation(String s)		{ this._Location = s;	}
		public void setUpdatedAt(Timestamp t)	{ this._UpdatedAt = t;	}
		public void setUpdatedBy(String s)		{ this._UpdatedBy = s;	}
		// Created after a read
		public void setPeriodMin(Date d)		{ this._PeriodMin = d;	}
		public void setPeriodMax(Date d)		{ this._PeriodMax = d;	}
		public void setStatus(CifpStatus c)		{ this._Status = c;		}

		/**
		 * Constructor
		 */
		public Data() {
			super();

			this._ID			= 0;
			this._User			= "";
			this._Group			= SysTool.getEmptyUuidValue();
			this._Type			= "";
			this._SubType		= "";
			this._IsUnedited	= false;
			this._IsActive		= false;
			this._Descr			= "";
			this._Period		= new JSONObject();
			this._Location		= "";
			this._UpdatedAt		= FmtTool.DATEZERO;
			this._UpdatedBy		= "";
			this._PeriodMin		= FmtTool.DATEZERO;
			this._PeriodMax		= FmtTool.DATEZERO;
		}
	}

	/**
	 * Return all Bookmarks for a given user
	 * @return Vector<Data> of results 
	 */
	public Vector<Data> getAll(String user) {
		return(
			_fill_ubk_vector(
				"SELECT " +
					"UBK_ID, UBK_User, UBK_Group, " +
					"GRP_Type, GRP_SubType, GRP_isUnedited, GRP_isActive, GRP_Description, GRP_Period, " +
					"CONCAT(" +
						"GRP_Country, " +
						"IF (GRP_State = '', '', CONCAT(' / ', GRP_State)), " +
						"IF (GRP_City = '', '', CONCAT(' / ', GRP_City))" +
					") AS UBK_Location, " +
					"GRP_UpdatedAt, GRP_UpdatedBy " +
				"FROM " + TBL_NAME + " " +
				"LEFT JOIN " +
					"CIFP_groups ON UBK_Group = GRP_ID " +
				"WHERE UBK_User = '" + user + "' " +
				"ORDER BY UBK_Location, GRP_Description" +
				";"
			)
		);
	}

	/**
	 * Add a bookmark
	 */
	public void Add(String user, String grpId) throws Exception {

		Statement st = this.cn.createStatement();

		st.executeUpdate(
			"INSERT INTO " + TBL_NAME + " (" +
				"UBK_User, UBK_Group" +
			") VALUES (" +
				"'" + user + "', " +
				"'" + grpId + "'" +
			");"
		);

		st.close();
	}

	/**
	 * Delete a bookmark by UBK_ID
	 */
	public void Delete(int UbkID) throws Exception {

		Statement st = this.cn.createStatement();

		st.executeUpdate("DELETE FROM " + TBL_NAME + " WHERE UBK_ID = " + UbkID + ";");

		st.close();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// +++ PRIVATE +++
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parse a given ResultSet into a Bookmark.Data object
	 * @return <Bookmark.Data> result 
	 */
	private Data _parse_obj_from_rs(ResultSet rs) {

		Data data = new Data();

		try {

			data.setID(rs.getInt("UBK_ID"));
			data.setUser(rs.getString("UBK_User"));
			data.setGroup(rs.getString("UBK_Group"));
			data.setType(rs.getString("GRP_Type"));
			data.setSubType(rs.getString("GRP_SubType"));
			data.setIsUnedited(rs.getString("GRP_isUnedited").equals("Y"));
			data.setIsActive(rs.getString("GRP_isActive").equals("Y"));
			data.setDescr(rs.getString("GRP_Description"));
			data.setPeriod(new JSONObject(rs.getString("GRP_Period")));
			data.setLocation(rs.getString("UBK_Location"));
			data.setUpdatedAt(rs.getTimestamp("GRP_UpdatedAt"));
			data.setUpdatedBy(rs.getString("GRP_UpdatedBy"));

			// Created after a read

			data.setPeriodMin(FmtTool.scnDateTimeSqlStyle(
				data.getPeriod().getJSONObject("min").getString("date") + " " +
				data.getPeriod().getJSONObject("min").getString("time") + ":00"
			));

			data.setPeriodMax(FmtTool.scnDateTimeSqlStyle(
				data.getPeriod().getJSONObject("max").getString("date") + " " +
				data.getPeriod().getJSONObject("max").getString("time") + ":59"
			));

			data.setStatus(
				(data.isUnedited()
					? CifpStatus.ORPHAN
					: (data.isActive()
						? CifpStatus.ACTIVE
						: CifpStatus.PAUSED
					)
				)
			);

		} catch (Exception e) { }

		return(data);
	}

	/**
	 * Read UBK Records based on given query
	 * @return Vector<Data> of results 
	 */
	private Vector<Data> _fill_ubk_vector(String query) {

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
