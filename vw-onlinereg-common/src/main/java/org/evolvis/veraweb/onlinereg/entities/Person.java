package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by mley on 01.09.14.
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tperson")
@NamedQueries(value = {
        @NamedQuery(name = "Person.findByUsername", query = "SELECT p FROM Person p where note_a_e1 like :username"),
        @NamedQuery(name = "Person.findPersonIdByUsername", query = "SELECT p.pk FROM Person p where note_a_e1 like :username")
})
public class Person {


    private static final String USERNAME_TEMPLATE = "username:";
    private static final String USERNAME_REGEX = USERNAME_TEMPLATE+"(\\w+)";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk;

    private Date created = new Date();
    private String createdby = "onlinereg";
    private Date changed;
    private String changedby;

    private int fk_orgunit = 1;
    private char isCompany = 'f';
    private char deleted = 'f';
    private int fk_workarea = 0;

    private String note_a_e1;
    private String firstname_a_e1;
    private String firstname_a_e2;
    private String firstname_a_e3;
    private String lastname_a_e1;
    private String lastname_a_e2;
    private String lastname_a_e3;

    public void setFirstName(String firstName) {
        firstname_a_e1 = firstname_a_e2 = firstname_a_e3 = firstName;
    }

    public void setLastName(String lastName) {
        lastname_a_e1 = lastname_a_e2 = lastname_a_e3 = lastName;
    }

    public void setUsername(String username) {
        if(note_a_e1 != null && note_a_e1.contains(USERNAME_REGEX)) {
            note_a_e1.replaceFirst(USERNAME_REGEX, USERNAME_TEMPLATE+username);
        } else {
            note_a_e1 = USERNAME_TEMPLATE+username;
        }
    }

    public void setFk_orgunit(Integer fk_orgunit) {
        this.fk_orgunit = fk_orgunit;
    }

    public int getFk_orgunit() {
        return this.fk_orgunit;
    }

    public int getPk() {
        return this.pk;
    }
}
