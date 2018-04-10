package de.tarent.octopus.client;
/**
 *
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class FactoryConfigurationException extends RuntimeException {
    /**
	 * serialVersionUID = 5571742019254783113L
	 */
	private static final long serialVersionUID = 5571742019254783113L;

	public FactoryConfigurationException(String msg, Exception e) {
        super(msg, e);
    }
}
