package de.tarent.commons.datahandling;

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
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import de.tarent.commons.utils.Log;

// TODO: This should also support XML DOM i/o

/**
 * This class implements a simple wrapper interface to a XML object serializer.
 * You can use this implementation to serialize an arbitrary Serializable to
 * a XML string or write it directly to a file.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public class XMLSerializer {
    /**
     * Serializes the given object to a xml string.
     *
     * @param object Serializable that should be serialized.
     * @return String containing the XML representation of the object instance.
     */
    public String serialize(Serializable object) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        encode(byteStream, object);
        return byteStream.toString();
    }

    /**
     * Serializes the given object to a xml file on mass storage.
     *
     * @param object Serializable that should be serialized.
     * @param file   File that should be written.
     */
    public void serialize(File file, Serializable object) {
        FileOutputStream fileStream = null;
        try {
            fileStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.error(this.getClass(), "Can't open file " + file.getAbsolutePath(), e);
            return;
        }

        encode(fileStream, object);
    }

    /**
     * Deserializes an object from a xml string representation created with the
     * corresponding serialize method of this class.
     *
     * @param encodedXML Encoded XML string.
     * @return Re-created object instance.
     */
    public Serializable deserialize(String encodedXML) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(encodedXML.getBytes());
        return decode(byteStream);
    }

    /**
     * Deserializes an object from a xml file representation created with the
     * corresponding serialize method of this class.
     *
     * @param encodedXML Encoded XML file.
     * @return Re-created object instance.
     */
    public Serializable deserialize(File xmlFile) {
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(xmlFile);
        } catch (FileNotFoundException e) {
            Log.error(this.getClass(), "Can't open file " + xmlFile.getAbsolutePath(), e);
            return null;
        }

        return decode(fileStream);
    }

    private void encode(OutputStream outputStream, Serializable object) {
        XMLEncoder encoder = null;
        encoder = new XMLEncoder(new BufferedOutputStream(outputStream));
        encoder.writeObject(object);
        encoder.close();
    }

    private Serializable decode(InputStream inputStream) {
        XMLDecoder decoder;
        decoder = new XMLDecoder(new BufferedInputStream(inputStream));
        Serializable temp = (Serializable) decoder.readObject();
        decoder.close();

        return temp;
    }
}
