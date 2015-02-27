package de.tarent.aa.veraweb.utils;

import de.tarent.octopus.server.OctopusContext;

/**
 * Helper to play with the Online-Application Configuration
 *
 * @Return true = activated
 * @Return false = deactivated
 *
 * @author jnunez
 */
public class OnlineRegistrationHelper {

	private static final String VWOR_PARAM = "vwor.activated";
	private static final String VWOR_VALUE_TRUE = "true";

	public static Boolean isOnlineregActive(OctopusContext cntx) {

		String active = cntx.moduleConfig().getParam(VWOR_PARAM);

		if (active != null && VWOR_VALUE_TRUE.equals(active)) {
			return true;
		}
		return false;
	}
}
