/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.utils;

import de.tarent.octopus.server.OctopusContext;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

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

	private static final String MANDANTEN_PARAM = "mandanten-online-registration.deactivated";

    /** Logger für diese Klasse */
    private final static Logger logger = Logger.getLogger(OnlineRegistrationHelper.class);

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

	public static int[] getDeactivatedMandantsAsArray(OctopusContext cntx) {

		final String list = cntx.moduleConfig().getParam(MANDANTEN_PARAM);
		final String[] sepList = list.split(",");

		int[] result = new int[sepList.length];


        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = Integer.parseInt(sepList[i].trim());
            } catch (NumberFormatException ex) {
                result[i] = 0;
                logger.error("PARAM \"mandanten-online-registration.deactivated\" in config_override.xml set wrong!", ex);
            }
        }

		return result;
	}
}
