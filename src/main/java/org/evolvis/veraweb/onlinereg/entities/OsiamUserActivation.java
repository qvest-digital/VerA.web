package org.evolvis.veraweb.onlinereg.entities;

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
