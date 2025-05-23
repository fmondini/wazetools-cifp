////////////////////////////////////////////////////////////////////////////////////////////////////
//
// CifpType.java
//
// CIFP Types
//
// First Release: Mar/2024 by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

/**
 * CIFP Types
 */
public enum CifpType {

	ALL			("-=[ ALL ]=-",	""			),
	ROAD_CLOSED	("Road Closed",	"road"		),
	ACCIDENT	("Accident",	"accident"	);
//	HAZARD		("Hazard",		"hazard"	),
//	POLICE		("Police",		"police"	),
//	CHIT_CHAT	("Chat",		"chat"		),
//	JAM			("Jam",			"jam"		);

	private final String _Desc;
	private final String _Icon;

	/**
	 * Constructor
	 */
	CifpType(String desc, String icon) {
		this._Desc = desc;
		this._Icon = icon;
	}

	public String getDesc()	{ return(this._Desc); }
	public String getIcon()	{ return(this._Icon); }

	/**
	 * Get Icon Src Relative Path
	 */
	public static String getIconSrc(Group.Data grpData) {

		return(
			"../images/gmap/" +
			getEnum(grpData.getType()).getIcon() +
			"-" +
			(grpData.isUnedited()
				? "orphan"
				: (grpData.isActive()
					? "active"
					: "paused"
				)
			) +
			".png"
		);
	}

	/**
	 * GET Enum by Code
	 */
	public static CifpType getEnum(String name) {

		CifpType cifpType = ROAD_CLOSED;

		for (CifpType X : CifpType.values())
			if (X.toString().equals(name))
				cifpType = X;

		return(cifpType);
	}

	/**
	 * Get Combo
	 */
	public static String getCombo(String selected, boolean addOptionAll) {

		String rc = "";

		for (CifpType X : CifpType.values()) {
			if (addOptionAll || !X.equals(ALL)) {
				rc +=
					"<option value=\"" + X.toString() + "\" " + (X.toString().equals(selected) ? "selected" : "") + ">" +
						X.getDesc() +
					"</option>"
				;
			}
		}

		return(rc);
	}

}
