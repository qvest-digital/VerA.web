package de.tarent.commons.config;

/**
 * An exception subclass denoting that a key was requested that
 * does not exist.
 *
 * <p>This exception class can be used by {@Base.Key} implementations
 * that have a fixed set of keys (= constants) and want to flag the
 * request of an unexisting key as erroneous.</p>
 *
 * <p>This mechanism is used to make sure that ids or unique names used
 * in the XML documents match the labels given for the keys in the
 * application code.</p>
 *
 * @author Robert Schuster
 *
 */
class KeyUnavailableException extends Exception
{
  private String label;

  KeyUnavailableException(String label)
  {
    super();
    this.label = label;
  }

  String getKeyLabel()
  {
    return label;
  }

}
