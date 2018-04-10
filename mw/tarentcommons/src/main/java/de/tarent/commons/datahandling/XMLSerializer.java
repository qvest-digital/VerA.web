/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 02.12.2005
 */

package de.tarent.commons.datahandling;

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
public class XMLSerializer
{
    /**
     * Serializes the given object to a xml string.
     *
     * @param object Serializable that should be serialized.
     * @return String containing the XML representation of the object instance.
     */
    public String serialize(Serializable object)
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        encode(byteStream, object);
        return byteStream.toString();
    }

    /**
     * Serializes the given object to a xml file on mass storage.
     *
     * @param object Serializable that should be serialized.
     * @param file File that should be written.
     */
    public void serialize(File file, Serializable object)
    {
        FileOutputStream fileStream = null;
        try
        {
            fileStream = new FileOutputStream(file);
        }
        catch (FileNotFoundException e)
        {
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
    public Serializable deserialize(String encodedXML)
    {
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
    public Serializable deserialize(File xmlFile)
    {
        FileInputStream fileStream = null;
        try
        {
            fileStream = new FileInputStream(xmlFile);
        }
        catch (FileNotFoundException e)
        {
            Log.error(this.getClass(), "Can't open file " + xmlFile.getAbsolutePath(), e);
            return null;
        }

        return decode(fileStream);
    }

    private void encode(OutputStream outputStream, Serializable object)
    {
        XMLEncoder encoder = null;
        encoder = new XMLEncoder(new BufferedOutputStream(outputStream));
        encoder.writeObject(object);
        encoder.close();
    }

    private Serializable decode(InputStream inputStream)
    {
        XMLDecoder decoder;
        decoder = new XMLDecoder(new BufferedInputStream(inputStream));
        Serializable temp = (Serializable)decoder.readObject();
        decoder.close();

        return temp;
    }
}
