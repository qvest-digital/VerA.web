package de.tarent.commons.utils;

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
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;

/**
 * This class provides common checksum-methods based on the MD5-Algorithm
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 */
public class ChecksumTool {
    private static Log logger = LogFactory.getLog(ChecksumTool.class);

    /**
     * Determines whether the given file and the given checksum are equal
     *
     * @param pFileName The filename of the file to checksum
     * @param pChecksum The MD5-checksum to compare with
     * @return true if equal
     */

    public static boolean validateFile(String pFileName, String pChecksum) {
        return pChecksum.equals(createChecksum(pFileName));
    }

    /**
     * Returns the MD5-checksum of the given file as a <code>String</code>
     *
     * @param pFileName The file to checksum
     * @return The MD5-checksum of the file as a <code>String</code> or null if file not found
     */

    public static String createChecksum(String pFileName) {
        String checksumString = null;

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            if (logger.isWarnEnabled()) {
                logger.warn(e.toString());
            }
            return null;
        }

        FileInputStream fis;
        try {
            fis = new FileInputStream(pFileName);
        } catch (FileNotFoundException e) {
            return null;
        }

        DigestInputStream dis = new DigestInputStream(fis, md);

        // The dis.read method reads one byte of fis and updates the
        // MessageDigest. The following code reads the whole file.
        try {
            while (dis.read() != -1) {
                ;
            }
            dis.close();
        } catch (IOException e) {
            if (logger.isWarnEnabled()) {
                logger.warn(e.toString());
            }
            return null;
        }

        // Now we can compute the checksum.
        byte[] digest = md.digest();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            String s = Integer.toHexString(digest[i] & 0xff);
            s = (s.length() == 1) ? "0" + s : s;
            buffer.append(s);
        }
        checksumString = buffer.toString();

        return checksumString;
    }
}
