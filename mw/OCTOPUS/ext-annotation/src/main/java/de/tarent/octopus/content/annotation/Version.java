package de.tarent.octopus.content.annotation;

import java.lang.annotation.*;

/**
 * Element zur Beschreibung der Version eines Objektes z.B. eines octopus ContentWorkers.
 * Kann in Verbindung mit JSR181 Annotations (WebService Meta Data) zur Beschreibung von Workern verwendet werden.
 *
 *
 * @see javax.jws.WebMethod;
 * @see http://jcp.org/aboutJava/communityprocess/final/jsr181/index.html
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface Version {

    /**
     * Versionsbezeichnung des Objektes
     */
    String value() default "";
}
