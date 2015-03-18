package de.tarent.aa.veraweb.utils;

import de.tarent.octopus.server.OctopusContext;

/**
 * Helper to play with the Online-Application Configuration.
 *
 * @return true if activated, otherwise false
 *
 * @author jnunez
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class OnlineRegistrationHelper {

	private static final String VWOR_PARAM = "online-registration.activated";
	private static final String VWOR_VALUE_TRUE = "true";

	/**
	 * Check for enabled online registration module.
	 *
	 * @param cntx The context
	 * @return true if online registration is enabled, otherwise false
	 */
	public static Boolean isOnlineregActive(OctopusContext cntx) {

		final String active = cntx.moduleConfig().getParam(VWOR_PARAM);

		if (active != null && VWOR_VALUE_TRUE.equals(active)) {
			return true;
		}
		return false;
	}
}
