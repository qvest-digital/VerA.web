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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>ConfigManager</code> handles the configuration loading mechanism
 * and provides accessor methods to the environment and appearance
 * configuration.
 * <p>
 * For a general introduction to the configuration system refer to the
 * respective document on the project's website.
 * </p>
 * <p>
 * An instance of this class can be acquired by calling {@link #getInstance()}.
 * It is only needed to start the configuration loading step.
 * </p>
 * <p>
 * When the configuration has been loaded one can get the appearance
 * configuration via {@link #getAppearance()} and the environment configuration
 * via {@link #getEnvironment()}.
 *
 * @author Robert Schuster
 */
public class ConfigManager {
    private static Logger logger = Logger.getLogger(ConfigManager.class.getName());

    private static ConfigManager instance;

    // Configuration files
    public String APPEARANCE_CONFIG = "appearance.xml";

    public String ENVIRONMENT_CONFIG = "environment.xml";

    /**
     * A list of document which failed to parse.
     */
    private List/*<String>*/ missingDocuments;

    /**
     * The loader instance which is used to retrieve the configuration documents.
     */
    private Loader loader;

    /**
     * Base system property key for the bootstrap configuration.
     */
    private static final String BOOTSTRAP_BASE = "de.tarent.commons.config.bootstrap";

    /**
     * System property key for the bootstrap configuration type.
     */
    private static final String BOOTSTRAP_TYPE = BOOTSTRAP_BASE + ".type";

    /**
     * System property value denoting that configuration documents are to be
     * retrieved from the local filesystem.
     * <p>
     * This is the default if the bootstrap configuration does not contain
     * explicit information about this.
     * </p>
     */
    private static final String BOOTSTRAP_TYPE_FILE = "file";

    /**
     * System property key for the application variant.
     */
    private static final String BOOTSTRAP_VARIANT = BOOTSTRAP_BASE + ".variant";

    /**
     * System property value for the default application variant "contact".
     */
    private static final String BOOTSTRAP_VARIANT_CONTACT = "contact";

    /**
     * Name of the root tag in the appearance configuration documents.
     */
    private static final String ROOT_TAG_APPEARANCE = "appearance";

    /**
     * Name of the root tag in the environment configuration documents.
     */
    private static final String ROOT_TAG_ENVIRONMENT = "environment";

    /**
     * Name of the root tag in the action definition documents.
     */
    private static final String ROOT_TAG_ACTIONS = "actions";

    /**
     * Name of the root tag in the plugin definition documents.
     */
    private static final String ROOT_TAG_PLUGINS = "plugins";

    /**
     * Name of the root tag in the connection definition documents.
     */
    private static final String ROOT_TAG_CONNECTIONS = "connections";

    /**
     * Preferences node name of personal application-settings.
     */
    private String prefBaseNodeName;

    private Class applicationClass;

    private String variant;

    private Appearance ape = new Appearance();

    private Environment env = new Environment();

    private Preferences prefs;

    private ConfigManager() {
    }

    public void setPrefBaseNodeName(String prefBaseNodeName) {
        this.prefBaseNodeName = prefBaseNodeName;
    }

    public void setBootstrapVariant(String bootstrapVariant) {
        this.variant = bootstrapVariant;
    }

    public void setApplicationClass(Class applicationClass) {
        this.applicationClass = applicationClass;
    }

    /**
     * Initializes the configuration system by evaluating the bootstrap
     * configuration and loading the configuration documents.
     * <p>
     * After this step the instances returned by {@link #getAppearance()} and
     * {@link #getEnvironment()} contain meaningful configuration data which can
     * be used throughout the application.
     * </p>
     *
     * <p>
     * If no appearance configuration can be loaded an {@link IllegalStateException}
     * is thrown.
     * </p>
     *
     * @throws IllegalStateException when an important configuration document cannot be found or
     *                               parsed.
     */
    public void init() throws IllegalStateException {
        String type = System.getProperty(BOOTSTRAP_TYPE);
        if (type == null) {
            logger.warning("Sytem property " + BOOTSTRAP_TYPE + " is not set. Using default: " + BOOTSTRAP_TYPE_FILE);
            type = BOOTSTRAP_TYPE_FILE;
        }

        if (System.getProperty(BOOTSTRAP_VARIANT) != null) {
            variant = System.getProperty(BOOTSTRAP_VARIANT);
        }

        if (variant == null) {
            logger.warning("System property " + BOOTSTRAP_VARIANT + " is not set. Using default: " + BOOTSTRAP_VARIANT_CONTACT);
            variant = BOOTSTRAP_VARIANT_CONTACT;
        }

        loadConfiguration(type, variant);
    }

    public static Appearance getAppearance() {
        return getInstance().ape;
    }

    public static Environment getEnvironment() {
        return getInstance().env;
    }

    public static Preferences getPreferences() {
        return getInstance().prefs;
    }

    public void store() {
        Map/*<String, Document>*/ documents = getEnvironment().prepareDocuments();

        Iterator ite = documents.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry entry = (Map.Entry) ite.next();
            String name = (String) entry.getKey();
            Document doc = (Document) entry.getValue();

            try {
                loader.storeDocument(Scope.USER, name, doc);
            } catch (DocumentUnavailableException e) {
                logger.warning("storing of document failed: " + e.docURL);
            }
        }
    }

    /**
     * Returns a list of documents which failed to parse and are therefore
     * missing.
     *
     * <p>The list is empty of no documents are missing.</p>
     *
     * @return
     */
    public static List getMissingDocuments() {
        ConfigManager instance = getInstance();
        if (instance.missingDocuments == null) {
            return Collections.EMPTY_LIST;
        }

        return new ArrayList(instance.missingDocuments);
    }

    private void loadConfiguration(String configType, String variant)
            throws IllegalStateException {
        // Instantiates the Loader instance to be used for retrieving the
        // configuration documents.
        if (configType.equals(BOOTSTRAP_TYPE_FILE)) {
            if (applicationClass == null) {
                applicationClass = FileLoader.class;
            }

            loader = new FileLoader(variant, applicationClass);
            //loader = new JARLoader(variant, applicationClass);
        }

        // Initializes the application variant's Preferences instance.
        prefs = Preferences.userRoot().node(prefBaseNodeName + "/" + variant);

        // Loads the appearance configuration from site and installation scope.
        Document doc = null;
        Handler handler = new AppearanceHandler();
        String rootTag = ROOT_TAG_APPEARANCE;
        boolean appearanceConfigAvailable = false;

        try {
            doc = loader.getDocument(Scope.SITE, APPEARANCE_CONFIG);
            insertElements(Scope.SITE, doc, rootTag, handler);
            appearanceConfigAvailable = true;
        } catch (DocumentUnavailableException e) {
            addMissingDocument(e.docURL);  // site-scope appearance config is mandatory
            logger.warning(
                    "appearance configuration at site scope not available: "
                            + e.getReason());
        }

        try {
            doc = loader.getDocument(Scope.INSTALLATION,
                    APPEARANCE_CONFIG);
            insertElements(Scope.INSTALLATION, doc, rootTag, handler);
        } catch (DocumentUnavailableException e) {
            // installation-scope apprearance config is optional
            logger.warning(
                    "appearance configuration at installation scope not available: "
                            + e.getReason());

            if (!appearanceConfigAvailable) {
                throw new IllegalStateException(
                        "No appearance configuration available at all.");
            }
        }

        rootTag = ROOT_TAG_ENVIRONMENT;
        handler = new EnvironmentHandler();
        try {
            // Loads the environment configuration from site, installation and user
            // scope.
            doc = loader.getDocument(Scope.SITE,
                    ENVIRONMENT_CONFIG);
            insertElements(Scope.SITE, doc, rootTag, handler);
        } catch (DocumentUnavailableException e) {
            addMissingDocument(e.docURL);  // site-scope appearance config is mandatory
            logger.warning(
                    "environment configuration at site scope not available: "
                            + e.getReason());

        }

        try {
            doc = loader.getDocument(Scope.INSTALLATION,
                    ENVIRONMENT_CONFIG);
            insertElements(Scope.INSTALLATION, doc, rootTag, handler);
        } catch (DocumentUnavailableException e) {
            // installation-scope environment config is optional
            addMissingDocument(e.docURL);
            logger.warning(
                    "environment configuration at installation scope not available: "
                            + e.getReason());

        }

        // Prevents trying to read the user scope environment configuration document.
        if (!env.isUserOverridable()) {
            logger.info("user scope environment configuration disabled.");
            return;
        }

        try {
            doc = loader.getDocument(Scope.USER,
                    ENVIRONMENT_CONFIG);
            insertElements(Scope.USER, doc, rootTag, handler);
        } catch (DocumentUnavailableException e) {
            // user-scope environment config is optional
            logger.warning(
                    "environment configuration at user scope not available: "
                            + e.getReason());

        }

    }

    private void insertElements(Scope scope, Document doc, String rootTag,
            Handler handler) throws DocumentUnavailableException {
        NodeList list = doc.getElementsByTagName(rootTag).item(0).getChildNodes();
        int size = list.getLength();

        for (int i = 0; i < size; i++) {
            Node n = list.item(i);
            NamedNodeMap attributes = n.getAttributes();

            try {
                handler.handle(scope, n, n.getNodeName(), attributes);
            } catch (KeyUnavailableException e) {
                logger.warning("The " + rootTag + " configuration of scope "
                        + scope + " contains the unknown key: "
                        + e.getKeyLabel());
            } catch (ParseException pe) {
                logger.warning(pe.getMessage());
            }

        }
    }

    /**
     * Reads a document with the given loader from the given scope. The method
     * is supposed to be used with secondary documents only whose absence (e.g.
     * due to errors) is tolerable.
     *
     * <p>In case the loading fails an error message is logged and <code>null</code>
     * is returned. This gives the callee the possibility to continue parsing.
     * Additionally the document's name is added to the list of missing documents
     * which can be accessed later to show a proper diagnostic dialog.</p>
     *
     * @param loader
     * @param scope
     * @param docName
     * @return
     */
    private Document getLinkedDocument(Loader loader, Scope scope, String docName) {
        try {
            return loader.getDocument(scope, docName);
        } catch (DocumentUnavailableException e) {
            addMissingDocument(e.docURL);
            logger.warning("linked document from scope \"" + scope + "\" is not available: " + e.reason);

            return null;
        }
    }

    private void addMissingDocument(String docURL) {
        if (missingDocuments == null) {
            missingDocuments = new ArrayList();
        }

        missingDocuments.add(docURL);
    }

    public static ConfigManager getInstance() {
        // Try to avoid synchronized block.
        if (instance != null) {
            return instance;
        }

        synchronized (ConfigManager.class) {
            if (instance == null) {
                return instance = new ConfigManager();
            }

            return instance;
        }

    }

    /**
     * Abstract base class for document loaders.
     * <p>
     * A <code>Loader</code> implementation encapsulates the way configuration
     * documents are retrieved.
     * </p>
     * <p>
     * Additionally the implementation controls which system properties control it
     * and where the documents are located in the different scopes.
     * </p>
     * <p>
     * For a general introduction to the configuration system refer to the
     * respective document on the project's website.
     * </p>
     * <p>
     * Subclass instances have access to the constant field <code>VARIANT</code>
     * which denotes the application variant.
     * </p>
     *
     * @author Robert Schuster
     */
    static abstract class Loader {
        protected final String VARIANT;

        protected Loader(String variant) {
            VARIANT = variant;
        }

        protected abstract Document getDocument(Scope scope, String docName)
                throws DocumentUnavailableException;

        protected abstract boolean isStoringSupported();

        protected void storeDocument(Scope scope, String docName, Document doc)
                throws DocumentUnavailableException, UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

    }

    static class DocumentUnavailableException extends Exception {
        /**
         *
         */
        private static final long serialVersionUID = -1311824187433589069L;
        String docURL;
        String reason;

        DocumentUnavailableException(String docURL, String reason) {
            super();
            this.docURL = docURL;
            this.reason = reason;
        }

        String getReason() {
            return reason;
        }
    }

    static class ParseException extends Exception {
        /**
         *
         */
        private static final long serialVersionUID = -6643357532451708370L;

        ParseException(String msg) {
            super(msg);
        }

        ParseException(String msg, Throwable cause) {
            super(msg, cause);
        }

    }

    /**
     * Typesafe enumeration whose instances denote the configuration scopes.
     * <p>
     * The constant instance of this class are needed for {@link Loader}
     * implementations.
     * </p>
     * <p>
     * For a general introduction to the configuration system refer to the
     * respective document on the project's website.
     * </p>
     */
    static class Scope {
        private String label;

        static final Scope SITE = new Scope("site");

        static final Scope INSTALLATION = new Scope("installation");

        static final Scope USER = new Scope("user");

        private Scope(String label) {
            this.label = label;
        }

        public boolean equals(Object o) {
            return o instanceof Scope && label == ((Scope) o).label;
        }

        public String toString() {
            return label;
        }
    }

    private interface Handler {
        void handle(Scope scope, Node node, String nodeName, NamedNodeMap attributes)
                throws KeyUnavailableException, DocumentUnavailableException, ParseException;
    }

    private class AppearanceHandler implements Handler {

        public void handle(Scope scope, Node node, String nodeName,
                NamedNodeMap attributes) throws KeyUnavailableException,
                DocumentUnavailableException, ParseException {
            if (nodeName == null) {
                return;
            } else if (nodeName.equals("param")) {
                Node name = attributes.getNamedItem("name");

                if (name == null) {
                    throw new ParseException("expected name attribute in <param> tag");
                }

                ape.put(name.getNodeValue(), node);

            } else if (nodeName.equals("include")) {
                Node documentName = attributes.getNamedItem("file");

                if (documentName == null) {
                    throw new ParseException("expected file attribute in <include> tag");
                }

                Document linkedDoc = getLinkedDocument(loader, scope, documentName.getNodeValue());
                if (linkedDoc != null) {
                    insertElements(scope, linkedDoc, ROOT_TAG_APPEARANCE, this);
                }
            } else if (nodeName.equals("actions")) {
                Node documentName = attributes.getNamedItem("file");

                if (documentName == null) {
                    throw new ParseException("expected file attribute in <actions> tag");
                }

                Document actionsDoc = getLinkedDocument(loader, scope, documentName.getNodeValue());

                if (actionsDoc != null) {
                    insertElements(scope, actionsDoc, ROOT_TAG_ACTIONS,
                            new ActionsHandler());
                }
            } else if (nodeName.equals("plugins")) {
                Node documentName = attributes.getNamedItem("file");

                if (documentName == null) {
                    throw new ParseException("expected file attribute in <plugins> tag");
                }

                Document pluginsDoc = getLinkedDocument(loader, scope, documentName.getNodeValue());

                if (pluginsDoc != null) {
                    insertElements(scope, pluginsDoc, ROOT_TAG_PLUGINS,
                            new PluginsHandler());
                }
            }
        }

    }

    private class ActionsHandler implements Handler {
        public void handle(Scope scope, Node node, String nodeName,
                NamedNodeMap attributes) throws KeyUnavailableException,
                DocumentUnavailableException, ParseException {
            if (nodeName.equals("action")) {
                String key = getId(node);
                try {
                    ape.addActionDefinition(key, node);
                } catch (DataFormatException dfe) {
                    throw new ParseException("Error parsing action definition " + key, dfe);
                }
            } else if (nodeName.equals("include")) {
                Node documentName = attributes.getNamedItem("file");

                if (documentName == null) {
                    throw new ParseException("expected file attribute in <include> tag");
                }

                Document actionsDoc = getLinkedDocument(loader, scope, documentName.getNodeValue());

                if (actionsDoc != null) {
                    insertElements(scope, actionsDoc, ROOT_TAG_ACTIONS, this);
                }
            }
        }

        String getId(Node action) throws ParseException {
            NodeList params = action.getChildNodes();
            int size = params.getLength();

            for (int i = 0; i < size; i++) {
                Node param = params.item(i);
                NamedNodeMap attributes = param.getAttributes();
                if (attributes == null) {
                    continue;
                }

                Node attr = attributes.getNamedItem("name");
                if (attr != null && attr.getFirstChild() != null && attr.getFirstChild().getNodeValue().equals("UniqueName")) {
                    Node attrVal = attributes.getNamedItem("value");
                    if (attrVal != null && attrVal.getFirstChild() != null) {
                        return attrVal.getFirstChild().getNodeValue();
                    }
                }
            }

            throw new ParseException(
                    "expected a <param> tag with an name attribute and a value of \"UniqueName\" within an <action> tag");
        }

    }

    private class PluginsHandler implements Handler {
        public void handle(Scope scope, Node node, String nodeName,
                NamedNodeMap attributes) throws KeyUnavailableException,
                DocumentUnavailableException, ParseException {
            if (nodeName.equals("plugin")) {
                String key = getId(node);
                ape.addPluginDefinition(key, node);
            } else if (nodeName.equals("include")) {
                Node documentName = attributes.getNamedItem("file");

                if (documentName == null) {
                    throw new ParseException("expected file attribute in <include> tag");
                }

                Document includeDoc = getLinkedDocument(loader, scope, documentName.getNodeValue());

                if (includeDoc != null) {
                    insertElements(scope, includeDoc, ROOT_TAG_PLUGINS, this);
                }
            }
        }

        String getId(Node action) throws ParseException {
            NodeList params = action.getChildNodes();
            int size = params.getLength();

            for (int i = 0; i < size; i++) {
                Node param = params.item(i);
                NamedNodeMap attributes = param.getAttributes();
                if (attributes == null) {
                    continue;
                }

                Node attr = attributes.getNamedItem("name");
                if (attr != null && attr.getFirstChild() != null && attr.getFirstChild().getNodeValue().equals("id")) {
                    Node attrVal = attributes.getNamedItem("value");

                    if (attrVal != null && attrVal.getFirstChild() != null) {
                        return attrVal.getFirstChild().getNodeValue();
                    }
                }
            }

            throw new ParseException("expected a <param> tag with an value attribute within a <plugin> tag");
        }

    }

    private class EnvironmentHandler implements Handler {

        public void handle(Scope scope, Node node, String nodeName,
                NamedNodeMap attributes) throws KeyUnavailableException,
                DocumentUnavailableException, ParseException {
            if (nodeName == null) {
                return;
            } else if (nodeName.equals("param")) {
                Node name = attributes.getNamedItem("name");

                env.put(name.getNodeValue(), node);
            } else if (nodeName.equals("include")) {
                Node documentName = attributes.getNamedItem("file");

                if (documentName == null) {
                    throw new ParseException("expected file attribute in <include> tag");
                }

                Document linkedDoc = getLinkedDocument(loader, scope, documentName.getNodeValue());

                if (linkedDoc != null) {
                    insertElements(scope, linkedDoc, ROOT_TAG_ENVIRONMENT, this);
                }
            } else if (nodeName.equals("connections")) {
                Node documentName = attributes.getNamedItem("file");

                if (documentName == null) {
                    throw new ParseException("expected file attribute in <include> tag");
                }

                Document includeDoc = getLinkedDocument(loader, scope, documentName.getNodeValue());

                if (includeDoc != null) {
                    insertElements(scope, includeDoc, ROOT_TAG_CONNECTIONS, new ConnectionsHandler());
                }

            } else if (nodeName.equals("userOverridable")) {
                // Detects and handles the userOverridable tag which is only allowed in
                // site and installation scope.
                if (scope == Scope.USER) {
                    throw new ParseException("<userOverridable> tag not allowed in user scope configuration document.");
                }

                Node name = attributes.getNamedItem("value");

                env.setUserOverridable(name.getNodeValue());

            }
        }

    }

    private class ConnectionsHandler implements Handler {
        public void handle(Scope scope, Node node, String nodeName,
                NamedNodeMap attributes) throws KeyUnavailableException,
                DocumentUnavailableException, ParseException {
            if (nodeName.equals("connection")) {
                env.addConnectionDefinition(scope, node);
            }

        }

    }

}
