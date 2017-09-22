package org.evolvis.veraweb.onlinereg.entities;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
