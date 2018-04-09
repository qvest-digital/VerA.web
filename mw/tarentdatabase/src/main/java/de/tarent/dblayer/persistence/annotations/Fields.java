package de.tarent.dblayer.persistence.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** annotation for defining the fields a mapped field should belong to (when
 * used as annotation for a method) or for defining new field sets (when
 * used as annotation for the whole bean)
 *
 * @author Martin Pelzer, tarent GmbH
 *
 */
@Target (value = { METHOD } )
@Documented
@Retention( RUNTIME )
public @interface Fields {

	// an array of all field sets (given by their name as string)
	String [] value();

}
