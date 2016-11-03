package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;

import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */

@Data
@XmlRootElement
@Entity
@Table(name = "salutation_alternative")
@SqlResultSetMappings({
        @SqlResultSetMapping(name = "salutationMapping", columns = {
                @ColumnResult(name = "pk"),
                @ColumnResult(name = "salutation_id"),
                @ColumnResult(name = "pdftemplate_id"),
                @ColumnResult(name = "salutation_alternative"),
                @ColumnResult(name = "salutation_default"),
        })
})
@NamedQueries(value = {
        @NamedQuery(name = "SalutationAlternative.getAlternativeSalutationsByPdftemplate", query = "SELECT sa FROM SalutationAlternative sa where pdftemplate_id = :pdftemplate_id")
})
@NamedNativeQueries(value={
        @NamedNativeQuery(name = "SalutationAlternative.getSalutationsFacadeByPdftemplate",
                query = "SELECT sa.pk, sa.salutation_id, sa.pdftemplate_id, sa.content as salutation_alternative, s.salutation as salutation_default FROM salutation_alternative sa LEFT JOIN tsalutation s on s.pk = sa.salutation_id " +
                        "where sa.pdftemplate_id = :pdftemplate_id", resultSetMapping="salutationMapping"),
        @NamedNativeQuery(name="Salutation.getSalutationsWithoutAlternativeContent", query = "SELECT s.* FROM tsalutation s WHERE pk NOT IN (SELECT salutation_id FROM salutation_alternative WHERE pdftemplate_id=:pdftemplate_id)", resultClass=Salutation.class)
})
public class SalutationAlternative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer pk;
    protected Integer salutation_id;
    protected Integer pdftemplate_id;
    protected String content;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public Integer getSalutation_id() {
        return salutation_id;
    }

    public void setSalutation_id(Integer salutation_id) {
        this.salutation_id = salutation_id;
    }

    public Integer getPdftemplate_id() {
        return pdftemplate_id;
    }

    public void setPdftemplate_id(Integer pdftemplate_id) {
        this.pdftemplate_id = pdftemplate_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
