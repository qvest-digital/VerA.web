package de.tarent.octopus.response;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;

public class TcCsvResponseEngine implements TcResponseEngine {
    public void sendResponse(TcConfig tcConfig, TcResponse tcResponse, TcContent tcContent, TcResponseDescription desc,
            TcRequest tcRequest)
            throws ResponseProcessingException {
        tcResponse.setContentType("text/plain");

        // Daten fuer die Ausgabe holen
        Object data = tcContent.get(desc.getDescName());

        // OutputStream holen
        OutputStream os = tcResponse.getOutputStream();

        try {
            if (data instanceof Object[]) {
                generateCSV(os, Arrays.asList(((Object[]) data)));
            } else if (data instanceof List) {
                generateCSV(os, (List) data);
            } else {
                throw new ResponseProcessingException(
                        "Given data in response field " + desc.getDescName() + " has to be either List or Array.");
            }

            os.close();
        } catch (IOException e) {
            throw new ResponseProcessingException("Error processing CSV output.", e);
        }
    }

    private void generateCSV(OutputStream os, List processMe) throws ResponseProcessingException, IOException {
        // generate header
        Object firstEntry = processMe.get(0);
        if (firstEntry instanceof Map) {
            generateHeaderLine(os, (Map) firstEntry);
        }

        for (int i = 0; i < processMe.size(); i++) {
            generateCSVLine(os, processMe.get(i));
        }
    }

    private void generateHeaderLine(OutputStream os, Map input) throws IOException {
        Iterator iter = input.keySet().iterator();
        while (iter.hasNext()) {
            String thisValue = (String) iter.next();
            thisValue = thisValue.replaceAll("\"", "\"\"");
            os.write("\"".getBytes());
            os.write(thisValue.getBytes());
            os.write("\"".getBytes());
            if (iter.hasNext()) {
                os.write(";".getBytes());
            }
        }

        os.write("\r\n".getBytes());
    }

    private void generateCSVLine(OutputStream os, Object input) throws ResponseProcessingException, IOException {
        if (input instanceof Map) {
            generateCSVLine(os, new ArrayList(((Map) input).values()));
        } else if (input instanceof Object[]) {
            generateCSVLine(os, Arrays.asList((Object[]) input));
        } else if (input instanceof List) {
            generateCSVLine(os, (List) input);
        } else {
            throw new ResponseProcessingException(
                    "Given second level data in response field has to be either List, Map or Array.");
        }
    }

    private void generateCSVLine(OutputStream os, List input) throws IOException {
        for (int i = 0; i < input.size(); i++) {
            String thisValue = input.get(i) != null ? input.get(i).toString() : "";
            thisValue = thisValue.replaceAll("\"", "\"\"");
            os.write("\"".getBytes());
            os.write(thisValue.getBytes());
            os.write("\"".getBytes());
            if (i < input.size() - 1) {
                os.write(";".getBytes());
            }
        }

        os.write("\r\n".getBytes());
    }

    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
    }
}
