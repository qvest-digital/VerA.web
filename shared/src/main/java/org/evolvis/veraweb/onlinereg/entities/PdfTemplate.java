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

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Data
@XmlRootElement
@Entity
@Table(name = "pdftemplate")
@NamedQueries({
  @NamedQuery(name = PdfTemplate.GET_PDF_TEMPLATE,
    query = "select p from PdfTemplate p where p.pk=:" + PdfTemplate.PARAM_PDF_ID),
  @NamedQuery(name = PdfTemplate.GET_PDF_TEMPLATE_LIST_BY_ORGUNIT,
    query = "select p.pk, p.name from PdfTemplate p where p.fk_orgunit=:" + PdfTemplate.PARAM_PDF_ORGUNIT),
  @NamedQuery(name = PdfTemplate.UPDATE_PDF_TEMPLATE,
    query = "update PdfTemplate p set name=:" + PdfTemplate.PARAM_PDF_NAME + " where p.pk=:" +
      PdfTemplate.PARAM_PDF_ID),
  @NamedQuery(name = PdfTemplate.UPDATE_PDF_TEMPLATE_CONTENT,
    query = "update PdfTemplate p set name=:" + PdfTemplate.PARAM_PDF_NAME + ", content=:" +
      PdfTemplate.PARAM_PDF_CONTENT +
      " where p.pk=:" + PdfTemplate.PARAM_PDF_ID),
  @NamedQuery(name = PdfTemplate.DELETE_PDF_TEMPLATE,
    query = "delete from PdfTemplate p where p.pk=:" + PdfTemplate.PARAM_PDF_ID)
})
public class PdfTemplate {
    public static final String GET_PDF_TEMPLATE = "PdfTemplate.getPdfTemplateById";
    public static final String GET_PDF_TEMPLATE_LIST_BY_ORGUNIT = "PdfTemplate.getPdfTemplateListByOrgunit";
    public static final String UPDATE_PDF_TEMPLATE = "PdfTemplate.updatePdfTemplateById";
    public static final String UPDATE_PDF_TEMPLATE_CONTENT = "PdfTemplate.updatePdfTemplateByIdWithContent";
    public static final String DELETE_PDF_TEMPLATE = "PdfTemplate.deletePdfTemplateById";
    public static final String PARAM_PDF_ID = "id";
    public static final String PARAM_PDF_ORGUNIT = "fk_orgunit";
    public static final String PARAM_PDF_NAME = "name";
    public static final String PARAM_PDF_CONTENT = "content";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer pk;
    public String name;
    @Type(type = "org.hibernate.type.BinaryType")
    public byte[] content;
    private Integer fk_orgunit;
}
