/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 18.07.2006
 */

package de.tarent.octopus.rpctunnel;

import java.util.Map;

interface RPCListener
{
	Map execute(String myRole, String partnerRole, String module, String task, Map parameters);
}
