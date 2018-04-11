package de.tarent.veraweb.proxy;

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
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
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
