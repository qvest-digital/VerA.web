package de.tarent.dblayer.persistence.annotations;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for defining a reference to another bean
 *
 * @author Martin Pelzer, tarent GmbH
 */
@Target(value = { METHOD })
@Documented
@Retention(RUNTIME)
public @interface Reference {

    /**
     * the bean which is referenced
     */
    Class bean();

    /**
     * the field referenced in the bean
     */
    String[] fields() default "commonFields";
}
