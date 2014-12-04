package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
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
@NamedNativeQueries(value={
		 @NamedNativeQuery(name="Person.getDelegatesByUUID", query = "SELECT tperson.* FROM tperson LEFT JOIN tguest g on tperson.pk=g.fk_person WHERE g.delegation=:uuid AND tperson.iscompany='f'", resultClass=Person.class)	
})
public class Person {


    private static final String USERNAME_TEMPLATE = "username:";
    private static final String DELEGIERT_NAME = "delegiert";
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
    
    private String mail_a_e1;
    private String street_a_e1;
    private String zipcode_a_e1;
    private String city_a_e1;
    private String country_a_e1;
    private String sex_a_e1;

    public void setFirstName(String firstName) {
        firstname_a_e1 = firstname_a_e2 = firstname_a_e3 = firstName;
    }

    public void setLastName(String lastName) {
        lastname_a_e1 = lastname_a_e2 = lastname_a_e3 = lastName;
    }

    public void setUsername(String username) {
        if(note_a_e1 != null && note_a_e1.contains(USERNAME_REGEX)) {
            note_a_e1.replaceFirst(USERNAME_REGEX, USERNAME_TEMPLATE+username);
        }
        else {
            note_a_e1 = USERNAME_TEMPLATE+username;
        }
    }

    public void setNote_a_e1(String note_a_e1) {
        this.note_a_e1 = note_a_e1;
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public char getIsCompany() {
		return isCompany;
	}

	public void setIsCompany(char isCompany) {
		this.isCompany = isCompany;
	}

	public char getDeleted() {
		return deleted;
	}

	public void setDeleted(char deleted) {
		this.deleted = deleted;
	}

	public String getFirstname_a_e1() {
		return firstname_a_e1;
	}

	public void setFirstname_a_e1(String firstname_a_e1) {
		this.firstname_a_e1 = firstname_a_e1;
	}

	public String getFirstname_a_e2() {
		return firstname_a_e2;
	}

	public void setFirstname_a_e2(String firstname_a_e2) {
		this.firstname_a_e2 = firstname_a_e2;
	}

	public String getFirstname_a_e3() {
		return firstname_a_e3;
	}

	public void setFirstname_a_e3(String firstname_a_e3) {
		this.firstname_a_e3 = firstname_a_e3;
	}

	public String getLastname_a_e1() {
		return lastname_a_e1;
	}

	public void setLastname_a_e1(String lastname_a_e1) {
		this.lastname_a_e1 = lastname_a_e1;
	}

	public String getLastname_a_e2() {
		return lastname_a_e2;
	}

	public void setLastname_a_e2(String lastname_a_e2) {
		this.lastname_a_e2 = lastname_a_e2;
	}

	public String getLastname_a_e3() {
		return lastname_a_e3;
	}

	public void setLastname_a_e3(String lastname_a_e3) {
		this.lastname_a_e3 = lastname_a_e3;
	}

	public String getMail_a_e1() {
		return mail_a_e1;
	}

	public void setMail_a_e1(String mail_a_e1) {
		this.mail_a_e1 = mail_a_e1;
	}

	public String getStreet_a_e1() {
		return street_a_e1;
	}

	public void setStreet_a_e1(String street_a_e1) {
		this.street_a_e1 = street_a_e1;
	}

	public String getZipcode_a_e1() {
		return zipcode_a_e1;
	}

	public void setZipcode_a_e1(String zipcode_a_e1) {
		this.zipcode_a_e1 = zipcode_a_e1;
	}

	public String getCity_a_e1() {
		return city_a_e1;
	}

	public void setCity_a_e1(String city_a_e1) {
		this.city_a_e1 = city_a_e1;
	}

	public String getCountry_a_e1() {
		return country_a_e1;
	}

	public void setCountry_a_e1(String country_a_e1) {
		this.country_a_e1 = country_a_e1;
	}

	public String getSex_a_e1() {
		return sex_a_e1;
	}

	public void setSex_a_e1(String sex_a_e1) {
		this.sex_a_e1 = sex_a_e1;
	}

	public String getNote_a_e1() {
		return note_a_e1;
	}

	public void setPk(int pk) {
		this.pk = pk;
	}
    
}
