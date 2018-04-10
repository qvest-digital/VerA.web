package de.tarent.octopus.content.annotation;

import java.lang.annotation.*;

/**
 * Element Kennzeichnung eines Parameters als optionalen Parameter.
 * Der Defaultwert ist 'nicht optional', wenn ein WorkerParameter diese
 * Annotation nicht besitzt wird er somit als zwingen erforderlich angesehen.
 * <p>Kann in Verbindung mit JSR181 Annotations (WebService Meta Data)
 * zur Beschreibung von Workern verwendet werden.
 *
 *
 * @see javax.jws.WebMethod;
 * @see http://jcp.org/aboutJava/communityprocess/final/jsr181/index.html
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER})
public @interface Optional {

    /**
     * Beschreibung des Objektes
     */
    boolean value() default true;
}
