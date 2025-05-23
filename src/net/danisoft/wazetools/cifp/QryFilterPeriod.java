////////////////////////////////////////////////////////////////////////////////////////////////////
//
// QryFilterPeriod.java
//
// CIFP Query Period
//
// First Release: ???/???? by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

/**
 * CIFP Query Period
 */
public enum QryFilterPeriod {

	LAST_Y ("Last Year" ),
	LAST_M ("Last Month" ),
	THIS_M ("This Month" ),
	NEXT_M ("Next Month" ),
	THIS_Y ("This Year" ),
	NEXT_Y ("Next Year" ),
	ALLTIM ("All" );

	private final String _Desc;

	/**
	 * Constructor
	 */
	QryFilterPeriod(String desc) {
		this._Desc = desc;
    }

	public String getDesc() { return(this._Desc); }

	/**
	 * Get enum by name
	 */
	public static QryFilterPeriod getEnum(String name) {

		QryFilterPeriod rc = THIS_M;

		for (QryFilterPeriod X : QryFilterPeriod.values())
			if (X.toString().equals(name))
				rc = X;

		return(rc);
	}

	/**
	 * Create Combo
	 */
	public static String getCombo(String Default) {

		String rc = "";

		for (QryFilterPeriod X : QryFilterPeriod.values())
			rc +=
				"<option value=\"" + X.toString() + "\"" + (X.toString().equals(Default) ? " selected" : "") + ">" +
					X.getDesc() +
				"</option>"
			;

		return(rc);
	}

}
