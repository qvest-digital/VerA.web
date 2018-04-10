package de.tarent.commons.config;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.w3c.dom.Node;

import de.tarent.commons.config.ConfigManager.Scope;

/**
 * Provides base functionality for configuration item
 * retrieval and value conversion.
 *
 * <p>This class is very similar to the {@link de.tarent.config.Appearance}
 * class. Please refer to it for detailed information.</p>
 *
 * <p><code>Environment</code> contains only parameter configuration
 * and you can access them via a fixed set of {@link Environment.Key}
 * instances.</p>
 *
 * <p>For a general introduction to the configuration system
 * refer to the respective document on the project's website.</p>
 */
public final class Environment extends Base
{
  private LinkedHashMap connectionDefinitions = new LinkedHashMap();

  private String defaultConnection;

  private boolean userOverridable = true;

  Environment()
  {
  }

  /**
   * Returns a map containing all the documents
   * which should be written in order to make the
   * environment's state persistant.
   *
   * <p>The map's keys are the document names while
   * the values are the XML documents itself.</p>
   *
   * <p>TODO: How to handle scopes?</p>
   *
   * <p>TODO: This is just a basic implementation that is not
   * supposed to handle every possible configuration state. For
   * now this is the modifiable connection definitions only.</p>
   *
   * @return
   */
  Map /*<String, Document>*/ prepareDocuments()
  {
	 HashMap docs = new HashMap();

	 // TODO: Quick and dirty implementation that creates a
	 // environment.xml with a single <connections file="connections.xml"/>
	 // entry and a the corresponding connections.xml which has a param-value
	 // entry for each (user-scope) connection definition.
	 docs.put("environment.xml", XmlUtil.createQnDEnvironmentDocument());
	 docs.put("connections.xml", XmlUtil.createQnDConnectionsDocument(getModifiableConnectionDefinitions()));

	 return docs;
  }

  void addConnectionDefinition(Scope scope, Node node) throws KeyUnavailableException
  {
    ConnectionDefinition p = new ConnectionDefinition(scope, node);
    String labelText = p.get(ConnectionDefinition.Key.LABEL);
    if(labelText == null) throw new IllegalStateException("Missing 'label' parameter within 'connection' node");

    if (defaultConnection == null)
      defaultConnection = labelText;

    connectionDefinitions.put(labelText, p);
  }

  /**
   * Returns a collection of all known {@link ConnectionDefinition} instances
   * which have been defined by the configuration documents.
   *
   * @return
   */
  public Collection getConnectionDefinitions()
  {
    return connectionDefinitions.values();
  }

  /**
   * Returns a collection of {@link ConnectionDefinition} instances
   * which cannot be modified by program's user.
   *
   * <p>Note: The current implementation marks every {@link ConnectionDefinition}
   * as fixed which was defined at site or installation scope.</p>
   *
   * @return
   */
  public Collection getFixedConnectionDefinitions()
  {
	  LinkedList ll = new LinkedList();
	  Iterator ite = connectionDefinitions.values().iterator();
	  while (ite.hasNext())
	  {
		ConnectionDefinition cd = (ConnectionDefinition) ite.next();
		if (cd.scope != Scope.USER)
		  ll.add(cd);
	  }

	  return ll;
  }

  /**
   * Returns a collection of {@link ConnectionDefinition} instances
   * which can be modified by program's user.
   *
   * <p>Note: The current implementation marks every {@link ConnectionDefinition}
   * as fixed which was defined at user scope.</p>
   *
   * @return
   */
  public Collection getModifiableConnectionDefinitions()
  {
	  LinkedList ll = new LinkedList();
	  Iterator ite = connectionDefinitions.values().iterator();
	  while (ite.hasNext())
	  {
		ConnectionDefinition cd = (ConnectionDefinition) ite.next();
		if (cd.scope == Scope.USER)
		  ll.add(cd);
	  }

	  return ll;
  }

