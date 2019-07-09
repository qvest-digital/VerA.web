package de.tarent.octopus.client;
import javax.xml.namespace.QName;

/**
 * Konstanten, die ein Octopus Client benötigen kann.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface OctopusConstants {

    public static final String OCTOPUS_NAMESPACE = "http://schemas.tarent.de/octopus";

    public static final String SERVER_ERROR_PREFIX = "Server";
    public static final String AUTHENTICATION_ERROR_PREFIX = "Client.authentication";

    public static final String AUTHENTICATION_FAILED = "Client.authentication.authenticationFailed";
    public static final String AUTHENTICATION_UNKNOWN_ERROR = "Client.authentication.unknownError";
    public static final String AUTHENTICATION_NEED_LOGIN = "Client.authentication.needLogin";
    public static final String AUTHENTICATION_NOT_ENOUGH_RIGHTS = "Client.authentication.notEnoughRights";
    public static final String AUTHENTICATION_CANCELED = "Client.authentication.canceled";

    public static final QName SOAPF_AUTHENTICATION_UNKNOWN_ERROR = new QName(OCTOPUS_NAMESPACE, AUTHENTICATION_UNKNOWN_ERROR);
    public static final QName SOAPF_AUTHENTICATION_NEED_LOGIN = new QName(OCTOPUS_NAMESPACE, AUTHENTICATION_NEED_LOGIN);
    public static final QName SOAPF_AUTHENTICATION_NOT_ENOUGH_RIGHTS =
      new QName(OCTOPUS_NAMESPACE, AUTHENTICATION_NOT_ENOUGH_RIGHTS);
    public static final QName SOAPF_AXIS_HTTP_ERROR = new QName("http://xml.apache.org/axis/", "HTTP");
}
