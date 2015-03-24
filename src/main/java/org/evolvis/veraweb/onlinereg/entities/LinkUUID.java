package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;

import javax.persistence.Entity;
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
        @NamedQuery(name = "LinkUUID.getUserIdByUUID", query = "SELECT l FROM LinkUUID l where uuid = :uuid"),
})
public class LinkUUID {
    private Long id;
    private String uuid;
    private String linktype;
    private Integer personid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
