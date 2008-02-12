package de.tarent.dblayer.persistence.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** annotation for marking a Java class as an
 * persistence entity
 * 
 * @author Martin Pelzer, tarent GmbH
 *
 */
@Target (value = { TYPE } )
@Documented
@Retention( RUNTIME )
public @interface Entity {

}
