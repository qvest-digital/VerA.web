package de.tarent.octopus.content.annotation;

import java.lang.annotation.*;

/**
 * Element zur Beschreibung einer Methode oder eines Paramters
 * Kann in Verbindung mit JSR181 Annotations (WebService Meta Data) zur Beschreibung von Workern verwendet werden.
 *
 *
 * @see javax.jws.WebMethod;
 * @see http://jcp.org/aboutJava/communityprocess/final/jsr181/index.html
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
public @interface Description {

    /**
     * Beschreibung des Objektes
     */
    String value() default "";
}
