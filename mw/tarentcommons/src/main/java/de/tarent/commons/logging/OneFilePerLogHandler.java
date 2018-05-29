package de.tarent.commons.logging;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class OneFilePerLogHandler extends Handler {
    protected LogFormatter formatter = new LogFormatter();
    protected String path;
    protected String encoding;

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        if (encoding != null && encoding.length() != 0) {
            return encoding;
        } else {
            return "UTF-8";
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        if (path != null && path.length() != 0) {
            return path;
        } else {
            return System.getProperty("user.home");
        }
    }

    protected String getFilePrefix() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS_").format(new Date());
    }

    protected String getPlainMessage(String message) {
        StringBuffer buffer = new StringBuffer(message.length());

        char c[] = message.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if ("-_.,".indexOf(c[i]) != -1 ||
              (c[i] >= 'a' && c[i] <= 'z') ||
              (c[i] >= 'A' && c[i] <= 'Z') ||
              (c[i] >= '0' && c[i] <= '9')) {
                buffer.append(c[i]);
            } else if (",;:@$~".indexOf(c[i]) != -1) {
                buffer.append('.');
            } else if (" ".indexOf(c[i]) != -1) {
                buffer.append('_');
            } else if ("\r\n".indexOf(c[i]) != -1) {
                break;
            }
        }
        return buffer.toString();
    }

    protected File getNonExistingFile(String message) {
        String filename = getFilePrefix() + getPlainMessage(message);
        if (filename.length() > 220) {
            filename = filename.substring(0, 220);
        }
        File logfile = new File(getPath(), filename + ".log");
        for (int i = 1; logfile.exists(); i++) {
            logfile = new File(getPath(), filename + "." + i + ".log");
        }
        if (!logfile.getParentFile().exists()) {
            logfile.getParentFile().mkdirs();
        }
        return logfile;
    }

    protected Writer getWriter(String message) throws IOException {
        OutputStream out = null;
        try {
            File file = getNonExistingFile(message);
            out = new FileOutputStream(file);
            out = new BufferedOutputStream(out);
            return new OutputStreamWriter(out, getEncoding());
        } catch (IOException e) {
            if (out != null) {
                out.close();
            }
            throw e;
        }
    }

    public void publish(LogRecord record) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(getWriter(record.getMessage()));

            formatter.format(writer, record);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void flush() {
        // Nothing todo here.
    }

    public void close() throws SecurityException {
        // Nothing todo here.
    }
}
