package de.tarent.ldap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Resource für die Strings....
 *
 * @author philipp
 */
public class Messages {

    private static final String BUNDLE_NAME = "de.tarent.ldap.resources.ldap"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE =
      ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * Konstuktor
     */
    private Messages() {

    }

    /**
     * Methode, die die externen Strings holt
     *
     * @param key FIXME
     * @return Message
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
