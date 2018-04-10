package de.tarent.octopus.response;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.util.Xml;

/** Diese Klasse repräsentiert die Anfangs verwendete Template-Engine.
 *
 *  @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
public class TcSimpleResponseEngine implements TcResponseEngine {
    private static Log logger = LogFactory.getLog(TcSimpleResponseEngine.class);

    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
    }

    public void sendResponse(TcConfig config, TcResponse tcResponse, TcContent theContent, TcResponseDescription desc, TcRequest request)
        throws ResponseProcessingException {

        TcPageDescription pageDescription =
            (TcPageDescription) getResponseDescription(desc.getDescName(), null, config);
        if (pageDescription == null)
            throw new ResponseProcessingException(
                "Die Page Description zu '" + desc.getDescName() + "' kann nicht erstellt werden.");

        Map fieldAssignment = pageDescription.getFieldAssignment();
        theContent.setField("pageParams", fieldAssignment);
        TarTemplateDescription rootTemplate = pageDescription.getRootTemplate();
        TcTemplateSystemParams systemParams = new TcTemplateSystemParams(config);
        systemParams.setTemplateRootPath(pageDescription.getTemplateRootPath());
        TarParsedTemplate parsedRootTemplate = createTemplateTree(systemParams, rootTemplate);

        tcResponse.print(parsedRootTemplate.getFilledTemplate(theContent));
    }

    public TarParsedTemplate createTemplateTree(TcTemplateSystemParams systemParams, TarTemplateDescription node)
        throws ResponseProcessingException {

        Map childs = node.getChildTemplateDescriptions();
        Map parsedChilds = new HashMap();

        for (Iterator e = childs.keySet().iterator(); e.hasNext();) {
            String position = (String) e.next();
            TarParsedTemplate parsedChild =
                createTemplateTree(systemParams, (TarTemplateDescription) childs.get(position));
            parsedChilds.put(position, parsedChild);
        }

        TarParsedTemplate parsedNode = null;

        parsedNode = new TarParsedTemplate(systemParams, node);
        parsedNode.setChildTemplates(parsedChilds);

        return parsedNode;
    }

    /**
     * Parst eine Seitenbeschreibung und initialisiert damit ein Beschreibungsobjekt
     * <br><br>
     * Wenn Fehler auftreten, werden diese nur durch den Logger protokolliert
     * und nicht zurück gegeben.
     *
     * @param filename Dateiname relativ zum pageDescriptionRoot
     * @return TcPageDescription die aus der Datei erstellt wurde
     */
    private TcPageDescription getPageDescriptionFromFile(String filename) {
        Document document = null;
        try {
            document = Xml.getParsedDocument(filename);
            logger.debug("PageDescription '" + filename + "' Datei geparst.");
        } catch (SAXParseException se) {
            logger.error(" SaxExeption beim Parsen der PageDescription '"
                    + filename
                    + "' Datei (Zeile "
                    + se.getLineNumber()
                    + ",  Spalte "
                    + se.getColumnNumber()
                    + ")",
                se);
            return null;
        } catch (Exception e) {
            logger.error("Fehler beim Parsen der PageDescription '" + filename + "' Datei.", e);
            return null;
        }

        return new TcPageDescription(document);
    }

    /**
     * Liefert das Beschreibungsobjekt einer Ausgabeseite.
     * <br><br>
     * Diese enthält im wesentlichen Namen der Templates und wie diese geschachtelt werden
     * sollen, um eine Ausgabeseite zu erstellen.
     * Außerdem werden in den PageDescriptions einige Felder der Templates schon definiert.
     * <br><br>
     * Welche Ausgabeseite benutzt werden soll hängt nur von dem pageName parameter ab.
     * Es kann aber später auch noch an die Benutzerrolle angepasst werden.
     *
     * @param descName Name der Response
     * @param responseType Typ, von der die Response sein soll (z.B. htmlPage )
     *
     * @return Beschreibungsobjekt einer Ausgabeseite als TcResponseDescription.
     *         Das Objekt muss dann abhängig vom Typ der Rückgabeseite z.B. zu
     *         einem TcPageDescription herunter gecastet werden.
     */
    public TcResponseDescription getResponseDescription(String descName, String responseType, TcConfig config)
        throws ResponseProcessingException {

        if (responseType == null)
            responseType = config.getDefaultResponseType();

        String suffix = ".page";

        String templatePath = getTemplatePath(descName, config, suffix);
        String templateFile = templatePath + descName + suffix;

        TcPageDescription desc = getPageDescriptionFromFile(templateFile);

        if (desc == null) {
            logger.error("PageDescription '"
                    + templateFile
                    + "' konnte nicht erzeugt werden. Nehme jetzt 'pageDescriptionError'"
                    + suffix);

            templatePath = getTemplatePath("pageDescriptionError", config, suffix);
            templateFile = templatePath + descName + suffix;
            desc = getPageDescriptionFromFile(templateFile);
            desc.setTemplateRootPath(templatePath);
        } else {
            desc.setTemplateRootPath(templatePath);
            logger.debug("PageDescription '" + templateFile + "' erstellt.");
        }

        return desc;
    }

    /**
     * Liefert den Verzeichnispfad zurück, indem die Pagedescription enthalten ist.
     */
    private String getTemplatePath(String descName, TcConfig config, String suffix)
        throws ResponseProcessingException {

        String templateFile = config.getTemplateRootPath() + "simple/" + descName + suffix;

        logger.debug("Templatefile '" + templateFile + "' existiert:" + (new File(templateFile)).exists());
        // Nach einem gleichnamigen Template in dem Default Modul suchen

        if (!(new File(templateFile)).exists()) {
            String defaultTemplateFile =
                config.getCommonConfig().getTemplateRootPath(config.getCommonConfig().getDefaultModuleName())
                    + "simple/"
                    + descName
                    + suffix;

            if ((new File(defaultTemplateFile)).exists())
                return config.getCommonConfig().getTemplateRootPath(config.getCommonConfig().getDefaultModuleName())
                    + "simple/";
            else
                throw new ResponseProcessingException(
                    "Weder Template '"
                        + templateFile
                        + "' noch ein gleichnamiges Template des Default Modules '"
                        + defaultTemplateFile
                        + "' kann gefunden werden.");
        }
        return config.getTemplateRootPath() + "simple/";
    }
}

