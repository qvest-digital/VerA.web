package org.evolvis.veraweb.onlinereg.entities;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mley on 02.09.14.
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tconfig")
@NamedQueries(value = {
  @NamedQuery(name = "Config.find", query = "SELECT c FROM Config c where cname = :key")
})
public class Config {
    @Id
    private int pk;
    private String cname;
    private String cvalue;
}
