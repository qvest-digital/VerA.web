package de.tarent.commons.spreadsheet.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Stellt Methoden zum laden und speichern eines XML-Dokumentes
 * zur Verfügung.
 *
 * @author Christoph Jerolimov
 */
public class XMLDocument {
	protected Document document;

	/**
	 * Lädt ein XML-Dokument aus dem übergebenem InputStream.
	 *
	 * @param inputStream
	 * @throws IOException
	 */
	public void loadDocument(InputStream inputStream) throws IOException {
		try {
			document = getDocument();
			Source source = new StreamSource(new InputStreamReader(inputStream, "UTF-8"));
			source.setSystemId("file:/tarent");
			Result result = new DOMResult(document);
			getTransformer().transform(source, result);
		} catch (Exception e) {
			throwIOException(e);
		}
	}

	/**
	 * Speichert ein XML-Dokument in dem übergebenem OutputStream.
	 *
	 * @param outputStream
	 * @throws IOException
	 */
	public void saveDocument(OutputStream outputStream) throws IOException {
		try {
			Source source = new DOMSource(document);
			Result result = new StreamResult(new OutputStreamWriter(outputStream, "UTF-8"));
			getTransformer().transform(source, result);
		} catch (Exception e) {
			throwIOException(e);
		}
	}

	/**
	 * Gibt das erste Node-Element mit dem übergebenem Namen
	 * aus dem aktuellen Dokumente zurück.
	 *
	 * @param name Name des Elements
	 * @return Node
	 */
	protected Node getNode(String name) {
		return document.getElementsByTagName(name).item(0);
	}

	protected Transformer getTransformer() throws TransformerConfigurationException, TransformerFactoryConfigurationError {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
		return transformer;
	}

	protected Document getDocument() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		return factory.newDocumentBuilder().newDocument();
	}

	/**
	 * Verpackt eine beliebige Exception in eine IOException
	 * und wirft diese, Fehlermeldung und Stacktrace werden dabei
	 * beibehalten.
	 *
	 * @param t
	 * @throws IOException IMMER
	 */
	protected static void throwIOException(Throwable t) throws IOException {
		if (t instanceof IOException)
			throw (IOException)t;
		else if (t instanceof RuntimeException)
			throw (RuntimeException)t;
		else if (t instanceof Error)
			throw (Error)t;

		IOException e = new IOException(t.toString());
		e.setStackTrace(t.getStackTrace());
		throw e;
	}
}
