////////////////////////////////////////////////////////////////////////////////////////////////////
//
// QryFilter.java
//
// CIFP Query Filter
//
// First Release: Mar/2024 by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

import java.util.Vector;

import javax.servlet.http.Cookie;

import net.danisoft.dslib.Database;

/**
 * CIFP Query Filter
 */
public class QryFilter {

	/**
	 * Constructor
	 */
	public QryFilter() {
		super();
	}

	/**
	 * Get Order from cookies 
	 */
	public QryFilterOrder getOrderFromCookie(Cookie[] cookies) {

		QryFilterOrder qryFilterOrder = QryFilterOrder.LOCATION;

		try {

			for (Cookie cookie : cookies)
				if (cookie.getName().equals(CifpCookie.QRY_ORDER.getName()))
					qryFilterOrder = QryFilterOrder.getEnum(cookie.getValue());

		} catch (Exception e) {	}

		return(qryFilterOrder);
	}

	/**
	 * Get Period from cookies 
	 */
	public QryFilterPeriod getPeriodFromCookie(Cookie[] cookies) {

		QryFilterPeriod qryFilterPeriod = QryFilterPeriod.THIS_M;

		try {

			for (Cookie cookie : cookies)
				if (cookie.getName().equals(CifpCookie.QRY_PERIOD.getName()))
					qryFilterPeriod = QryFilterPeriod.getEnum(cookie.getValue());

		} catch (Exception e) {	}

		return(qryFilterPeriod);
	}

	/**
	 * Get Editor from cookies 
	 */
	public QryFilterEditor getEditorFromCookie(Cookie[] cookies) {

		QryFilterEditor qryFilterEditor = QryFilterEditor.ALL_EDIT;

		try {

			for (Cookie cookie : cookies)
				if (cookie.getName().equals(CifpCookie.QRY_EDITOR.getName()))
					qryFilterEditor = QryFilterEditor.getEnum(cookie.getValue());

		} catch (Exception e) {	}

		return(qryFilterEditor);
	}

	/**
	 * Get Editor Name from cookies 
	 */
	public String getEditorNameFromCookie(Cookie[] cookies) {

		String editorName = "";

		try {

			for (Cookie cookie : cookies)
				if (cookie.getName().equals(CifpCookie.QRY_ED_NAME.getName()))
					editorName = cookie.getValue();

		} catch (Exception e) {	}

		return(editorName);
	}

	/**
	 * Create Editor Names Combo
	 */
	public String getEditorsCombo(String selectedEditor, Vector<String> vecActiveCountries) {

		Database DB = null;
		String rc = "";

		try {

			DB = new Database();
			Group GRP = new Group(DB.getConnection());
			Vector<String> vecEditors = GRP.getEditors(vecActiveCountries);

			for (String editorName : vecEditors)
				rc +=
					"<option value=\"" + editorName + "\"" + (editorName.equals(selectedEditor) ? " selected" : "") + ">" +
						editorName +
					"</option>"
				;

		} catch (Exception e) {
			rc = "<option value=\"\" selected>" + e.toString() + "</option>";
		}

		if (DB != null)
			DB.destroy();

		return(rc);
	}

}
