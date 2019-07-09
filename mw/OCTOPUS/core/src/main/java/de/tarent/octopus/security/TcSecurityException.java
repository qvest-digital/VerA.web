package de.tarent.octopus.security;
import de.tarent.octopus.resource.Resources;

import javax.xml.namespace.QName;

/**
 * Kapselt Fehlermeldungen, die auftreten, wenn ein User etwas ausführt, wofür er nicht autorisiert ist.
 * Wird auch benutzt, wenn ein User versucht sich mit falscher Benutzername/Paßword-Kombination einzuloggen.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke </a>, <b>tarent GmbH </b>
 */
public class TcSecurityException extends Exception {
    private static final long serialVersionUID = 2337052834275279833L;

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

    private int errorCode = 0;

    private String message;

    private String detailMessage;

    public TcSecurityException(int errorCode) {
        super();
        this.errorCode = errorCode;
        setMessage(getMessageByErrorCode(errorCode));
    }

    public TcSecurityException(String message) {
        setMessage(message);
    }

    private String getMessageByErrorCode(int errorCode) {
        switch (errorCode) {
        case ERROR_WRONG_GROUP_FOR_TASK:
            return Resources.getInstance().get("SECURITYEXCEPTION_WRONG_GROUP_FOR_TASK");
        case ERROR_INCOMPLETE_USER_DATA:
            return Resources.getInstance().get("SECURITYEXCEPTION_INCOMPLETE_USER_DATA");
        case ERROR_AUTH_ERROR:
            return Resources.getInstance().get("SECURITYEXCEPTION_AUTH_ERROR");
        case ERROR_NO_VALID_SESSION:
            return Resources.getInstance().get("SECURITYEXCEPTION_NO_VALID_SESSION");
        case ERROR_SERVER_AUTH_ERROR:
            return Resources.getInstance().get("SECURITYEXCEPTION_SERVER_AUTH_ERROR");
        case ERROR_SERVER_USERMANAGEMENT_ERROR:
            return Resources.getInstance().get("SECURITYEXCEPTION_USERMANAGEMENT_ERROR");
        default:
            return "Unknown Errorcode";
        }
    }

    public TcSecurityException(String string, Throwable e) {
        super(string, e);
    }

    public TcSecurityException(int errorCode, Throwable e) {
        this(errorCode);
        initCause(e);
    }

    public TcSecurityException(int errorCode, String detailMessage) {
        this(errorCode);
        this.detailMessage = detailMessage;
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

    public String getDetailMessage() {
        return detailMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
