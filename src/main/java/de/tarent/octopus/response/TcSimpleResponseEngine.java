/* $Id: TcSimpleResponseEngine.java,v 1.4 2007/03/11 14:04:34 christoph Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.response;

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

/** Diese Klasse repr�sentiert die Anfangs verwendete Template-Engine.
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
     * und nicht zur�ck gegeben.
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
     * Diese enth�lt im wesentlichen Namen der Templates und wie diese geschachtelt werden
     * sollen, um eine Ausgabeseite zu erstellen.
     * Au�erdem werden in den PageDescriptions einige Felder der Templates schon definiert.
     * <br><br>
     * Welche Ausgabeseite benutzt werden soll h�ngt nur von dem pageName parameter ab.
     * Es kann aber sp�ter auch noch an die Benutzerrolle angepasst werden.
     *
     * @param descName Name der Response
     * @param responseType Typ, von der die Response sein soll (z.B. htmlPage )
     *
     * @return Beschreibungsobjekt einer Ausgabeseite als TcResponseDescription.
     *         Das Objekt muss dann abh�ngig vom Typ der R�ckgabeseite z.B. zu
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
     * Liefert den Verzeichnispfad zur�ck, indem die Pagedescription enthalten ist.
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
 * Parsen und f�llen eines Templates im mehreren Schritten.
 *
 * <ol>
 *   <li>Parsen eines Templates im Constructor. Dabei werden Untermodule direkt eingebunden.</li>
 *   <li>Aufbau eines Templatebaumes durch setzen der Childtemplates.</li>
 *   <li>Rekursives Einsetzen des Contents und Zusammenbauen der ganzen Page durch Aufruf von getFilledTemplate() auf dem RootTempplate
 * </ol>
 * <ul><b>Templatedialekt:</b>
 *   <li><b>{$feldName$}</b> Wird durch den Wert im TcContent Objekt ersetzt.</li>
 *   <li><b>{$�templatePlatzhalter$}</b> Wird durch das geparste Template in childs.get( templatePlatzhalter ) ersetzt, wobei childs �ber setChildTemplates() gesetzt wird.</li>
 *   <li><b>{$>templateDateiName$}</b> Wird direkt w�rend dem Parsen aufgel��t und durch den Inhalt der Datei rekursiv templateDateiName+".tct" (absolut zum Templateverzeichniss) ersetzt.</li>
 * </ul>
 * @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
class TarParsedTemplate {
    /**
     * Felder des Templates. Abwechselnd Stings mit beliebigem Inhalt und 
     * Direktiven. Direktiven k�nnen wieder Vectoren oder Strings mit Anweisungen sein.
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
     * Hier werden die Templates mit den Daten gef�llt.
     * Die Ausgabe sollte sp�ter eventuell auf Streams umgestellt werden
     */
    public String getFilledTemplate(TcContent content) throws ResponseProcessingException {
        return getFilledTemplate(content, this.template);
    }

    /**
     * Hier werden die Templates mit den Daten gef�llt.
     * Die Ausgabe sollte sp�ter eventuell auf Streams umgestellt werden
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
                    case '�' : //Referenz auf anderes Template
                        if (childs == null)
                            throw new ResponseProcessingException("Referenz kann nicht aufgel�st werden; Keine Templates angeh�ngt.");
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
        //F�r den End-String.
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
                    if (tag_index > 0) { //Anweisungen f�r falsch erkannte Tags.
                        //muss erweitert werden wenn TAG[i].length
                        //gr�sser als zwei ist.
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
 * Die TcPageDescription ent�lt Informationen dar�ber,
 * aus was f�r Templates sich eine Seite zusammen setzt.
 * <br><br>
 * Au�erdem k�nnen hier auch schon ein paar Felder im Template belegt werden.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
class TcPageDescription extends TcResponseDescription {
    /**  Das Hapt Template, da� alle anderen einbindet, muss gesetzt sein */
    private TarTemplateDescription rootTemplate;

    /** Zuweisung f�r Felder der Templates, optoinal */
    private Map fieldAssignment;

    /**
     * Fehlermeldungen k�nnen hier abgelegt und von einer aufrufenden Klasse abgefragt werden
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
     * Gibt das Verzeichniss zur�ck, indem nach Templates gesucht werden soll.
     *
     * @return Absolute Pfadangabe mit abschlie�endem Slash
     */
    public String getTemplateRootPath() {
        return templateRootPath;
    }

    /**
     * Setzt das Verzeichniss indem nach Templates gesucht werden soll.
     *
     * @param path Absolute Pfadangabe mit abschlie�endem Slash
     */
    public void setTemplateRootPath(String path) {
        templateRootPath = path;
    }

    /**
     * Liefert immer "htmlPage" zur�ck,
     * da diese Description nur f�r diesen Typ ist.
     * <br><br>
     * Ist f�r die Schnittstelle TcResponseDescription implementiert
     *
     * @return "htmlPage"
     */
    public String getResponseType() {
        return "simple";
    }

    /**
     * Gibt die TarTemplateDescription des Obersten Templates im Templatebaum zur�ck.
     */
    public TarTemplateDescription getRootTemplate() {
        return rootTemplate;
    }

    /**
     * Gibt die Map mit den Feldzuweisungen zur�ck.
     *
     * @return Map mit Strings als Keys und Values
     */
    public Map getFieldAssignment() {
        return fieldAssignment;
    }

    /**
     * Gibt Fehlermeldungen, die w�rend der Erstellung aufgetreten sind zur�ck
     * @return Fehlermeldung, oder null, wenn keine Meldung auf getreten ist.
     */
    public String getMessages() {
        return messages;
    }

    /**
     * F�gt eine Meldung an messages am
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
