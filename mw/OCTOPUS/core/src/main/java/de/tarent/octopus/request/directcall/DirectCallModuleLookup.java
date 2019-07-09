package de.tarent.octopus.request.directcall;
import de.tarent.octopus.config.TcModuleLookup;
import de.tarent.octopus.request.TcEnv;

import java.io.File;

/**
 * Diese Klasse liefert dem Octopus notwendige Daten.
 */
class DirectCallModuleLookup implements TcModuleLookup {
    /**
     * DirectCallModuleLookup
     */
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

        if (realPath.length() != 0) {
            final File res = new File(realPath);
            if (res.exists()) {
                return res;
            }
        }
        return null;
    }
}
