package de.tarent.dblayer.persistence.annotations;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * annotation for marking getter methods that shall
 * not be persisted
 *
 * @author Martin Pelzer, tarent GmbH
 */
@Target(value = { METHOD })
@Retention(RUNTIME)
@Documented
public @interface Transient {

}
