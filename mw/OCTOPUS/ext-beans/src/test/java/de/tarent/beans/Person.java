package de.tarent.beans;
import java.util.Date;

import de.tarent.octopus.beans.MapBean;

/**
 * This bean class represents an entry of the table <code>person</code> of the
 * test database schema.
 *
 * @author mikel
 */
public class Person extends MapBean {
    //
    // bean attributes
    //
    /**
     * pk_person integer
     */
    public Integer id;
    /**
     * fk_firma integer
     */
    public Integer firmId;
    /**
     * vorname varchar(50)
     */
    public String forename;
    /**
     * nachname varchar(50)
     */
    public String surname;
    /**
     * geburtstag date
     */
    public Date dateOfBirth;

    //
    // sample bean
    //
    /**
     * sample {@link Person} bean for easier use of the bean framework
     */
    public final static Person SAMPLE = new Person();
}
