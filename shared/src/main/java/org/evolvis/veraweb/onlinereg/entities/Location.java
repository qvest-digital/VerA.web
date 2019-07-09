package org.evolvis.veraweb.onlinereg.entities;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mley on 03.08.14.
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tlocation")
public class Location {
    @Id
    private int pk;

    private String locationname;
}
