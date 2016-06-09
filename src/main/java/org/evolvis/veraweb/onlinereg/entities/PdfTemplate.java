package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Data
@XmlRootElement
@Entity
@Table(name = "pdftemplate")
public class PdfTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer pk;
    public String name;
    @Type(type = "org.hibernate.type.BinaryType")
    public byte[] content;
}
