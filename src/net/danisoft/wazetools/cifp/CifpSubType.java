////////////////////////////////////////////////////////////////////////////////////////////////////
//
// CifpSubType.java
//
// CIFP SubTypes
//
// First Release: Mar/2024 by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wazetools.cifp;

/**
 * CIFP SubTypes
 */
public enum CifpSubType {

	ACCIDENT_MINOR,
	ACCIDENT_MAJOR,
/*
	HAZARD_ON_ROAD,
	HAZARD_ON_ROAD_CAR_STOPPED,
	HAZARD_ON_ROAD_CONSTRUCTION,
	HAZARD_ON_ROAD_EMERGENCY_VEHICLE,
	HAZARD_ON_ROAD_ICE,
	HAZARD_ON_ROAD_LANE_CLOSED,
	HAZARD_ON_ROAD_OBJECT,
	HAZARD_ON_ROAD_OIL,
	HAZARD_ON_ROAD_POT_HOLE,
	HAZARD_ON_ROAD_ROAD_KILL,
	HAZARD_ON_ROAD_TRAFFIC_LIGHT_FAULT,
	HAZARD_ON_SHOULDER,
	HAZARD_ON_SHOULDER_ANIMALS,
	HAZARD_ON_SHOULDER_CAR_STOPPED,
	HAZARD_ON_SHOULDER_MISSING_SIGN,
	HAZARD_WEATHER,
	HAZARD_WEATHER_FLOOD,
	HAZARD_WEATHER_FOG,
	HAZARD_WEATHER_FREEZING_RAIN,
	HAZARD_WEATHER_HAIL,
	HAZARD_WEATHER_HEAT_WAVE,
	HAZARD_WEATHER_HEAVY_RAIN,
	HAZARD_WEATHER_HEAVY_SNOW,
	HAZARD_WEATHER_HURRICANE,
	HAZARD_WEATHER_MONSOON,
	HAZARD_WEATHER_TORNADO,
*/
	ROAD_CLOSED_CONSTRUCTION,
	ROAD_CLOSED_HAZARD,
	ROAD_CLOSED_EVENT;
/*
	JAM_LIGHT_TRAFFIC,
	JAM_MODERATE_TRAFFIC,
	JAM_HEAVY_TRAFFIC,
	JAM_STAND_STILL_TRAFFIC,

	POLICE_VISIBLE,
	POLICE_HIDING;
*/
	/**
	 * Get Type
	 */
	public static CifpType getType(String subType) {

		CifpType rc = CifpType.ALL;

		for (CifpType cifpType : CifpType.values())
			if (subType.startsWith(cifpType.toString()))
				rc = cifpType;

		return(rc);
	}

	/**
	 * Get Combo
	 */
	public static String getCombo(String type, String selected) {

		String rc = "<option value=\"\"" + (selected.equals("") ? " selected" : "") + "></option>";

		for (CifpSubType X : CifpSubType.values()) {
			if (X.toString().startsWith(type)) {
				rc +=
					"<option value=\"" + X.toString() + "\" " + (X.toString().equals(selected) ? "selected" : "") + ">" +
						X.toString() +
					"</option>"
				;
			}
		}

		return(rc);
	}

}
