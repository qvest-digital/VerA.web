/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

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
 *
 */
public class SerializationHelper
{
  private SerializationHelper()
  {
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
   * @return
   * @throws IllegalStateException if something goes wrong.
   *
   */
  public static List deserializeList(byte[] serializedList)
  {
    if (serializedList == null)
      return Collections.EMPTY_LIST;

    ByteArrayInputStream bais = new ByteArrayInputStream(serializedList);

    try
      {
        ObjectInputStream ois = new ObjectInputStream(bais);

        return (List) ois.readObject();
      }
    catch (IOException e)
      {
        throw (IllegalStateException) new IllegalStateException(
                                                                "Unable to deserialize list").initCause(e);
      }
    catch (ClassNotFoundException e)
      {
        throw (IllegalStateException) new IllegalStateException(
                                                                "Unable to deserialize list").initCause(e);
      }
  }

  /**
   * Serializes the given {@link List} instance into a byte array.
   *
   * @param l
   * @return
   * @throws IllegalStateException if something goes wrong.
   */
  public static byte[] serializeList(List l)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try
      {
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(l);

        return baos.toByteArray();
      }
    catch (IOException e)
      {
        throw (IllegalStateException) new IllegalStateException(
                                                                "Unable to serialize list").initCause(e);

      }
  }

}
