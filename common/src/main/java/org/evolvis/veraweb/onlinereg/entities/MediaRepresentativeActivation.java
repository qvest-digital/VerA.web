package org.evolvis.veraweb.onlinereg.entities;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
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
@NamedQueries({
        @NamedQuery(name = "MediaRepresentativeActivation.getActivationByActivationToken",
                query = "SELECT m FROM MediaRepresentativeActivation m where activation_token=:activation_token"),
        @NamedQuery(name = "MediaRepresentativeActivation.activate",
                query = "UPDATE MediaRepresentativeActivation m SET activated=1 WHERE fk_event=:fk_event AND email=:email")
})
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
    private Integer activated;

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

    public Integer getActivated() {
        return activated;
    }

    public void setActivated(Integer activated) {
        this.activated = activated;
    }
}
