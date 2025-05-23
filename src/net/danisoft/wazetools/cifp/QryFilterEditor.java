////////////////////////////////////////////////////////////////////////////////////////////////////
//
// QryFilterEditor.java
//
// AUTH Query Editors
//
// First Release: ???/???? by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

/**
 * AUTH Query Editors
 */
public enum QryFilterEditor {

	ALL_EDIT ("All" ),
	SEL_EDIT ("Selected" );

	private final String _Desc;

	/**
	 * Constructor
	 */
	QryFilterEditor(String desc) {
		this._Desc = desc;
    }

	public String getDesc() { return(this._Desc); }

	/**
	 * Get enum by name
	 */
	public static QryFilterEditor getEnum(String name) {

		QryFilterEditor rc = ALL_EDIT;

		for (QryFilterEditor X : QryFilterEditor.values())
			if (X.toString().equals(name))
				rc = X;

		return(rc);
	}

	/**
	 * Create Combo
	 */
	public static String getCombo(String Default) {

		String rc = "";

		for (QryFilterEditor X : QryFilterEditor.values())
			rc +=
				"<option value=\"" + X.toString() + "\"" + (X.toString().equals(Default) ? " selected" : "") + ">" +
					X.getDesc() +
				"</option>"
			;

		return(rc);
	}

}
