package de.tarent.octopus.response;
import lombok.extern.log4j.Log4j2;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

@Log4j2
public class TcVelocityResponseLogger implements LogSystem {
    public TcVelocityResponseLogger() {
        // do Nothing
    }

    public void init(RuntimeServices rsvc) {
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
