package de.tarent.veraweb.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import de.tarent.octopus.server.PersonalConfig;

public class ProxyServlet extends org.mitre.dsmiley.httpproxy.ProxyServlet {
    private static final long serialVersionUID = 1L;
    
    private static final String PERSONAL_CONFIG_VERAWEB = "personalConfig-veraweb";
    private static final String VWOR_AUTH_PASSWORD = "vwor.auth.password";
    private static final String VWOR_AUTH_USER = "vwor.auth.user";
    private static final String VWOR_ENDPOINT = "vwor.endpoint";
    private static final String P_CONFIG_FILE = "configFile";
    private static final String P_REQUIRED_GROUP = "requiredGroup";
    private static final String P_USER = "user";
    private static final String P_PASSWORD = "password";
    private static final String P_TARGET_PATH = "targetPath";

    private final Properties verawebProperties = new Properties();
    private final Properties  implicitProperties = new Properties();

    protected boolean hasGroup(final HttpSession session, final String requiredGroup) {
        final PersonalConfig pc = (PersonalConfig) session.getAttribute(PERSONAL_CONFIG_VERAWEB);

        if (pc != null) {
            final String[] userGroups = pc.getUserGroups();

            for (String group : userGroups) {
                if (group.equals(requiredGroup)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void init() throws ServletException {

        String configFilePath = getServletConfig().getInitParameter(P_CONFIG_FILE);
        if (configFilePath == null) {
            configFilePath = "/etc/veraweb/veraweb.properties";
        }
        final File configFile = new File(configFilePath);
        if (configFile.canRead()) {
            try {
                readVerawebProperties(configFile);
            } catch (IOException e) {
                throw new ServletException("could not read config file", e);
            }
            implicitProperties.setProperty(P_TARGET_URI, verawebProperties.getProperty(VWOR_ENDPOINT)+getServletConfig().getInitParameter(P_TARGET_PATH));
            implicitProperties.setProperty(P_USER, verawebProperties.getProperty(VWOR_AUTH_USER));
            implicitProperties.setProperty(P_PASSWORD, verawebProperties.getProperty(VWOR_AUTH_PASSWORD));
            implicitProperties.setProperty(P_REQUIRED_GROUP, PersonalConfig.GROUP_ADMINISTRATOR);
        }
        super.init();
    }

    private void readVerawebProperties(final File configFile) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        final FileInputStream fis = new FileInputStream(configFile);
        final Reader reader = new InputStreamReader(fis, "utf-8");
        verawebProperties.load(reader);
        reader.close();
    }

    @Override
    protected String getConfigParam(String key0) {
        final String key = "proxy." + getServletName() + "." + key0;
        if (verawebProperties.containsKey(key)) {
            return verawebProperties.getProperty(key);
        }
        
        final String configParam = super.getConfigParam(key0);
        if(configParam!=null){
            return configParam;
        }
        if (implicitProperties.containsKey(key0)){
            return implicitProperties.getProperty(key0);
        }
        return null;
    }

    @Override
    protected HttpClient createHttpClient(HttpParams hcParams) {
         HttpRequestInterceptor itcp = new HttpRequestInterceptor() {
            
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                if(request instanceof HttpEntityEnclosingRequest){
                    
                    HttpEntityEnclosingRequest eeRequest = (HttpEntityEnclosingRequest) request;
                    eeRequest.setEntity(new BufferedHttpEntity(eeRequest.getEntity()));
                }
                
            }
        };
        final HttpClientBuilder builder = HttpClientBuilder
                 .create()
                 .disableAutomaticRetries()
                 .addInterceptorLast(itcp)
                 .disableRedirectHandling();

        String username = getConfigParam(P_USER);
        String password = getConfigParam(P_PASSWORD);
        if (username != null) {
            Credentials credentials = new UsernamePasswordCredentials(username, password);
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, credentials);
            builder.setDefaultCredentialsProvider(credentialsProvider);
        }
        return builder.build();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (hasGroup(req.getSession(), getConfigParam(P_REQUIRED_GROUP))) {
            super.service(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
