package de.tarent.octopus.response;
import org.apache.commons.logging.Log;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

import de.tarent.octopus.logging.LogFactory;

public class TcVelocityResponseLogger implements LogSystem {
	/** Der Logger */
	private static Log logger = LogFactory.getLog(TcVelocityResponseLogger.class);

	public TcVelocityResponseLogger () {
		// do Nothing
	}

	public void init (RuntimeServices rsvc) {
		// do Nothing
	}

	public void logVelocityMessage(int level, String message) {
		switch (level) {
			case 1:
				logger.trace(message);
				break;
			case 2:
				logger.debug(message);
				break;
			case 3:
				logger.warn(message);
				break;
			default:
				logger.error("[Unknown Level (" + level + ")] " + message);
				break;
		}
	}
}
