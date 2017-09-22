package org.evolvis.veraweb.onlinereg.entities;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
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
    @NamedQuery(name = PdfTemplate.GET_PDF_TEMPLATE,
        query = "select p from PdfTemplate p where p.pk=:" + PdfTemplate.PARAM_PDF_ID),
    @NamedQuery(name = PdfTemplate.GET_PDF_TEMPLATE_LIST_BY_ORGUNIT,
        query = "select p.pk, p.name from PdfTemplate p where p.fk_orgunit=:" + PdfTemplate.PARAM_PDF_ORGUNIT),
    @NamedQuery(name = PdfTemplate.UPDATE_PDF_TEMPLATE,
            query = "update PdfTemplate p set name=:" + PdfTemplate.PARAM_PDF_NAME + " where p.pk=:" + PdfTemplate.PARAM_PDF_ID),
    @NamedQuery(name = PdfTemplate.UPDATE_PDF_TEMPLATE_CONTENT,
            query = "update PdfTemplate p set name=:" + PdfTemplate.PARAM_PDF_NAME + ", content=:" + PdfTemplate.PARAM_PDF_CONTENT + " where p.pk=:" + PdfTemplate.PARAM_PDF_ID),
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
