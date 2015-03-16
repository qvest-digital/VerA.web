package de.tarent.aa.veraweb.utils;

import de.tarent.octopus.server.OctopusContext;
import org.apache.commons.lang.RandomStringUtils;

import java.util.Random;

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
	private static final String PASSWORD_GENERATOR_AUSWAHLMOEGLICHKEITEN =
			"abzdefghijklmnopqrstuvwxyzABZDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$-_#<>@&()+=}|";

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

	/**
	 * Generate random password for "Online-Anmeldung".
	 *
	 * @return The password
	 */
	public String generatePassword() {

		String random = null;
		do {
			random = RandomStringUtils.random(8, PASSWORD_GENERATOR_AUSWAHLMOEGLICHKEITEN.toCharArray());
		} while (!random.matches((".*(?=.*\\d)(?=.*[A-Z])(?=.*[-_$!#<>@&()+=}]).*")));

		return random;
	}
}
