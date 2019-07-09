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
  @NamedQuery(name = SalutationAlternative.GET_SALUTATION_ALTERNATIVE_BY_PDF_ID,
    query = "SELECT sa FROM SalutationAlternative sa WHERE sa.pdftemplate_id =:" +
      SalutationAlternative.PARAM_PDFTEMPLATE_ID),
  @NamedQuery(name = SalutationAlternative.DELETE_SALUTATION_ALTERNATIVE_BY_ID,
    query = "DELETE FROM SalutationAlternative sa WHERE sa.pk =:" + SalutationAlternative.PARAM_PK)
})
@NamedNativeQueries(value = {
  @NamedNativeQuery(name = SalutationAlternative.GET_SALUTATION_ALTERNATIVE_FACADE_BY_PDF_ID,
    query = "SELECT sa.pk, sa.salutation_id, sa.pdftemplate_id, sa.content as salutation_alternative, s.salutation " +
      "as " +
      "salutation_default FROM salutation_alternative sa LEFT JOIN tsalutation s on s.pk = sa.salutation_id " +
      "WHERE sa.pdftemplate_id =:" + SalutationAlternative.PARAM_PDFTEMPLATE_ID,
    resultSetMapping = "salutationMapping")
})
public class SalutationAlternative {
    public static final String GET_SALUTATION_ALTERNATIVE_BY_PDF_ID =
      "SalutationAlternative.getAlternativeSalutationsByPdftemplate";
    public static final String GET_SALUTATION_ALTERNATIVE_FACADE_BY_PDF_ID =
      "SalutationAlternative.getSalutationsFacadeByPdftemplate";
    public static final String DELETE_SALUTATION_ALTERNATIVE_BY_ID = "SalutationAlternative.deleteAlternativeSalutationsById";
    public static final String PARAM_PDFTEMPLATE_ID = "pdftemplate_id";
    public static final String PARAM_PK = "pk";

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