  public void setModifiableConnectionDefinitions(Collection c)
  {
	  // Remove former modifiable connection definitions.
	  Iterator ite = getModifiableConnectionDefinitions().iterator();
	  while (ite.hasNext())
	  {
		ConnectionDefinition cd = (ConnectionDefinition) ite.next();

		connectionDefinitions.remove(cd.get(ConnectionDefinition.Key.LABEL));
	  }

	  ite = c.iterator();
	  while (ite.hasNext())
	  {
		ConnectionDefinition cd = (ConnectionDefinition) ite.next();
		cd.scope = Scope.USER;

		connectionDefinitions.put(cd.get(ConnectionDefinition.Key.LABEL), cd);
	  }
  }

  public ConnectionDefinition getDefaultConnectionDefinition()
  {
    return (ConnectionDefinition) connectionDefinitions.get(defaultConnection);
  }

  /**
   * Refer to {@link Appearance#put(String, Node)} for
   * documentation as the implementation is the same.
   *
   * @param name
   * @param node
   */
  void put(String name, Node node) throws KeyUnavailableException
  {
    putParam(Key.getInstance(name), node);
  }

  /**
   * Refer to {@link Appearance#get(Appearance.Key)} for
   * documentation as the implementation is the same.
   *
   * @param key
   * @param node
   */
  public String get(Key key)
  {
    return getParamValue(key, null);
  }

  /**
   * Refer to {@link Appearance#get(Appearance.Key, String)} for
   * documentation as the implementation is the same.
   *
   * @param key
   * @param node
   */
  public String get(Key key, String defaultValue)
  {
    return getParamValue(key, defaultValue);
  }

  /**
   * Refer to {@link Appearance#getAsBoolean(Appearance.Key)} for
   * documentation as the implementation is the same.
   *
   * @param key
   * @param node
   */
  public boolean getAsBoolean(Key key)
  {
    return getParamAsBoolean(key, false);
  }

  /**
   * Refer to {@link Appearance#getAsBoolean(Appearance.Key, boolean)} for
   * documentation as the implementation is the same.
   *
   * @param key
   * @param node
   */
  public boolean getAsBoolean(Key key, boolean defaultValue)
  {
    return getParamAsBoolean(key, defaultValue);
  }

  /**
   * Refer to {@link Appearance#getAsInt(Appearance.Key)} for
   * documentation as the implementation is the same.
   *
   * @param key
   * @param node
   */
  public int getAsInt(Key key)
  {
    return getParamAsInt(key, 0);
  }

  /**
   * Refer to {@link Appearance#getAsInt(Appearance.Key, int)} for
   * documentation as the implementation is the same.
   *
   * @param key
   * @param node
   */
  public int getAsInt(Key key, int defaultValue)
  {
    return getParamAsInt(key, defaultValue);
  }

  /**
   * Refer to {@link Appearance#getAsInt(Appearance.Key, int)} for
   * documentation as the implementation is the same.
   *
   * @param key
   * @param node
   */

  public long getAsLong(Key key, long defaultValue)
  {
	  return getParamAsLong(key, defaultValue);
  }

  /**
   * Returns if the environment configuration settings may
   * be overridden by the user.
   *
   * <p>By default this method returns <code>true</code>. This
   * can be changed by the installation and site scope environment
   * configuration document.</p>
   *
   * @return true if users are allowed to overwrite environment settings.
   */

  public boolean isUserOverridable()
  {
	return userOverridable;
  }

  void setUserOverridable(String value)
  {
    userOverridable = getAsBoolean(value, true);
  }

  /**
   * This class defines the configuration keys for the
   * environment configuration and provides the infrastructure
   * of a type-safe enumeration.
   *
   * <p>For general information about the configuration system
   * refer to the documentation on the project's website.</p>
   *
   * @author Robert Schuster
   *
   */
  public static final class Key extends Base.Key
  {

    /**
     * Refer to the {@link Appearance.Key#instances} documentation
     * for detailed documentation as the information applies here,
     * too.
     */
    private static HashMap instances = new HashMap();

    // Define constants for the environment parameters after this line.
    public final static Key DEBUG = make("debug");

