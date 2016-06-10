package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@NamedQueries({
    @NamedQuery(name = PdfTemplate.DELETE_PDF_TEMPLATE,
        query = "delete from PdfTemplate p where fk=:" + PdfTemplate.PARAM_PDF_ID)
})
public class PdfTemplate {

    public static final String DELETE_PDF_TEMPLATE = "PdfTemplate.deletePdfTemplateById";
    public static final String PARAM_PDF_ID = "id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer pk;
    public String name;
    @Type(type = "org.hibernate.type.BinaryType")
    public byte[] content;
}
