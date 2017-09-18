package de.tarent.octopus.security;

/*
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.io.File;
import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.server.PersonalConfig;

/** 
 * Implementierung eines LoginManagers, über eine XML Datei
 * <br><br>
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class LoginManagerXML extends AbstractLoginManager {

    private static Log logger = LogFactory.getLog(LoginManagerXML.class);

    public static final String KEY_USER_FILE = "userFile";
    public static final String DEFAULT_USER_FILE_NAME = "user.xml";
    
    protected void doLogin(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest) 
        throws TcSecurityException {
        
        File userFile = null;
        File base = commonConfig.getModuleConfig(tcRequest.getModule()).getRealPath();
        if (getConfigurationString(KEY_USER_FILE) != null) {
            userFile = new File(base,getConfigurationString(KEY_USER_FILE));
        } else {
            userFile = new File(base,DEFAULT_USER_FILE_NAME);
        }        
               
        String fileUrl = Resources.getInstance().get("LOGINMANAGERXML_URL_USER_FILE", userFile.getAbsolutePath());
        MyContentHandler ch = new MyContentHandler();
        try {
            SAXParserFactory.newInstance().newSAXParser().parse(fileUrl, ch);
        } catch (SAXParseException e) {
            logger.warn(Resources.getInstance().get(
                                                   "LOGINMANAGERXML_LOG_USER_PARSE_SAX_EXCEPTION",
                                                   new Integer(e.getLineNumber()),
                                                   new Integer(e.getColumnNumber())),
                       e);
            throw new TcSecurityException(Resources.getInstance().get("LOGINMANAGERXML_EXC_USER_PARSE_ERROR", userFile));
        } catch (Exception e) {
            logger.warn(Resources.getInstance().get("LOGINMANAGERXML_LOG_USER_PARSE_ERROR"), e);
            throw new TcSecurityException(Resources.getInstance().get("LOGINMANAGERXML_EXC_USER_PARSE_ERROR", userFile));
        }

        PasswordAuthentication pwdAuth = tcRequest.getPasswordAuthentication();
        if (pwdAuth == null
            || ! ch.getUsermap().containsKey(pwdAuth.getUserName())
            || ! ch.getUsermap().get(pwdAuth.getUserName()).toString().equals(new String(pwdAuth.getPassword()))) {
            throw new TcSecurityException(TcSecurityException.ERROR_AUTH_ERROR);
        }

        pConfig.setUserGroups((String[])ch.getGroupmap().get(pwdAuth.getUserName()));
        pConfig.userLoggedIn(pwdAuth.getUserName());
    }
    
    protected void doLogout(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest)
        throws TcSecurityException {
        pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_LOGGED_OUT});
        pConfig.userLoggedOut();
    }


    private class MyContentHandler extends DefaultHandler {
        private Map usermap = new HashMap();
        private Map groupmap = new HashMap();
        

        public void setDocumentLocator(Locator arg0) {
        }
        public void startDocument() throws SAXException {
        }
        public void endDocument() throws SAXException {
        }
        public void startPrefixMapping(String arg0, String arg1) throws SAXException {
        }
        public void endPrefixMapping(String arg0) throws SAXException {
        }
        public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException {
            if ("user".equals(arg2)) {
                usermap.put(arg3.getValue("name"), arg3.getValue("password"));
                if (arg3.getValue("groups") != null)
                    groupmap.put(arg3.getValue("name"), arg3.getValue("groups").split("[:]"));
            }
        }
        public void endElement(String arg0, String arg1, String arg2) throws SAXException {
        }
        public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
        }
        public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
        }
        public void processingInstruction(String arg0, String arg1) throws SAXException {
        }
        public void skippedEntity(String arg0) throws SAXException {
        }
        public Map getUsermap() {
            return usermap;
        }

        public Map getGroupmap() {
            return groupmap;
        }
    }
}