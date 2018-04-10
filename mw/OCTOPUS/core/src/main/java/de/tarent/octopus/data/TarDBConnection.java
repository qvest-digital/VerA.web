package de.tarent.octopus.data;
import java.util.Map;

/**
 * Enthält die Verbindungsinformationen zu einer Datenquelle
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TarDBConnection {
    public static final String ACCESS_WRAPPER_CLASS_NAME = "accessWrapperClassName";
    public static final String DEFAULT_ACCESS_WRAPPER_CLASS_NAME = "de.tarent.octopus.data.TcGenericDataAccessWrapper";
    Map params;
    private String schema = "";

    public TarDBConnection(Map params) {
        this.params = params;
        if(params.get("schema") != null){
            schema = params.get("schema").toString();
        }
    }

    public String get(String key) {
        return (String) params.get(key);
    }

    public String getAccessWrapperClassName() {
        String out = (String) params.get(ACCESS_WRAPPER_CLASS_NAME);
        if (out != null)
            return out;
        return DEFAULT_ACCESS_WRAPPER_CLASS_NAME;
    }
    /**
     * @return String
     */
    public String getSchema() {
        if(schema.equals(""))return "";
        else
      return schema+".";
    }
}
