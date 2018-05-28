package org.evolvis.veraweb.onlinereg.entities;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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
