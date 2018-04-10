package de.tarent.commons.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class Config
{
    private static Configuration config = null;

    public static void parse(String configurationFile)
    {
        try
        {
            config = new XMLConfiguration(configurationFile);
        }
        catch (ConfigurationException e)
        {
            throw new RuntimeException("Can't parse configuration file " + configurationFile);
        }
    }

    public static Configuration getConfig()
    {
        return config;
    }
}
