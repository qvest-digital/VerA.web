package de.tarent.veraweb.proxy;

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
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
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
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.aa.veraweb.utils.PropertiesReader;
import de.tarent.octopus.server.PersonalConfig;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpParams;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;

public class ProxyServlet extends org.mitre.dsmiley.httpproxy.ProxyServlet {
    private static final long serialVersionUID = -7334393942401621163L;

    private static final String PERSONAL_CONFIG_VERAWEB = "personalConfig-veraweb";
    private static final String VWOR_AUTH_PASSWORD = "vwor.auth.password";
    private static final String VWOR_AUTH_USER = "vwor.auth.user";
    private static final String VWOR_ENDPOINT = "vwor.endpoint";
    private static final String P_REQUIRED_GROUP = "requiredGroup";
    private static final String P_USER = "user";
    private static final String P_PASSWORD = "password";
    private static final String P_TARGET_PATH = "targetPath";

    private Properties verawebProperties;
    private final Properties implicitProperties = new Properties();

    @Override
    public void init() throws ServletException {
        final PropertiesReader reader = new PropertiesReader();
        verawebProperties = reader.getProperties();

        implicitProperties.setProperty(P_TARGET_URI,
          verawebProperties.getProperty(VWOR_ENDPOINT) + getServletConfig().getInitParameter(P_TARGET_PATH));
        implicitProperties.setProperty(P_USER, verawebProperties.getProperty(VWOR_AUTH_USER));
        implicitProperties.setProperty(P_PASSWORD, verawebProperties.getProperty(VWOR_AUTH_PASSWORD));
        implicitProperties.setProperty(P_REQUIRED_GROUP, PersonalConfig.GROUP_ADMINISTRATOR);
        super.init();

        // Authorization headers must not be copied from the original request!
        hopByHopHeaders.addHeader(new BasicHeader("Authorization", null));
    }

    @Override
    protected String getConfigParam(final String key0) {
        final String key = "proxy." + getServletName() + "." + key0;
        if (verawebProperties.containsKey(key)) {
            return verawebProperties.getProperty(key);
        }

        final String configParam = super.getConfigParam(key0);
        if (configParam != null) {
            return configParam;
        }
        if (implicitProperties.containsKey(key0)) {
            return implicitProperties.getProperty(key0);
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected HttpClient createHttpClient(final HttpParams hcParams) {
        final HttpRequestInterceptor itcp = (request, context) -> {
            if (request instanceof HttpEntityEnclosingRequest) {
                final HttpEntityEnclosingRequest eeRequest = (HttpEntityEnclosingRequest) request;
                eeRequest.setEntity(new BufferedHttpEntity(eeRequest.getEntity()));
            }
        };
        final HttpClientBuilder builder =
          HttpClientBuilder.create().disableAutomaticRetries().addInterceptorLast(itcp).disableRedirectHandling();

        final String username = getConfigParam(P_USER);
        final String password = getConfigParam(P_PASSWORD);
        if (username != null) {
            final Credentials credentials = new UsernamePasswordCredentials(username, password);
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, credentials);
            builder.setDefaultCredentialsProvider(credentialsProvider);
        }
        return builder.build();
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        if (hasGroup(req.getSession(), getConfigParam(P_REQUIRED_GROUP))) {
            super.service(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean hasGroup(final HttpSession session, final String requiredGroup) {
        if ("ANY".equals(requiredGroup)) {
            return true;
        }

        final PersonalConfig pc = (PersonalConfig) session.getAttribute(PERSONAL_CONFIG_VERAWEB);
        if (pc != null) {
            final String[] requiredGroups = requiredGroup.split(",");
            final String[] userGroups = pc.getUserGroups();
            for (final String group : userGroups) {
                for (final String reqGroup : requiredGroups) {
                    if (group.equalsIgnoreCase(reqGroup)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
