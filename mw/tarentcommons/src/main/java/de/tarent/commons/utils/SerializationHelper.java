package de.tarent.commons.utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

/**
 * Helper class for all kind of Java serialization needs.
 *
 * @author Robert Schuster
 */
public class SerializationHelper {
    private SerializationHelper() {
        // Nothing to do.
    }

    /**
     * Deserializes the given byte array into an instance
     * of {@link List}.
     *
     * <p>For convenience an empty list is returned if the
     * argument is null.</p>
     *
     * @param serializedList
     * @throws IllegalStateException if something goes wrong.
     */
    public static List deserializeList(byte[] serializedList) {
        if (serializedList == null) {
            return Collections.EMPTY_LIST;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(serializedList);

        try {
            ObjectInputStream ois = new ObjectInputStream(bais);

            return (List) ois.readObject();
        } catch (IOException e) {
            throw (IllegalStateException) new IllegalStateException(
              "Unable to deserialize list").initCause(e);
        } catch (ClassNotFoundException e) {
            throw (IllegalStateException) new IllegalStateException(
              "Unable to deserialize list").initCause(e);
        }
    }

    /**
     * Serializes the given {@link List} instance into a byte array.
     *
     * @param l
     * @throws IllegalStateException if something goes wrong.
     */
    public static byte[] serializeList(List l) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(l);

            return baos.toByteArray();
        } catch (IOException e) {
            throw (IllegalStateException) new IllegalStateException(
              "Unable to serialize list").initCause(e);
        }
    }
}
