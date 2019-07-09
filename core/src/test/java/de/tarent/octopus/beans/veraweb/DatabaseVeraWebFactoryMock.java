package de.tarent.octopus.beans.veraweb;
import de.tarent.octopus.server.OctopusContext;

public class DatabaseVeraWebFactoryMock extends DatabaseVeraWebFactory {

    private final DatabaseVeraWeb mock;

    public DatabaseVeraWebFactoryMock(DatabaseVeraWeb mock) {
        this.mock = mock;
    }

    public DatabaseVeraWeb createDatabaseVeraWeb(OctopusContext cntx) {
        return mock;
    }
}
