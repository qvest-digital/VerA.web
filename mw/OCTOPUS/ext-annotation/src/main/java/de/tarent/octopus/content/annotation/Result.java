package de.tarent.octopus.content.annotation;

import java.lang.annotation.*;

/**
 * Element zur Beschreibung des Namen des Ausgabeparamters einer Methode
 * Kann in Verbindung mit JSR181 Annotations (WebService Meta Data) zur Beschreibung von Workern verwendet werden.
 * Dieser Annotation ist gleichbedeutend mit dem name-Attribut von WebResult,
 * hat jedoch eine einfachere Schreibweise.
 *
 *
 * @see javax.jws.WebResult;
 * @see javax.jws.WebMethod;
 * @see http://jcp.org/aboutJava/communityprocess/final/jsr181/index.html
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
public @interface Result {

    /**
     * Beschreibung des Objektes
     */
    String value() default "return";
}
