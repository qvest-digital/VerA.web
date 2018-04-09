package de.tarent.dblayer.persistence.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Annotation for defined the standard table for a whole entity.
 * All attributes in this entity will be mapped to columns in this table
 * if no other table is defined.
 *
 * @author Martin Pelzer, tarent GmbH
 *
 */
@Target (value = { TYPE } )
@Retention( RUNTIME )
@Documented
public @interface Table {

	/** the name of the table in the database
	 *
	 * @return
	 */
	String name();

}
