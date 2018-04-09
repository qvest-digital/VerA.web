package de.tarent.dblayer.persistence.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** annotation for setting the type with which java variable
 * names should be mapped to column names.
 * 
 * @author Martin Pelzer, tarent GmbH
 *
 */
@Target (value = { TYPE } )
@Documented
@Retention( RUNTIME )
public @interface MappingType {

	enum Value { CAMEL_CASE, LOWER_CASE, UPPER_CASE, LOWER_CASE_UNDERSCORE, UPPER_CASE_UNDERSCORE }
	
	Value value() default Value.LOWER_CASE_UNDERSCORE; 
}
