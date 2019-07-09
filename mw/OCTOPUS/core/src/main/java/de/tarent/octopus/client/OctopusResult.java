package de.tarent.octopus.client;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Aufruf eines Task des Octopus.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface OctopusResult {

    public boolean hasStreamContent();

    public InputStream getContent();

    public String getContentType();

    public void writeContent(OutputStream to)
      throws IOException;

    public boolean hasMoreData();

    public Iterator getDataKeys();

    public Object getData(String key);

    public Object nextData();

    //     /**
    //      * @throws java.lang.ClassCastException
    //      */
    //     public Object nextDataAs(Class type);

    /**
     * @throws java.lang.ClassCastException
     */
    public String nextDataAsString();

    //     /**
    //      * @throws java.lang.ClassCastException
    //      */
    //     public int nextDataAsInt();

    //     /**
    //      * @throws java.lang.ClassCastException
    //      */
    //     public float nextDataAsFloat();

    //     /**
    //      * @throws java.lang.ClassCastException
    //      */
    //     public byte[] nextDataAsByteArray();
}
