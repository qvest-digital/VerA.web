package de.tarent.octopus.content;

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
import de.tarent.octopus.util.Xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;

/**
 * Schnittstellenbeschreibung; Mit dieser Klasse ist es möglich, die Operationen mit ihren Ein- und Ausgabeparametern,
 * die von einem Modul angeboten werden zu beschreiben.
 * <br><br>
 * <ul>
 * <b>Die Beschreibung ist folgendermaßen gegliedert:</b>
 * <li>Ein Port ist z.B. ein TcContentWorker oder eine Ansammlung von Tasks.
 * Er benötigt genau eine TcPortDefinition.</li>
 * <li>Die Actions eines Workers, bzw. die Tasks einer Tasksammlung sind die Operationen. Sie werden von TcOperationDefinition
 * Objekten repräsentiert.
 * Ein Port hat in der Regel viele Operationen, die er zur Verfügung stellt.</li>
 * <li>Eine Operation hat eine InputMessage und eine OutputMessage, dazu kommen beliebig viele FaultMessages.
 * Eine Message wird durch TcMessageDefinition dar gestellt und beschreibt die Parameter, die eine Operation benötigt,
 * bzw. zurück liefert. </li>
 * <li>Eine Message besteht aus TcMessageDefinitionParts. Diese beschreiben einzelne Eingabeparameter mit ihrem Typ und Namen</li>
 * </ul>
 *
 * Die Art dieser Beschreibung ist an WSDL angepasst, so daß unmittelbar ein XML-Dokument daraus erzeugt werden kann, daß
 * der PortType-Section eines  WSDL-Dokumentes ähnlich ist und sich mit wehnig Aufwand darin transformieren lässt.
 *
 * @see TcOperationDefinition
 * @see TcMessageDefinition
 * @see TcMessageDefinitionPart
 */
public class TcPortDefinition {
    static final String SOAP_ACTION_EXECUTE = "execute";
    static final String ENCODING_STYLE_URI_SOAP = "http://schemas.xmlsoap.org/soap/encoding/";
    static final String SOAP_BODY_USE_ENCODED = "encoded";
    static final String WSDL_OUTPUT_MESSAGE_POSTFIX = "_OutputMessage";
    static final String WSDL_INPUT_MESSAGE_POSTFIX = "_InputMessage";
    static final String WSDL_PORTTYPE_POSTFIX = "PortType";
    static final String SOAP_TRANSPORT_URI_HTTP = "http://schemas.xmlsoap.org/soap/http";
    static final String WSDL_DEFINITION_POSTFIX = "Definition";
    static final String SOAP_BINDING_STYLE_RPC = "rpc";
    static final String WSDL_BINDING_POSTFIX = "Binding";
    static final String WSDL_PORT_POSTFIX = "Port";
    static final String WSDL_SERVICE_POSTFIX = "Service";
    static final String NS_SOAP = "soap";
    static final String NS_XML_SCHEMA = "xsd";
    static final String NS_TARGET = "tns";
    final static String NS_URI_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    final static String NS_URI_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";

    private Map operations;
    private String name;
    private String description;

    /**
     * Initialisiert eine Port Definition
     *
     * @param name        Name der TcPortDefinition
     * @param description Sinnvolle Beschreibung des Ports
     */
    public TcPortDefinition(String name, String description) {
        this.name = name;
        this.description = description;
        operations = new HashMap();
    }

    /**
     * Liefert den Namen, der im Constructor übergeben wurde.
     *
     * @return String mit dem Namen
     */
    public String getName() {
        return this.name;
    }

    /**
     * Liefert die Beschreibung, die im Constructor übergeben wurde.
     *
     * @return String mit der Beschreibung
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Fügt eine neue Operation hinzu, die von diesem, Port angeboten wird.
     *
     * @param theOperation Die Beschreibung der Operation, die hinzu gefügt werden soll
     */
    public TcOperationDefinition addOperation(TcOperationDefinition theOperation) {
        operations.put(theOperation.getName(), theOperation);
        return theOperation;
    }

    /**
     * Fügt eine neue Operation hinzu, die von diesem, Port angeboten wird.
     * Dabei wird automatisch ein Beschreibungsobjekt erstellt und hinzu gefügt.
     *
     * @param name        Der Name der Operation
     * @param description Eine Beschreibung der Operation
     */
    public TcOperationDefinition addOperation(String name, String description) {
        return addOperation(new TcOperationDefinition(name, description));
    }

