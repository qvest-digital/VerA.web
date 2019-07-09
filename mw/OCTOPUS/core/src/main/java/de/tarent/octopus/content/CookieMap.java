package de.tarent.octopus.content;
/**
 * @author Jens Neumaier, tarent GmbH
 */
public interface CookieMap {

    /**
     * Prefix for the Map in which cookies will be stored
     **/
    public static final String PREFIX_COOKIE_MAP = "cookies";

    /**
     * New cookies will be stored in the octopus-content.
     * Each cookie is saved in another Map either as a Cookie-Object
     * in the field "cookie" that has to be assigned manually or
     * in the field "value" which will automatically be used
     * if a user preference is saved in the PersonalConfig.
     */
    public static final String COOKIE_MAP_FIELD_VALUE = "value";
    public static final String COOKIE_MAP_FIELD_COOKIE = "cookie";

    /**
     * Configuration settings for default cookie creation
     *
     * e.g. <param name="cookies.defaultMaxAge" value="5000000"/>
     */
    public static final String PREFIX_CONFIG_MAP = "cookies";
    public static final String CONFIG_MAXAGE = "defaultMaxAge";
}
