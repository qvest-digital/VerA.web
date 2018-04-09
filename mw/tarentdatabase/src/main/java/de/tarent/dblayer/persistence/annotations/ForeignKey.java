package de.tarent.dblayer.persistence.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** annotation for defining a foreign key
 * 
 * @author Martin Pelzer, tarent GmbH
 *
 */
@Target (value = { METHOD } )
@Documented
@Retention( RUNTIME )
public @interface ForeignKey {

	/** the bean which is referenced
	 * 
	 */
	Class bean();
	
}