    /**
     * Liefert die Operationen, die von diesem Port unterstützt werden zurück.
     *
     * @return Map mit den Namen der Operationen als Keys und TcOperationDefinition Objekten als Values
     */
    public Map getOperations() {
        return operations;
    }

    /**
     * Liefert die Beschreibgung einer Operation.
     *
     * @param name Der Name der Operation, deren Beschreibung zurück gegeben werden soll.
     * @return Die Beschreibung oder null, wenn keine Operation zu dme Namen existiert.
     */
    public TcOperationDefinition getOperation(String name) {
        return (TcOperationDefinition) operations.get(name);
    }

    /**
     * Diese Methode liefert ein Definition-Objekt, aus dem mittels WSDL4J
     * einfach eine WSDL-Darstellung erstellbar ist.
     *
     * @param includeCredentials <code>true</code>, falls jeder Operation,
     *                           die nicht anonym ist, Benutzername und Passwort als Pflichtparameter
     *                           hinzugefügt werden sollen.
     * @return ein WSDL4J-Definition-Objekt.
     * @throws WSDLException Ausnahmen, die in WSDL4J aufkommen
     */
    public Definition getWsdlDefinition(boolean includeCredentials, String tns, String prefix, String locationUri)
            throws WSDLException {
        WSDLFactory factory = WSDLFactory.newInstance();
        Definition def = factory.newDefinition();

        def.setQName(new QName(tns, prefix + WSDL_DEFINITION_POSTFIX));
        def.setTargetNamespace(tns);
        def.addNamespace(NS_TARGET, tns);
        def.addNamespace(NS_XML_SCHEMA, NS_URI_XML_SCHEMA);
        def.addNamespace(NS_SOAP, NS_URI_SOAP);

        // Service
        Service service = createService(def, tns, prefix);
        def.addService(service);

        // Port
        Port port = createPort(def, tns, prefix, locationUri);
        service.addPort(port);

        // Binding
        Binding binding = createBinding(def, tns, prefix);
        port.setBinding(binding);
        def.addBinding(binding);

        // PortType
        PortType portType = createPortType(def, tns, prefix);
        binding.setPortType(portType);
        def.addPortType(portType);

        // Operationen
        Iterator itOperations = getOperations().values().iterator();
        while (itOperations.hasNext()) {
            TcOperationDefinition octopusOperation = (TcOperationDefinition) itOperations.next();

            Message inputMessage = createInputMessage(def, tns, octopusOperation.getName(), octopusOperation.getInputMessage(),
                    includeCredentials);
            def.addMessage(inputMessage);
            Message outputMessage =
                    createOutputMessage(def, tns, octopusOperation.getName(), octopusOperation.getOutputMessage());
            def.addMessage(outputMessage);

            Input input = createInput(def, inputMessage);
            Output output = createOutput(def, outputMessage);

            Operation operation = createOperation(def, octopusOperation.getName(), input, output);
            portType.addOperation(operation);

            BindingOperation bindingOperation = createBindingOperation(def, tns, input, output, operation);
            binding.addBindingOperation(bindingOperation);
        }

        return def;
    }

    protected Service createService(Definition def, String tns, String prefix) {
        Service service = def.createService();
        service.setQName(new QName(tns, prefix + WSDL_SERVICE_POSTFIX));
        return service;
    }

    protected Port createPort(Definition def, String tns, String prefix, String locationUri) {
        Port port = def.createPort();
        port.setName(prefix + WSDL_PORT_POSTFIX);
        SOAPAddress soapAddress = new SOAPAddressImpl();
        soapAddress.setLocationURI(locationUri);
        port.addExtensibilityElement(soapAddress);
        return port;
    }

    protected Binding createBinding(Definition def, String tns, String prefix) {
        Binding binding = def.createBinding();
        binding.setQName(new QName(tns, prefix + WSDL_BINDING_POSTFIX));
        SOAPBinding soapBinding = new SOAPBindingImpl();
        soapBinding.setStyle(SOAP_BINDING_STYLE_RPC);
        soapBinding.setTransportURI(SOAP_TRANSPORT_URI_HTTP);
        binding.addExtensibilityElement(soapBinding);
        binding.setUndefined(false);
        return binding;
    }

    protected PortType createPortType(Definition def, String tns, String prefix) {
        PortType portType = def.createPortType();
        portType.setQName(new QName(tns, prefix + WSDL_PORTTYPE_POSTFIX));
        portType.setUndefined(false);
        return portType;
    }

