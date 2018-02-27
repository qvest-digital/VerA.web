package org.evolvis.veraweb.onlinereg.auth;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
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
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
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
import com.sun.jersey.api.client.Client;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.osiam.OsiamClient;
import org.evolvis.veraweb.onlinereg.user.LoginResource;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.user.BasicUser;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    final private Config config;
    final private Client client;
    final private OsiamClient osiamClient;

    public AuthenticationFilter(Config config, Client client) {
        this.config = config;
        this.client = client;
        osiamClient = config.getOsiam().getClient(client);
    }

    private boolean needsAuthentication(final String path) {
        if (!path.startsWith("/api")) {
            System.out.println("ignore " + path);
            return false;
        }
        if (path.startsWith("/api/idm/login")) {
            return false;
        }
        if (path.startsWith("/api/user/register")) {
            return false;
        }
        if (path.startsWith("/api/user/activate")) {
            return false;
        }
        if (path.startsWith("/api/reset/password")) {
            return false;
        }
        if (path.startsWith("/api/user/request/resend-login")) {
            return false;
        }
        if (path.startsWith("/api/user/request/reset-password-link")) {
            return false;
        }
        if (path.startsWith("/api/media")){
            return false;
        }
        if (path.startsWith("/api/event/list")){
            return false;
        }
        if (path.startsWith("/api/freevisitors")){
            return false;
        }
        if (path.startsWith("/api/imprint/")){
            return false;
        }
        return true;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest webRequest = (HttpServletRequest) request;
        final String path = webRequest.getServletPath() + webRequest.getPathInfo();
        final HmacToken token = readHmacToken(webRequest);

        if ((token == null || tokenExpired(token))) {
            if (needsAuthentication(path)) {
                goAway(response);
            } else {
                proceed(request, response, chain);
            }
            return;
        }
        BasicUser user;
        try {
            user = osiamClient.getUserBasic(token.getOsiamAccessToken());
        } catch (UnauthorizedException | ForbiddenException | ConnectionInitializationException | IllegalStateException e) {
            // right now we frankly don't care *why* the authorization failed...
            user = null;
        }
        if (user == null) {
            if (needsAuthentication(path)) {
                goAway(response);
            } else {
                proceed(request, response, chain);
            }
            return;
        }
        // Authorization should happen in the individual resources.
        // Here we only provide the "principal".
        // (TODO: Probably we should use a SecurityContext for this?)
        request.setAttribute(LoginResource.USERNAME, user.getUserName());
        request.setAttribute(LoginResource.ACCESS_TOKEN, token.getOsiamAccessToken());
        chain.doFilter(request, response);

    }

    private void proceed(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (WebApplicationException e) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(e.getResponse().getStatus());

        }
    }

    private void goAway(ServletResponse response) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.getWriter().print("Go away.");
    }

    private boolean tokenExpired(HmacToken token) {
        // TODO check the timestamp, ask osiam, maybe both...
        return false;
    }

    private HmacToken readHmacToken(HttpServletRequest webRequest) {
        final Cookie[] cookies = webRequest.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(LoginResource.VWOA_ACCESS_TOKEN)) {
                try {
                    return new HmacToken(cookie.getValue());
                } catch (InvalidTokenException e) {
                    // ignore invalid tokens.
                }
            }
        }
        return null;
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}
