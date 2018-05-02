package de.tarent.commons.config;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Provides accessor methods for the parameters of the appearance
 * configuration which contains the plugin and action configuration.
 *
 * <p>For a detailed explanation of the configuration system and the
 * meaning of the appearance configuration consult the project page.</p>
 *
 * <p>To retrieve a configuration item (usually a <code>String</code>)
 * use the appropriate getter method with a {@link Appearance.Key}
 * constant. It is deliberate that the <code>Appearance.Key</code>
 * instances are not compatible with those of the {@link Environment.Key}
 * class.</p>
 *
 * <p>While parameter items need constants to be declared in
 * {@link Appearance.Key} this is not needed for actions and
 * plugins.</p>
 *
 * @author Robert Schuster
 */
public final class Appearance extends Base {
    private LinkedHashMap actions = new LinkedHashMap();
    private LinkedHashMap plugins = new LinkedHashMap();

    /**
     * Package-level constructor allows no one else
     * than the configuration system to create instances.
     */
    Appearance() {
    }

    /**
     * Returns the XML nodes specifying the action
     * definitions (it's parameters)
     */
    public Iterator getActionDefinitions() {
        return actions.values().iterator();
    }

    /**
     * Appends a new action definition.
     *
     * <p>This is to be used solely by the configuration
     * system itself.</p>
     *
     * @param node
     */
    void addActionDefinition(String key, Node node)
      throws DataFormatException {
        actions.put(key, XmlUtil.getParamMap(node));
    }

    /**
     * Returns the XML nodes specifying the plugin
     * definitions (it's parameters)
     */
    public Iterator getPluginDefinitions() {
        return plugins.values().iterator();
    }

    /**
     * Appends a new plugin definition.
     *
     * <p>This is to be used solely by the configuration
     * system itself.</p>
     *
     * @param name
     * @param node
     */
    void addPluginDefinition(String key, Node node) {
        plugins.put(key, node);
    }

    /**
     * Inserts a new configuration item.
     *
     * <p>This is to be used solely by the configuration
     * system itself.</p>
     *
     * <p>A {@link KeyUnavailableException} is thrown if a
     * key with the given label does not exist. This means
     * that a configuration document uses a name or id ...
     * <ul>
     * <li>... which has been deleted in the application or</li>
     * <li>... which has not been added to the application or</li>
     * <li>... which simply contains a syntax error.</li>
     * </ul>
     * </p>
     *
     * @param name
     * @param node
     */
    void put(String name, Node node) throws KeyUnavailableException {
        putParam(Key.getInstance(name), node);
    }

    /**
     * Returns the value for the given <code>Key</code> of
     * the appearance configuration or <code>null</code>
     * if the value is not set.
     *
     * @param key
     */
    public String get(Key key) {
        return getParamValue(key, null);
    }

    /**
     * Returns the value for the given <code>Key</code> of
     * the appearance configuration or <code>defaultValue</code>
     * if the value is not set.
     *
     * @param key
     */
    public String get(Key key, String defaultValue) {
        return getParamValue(key, defaultValue);
    }

    /**
     * Returns the value for the given <code>Key</code> of
     * the appearance configuration parsed as a <code>boolean</code>
     * or <code>false</code> if the value is not set.
     *
     * @param key
     */
    public boolean getAsBoolean(Key key) {
        return getParamAsBoolean(key, false);
    }

    /**
     * Returns the value for the given <code>Key</code> of
     * the appearance configuration parsed as a <code>boolean</code>
     * or <code>defaultValue</code> if the value is not set.
     *
     * @param key
     */
    public boolean getAsBoolean(Key key, boolean defaultValue) {
        return getParamAsBoolean(key, defaultValue);
    }

    /**
     * Returns the value for the given <code>Key</code> of
     * the appearance configuration parsed as an <code>int</code>
     * or <code>0</code> if the value is not set.
     *
     * @param key
     */
    public int getAsInt(Key key) {
        return getParamAsInt(key, 0);
    }

    /**
     * Returns the value for the given <code>Key</code> of
     * the appearance configuration parsed as an <code>int</code>
     * or <code>defaultValue</code> if the value is not set.
     *
     * @param key
     */
    public int getAsInt(Key key, int defaultValue) {
        return getParamAsInt(key, defaultValue);
    }

    /**
     * Returns the XML element for the given <code>Key</code> of
     * the appearance configuration or <code>null</code> if the
     * value is not set.
     *
     * @param key
     */
    public Element getAsElement(Key key) {
        return (Element) getParamAsObject(key);
    }

    /**
     * This class defines the configuration keys for the
     * appearance configuration and provides the infrastructure
     * of a type-safe enumeration.
     *
     * <p>For general information about the configuration system
     * refer to the documentation on the project's website.</p>
     *
     * @author Robert Schuster
     */
    public static final class Key extends Base.Key {
        /**
         * A map holding all the instances created. It is used
         * to resolve the constant instances by their string label.
         *
         * <p>The set of appearance configuration keys is considered
         * fixed. If an unknown key is requested through the
         * {@link #getInstance(String)} method an exception will be thrown.
         *
         * <p>Note: Putting the declaration before the constants is important
         * and would otherwise provoke a <code>NullPointerException</code>
         * (Java's static initialization is done in the order of the
         * declarations).</p>
         *
         * <p>For a general introduction to the configuration system
         * refer to the respective document on the project's website.</p>
         */
        private static HashMap instances = new HashMap();

