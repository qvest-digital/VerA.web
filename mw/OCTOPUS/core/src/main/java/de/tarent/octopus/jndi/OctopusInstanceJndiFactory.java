package de.tarent.octopus.jndi;
import de.tarent.octopus.client.OctopusConnectionFactory;
import de.tarent.octopus.request.Octopus;

public class OctopusInstanceJndiFactory extends AbstractJndiFactory implements OctopusInstanceLookup {
	protected String getLookupPath() {
		return "octopus/instance";
	}

	public Octopus getOctopusInstance() {
		return OctopusConnectionFactory.getInstance().getInternalOctopusInstance();
	}
}