    protected Message createInputMessage(Definition def, String tns, String operationName, TcMessageDefinition octopusMessage,
            boolean includeCredentials) {
        Message message = def.createMessage();
        message.setQName(new QName(tns, operationName + WSDL_INPUT_MESSAGE_POSTFIX));
        List octopusParts = octopusMessage.getParts();
        if (includeCredentials) {
            Part namePart = def.createPart();
            namePart.setName("username");
            namePart.setTypeName(new QName(TcMessageDefinition.TYPE_SCALAR));
            message.addPart(namePart);
            Part passPart = def.createPart();
            passPart.setName("password");
            passPart.setTypeName(new QName(TcMessageDefinition.TYPE_SCALAR));
            message.addPart(passPart);
        }
        if (octopusParts != null && !octopusParts.isEmpty()) {
            Iterator itOctopusParts = octopusParts.iterator();
            while (itOctopusParts.hasNext()) {
                TcMessageDefinitionPart octopusPart = (TcMessageDefinitionPart) itOctopusParts.next();
                Part part = def.createPart();
                part.setName(octopusPart.getName());
                part.setTypeName(new QName(octopusPart.getPartDataType()));
                //TODO: octopusPart.getDescription();
                message.addPart(part);
            }
        }
        message.setUndefined(false);
        return message;
    }

    protected Message createOutputMessage(Definition def, String tns, String operationName, TcMessageDefinition octopusMessage) {
        Message message = def.createMessage();
        message.setQName(new QName(tns, operationName + WSDL_OUTPUT_MESSAGE_POSTFIX));
        List octopusParts = octopusMessage.getParts();
        if (octopusParts != null && !octopusParts.isEmpty()) {
            Iterator itOctopusParts = octopusParts.iterator();
            while (itOctopusParts.hasNext()) {
                TcMessageDefinitionPart octopusPart = (TcMessageDefinitionPart) itOctopusParts.next();
                Part part = def.createPart();
                part.setName(octopusPart.getName());
                part.setTypeName(new QName(octopusPart.getPartDataType()));
                //TODO: octopusPart.getDescription();
                message.addPart(part);
            }
        }
        message.setUndefined(false);
        return message;
    }

    protected Input createInput(Definition def, Message message) {
        Input input = def.createInput();
        input.setMessage(message);
        return input;
    }

    protected Output createOutput(Definition def, Message message) {
        Output output = def.createOutput();
        output.setMessage(message);
        return output;
    }

    protected Operation createOperation(Definition def, String name, Input input, Output output) {
        Operation operation = def.createOperation();
        operation.setName(name);
        operation.setInput(input);
        operation.setOutput(output);
        operation.setUndefined(false);
        return operation;
    }

    protected BindingOperation createBindingOperation(Definition def, String namespaceURI, Input input, Output output,
            Operation operation) {
        BindingOperation bindingOperation = def.createBindingOperation();

        BindingInput bindingInput = def.createBindingInput();
        bindingInput.setName(input.getName());
        SOAPBody soapBodyInput = new SOAPBodyImpl();
        soapBodyInput.setUse(SOAP_BODY_USE_ENCODED);
        soapBodyInput.setEncodingStyles(Collections.singletonList(ENCODING_STYLE_URI_SOAP));
        soapBodyInput.setNamespaceURI(namespaceURI);
        bindingInput.addExtensibilityElement(soapBodyInput);

        BindingOutput bindingOutput = def.createBindingOutput();
        bindingOutput.setName(output.getName());
        SOAPBody soapBodyOutput = new SOAPBodyImpl();
        soapBodyOutput.setUse(SOAP_BODY_USE_ENCODED);
        soapBodyOutput.setEncodingStyles(Collections.singletonList(ENCODING_STYLE_URI_SOAP));
        soapBodyOutput.setNamespaceURI(namespaceURI);
        bindingOutput.addExtensibilityElement(soapBodyOutput);

        bindingOperation.setOperation(operation);
        bindingOperation.setName(operation.getName());
        bindingOperation.setBindingInput(bindingInput);
        bindingOperation.setBindingOutput(bindingOutput);
        SOAPOperation soapOperation = new SOAPOperationImpl();
        //soapOperation.setStyle();
        soapOperation.setSoapActionURI(SOAP_ACTION_EXECUTE);
        bindingOperation.addExtensibilityElement(soapOperation);

        return bindingOperation;
    }