/**
 * Parsen und füllen eines Templates im mehreren Schritten.
 *
 * <ol>
 *   <li>Parsen eines Templates im Constructor. Dabei werden Untermodule direkt eingebunden.</li>
 *   <li>Aufbau eines Templatebaumes durch setzen der Childtemplates.</li>
 *   <li>Rekursives Einsetzen des Contents und Zusammenbauen der ganzen Page durch Aufruf von getFilledTemplate() auf dem RootTempplate
 * </ol>
 * <ul><b>Templatedialekt:</b>
 *   <li><b>{$feldName$}</b> Wird durch den Wert im TcContent Objekt ersetzt.</li>
 *   <li><b>{$°templatePlatzhalter$}</b> Wird durch das geparste Template in childs.get( templatePlatzhalter ) ersetzt, wobei childs über setChildTemplates() gesetzt wird.</li>
 *   <li><b>{$>templateDateiName$}</b> Wird direkt wärend dem Parsen aufgelößt und durch den Inhalt der Datei rekursiv templateDateiName+".tct" (absolut zum Templateverzeichniss) ersetzt.</li>
 * </ul>
 * @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
class TarParsedTemplate {
    /**
     * Felder des Templates. Abwechselnd Stings mit beliebigem Inhalt und
     * Direktiven. Direktiven können wieder Vectoren oder Strings mit Anweisungen sein.
     */
    private List template = null;
    private String templatePath;
    private char[][] TAG = { { '{', '$' }, { '$', '}' } };
    private Map childs = null;

    public TarParsedTemplate(TcTemplateSystemParams systemParams, TarTemplateDescription description)
        throws ResponseProcessingException {
        templatePath = systemParams.getTemplateRootPath();
        template = getParsedTemplate(description.getName());
    }

    public void setChildTemplates(Map children) {
        this.childs = children;
    }

    /**
     * Hier werden die Templates mit den Daten gefüllt.
     * Die Ausgabe sollte später eventuell auf Streams umgestellt werden
     */
    public String getFilledTemplate(TcContent content) throws ResponseProcessingException {
        return getFilledTemplate(content, this.template);
    }

    /**
     * Hier werden die Templates mit den Daten gefüllt.
     * Die Ausgabe sollte später eventuell auf Streams umgestellt werden
     */
    public String getFilledTemplate(TcContent content, List template) throws ResponseProcessingException {
        StringBuffer sb = new StringBuffer();
        String str;
        for (int i = 0; i < template.size() - 1; i += 2) {
            sb.append(template.get(i));

            Object o = template.get(i + 1);

            if (o instanceof String) {
                String s = (String) o;

                switch (s.charAt(0)) {
                    case '°' : //Referenz auf anderes Template
                        if (childs == null)
                            throw new ResponseProcessingException("Referenz kann nicht aufgelöst werden; Keine Templates angehängt.");
                        TarParsedTemplate reftemp = (TarParsedTemplate) childs.get(s.substring(1));
                        str = reftemp.getFilledTemplate(content);
                        break;
                    default : //Variable
                        str = content.getAsString(s);
                }
            } else {
                str = getFilledTemplate(content, (List) o);
            }

            if (str != null)
                sb.append(str);
        }
        //Für den End-String.
        if ((template.size() % 2) != 0) {
            sb.append(template.get(template.size() - 1));
        }

        return sb.toString();
    }

    /**
     * Diese Methode holt das Template
     */
    private InputStream getTemplate(String name) throws ResponseProcessingException {
        StringBuffer sb = new StringBuffer();
        sb.append(templatePath);
        //sb.append( FILESEP );
        sb.append(name);
        //sb.append(description.getName());
        sb.append(".tct");
        //	sb = new StringBuffer("/home/hendrik/tarent/cvs/tc/web/WEB-INF/templates/test.tct");
        FileInputStream file = null;
        try {
            file = new FileInputStream(sb.toString());
        } catch (FileNotFoundException e) {
            throw new ResponseProcessingException("Template \"" + sb.toString() + "\" wurde nicht gefunden.");
        }
        return file;
    }

    private List getParsedTemplate(String name) throws ResponseProcessingException {
        //	Reader file = new BufferedReader(new FileReader(getFile(template, device, user)));
        InputStream in = getTemplate(name);

        StringBuffer sb = new StringBuffer();
        List vector = new ArrayList();

        int c;
        byte tag_index = 0; //der Index im String TAG.
        byte tag = 0; //gibt an ob sich der Dateizeiger innerhalb eines Tags befindet.
        //1 = true, 0 = false;
        try {
            while ((c = in.read()) != -1) {
                if ((char) c == TAG[tag][tag_index]) {
                    tag_index++;
                    if (tag_index == TAG[tag].length) {
                        if (tag == 0)
                            tag = 1;
                        else
                            tag = 0;
                        tag_index = 0;
                        if (sb.charAt(0) == '>') {
                            vector.add(getParsedTemplate(sb.substring(1)));
                            sb = new StringBuffer();
                        } else {
                            vector.add(sb.toString());
                            sb = new StringBuffer();
                        }
                    }
                } else {
                    if (tag_index > 0) { //Anweisungen für falsch erkannte Tags.
                        //muss erweitert werden wenn TAG[i].length
                        //grösser als zwei ist.
                        tag_index = 0;
                        sb.append(TAG[tag][0]);
                    }
                    sb.append((char) c);
                }
            }
            in.close();
        } catch (IOException e) {
            throw new ResponseProcessingException("Template konnte nicht gelesen werden.");
        }
        vector.add(sb.toString());

        // 	String[] str = new String[vector.size()];
        // 	for (int i=0; i<vector.size(); i++) {
        // 	    str[i] = (String)vector.elementAt(i);
        // 	}
        return vector;
    }
}