    public final static Key SMTP_SERVER = make("SMTPServer");

    public final static Key USE_OCTOPUS_SESSION_COOKIE = make("useOctopusSessionCookie");
    public final static Key OCTOPUS_SESSION_FILENAME = make("octopusSessionFilename");
    public final static Key OCTOPUS_KEEP_SESSION_ALIVE = make("keepSessionAlive");
    public final static Key EMAIL_COMMAND = make("eMailCommand");
    public final static Key PHONE_COMMAND = make("PhoneCommand");
    public final static Key BROWSER_COMMAND = make("BrowserCommand");

    public final static Key RETRY_AFTER_LOGIN_ERROR = make("retryAfterLoginError");
    public final static Key SLEEP_SECONDS = make("sleepSeconds");

    public final static Key LOG_FILE_NAME = make("logfile.name");
    public final static Key LOG_FILE_LEVEL = make("logfile.level");
    public final static Key LOG_FILE_LOGGER = make("logfile.logger");

    public final static Key OFFICE_START_COMMAND = make("officeStartCommand");
    public final static Key UNO_CONNECTION_PARAMETER = make("unoConnectionParameter");
    public final static Key ENABLE_AUTO_JOURNALING = make("enableAutoJournaling");
    public final static Key POST_TO_OPEN_OFFICE = make("postToOpenOffice");
    public final static Key POST_COMMUNICATION_COMMAND = make("postCommunicationCommand");
    public final static Key POST_OPTIMIZED_COMMAND = make("postOptimizedCommand");
    public final static Key POST_COMMUNICATION_FILE_NAME = make("postCommunicationFileName");

    public final static Key SINGLE_FAX_AS_EMAIL = make("singleFaxAsEMail");

    public final static Key POST_ENABLEFAX = make("enableFAX");
    public final static Key POST_ENABLEEMAIL = make("enableEMail");
    public final static Key POST_ENABLEPOST = make("enablePost");

    public final static Key ASSO_ZAM_SERVER_HOSTNAME = make("assoZamServerHostname");
    public final static Key ASSO_ZAM_SERVER_PORT = make("assoZamServerPort");

    public final static Key ENABLE_CATEGORY_CACHING = make("enableCategoryCaching");

    public final static Key FOLLOW_UP_DAYS_TO_ADD = make("followupDaysToAdd");

    public final static Key TODO_RED = make("Todo_Red");
    public final static Key TODO_YELLOW = make("Todo_Yellow");
    public final static Key TODO_GREEN = make("Todo_Green");

    public final static Key TEMPLATE_DIR = make("TemplateDir");

    public final static Key OFFICE_TYPE = make("officeType");
    public final static Key OFFICE_PATH = make("officePath");
    public final static Key OFFICE_CONNECTION_TYPE = make("officeConnectionType");
    public final static Key OFFICE_PORT = make("officePort");
    public final static Key OFFICE_PIPE_NAME = make("officePipeName");

    public final static Key EMAIL_ATTACHMENT_SIZE_LIMIT = make("attachmentSizeLimit");

    public final static Key EMAIL_DEMAND_MINIMUM = make("emailDemandMinimum");

    public final static Key EMAIL_SENDERS = make("emailSenders");

    // Define constants for the environment parameters before this line.

    private Key(String label)
    {
      super(label);
      instances.put(label, this);
    }

    /** Creates a new instance.
     *
     * <p>Use this instead of the constructor in light of
     * future additions.</p>
     *
     * @param label
     * @return
     */
    private static Key make(String label)
    {
      return new Key(label);
    }

    /** Returns an instance of this class or throws
     * a {@KeyUnavailableException} if it does not exist.
     *
     * @param label
     * @return
     * @throws KeyUnavailableException if the key does not exist.
     */
    private static Key getInstance(String label)
      throws KeyUnavailableException
    {
      Key k = (Key) instances.get(label);

      if (k == null)
	throw new KeyUnavailableException(label);

      return k;
    }

  }

}
