package de.tarent.octopus.data;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