    /**
     * Liefert eine Darstellung der gesamten PortDefinition mit Operationen und Messages als XML-DOM Tree
     *
     * @return Ein XML-Dom Tree
     */
    public Document getDomRepresentation() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            return null;
        }
        Document document = builder.newDocument();

        Element rootNode = document.createElement("PortDefinition");
        document.appendChild(rootNode);
        rootNode.setAttribute("name", this.getName());
        appendDescriptionNode(document, rootNode, this.getDescription());

        Map operations = this.getOperations();
        Element operationsNode = document.createElement("Operations");
        rootNode.appendChild(operationsNode);

        // Alle Messages werden wärend der Iteration hier gesammelt.
        // Dabei werden Dubletten nicht erkannt.
        Map messagesTable = new HashMap();

        for (Iterator e = operations.values().iterator(); e.hasNext(); ) {
            TcOperationDefinition currOperation = (TcOperationDefinition) e.next();

            Element currOperationNode = document.createElement("Operation");
            operationsNode.appendChild(currOperationNode);
            currOperationNode.setAttribute("name", currOperation.getName());
            appendDescriptionNode(document, currOperationNode, currOperation.getDescription());

            String messageName = currOperation.getName() + WSDL_INPUT_MESSAGE_POSTFIX;
            appendMessageNode(document, currOperationNode, messageName, "Input");
            messagesTable.put(messageName, currOperation.getInputMessage());

            messageName = currOperation.getName() + WSDL_OUTPUT_MESSAGE_POSTFIX;
            appendMessageNode(document, currOperationNode, messageName, "Output");
            messagesTable.put(messageName, currOperation.getOutputMessage());

            Map faultMessages = currOperation.getFaultMessages();
            for (Iterator e2 = faultMessages.keySet().iterator(); e2.hasNext(); ) {
                String faultCode = (String) e2.next();
                TcMessageDefinition currentFaultMessage = (TcMessageDefinition) faultMessages.get(faultCode);
                String currentFaultMessageDescription = currOperation.getFaultMessageDescription(faultCode);

                messageName = currOperation.getName() + "_FaultMessage_" + faultCode;
                Element faultNode = appendMessageNode(document, currOperationNode, messageName, "Fault");
                appendDescriptionNode(document, faultNode, currentFaultMessageDescription);
                messagesTable.put(messageName, currentFaultMessage);
            }
        }

        Element messagesNode = document.createElement("Messages");
        rootNode.appendChild(messagesNode);
        for (Iterator e3 = messagesTable.keySet().iterator(); e3.hasNext(); ) {
            String name = (String) e3.next();
            TcMessageDefinition message = (TcMessageDefinition) messagesTable.get(name);

            Element currentMessageNode = document.createElement("Message");
            messagesNode.appendChild(currentMessageNode);
            currentMessageNode.setAttribute("name", name);

            if (message != null) {
                Iterator itCurrentParts = message.getParts().iterator();
                while (itCurrentParts.hasNext()) {
                    TcMessageDefinitionPart currentPart = (TcMessageDefinitionPart) itCurrentParts.next();
                    Element currentPartNode = document.createElement("Part");
                    currentMessageNode.appendChild(currentPartNode);
                    currentPartNode.setAttribute("name", currentPart.getName());
                    currentPartNode.setAttribute("type", currentPart.getPartDataType());
                    if (currentPart.isOptional()) {
                        currentPartNode.setAttribute("optional", "true");
                    }

                    String description = currentPart.getDescription();
                    if (description != null && description.length() != 0) {
                        appendDescriptionNode(document, currentPartNode, description);
                    }
                }
            }
        }

        return document;
    }

    private Element appendDescriptionNode(Document document, Element parentNode, String description) {
        Element node = document.createElement("Description");
        Node text = document.createTextNode(description);
        node.appendChild(text);
        parentNode.appendChild(node);
        return node;
    }

    private Element appendMessageNode(Document document, Element parentNode, String messageName, String type) {
        Element messageNode = document.createElement(type + "Message");
        messageNode.setAttribute("name", messageName);
        parentNode.appendChild(messageNode);
        return messageNode;
    }

    /**
     * Liefert eine Darstellung der gesamten PortDefinition mit Operationen und Messages als XML Dokument
     *
     * @return Ein XML Fragment als String
     */
    public String toString() {
        return Xml.toString(getDomRepresentation());
    }
}
