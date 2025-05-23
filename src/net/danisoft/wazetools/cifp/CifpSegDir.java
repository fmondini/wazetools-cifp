////////////////////////////////////////////////////////////////////////////////////////////////////
//
// CifpSegDir.java
//
// CIFP Segment directions
//
// First Release: May/2024 by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

/**
 * CIFP Segment directions
 */
public enum CifpSegDir {

	ALL ("Both"		),
	FWD ("Forward"	),
	BCK ("Backward"	);

	private final String _Desc;

	/**
	 * Constructor
	 */
	CifpSegDir(String desc) {
		this._Desc = desc;
	}

	public String getDesc() { return(this._Desc); }

	/**
	 * GET Enum by Code
	 */
	public static CifpSegDir getEnum(String Code) {

		CifpSegDir rc = ALL;

		for (CifpSegDir X : CifpSegDir.values())
			if (X.toString().equals(Code))
				rc = X;

		return(rc);
	}

}
