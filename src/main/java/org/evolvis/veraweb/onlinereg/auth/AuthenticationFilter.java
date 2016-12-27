package org.evolvis.veraweb.onlinereg.auth;

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