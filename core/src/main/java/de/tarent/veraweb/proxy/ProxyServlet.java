package de.tarent.veraweb.proxy;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
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
import org.apache.http.protocol.HttpContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;

public class ProxyServlet extends org.mitre.dsmiley.httpproxy.ProxyServlet {
    private static final long serialVersionUID = 6744389006703790750L;

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

        implicitProperties.setProperty(P_TARGET_URI, verawebProperties.getProperty(VWOR_ENDPOINT)
                + getServletConfig().getInitParameter(P_TARGET_PATH));
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
    protected HttpClient createHttpClient(final HttpParams hcParams) {
        final HttpRequestInterceptor itcp = new HttpRequestInterceptor() {

            public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
                if (request instanceof HttpEntityEnclosingRequest) {
                    final HttpEntityEnclosingRequest eeRequest = (HttpEntityEnclosingRequest) request;
                    eeRequest.setEntity(new BufferedHttpEntity(eeRequest.getEntity()));
                }
            }
        };
        final HttpClientBuilder builder = HttpClientBuilder.create().disableAutomaticRetries().addInterceptorLast(itcp).disableRedirectHandling();

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

    protected boolean hasGroup(final HttpSession session, final String requiredGroup) {
        final PersonalConfig pc = (PersonalConfig) session.getAttribute(PERSONAL_CONFIG_VERAWEB);
        if (pc != null) {
            final String[] requiredGroups = requiredGroup.split(",");
            final String[] userGroups = pc.getUserGroups();
            for (final String group : userGroups) {
                for(final String reqGroup : requiredGroups){
                    if (group.equalsIgnoreCase(reqGroup)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
