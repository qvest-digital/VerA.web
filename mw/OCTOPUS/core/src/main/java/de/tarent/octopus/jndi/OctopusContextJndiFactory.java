package de.tarent.octopus.jndi;
import de.tarent.octopus.server.Context;
import de.tarent.octopus.server.OctopusContext;

public class OctopusContextJndiFactory extends AbstractJndiFactory implements OctopusContextLookup {
    protected String getLookupPath() {
        return "octopus/context";
    }

    public OctopusContext getOctopusContext() {
        return Context.getActive();
    }
}
