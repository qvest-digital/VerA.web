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
@Table(name = "tmaildraft")
@NamedQueries({
        @NamedQuery(name = MailTemplate.GET_MAILTEMPLATE_BY_ID,
                query = "SELECT mt FROM MailTemplate mt WHERE pk=:templateId AND fk_orgunit=:mandantId")

})
public class MailTemplate {

    public static final String GET_MAILTEMPLATE_BY_ID = "MailTemplate.getPdfTemplateByIdAndOrgUnit";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;
    private String name;
    private String subject;
    private String content;
    private Integer fk_orgunit;
}
