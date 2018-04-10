package de.tarent.commons.config;

import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tarent.commons.config.ConfigManager.Scope;

public final class ConnectionDefinition extends Base
{
  /** The scope in which this ConnectionDefinition was defined in.
   *
   * This property allows various evaluating various access rights
   * conditions (e.g. normal user may not modify site or installation scope
   * definitions).
   */
  Scope scope;

  public ConnectionDefinition(String label, String serverURL, String module)
  {
    putParam(Key.LABEL, label);
    putParam(Key.SERVER_URL, serverURL);
    putParam(Key.OCTOPUS_MODULE, module);
  }

  ConnectionDefinition(Scope scope, Node node) throws KeyUnavailableException
  {
	this.scope = scope;

    NodeList list = node.getChildNodes();
    int size = list.getLength();

    for (int i = 0; i < size; i++)
      {
        Node subNode = list.item(i);
        String nodeName = subNode.getNodeName();

        if (nodeName == null)
          continue;
        else if (nodeName.equals("param"))
          {
            NamedNodeMap attr = subNode.getAttributes();
            Node name = attr.getNamedItem("name");
            Key key = Key.getInstance(name.getNodeValue());
            putParam(key, subNode);
          }
      }

  }

  public String get(Key key)
  {
    return getParamValue(key, null);
  }

  public String toString()
  {
    return get(Key.LABEL);
  }

  /**
   * This class contains keys for the connection parameters. TODO: Enhance this
   * to allow existance checks at parse time or the ability to uncover
   * superfluous parameters.
   */
  public static final class Key extends Base.Key
  {
    private static HashMap instances = new HashMap();

    //

    public static final Key SERVER_URL = make("serverURL");

    public static final Key OCTOPUS_MODULE = make("octopusModule");

    public static final Key LABEL = make("label");

    //

    private Key(String label)
    {
      super(label);
      instances.put(label, this);
    }

    /**
     * Creates a new instance.
     * <p>
     * Use this instead of the constructor in light of future additions.
     * </p>
     * @param label
     * @return
     */
    private static Key make(String label)
    {
      return new Key(label);
    }

    /**
     * Returns an instance of this class or throws a {@KeyUnavailableException}
     * if it does not exist.
     * @param label
     * @return
     * @throws KeyUnavailableException
     *           if the key does not exist.
     */
    private static Key getInstance(String label) throws KeyUnavailableException
    {
      Key k = (Key) instances.get(label);

      if (k == null)
        throw new KeyUnavailableException(label);

      return k;
    }

  }

}
