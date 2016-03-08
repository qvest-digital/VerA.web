package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tmedia_representative_activation")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "MediaRepresentativeActivation.getEntryByEmailAndEventId",
                query = "SELECT count(m.*) FROM veraweb.tmedia_representative_activation m where m.email=:email AND m.fk_event=:fk_event"
        )
})
public class MediaRepresentativeActivation {
    @Id
    private String activation_token;
    private String email;
    private Integer fk_event;
    private String gender;
    private String firstname;
    private String lastname;
    private String address;
    private Integer zip;
    private String city;
    private String country;

    public MediaRepresentativeActivation() {
    }

    public String getActivation_token() {
        return activation_token;
    }

    public void setActivation_token(String activation_token) {
        this.activation_token = activation_token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getFk_event() {
        return fk_event;
    }

    public void setFk_event(Integer fk_event) {
        this.fk_event = fk_event;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
