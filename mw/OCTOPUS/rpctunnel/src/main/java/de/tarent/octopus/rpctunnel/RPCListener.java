package de.tarent.octopus.rpctunnel;

import java.util.Map;

public interface RPCListener
{
	Map execute(String myRole, String partnerRole, String module, String task, Map parameters);
}
