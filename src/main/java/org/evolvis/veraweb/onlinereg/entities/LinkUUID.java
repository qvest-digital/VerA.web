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

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */

@Data
@XmlRootElement
@Entity
@Table(name = "link_uuid")
@NamedQueries({
    @NamedQuery(name = "LinkUUID.getUserIdByUUID", query = "SELECT l.personid FROM LinkUUID l where l.uuid = :uuid")
})
public class LinkUUID {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;
    private String uuid;
    private String linktype;
    private Integer personid;

    public Integer getId() {
        return pk;
    }

    public void setId(Integer id) {
        this.pk = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLinktype() {
        return linktype;
    }

    public void setLinktype(String linktype) {
        this.linktype = linktype;
    }

    public Integer getPersonid() {
        return personid;
    }

    public void setPersonid(Integer personid) {
        this.personid = personid;
    }
}
