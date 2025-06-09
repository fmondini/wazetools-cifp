////////////////////////////////////////////////////////////////////////////////////////////////////
//
// AppCfg.java
//
// main application configuration file
//
// First Release: Apr/2025 by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools;

import net.danisoft.dslib.AppList;
import net.danisoft.dslib.FmtTool;
import net.danisoft.dslib.SiteCfg;
import net.danisoft.dslib.SysTool;

public class AppCfg {

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Editable parameters
	//

	private static final int	APP_VERS_MAJ = 0;
	private static final int	APP_VERS_MIN = 80;
	private static final String	APP_VERS_REL = "rc";
	private static final String	APP_DATE_REL = "Jun 9, 2025";

	private static final String	APP_NAME_TAG = AppList.CIFP.getName();
	private static final String	APP_NAME_TXT = "Waze.Tools " + APP_NAME_TAG;
	private static final String	APP_ABSTRACT = "Waze.Tools Closure and Incident Feed Project";
	private static final String	APP_EXITLINK = "https://waze.tools/";

	private static final String	SERVER_ROOTPATH_DEVL = "C:/WorkSpace/Eclipse/Waze.Tools/wazetools-cifp/Web Content";
	private static final String	SERVER_ROOTPATH_PROD = "/var/www/html/cifp.waze.tools/Web Content";

	private static final String	SERVER_HOME_URL_DEVL = "http://localhost:8080/cifp.waze.tools";
	private static final String	SERVER_HOME_URL_PROD = "https://cifp.waze.tools";

	// Login stuff
	private static final String	ONLOGOUT_URL = "../home/";

	// Local stuff
	private static final String CIFP_SCRP_ID = "9ef6797a-6358-4b31-8147-511099e3419f";
	private static final String	ZIP_DESTPATH = getServerRootPath() + "/zip";

	// Slack stuff
	private static final String	SLACK_BTNAME = "UREQ_NotifyBot";
	private static final String	SLACK_BTEMOJ = ":bowtie:";

	// GMAP stuff
	private static final String	GMAP_DIVNAME = "divGMapContent";
	private static final int	GMAP_DEFZOOM = 5;
	private static final double	GMAP_DEFCLAT = 43.0D;
	private static final double	GMAP_DEFCLNG = 11.0D;

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Getters
	//

	public static final String getAppTag()				{ return(APP_NAME_TAG);	}
	public static final String getAppName()				{ return(APP_NAME_TXT);	}
	public static final String getAppAbstract()			{ return(APP_ABSTRACT);	}
	public static final String getAppVersion()			{ return(APP_VERS_MAJ + "." + FmtTool.fmtZeroPad(APP_VERS_MIN, 2) + "." + APP_VERS_REL); }
	public static final String getAppRelDate()			{ return(APP_DATE_REL);	}
	public static final String getAppExitLink()			{ return(APP_EXITLINK);	}
	public static final String getServerRootPath()		{ return(SysTool.isWindog() ? SERVER_ROOTPATH_DEVL : SERVER_ROOTPATH_PROD); }
	public static final String getServerHomeUrl()		{ return(SysTool.isWindog() ? SERVER_HOME_URL_DEVL : SERVER_HOME_URL_PROD); }
	// Login stuff
	public static final String getAuthDefaultUser()		{ return(SysTool.isWindog() ? new SiteCfg().getPrivateParams().getDebugUser() : ""); }
	public static final String getAuthDefaultPass()		{ return(SysTool.isWindog() ? new SiteCfg().getPrivateParams().getDebugPass() : ""); }
	public static final String getAuthOnLogoutUrl()		{ return(ONLOGOUT_URL); }
	// Zip stuff
	public static final String getZipDestPath()			{ return(ZIP_DESTPATH);	}
	// Slack stuff
	public static final String getSlackBotName()		{ return(SLACK_BTNAME); }
	public static final String getSlackBotEmoji()		{ return(SLACK_BTEMOJ); }
	public static final String getSlackSecretToken()	{ return(new SiteCfg().getPrivateParams().getSlackToken()); }
	// Script stuff
	public static final String getCifpScriptId()		{ return CIFP_SCRP_ID;	}
	// GMap stuff
	public static final String getGMapDivName() 		{ return(GMAP_DIVNAME); }
	public static final int    getGMapDefZoom() 		{ return(GMAP_DEFZOOM); }
	public static final double getGMapDefCLat() 		{ return(GMAP_DEFCLAT); }
	public static final double getGMapDefCLng() 		{ return(GMAP_DEFCLNG); }
	public static final String getGMapActvKey() 		{ return(new SiteCfg().getPrivateParams().getGoogleMapKey()); }
}
