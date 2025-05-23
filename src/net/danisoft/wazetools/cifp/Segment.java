////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Segment.java
//
// DB Interface for the CIFP Segments table
//
// First Release: Mar/2024 by Fulvio Mondini (https://danisoft.software/)
//       Revised: May/2024 One Way Flag added
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.Vector;

import net.danisoft.dslib.SysTool;

/**
 * DB Interface for the CIFP Segments table
 */
public class Segment {

	private final static String TBL_NAME = "CIFP_segments";

	private Connection cn;

	/**
	 * Constructor
	 */
	public Segment(Connection conn) {
		this.cn = conn;
	}

	/**
	 * Segments Data Record
	 */
	public class Data {

		// Fields
		private String		_ID;			// `SEG_ID` varchar(36) NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'
		private String		_Group;			// `SEG_Group` varchar(36) NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'
		private int			_WmeID;			// `SEG_WmeID` int NOT NULL DEFAULT '0'
		private String		_Street;		// `SEG_Street` varchar(64) NOT NULL DEFAULT ''
		private CifpSegDir	_Direction;		// `SEG_Direction` enum('ALL','FWD','BCK') NOT NULL DEFAULT 'ALL' COMMENT 'ALL=Both - FWD=Forward - BCK=Backward'
		private String		_Polyline;		// `SEG_Polyline` linestring NOT NULL
		private String		_PolylineText;	// `SEG_Polyline` linestring NOT NULL

		// Getters
		public String		getID()				{ return this._ID;				}
		public String		getGroup()			{ return this._Group;			}
		public int			getWmeID()			{ return this._WmeID;			}
		public String		getStreet()			{ return this._Street;			}
		public CifpSegDir	getDirection()		{ return this._Direction;		}
		public String		getPolyline()		{ return this._Polyline;		}
		public String		getPolylineText()	{ return this._PolylineText;	}

		// Setters
		public void setID(String s)				{ this._ID = s;				}
		public void setGroup(String s)			{ this._Group = s;			}
		public void setWmeID(int i)				{ this._WmeID = i;			}
		public void setStreet(String s)			{ this._Street = s;			}
		public void setDirection(CifpSegDir o)	{ this._Direction = o;		}
		public void setPolyline(String s)		{ this._Polyline = s;		}
		public void setPolylineText(String s)	{ this._PolylineText = s;	}

		/**
		 * Constructor
		 */
		public Data() {
			super();

			this._ID			= SysTool.getEmptyUuidValue();
			this._Group			= SysTool.getEmptyUuidValue();
			this._WmeID			= 0;
			this._Street		= "";
			this._Direction		= CifpSegDir.ALL;
			this._Polyline		= "";
			this._PolylineText	= "";
		}

		/**
		 * Get Polyline in feed format
		 * @throws Exception
		 */
		public String getPolylineFeed() throws Exception {
			return(this._PolylineText
				.replace("LINESTRING(", "")
				.replace(")", "")
				.replace(",", " ")
			);
		}
	}

	/**
	 * Return all CIFP Segments for a given GRP_ID
	 * @return Vector<Data> of results 
	 */
	public Vector<Data> getAll(String GrpID) {
		return(
			_fill_seg_vector("SELECT *, ST_AsText(SEG_Polyline) AS PolylineText FROM " + TBL_NAME + " WHERE SEG_Group = '" + GrpID + "';")
		);
	}

	/**
	 * Delete all CIFP Segments for a given GRP_ID
	 */
	public void delAll(String GrpID) {

		try {

			Statement st = this.cn.createStatement();
			st.executeUpdate("DELETE FROM " + TBL_NAME + " WHERE SEG_Group = '" + GrpID + "';");
			st.close();

		} catch (Exception e) { }
	}

	/**
	 * Read a CIFP Segment by SEG_ID
	 */
	public Data Read(String SegID) {
		return(
			_read_obj_by_id(SegID)
		);
	}

	/**
	 * Update segment direction
	 */
	public void updateDirection(String SegID, CifpSegDir cifpSegDir) throws Exception {
		
		Statement st = this.cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		st.executeUpdate(
			"UPDATE " + TBL_NAME + " " +
			"SET SEG_Direction = '" + cifpSegDir.toString() + "' " +
			"WHERE SEG_ID = '" + SegID + "';"
		);

		st.close();
	}

