package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import org.evolvis.veraweb.common.RestPaths;
import org.evolvis.veraweb.export.CsvExporter;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.utils.KeepOpenWriter;
import org.evolvis.veraweb.Constants;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by mweier on 23.03.16.
 */
@Path(RestPaths.REST_EXPORT)
@Produces(Constants.CONTENT_TYPE_CSV)
public class ExportResource extends AbstractResource {
    @javax.ws.rs.core.Context
    ResourceContext resourceContext;

    private InitialContext initContext;
    private static final String CONFIG_FILE_NAME = "config.jsn";
    private static final String CONFIG_PLACEHOLDER = "__event_id_placeholder__";

    private Event getEvent(int eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.getEvent");
            query.setParameter("pk", eventId);
            return (Event) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    @POST
    @Path(RestPaths.REST_EXPORT_GET_GUESTLIST)
    public Response getGuestList(@PathParam("eventId") final int eventId,
      MultivaluedMap<String, String> params,
      @FormParam("selectedFields[]") List<String> selList)
      throws NamingException, UnsupportedEncodingException {
        final Event event = getEvent(eventId);
        final String downloadFilename = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "_export.csv";
        if (initContext == null) {
            initContext = new InitialContext();
        }
        final Context namingContext = (Context) initContext.lookup("java:comp/env");
        final DataSource dataSource = (DataSource) namingContext.lookup("jdbc/vwonlinereg");

        final Properties properties = new Properties();
        properties.setProperty("event.shortname", event.getShortname());
        properties.setProperty("event.begin", String.valueOf(event.getDatebegin().getTime()));

        params.keySet().forEach(key -> properties.setProperty(key, params.getFirst(key)));

        Map<String, String> filterSettings = new HashMap<>();
        params.keySet().stream().filter(key -> key.startsWith("filter"))
          .forEach(key -> filterSettings.put(key, params.getFirst(key)));

        final InputStream configFileAsStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
        final Reader reader = new InputStreamReader(configFileAsStream, StandardCharsets.UTF_8);
        final Map<String, String> substitutions = new HashMap<>();
        substitutions.put(CONFIG_PLACEHOLDER, String.valueOf(eventId));

        addOptionalFieldsSubstitutions(eventId, substitutions);

        StreamingOutput stream = os -> {
            final Writer writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            final CsvExporter csvExporter = new CsvExporter(reader, new KeepOpenWriter(writer), dataSource, properties, selList);

            csvExporter.export(substitutions, filterSettings);

            writer.flush();
        };

        return Response.ok(stream).header(Constants.HDR_CONT_DISP,
          "attachment; filename=\"" + downloadFilename + "\"").build();
    }

    private void addOptionalFieldsSubstitutions(@PathParam("eventId") int eventId, Map<String, String> substitutions) {
        final OptionalFieldResource optionalFieldResource = resourceContext.getResource(OptionalFieldResource.class);
        final List<OptionalField> optionalFields = optionalFieldResource.getOptionalFields(eventId);

        for (int i = 0; i < optionalFields.size(); i++) {
            final OptionalField currentField = optionalFields.get(i);
            if (i < 10) {
                substitutions.put("__OPTIONAL_FIELD_LABEL_0" + i + "__", currentField.getLabel());
                substitutions.put("__optional_field_0" + i + "_id_placeholder__", currentField.getPk().toString());
            } else {
                substitutions.put("__OPTIONAL_FIELD_LABEL_" + i + "__", currentField.getLabel());
                substitutions.put("__optional_field_" + i + "_id_placeholder__", currentField.getPk().toString());
            }
        }
    }
}
