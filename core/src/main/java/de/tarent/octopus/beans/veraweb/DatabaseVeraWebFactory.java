package de.tarent.octopus.beans.veraweb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Factory which creates a new {@link DatabaseVeraWeb}. This was needed to unit test the legacy code.
 *
 * @author Hendrik Helwich
 */
public class DatabaseVeraWebFactory {

    public DatabaseVeraWeb createDatabaseVeraWeb(OctopusContext cntx) {
        return new DatabaseVeraWeb(cntx);
    }
}
