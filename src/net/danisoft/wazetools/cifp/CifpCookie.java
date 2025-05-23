////////////////////////////////////////////////////////////////////////////////////////////////////
//
// CifpCookie.java
//
// CIFP Cookies
//
// First Release: ???/???? by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

/**
 * CIFP Cookies
 */
public enum CifpCookie {

	QRY_ORDER		("cifp-qry-order"),
	QRY_PERIOD		("cifp-qry-period"),
	QRY_EDITOR		("cifp-qry-editor"),
	QRY_ED_NAME		("cifp-qry-editor-name"),

	MAP_ZOOM		("cifp-qry-map-zoom"),
	MAP_LAT			("cifp-qry-map-lat"),
	MAP_LNG			("cifp-qry-map-lng"),

	MAP_VIEW_TYPE	("cifp-mapview-type"),
	MAP_VIEW_STATUS	("cifp-mapview-status");

	private final String _Name;

	/**
	 * Constructor
	 */
	CifpCookie(String name) {
		this._Name = name;
    }

	public String getName() { return(this._Name); }

}
