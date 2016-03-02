package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tmedia_representative_activation")
public class MediaRepresentativeActivation {
    @Id
    private String activation_token;
    private String email;
    private Integer fk_event;

    public MediaRepresentativeActivation(String activation_token, String email, Integer fk_event) {
        this.activation_token = activation_token;
        this.email = email;
        this.fk_event = fk_event;
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
}
