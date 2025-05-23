////////////////////////////////////////////////////////////////////////////////////////////////////
//
// CifpStatus.java
//
// CIFP Status
//
// First Release: Apr/2024 by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

/**
 * CIFP Status
 */
public enum CifpStatus {

	ALL		("-=[ ALL ]=-",	"",						""					),
	ACTIVE	("Active",		"DS-text-lightgreen",	"DS-back-DarkGreen"	), 
	PAUSED	("Paused",		"DS-text-lightred",		"DS-back-FireBrick"	),
	ORPHAN	("Orphan",		"DS-text-maroon",		"DS-back-Gold"		);

	private final String _Descr;
	private final String _ForeC;
	private final String _BackC;

	/**
	 * Constructor
	 */
	CifpStatus(String descr, String foreC, String backC) {
		this._Descr = descr;
		this._ForeC = foreC;
		this._BackC = backC;
    }

	public String getDescr() { return(this._Descr); }
	public String getForeC() { return(this._ForeC); }
	public String getBackC() { return(this._BackC); }

	/**
	 * GET Symbol span
	 */
	public String getSymbol() {
		return(
			"<span class=\"" + this.getBackC() + " DS-padding-updn-0px DS-padding-lfrg-4px DS-border-full DS-border-round\">" +
				"<span class=\"DS-text-bold " + this.getForeC() + " DS-text-fixed-compact\">" +
					this.getDescr() +
				"</span>" +
			"</span>"
		);
	}

	/**
	 * GET Enum by Code
	 */
	public static CifpStatus getEnum(String name) {

		CifpStatus cifpStatus = ACTIVE;

		for (CifpStatus X : CifpStatus.values())
			if (X.toString().equals(name))
				cifpStatus = X;

		return(cifpStatus);
	}

	/**
	 * Get Combo
	 */
	public static String getCombo(String selected, boolean addOptionAll) {

		String rc = "";

		for (CifpStatus X : CifpStatus.values()) {

			if (addOptionAll || !X.equals(ALL)) {
				rc +=
					"<option value=\"" + X.toString() + "\" " + (X.toString().equals(selected) ? "selected" : "") + ">" +
						X.getDescr() +
					"</option>"
				;
			}
		}

		return(rc);
	}

}
