package de.tarent.octopus.client;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
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

	public static final QName SOAPF_AUTHENTICATION_UNKNOWN_ERROR = new QName( OCTOPUS_NAMESPACE, AUTHENTICATION_UNKNOWN_ERROR );
	public static final QName SOAPF_AUTHENTICATION_NEED_LOGIN = new QName( OCTOPUS_NAMESPACE, AUTHENTICATION_NEED_LOGIN );
	public static final QName SOAPF_AUTHENTICATION_NOT_ENOUGH_RIGHTS = new QName( OCTOPUS_NAMESPACE, AUTHENTICATION_NOT_ENOUGH_RIGHTS );
    public static final QName SOAPF_AXIS_HTTP_ERROR = new QName("http://xml.apache.org/axis/", "HTTP");
}
