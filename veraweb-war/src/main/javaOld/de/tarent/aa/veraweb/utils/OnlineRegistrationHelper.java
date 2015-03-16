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
	
	/**
	 * Username generator
	 * 
	 * @return String username
	 */
	public static String generateOnlinerUsername(final String firstname, final String lastname) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(firstname).append(lastname);
		
		
		
		return sb.toString();
	}
	
	public static String generateOnlinePassword() {
		// TODO To code
		StringBuilder sb = new StringBuilder();
		
		return sb.toString();
	}
}
