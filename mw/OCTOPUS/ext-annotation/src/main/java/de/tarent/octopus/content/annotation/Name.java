package de.tarent.octopus.content.annotation;

import java.lang.annotation.*;

/**
 * Element zur Beschreibung des Names eines Parameters.
 * Dieser Annotation ist gleichbedeutend mit dem name-Attribut von WebParam,
 * hat jedoch eine einfachere Schreibweise.
 * Kann in Verbindung mit JSR181 Annotations (WebService Meta Data) zur Beschreibung von Workern verwendet werden.
 *
 *
 * @see javax.jws.WebMethod;
 * @see http://jcp.org/aboutJava/communityprocess/final/jsr181/index.html
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER})
public @interface Name {

    /**
     * Versionsbezeichnung des Objektes
     */
    String value();
}
