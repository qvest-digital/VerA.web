package de.tarent.dblayer.persistence.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Annotation for defining the mapping of a bean attribute
 * to a column in the database.
 *
 * @author Martin Pelzer, tarent GmbH
 *
 */
@Target (value = { METHOD } )
@Documented
@Retention( RUNTIME )
public @interface Column {

	/** the name of the column in the database
	 *
	 */
	String name();

	/** the table of the column; if not given the table defined for
	 * the whole bean is used
	 *
	 */
	String table() default "";

}