        // Define constants for the appearance parameters after this line.
        public final static Key EXIT_MODE = make("exitMode");
        public final static Key START_VERTEILERGRUPPE = make("startVerteilergruppe");

        public final static Key SHOW_MENU_SCROLLBAR_ALWAYS = make("ShowMenuScrollBarAlways");
        public final static Key SHOW_COMMONADRESSES = make("ShowCommonAdresses");
        public final static Key SHOW_DRAGBARASSINGLECOLUMN = make("ShowDragBarAsSingleColumn");

        public final static Key SHOW_DRAGBARINITIAL = make("ShowDragBarInitial");
        public final static Key SHOW_DEFAULT_ADDRESS_TABLE_SIZE = make("DefaultAddressTableSize");

        public final static Key ADMIN_CREATECATEGORY = make("ShowAdminCreateCategory");
        public final static Key ADMIN_DELETECATEGORY = make("ShowAdminDeleteCategory");
        public final static Key ADMIN_EDITCATEGORY = make("ShowAdminEditCategory");
        public final static Key ADMIN_CREATESUBCATEGORY = make("ShowAdminCreateSubCategory");
        public final static Key ADMIN_DELETESUBCATEGORY = make("ShowAdminDeleteSubCategory");
        public final static Key ADMIN_SHOWSUBCATEGORY = make("ShowAdminShowSubCategory");
        public final static Key ADMIN_EDITSUBCATEGORY = make("ShowAdminEditSubCategory");
        public final static Key ADMIN_CREATEUSER = make("ShowAdminCreateUser");
        public final static Key ADMIN_DELETEUSER = make("ShowAdminDeleteUser");
        public final static Key ADMIN_EDITUSER = make("ShowAdminEditUser");

        public final static Key ADMIN_CREATEGROUP = make("ShowAdminCreateGroup");
        public final static Key ADMIN_EDITGROUP = make("ShowAdminEditGroup");
        public final static Key ADMIN_DELETEGROUP = make("ShowAdminDeleteGroup");

        public final static Key ADMIN_GLOBALROLE = make("ShowAdminGlobalRole");
        public final static Key ADMIN_FOLDERROLE = make("ShowAdminFolderRole");

        public final static Key ADMIN_RIGHTSMANAGEMENT = make("ShowAdminRightsManagement");

        public final static Key ADMIN_ASSOCIATEUSER = make("ShowAdminAssociateUser");
        public final static Key ADMIN_SHOWCATEGORY = make("ShowAdminShowCategory");
        public final static Key ADMIN_HIDECATEGORY = make("ShowAdminHideCategory");
        public final static Key ADMIN_SETSTANDARDCATEGORY = make("ShowAdminSetStandardCategory");

        public final static Key ADMIN_CALENDARPROXY = make("ShowAdminCalendarProxy");
        public final static Key PREVIEW_COLUMNS = make("PreviewColumns");

        public final static Key PANELCONFIG = make("PanelConfig");
        public final static Key PANELDEFINITION = make("PanelDefinition");

        public final static Key TABLE_COLUMN_POSITIONS = make("columnPositions");

        public final static Key SHOW_SCHEDULEPANEL = make("showSchedulePanel");

        public final static Key TABDEFINITION = make("TabDefinition");

        public final static Key SHOW_STARTCONFIGURATOR = make("showStartConfiguratorPanel");

        public final static Key SCHEDULE_DAYWIDTH = make("DayWidth");
        public final static Key SCHEDULE_GRIDHEIGHT = make("GridHeight");
        public final static Key SCHEDULE_USEGRID = make("UseGrid");

        public final static Key PRIVATE_TODO = make("PRIVATE_TODO");
        public final static Key PRIVATE_DATE = make("PRIVATE_DATE");

        public final static Key LOGO_STILL = make("LogoStill");
        public final static Key LOGO_ANIM = make("LogoAnim");

        public final static Key START_MODE = make("startupMode");

        public final static Key DUBCHECKSENTIVEFIELDS = make("dubCheckSensitiveFields");

        // Define constants for the appearance parameters before this line.

        private Key(String label) {
            super(label);
            instances.put(label, this);
        }

        /**
         * Creates a new instance.
         *
         * <p>Use this instead of the constructor in light of
         * future additions.</p>
         *
         * @param label
         */
        private static Key make(String label) {
            return new Key(label);
        }

        /**
         * Returns an instance of this class or throws
         * a {@KeyUnavailableException} if it does not exist.
         *
         * @param label
         * @throws KeyUnavailableException if the key does not exist.
         */
        private static Key getInstance(String label)
          throws KeyUnavailableException {
            Key k = (Key) instances.get(label);

            if (k == null) {
                throw new KeyUnavailableException(label);
            }

            return k;
        }
    }
}
