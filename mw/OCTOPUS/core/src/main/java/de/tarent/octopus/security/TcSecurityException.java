package de.tarent.octopus.security;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
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
import de.tarent.octopus.resource.Resources;

import javax.xml.namespace.QName;

/**
 * Kapselt Fehlermeldungen, die auftreten, wenn ein User etwas ausführt, wofür er nicht autorisiert ist.
 * Wird auch benutzt, wenn ein User versucht sich mit falscher Benutzername/Paßword-Kombination einzuloggen.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke </a>, <b>tarent GmbH </b>
 */
public class TcSecurityException extends Exception {
    private static final long serialVersionUID = -5494704136570910471L;

    public static final int ERROR_NO_VALID_SESSION = 1;
    public static final int ERROR_AUTH_ERROR = 2;
    public static final int ERROR_WRONG_GROUP_FOR_TASK = 3;
    public static final int ERROR_INCOMPLETE_USER_DATA = 4;
    public static final int ERROR_SERVER_AUTH_ERROR = 5;
    public static final int ERROR_SERVER_USERMANAGEMENT_ERROR = 6;

    private static final String uri = "http://schemas.tarent.de/octopus";

    private static final QName[] soapFaultCodes = {
            new QName(uri, "Client.authentication.unknownError"),
            new QName(uri, "Client.authentication.needLogin"),
            new QName(uri, "Client.authentication.authenticationFailed"),
            new QName(uri, "Client.authentication.notEnoughRights"),
            new QName(uri, "Client.authentication.incompleteUserData"),
            new QName(uri, "Server.authentication.serviceFailed")
    };

    int errorCode = 0;

    String message;

    String detailMessage;

    public TcSecurityException(int errorCode) {
        super();
        this.errorCode = errorCode;
        setMessage(getMessageByErrorCode(errorCode));
    }

    public TcSecurityException(String message) {
        setMessage(message);
    }

    protected String getMessageByErrorCode(int errorCode) {
        if (errorCode == ERROR_WRONG_GROUP_FOR_TASK) {
            return Resources.getInstance().get("SECURITYEXCEPTION_WRONG_GROUP_FOR_TASK");
        }
        if (errorCode == ERROR_INCOMPLETE_USER_DATA) {
            return Resources.getInstance().get("SECURITYEXCEPTION_INCOMPLETE_USER_DATA");
        }
        if (errorCode == ERROR_AUTH_ERROR) {
            return Resources.getInstance().get("SECURITYEXCEPTION_AUTH_ERROR");
        }
        if (errorCode == ERROR_NO_VALID_SESSION) {
            return Resources.getInstance().get("SECURITYEXCEPTION_NO_VALID_SESSION");
        }
        if (errorCode == ERROR_SERVER_AUTH_ERROR) {
            return Resources.getInstance().get("SECURITYEXCEPTION_SERVER_AUTH_ERROR");
        }
        if (errorCode == ERROR_SERVER_USERMANAGEMENT_ERROR) {
            return Resources.getInstance().get("SECURITYEXCEPTION_USERMANAGEMENT_ERROR");
        }
        return "Unknown Errorcode";
    }

    /**
     * @param string
     * @param e
     */
    public TcSecurityException(String string, Throwable e) {
        super(string, e);
    }

    public TcSecurityException(int errorCode, Throwable e) {
        this(errorCode);
        initCause(e);
    }

    public TcSecurityException(int errorCode, String detailMessage) {
        this(errorCode);
        setDetailMessage(detailMessage);
    }

    public QName getSoapFaultCode() {
        if (errorCode < 0 || errorCode >= soapFaultCodes.length) {
            return soapFaultCodes[0];
        }
        return soapFaultCodes[errorCode];
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String newMessage) {
        this.message = newMessage;
    }

    /**
     * @return Returns the detailMessage.
     */
    public String getDetailMessage() {
        return detailMessage;
    }

    /**
     * @param detailMessage The detailMessage to set.
     */
    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }
}