	/**
	 * Insert a new Segment
	 * @return LAST_INSERT_ID()
	 * @throws Exception
	 */
	public String Insert(Data segData) throws Exception {

		segData.setID(UUID.randomUUID().toString());

		Statement st = this.cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		st.executeUpdate(
			"INSERT INTO " + TBL_NAME + " (" +
				"SEG_ID, SEG_Group, SEG_WmeID, SEG_Street, SEG_Direction, SEG_Polyline" +
			") VALUES (" +
				"'" + segData.getID() + "', " +
				"'" + segData.getGroup() + "', " +
				segData.getWmeID() + ", " +
				"'" + segData.getStreet() + "', " +
				"'" + segData.getDirection().toString() + "', " +
				"ST_GeomFromText('LINESTRING(" + segData.getPolyline() + ")')" +
			");"
		);

		st.close();

		return(segData.getID());
	}

	/**
	 * Count Segments for a gived GroupID
	 */
	public int countSegments(String GrpID) {

		int rc = 0;

		try {

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery("SELECT COUNT(*) AS SegNo FROM " + TBL_NAME + " WHERE SEG_Group = '" + GrpID + "';");

			if (rs.next())
				rc = rs.getInt("SegNo");

			rs.close();
			st.close();

		} catch (Exception e) { }

		return(rc);
	}
	
	/**
	 * Count Streets for a gived GroupID
	 */
	public int countStreets(String GrpID) {

		int rc = 0;

		try {

			Vector<String> vecAllStreets = new Vector<String>();

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM " + TBL_NAME + " WHERE SEG_Group = '" + GrpID + "';");

			while (rs.next())
				vecAllStreets.add(rs.getString("SEG_Street"));

			rs.close();
			st.close();

			// Sort & Merge

			LinkedHashSet<String> hashSet = new LinkedHashSet<String>(vecAllStreets);

			Vector<String> vecResult = new Vector<String>();
			vecResult.addAll(hashSet);
			Collections.sort(vecResult);

			rc = vecResult.size();
			
		} catch (Exception e) { }

		return(rc);
	}
	
	/**
	 * Get Street Names for a gived GroupID
	 */
	public Vector<String> getStreets(String GrpID) {

		Vector<String> vecResult = new Vector<String>();

		try {

			Vector<String> vecAllStreets = new Vector<String>();

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM " + TBL_NAME + " WHERE SEG_Group = '" + GrpID + "';");

			while (rs.next())
				vecAllStreets.add(rs.getString("SEG_Street"));

			rs.close();
			st.close();

			// Sort & Merge

			LinkedHashSet<String> hashSet = new LinkedHashSet<String>(vecAllStreets);
			vecResult.addAll(hashSet);
			Collections.sort(vecResult);
			
		} catch (Exception e) { }

		return(vecResult);
	}

	/**
	 * Count segments in a segments vector
	 */
	public static int countSegments(Vector<Data> vecData) {

		return(vecData.size());
	}
	
	/**
	 * Count unique street names in a segments vector
	 */
	public static int countStreets(Vector<Data> vecData) {

		Vector<String> vecStreets = new Vector<String>();
		
		for (Segment.Data data : vecData)
			vecStreets.add(data.getStreet());

		LinkedHashSet<String> hashSet = new LinkedHashSet<String>(vecStreets);

		return(hashSet.size());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// +++ PRIVATE +++
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parse a given ResultSet into a Segments.Data object
	 * @return <Segments.Data> result 
	 */
	private Data _parse_obj_from_rs(ResultSet rs) {

		Data data = new Data();

		try {
			
			data.setID(rs.getString("SEG_ID"));
			data.setGroup(rs.getString("SEG_Group"));
			data.setWmeID(rs.getInt("SEG_WmeID"));
			data.setStreet(rs.getString("SEG_Street"));
			data.setDirection(CifpSegDir.getEnum(rs.getString("SEG_Direction")));
			data.setPolyline(rs.getString("SEG_Polyline"));
			data.setPolylineText(rs.getString("PolylineText"));

		} catch (Exception e) { }

		return(data);
	}

	/**
	 * Read SEG Records based on given query
	 * @return Vector<ReqObject> of results 
	 */
	private Vector<Data> _fill_seg_vector(String query) {

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

	/**
	 * Read SEG Record based on given SegID
	 * @return <Segments.Data> result 
	 */
	private Data _read_obj_by_id(String SegID) {

		Data data = new Data();

		try {
			
			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery("SELECT *, ST_AsText(SEG_Polyline) AS PolylineText FROM " + TBL_NAME + " WHERE SEG_ID = '" + SegID + "';");

			if (rs.next())
				data = _parse_obj_from_rs(rs);

			rs.close();
			st.close();

		} catch (Exception e) { }

		return(data);
	}

}
