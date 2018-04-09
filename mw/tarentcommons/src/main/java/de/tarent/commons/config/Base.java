package de.tarent.commons.config;

import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.tarent.commons.utils.converter.StringToBoolean;
import de.tarent.commons.utils.converter.StringToLong;

/**
 * Provides base functionality for configuration item
 * retrieval and value conversion.
 *
 * <p>For a general introduction to the configuration system
 * refer to the respective document on the project's website.</p>
 *
 * <p>This class is not to be used outside the scope of
 * the configuration system. It exists solely to make
 * the code of its descendents shorter by unifying their
 * functionality.</p>
 *
 * <p><code>Base</code> is deeply tied to its inner class
 * {@link Base.Key}. Subclasses should therefore subclass the
 * {@link Base.Key} class, too and provide getter methods
 * in their <code>Base</code> implementation which
 * expect these <code>Base.Key</code> descendents as arguments.</p>
 *
 * <p>TODO: One day support for storing values as XML nodes should be
 * removed and depending program parts be updated.</p>
 *
 * @author Robert Schuster
 *
 */
class Base
{
  private HashMap values = new HashMap();

  private static final StringToBoolean STB = new StringToBoolean();
  private static final StringToLong STL = new StringToLong();

  /**
   * Sets a configuration value in the form of an XML node.
   *
   * <p>When the value is read from an XML document this is
   * the usual representation. As for now some parts of the
   * program still work on the raw XML value.</p>
   *
   * @param key
   * @param param
   */
  final void putParam(Key key, Node param)
  {
    values.put(key, param);
  }

  /**
   * Sets a configuration value in the form of a String.
   *
   * <p>When a value is generated at runtime (e.g. from user
   * input) you should use this variant because generating
   * the adequate XML value would be tedious.</p>
   *
   * @param key
   * @param param
   */
  final void putParam(Key key, String value)
  {
    values.put(key, value);
  }

  final Set getParamNames()
  {
    return values.keySet();
  }

  final protected Object getParamAsObject(Key key)
  {
    return values.get(key);
  }

  /**
   * Returns the <code>String</value> of a configuration key or the
   * given default value if the entry does not exist.
   *
   * <p>The method hides the fact whether values are stored
   * as XML nodes or plain strings.</p>
   *
   * @param key
   * @param defaultValue
   * @return
   */
  final protected String getParamValue(Key key, String defaultValue)
  {
    Object o = getParamAsObject(key);

    // The two variants of putParam() allows putting String values
    // into the map which should be returned without further interpretation.
    if (o instanceof String)
      return (String) o;

    if (o != null)
      return XmlUtil.getValue((Element) o);

    return defaultValue;
  }

  static boolean getAsBoolean(String value, boolean defaultValue)
  {
    if (value == null)
      return defaultValue;

    try
      {
        return ((Boolean) STB.doConversion(value)).booleanValue();
      }
    catch (NumberFormatException nfe)
      {
        throw new IllegalStateException("Invalid boolean value given: " + value);
      }
  }

  final protected boolean getParamAsBoolean(Key key, boolean defaultValue)
  {
    return getAsBoolean(getParamValue(key, null), defaultValue);
  }

  final protected int getParamAsInt(Key key, int defaultValue)
  {
    String value = getParamValue(key, null);

    if (value == null)
      return defaultValue;

    try
    {
      return Integer.parseInt(value);
    }
    catch (NumberFormatException nfe)
    {
      // RSCHUS_TODO: Log something
    }

    return defaultValue;
  }

  final protected long getParamAsLong(Key key, long defaultValue)
  {
	  String value = getParamValue(key, null);

	  if(value == null)
		  return defaultValue;

	  return ((Long)STL.doConversion(value)).longValue();
  }

  /** A basic implementation of a configuration key which
   * provides the semantics of a type-safe enumeration.
   *
   * <p>Subclasses are advised to provide a static <code>getInstance(String)</code>
   * method which returns a <code>Key</code> instance by its label.</p>
   *
   * <p>In case the existance of a <code>Key</code> instance is required
   * that method may throw a {@link KeyUnavailableException}.</p>
   *
   * <p>In case the key should not be fixed the <code>getInstance</code>
   * method may create an instance on-the-fly instead of throwing any
   * exception.</p>
   *
   * @author Robert Schuster
   *
   */
  protected abstract static class Key
  {
    private final String label;

    protected Key(String label)
    {
      if (label == null)
        throw new IllegalArgumentException("A null label is not allowed");

      this.label = label;
    }

    public final boolean equals(Object o)
    {
      return this == o;
    }

    public final int hashCode()
    {
      return label.hashCode();
    }

    public final String toString()
    {
      return label;
    }

  }

}
