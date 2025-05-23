////////////////////////////////////////////////////////////////////////////////////////////////////
//
// QryFilterOrder.java
//
// CIFP Query Order
//
// First Release: ???/???? by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

/**
 * CIFP Query Order
 */
public enum QryFilterOrder {

	LOCATION ("By Location" ),
	FRST_UPD ("By Creation Date" ),
	LAST_UPD ("By Last Update" ),
	LAST_EDT ("By Last Editor" );

	private final String _Desc;

	/**
	 * Constructor
	 */
	QryFilterOrder(String desc) {
		this._Desc = desc;
    }

	public String getDesc() { return(this._Desc); }

	/**
	 * Get enum by name
	 */
	public static QryFilterOrder getEnum(String name) {

		QryFilterOrder rc = LOCATION;

		for (QryFilterOrder X : QryFilterOrder.values())
			if (X.toString().equals(name))
				rc = X;

		return(rc);
	}

	/**
	 * Create Combo
	 */
	public static String getCombo(String Default) {

		String rc = "";

		for (QryFilterOrder X : QryFilterOrder.values())
			rc +=
				"<option value=\"" + X.toString() + "\"" + (X.toString().equals(Default) ? " selected" : "") + ">" +
					X.getDesc() +
				"</option>"
			;

		return(rc);
	}

}
