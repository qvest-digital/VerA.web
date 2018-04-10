package de.tarent.commons.config;

import de.tarent.commons.utils.VersionInfo;
import de.tarent.commons.utils.VersionTool;

/**
 * This class was once the configuration system.
 *
 * @deprecated Remaining functionality should be moved to more
 * appropriate places.
 *
 * @author mikel
 */
public abstract class Config {
    /**
     * Einige Strings, die der Anzeige dienen.
     */
    public static String APPLICATION_CAPTION;
    public static String APPLICATION_VERSION;
    public static String APPLICATION_BUILD;

    static {

      VersionInfo vi = VersionTool.getInfoFromClass(Config.class);

      if(vi != null)
      {
	  APPLICATION_CAPTION = vi.getName("contact-client");

	  APPLICATION_VERSION = vi.getVersion("n/a");

	  APPLICATION_BUILD = vi.getBuildID("development build");
      }
    }

  // RSCHUS_TODO: Put this in a ConfigHelper class
  public static String getApplicationCaption() {

	  // if we are running a development-build show as many information as possible
	  if(APPLICATION_VERSION == null || APPLICATION_VERSION.equals("n/a") || APPLICATION_VERSION.toLowerCase().indexOf("snapshot") != -1)
		  return APPLICATION_CAPTION + " " + APPLICATION_VERSION + " (" + APPLICATION_BUILD + ")";

	  // if we are running on a final build, do not show build ID
	  return APPLICATION_CAPTION + " " + APPLICATION_VERSION;
    }

}
