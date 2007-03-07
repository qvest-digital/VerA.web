package de.tarent.octopus.request.directCall;

import java.io.File;

import de.tarent.octopus.config.TcModuleLookup;
import de.tarent.octopus.request.TcEnv;

/**
 * Diese Klasse liefert dem Octopus notwendige Daten.
 */
class DirectCallModuleLookup implements TcModuleLookup {
	/** DirectCallModuleLookup */
	private final OctopusDirectCallStarter octopusDirectCallStarter;

	/**
	 * @param starter
	 */
	DirectCallModuleLookup(OctopusDirectCallStarter starter) {
		octopusDirectCallStarter = starter;
	}

	public File getModulePath(String module) {
		String realPath = octopusDirectCallStarter.getEnv().getValue(
				TcEnv.KEY_PATHS_ROOT) + "modules/" + module + "/";
		
		if (realPath != null && realPath.length() != 0) {
			if (new File(realPath).exists())
				return new File(realPath);
		}
		return null;
	}
}
