package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2015 benja <benja@benja.com>
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
 *  © 2015 Christian Luginbühl <dinkel@pimprecords.com>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2015 Dmytro Pishchukhin <demon@Demons-MBP.fritz.box>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 rysiekpl <rysiek@hackerspace.pl>
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
 *  © 2015 hong Xu <hong@topbug.net>
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
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * This class handles requests about category.
 */
@Path("/category")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource extends AbstractResource {

    private static final String CATEGORY_NAME = "catname";
    private static final String PERSON_ID = "personId";

    /**
     * Get category id by media representatives uuid and category name.
     *
     * @param catname Category name
     * @param uuid UUID for the media representatives
     *
     * @return The category id
     */
    @GET
    @Path("/{catname}/{uuid}")
    public Integer getCategoryId(@PathParam(CATEGORY_NAME) String catname, @PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Category.findIdByCatname");
            query.setString(CATEGORY_NAME, catname);
            query.setString("uuid", uuid);
            final Integer categoryId = (Integer) query.uniqueResult();
            if (categoryId != null) {
                return categoryId;
            }
            return 0;
        } finally {
            session.close();
        }
    }

    /**
     * Get category ID by category name.
     *
     * @param catname The name of the category
     *
     * @return Category ID
     */
    @GET
    @Path("/identify")
    public Integer getCategoryIdByCategoryName(@QueryParam(CATEGORY_NAME) String catname) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Category.getCategoryIdByCategoryName");
            query.setString(CATEGORY_NAME, catname);

            return (Integer) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Get category name by person ID and Delegation UUID
     * 
     * @param uuid delegation UUID
     * @param personId person ID
     * @return String catname
     */
    @GET
    @Path("/catname/{uuid}/{personId}")
    public String getCatnameByPersonIdAndDelegationUUID(@PathParam("uuid") String uuid,
                                                        @PathParam(PERSON_ID) String personId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Category.findCatnameByUserAndDelegation");
            query.setString("uuid", uuid);
            query.setInteger(PERSON_ID, Integer.parseInt(personId));
            return (String) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    @GET
    @Path("person/data")
    public Integer getCategoryByCatnameAndOrgunit(@QueryParam(CATEGORY_NAME) String categoryName,
                                                  @QueryParam(PERSON_ID) String personId) {
        final Session session = openSession();
        try {
            final Query queryCategory = session.getNamedQuery("Category.findCategoryByPersonIdAndCatname");
            queryCategory.setString(CATEGORY_NAME, categoryName);
            queryCategory.setInteger(PERSON_ID, Integer.valueOf(personId));
            return (Integer) queryCategory.uniqueResult();
        } finally {
            session.close();
        }
    }
}
