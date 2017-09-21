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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tosiam_user_activation")
@NamedQueries({
        @NamedQuery(name = "OsiamUserActivation.getOsiamUserActivationEntryByToken",
                    query = "SELECT oua FROM OsiamUserActivation oua where activation_token=:activation_token"),
        @NamedQuery(name = "OsiamUserActivation.getOsiamUserActivationEntryByUsername",
                query = "SELECT oua FROM OsiamUserActivation oua where username=:username"),
        @NamedQuery(name = "OsiamUserActivation.refreshOsiamUserActivationByUsername",
                    query = "UPDATE OsiamUserActivation oua SET activation_token=:activation_token, expiration_date=:expiration_date " +
                            "WHERE username=:username")
})
public class OsiamUserActivation {
    @Id
    private String activation_token;
    private String username;
    private Date expiration_date;

    public OsiamUserActivation() {
    }

    public OsiamUserActivation(String username, Date expiration_date, String activation_token) {
        this.username=username;
        this.expiration_date=expiration_date;
        this.activation_token=activation_token;
    }

    public String getActivation_token() {
        return activation_token;
    }

    public void setActivation_token(String activation_token) {
        this.activation_token = activation_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(Date expiration_date) {
        this.expiration_date = expiration_date;
    }
}