/**
 * Die TcPageDescription entält Informationen darüber,
 * aus was für Templates sich eine Seite zusammen setzt.
 * <br><br>
 * Außerdem können hier auch schon ein paar Felder im Template belegt werden.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
class TcPageDescription extends TcResponseDescription {
    /**  Das Hapt Template, daß alle anderen einbindet, muss gesetzt sein */
    private TarTemplateDescription rootTemplate;

    /** Zuweisung für Felder der Templates, optoinal */
    private Map fieldAssignment;

    /**
     * Fehlermeldungen können hier abgelegt und von einer aufrufenden Klasse abgefragt werden
     */
    private String messages;

    private String templateRootPath;

    /**
     * Inititalisiert die Seitenbeschreibung mit dem Inhalt
     * eines XML-Dom Documentes.
     */
    public TcPageDescription(Document descriptionDocument) {
        super("name", "simple");

        fieldAssignment = new HashMap();

        Node root = descriptionDocument.getFirstChild();
        NodeList sections = root.getChildNodes();

        for (int i = 0; i < sections.getLength(); i++) {
            Node currSection = sections.item(i);
            if ("template".equals(currSection.getNodeName()))
                rootTemplate = new TarTemplateDescription(currSection);
            else if ("fieldAssignment".equals(currSection.getNodeName()))
                readFieldAssignment(currSection);
        }

        if (rootTemplate == null)
            appendMessage("Ein Muss mindestens ein Template Element existieren.");
    }

    /**
     * Gibt das Verzeichniss zurück, indem nach Templates gesucht werden soll.
     *
     * @return Absolute Pfadangabe mit abschließendem Slash
     */
    public String getTemplateRootPath() {
        return templateRootPath;
    }

    /**
     * Setzt das Verzeichniss indem nach Templates gesucht werden soll.
     *
     * @param path Absolute Pfadangabe mit abschließendem Slash
     */
    public void setTemplateRootPath(String path) {
        templateRootPath = path;
    }

    /**
     * Liefert immer "htmlPage" zurück,
     * da diese Description nur für diesen Typ ist.
     * <br><br>
     * Ist für die Schnittstelle TcResponseDescription implementiert
     *
     * @return "htmlPage"
     */
    public String getResponseType() {
        return "simple";
    }

    /**
     * Gibt die TarTemplateDescription des Obersten Templates im Templatebaum zurück.
     */
    public TarTemplateDescription getRootTemplate() {
        return rootTemplate;
    }

    /**
     * Gibt die Map mit den Feldzuweisungen zurück.
     *
     * @return Map mit Strings als Keys und Values
     */
    public Map getFieldAssignment() {
        return fieldAssignment;
    }

    /**
     * Gibt Fehlermeldungen, die wärend der Erstellung aufgetreten sind zurück
     * @return Fehlermeldung, oder null, wenn keine Meldung auf getreten ist.
     */
    public String getMessages() {
        return messages;
    }

    /**
     * Fügt eine Meldung an messages am
     * und testet dabei ob  messages==null ist
     */
    private void appendMessage(String m) {
        if (messages == null) {
            messages = new String();
            messages += m;
        }
        messages += "; " + m;
    }

    /** Gibt eine lesbare Form aus. */
    public String toString() {
        return (new StringBuffer())
            .append("TcPageDescription:\n")
            .append("RootTemplate: " + rootTemplate + "\n")
            .append("FieldAssignment: " + fieldAssignment + "\n")
            .toString();
    }

    /** Zum auslesen der Section mit den Zuweisungen */
    private void readFieldAssignment(Node sectionNode) {
        NodeList assignments = sectionNode.getChildNodes();

        for (int i = 0; i < assignments.getLength(); i++) {
            try {
                Node curr = assignments.item(i);
                if ("field".equals(curr.getNodeName())) {
                    NamedNodeMap attr = curr.getAttributes();
                    fieldAssignment.put(
                        attr.getNamedItem("name").getNodeValue(),
                        attr.getNamedItem("value").getNodeValue());
                }
            } catch (Exception e) { //Vor allem NullPointerExceptions, wenn Attrubute nicht da sind
                appendMessage("Ein Field Tag muss die Attribute name, value haben");
            }
        }
    }
}
