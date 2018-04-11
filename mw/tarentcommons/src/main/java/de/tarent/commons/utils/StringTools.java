package de.tarent.commons.utils;

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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Contains methods for string processing.
 *
 * @author tim
 */
public class StringTools {

    /**
     * OS-specific character sequence for line break
     */
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Returns {@code s} with the first letter in upper case
     *
     * @param s the string to be processed
     * @return {@code s} with the first letter in upper case
     */
    public static String capitalizeFirstLetter(String s) {
        if (s == null) {
            return null;
        } else if (s.length() == 0) {
            return s;
        } else {
            return Character.toUpperCase(s.charAt(0)) +
              s.substring(1);
        }
    }

    /**
     * Returns {@code s} with the first letter in lower case
     *
     * @param s the string to be processed
     * @return {@code s} with the first letter in lower case
     */
    public static String minusculizeFirstLetter(String s) {
        if (s == null) {
            return null;
        } else if (s.length() == 0) {
            return s;
        } else {
            return Character.toLowerCase(s.charAt(0)) +
              s.substring(1);
        }
    }

    /**
     * Calculates the digest from input
     *
     * @param input        String to be hashed
     * @param hashfunction Hash-Function to use
     * @return hashed version of input
     * @throws NoSuchAlgorithmException
     */
    public static String digest(String input, String hashfunction) throws NoSuchAlgorithmException {
        String md5 = null;
        if (input == null) {
            return null;
        }
        Charset cs = Charset.forName("UTF8"); //$NON-NLS-1$
        ByteBuffer bb = cs.encode(CharBuffer.wrap(input));
        MessageDigest md = MessageDigest.getInstance(hashfunction); //$NON-NLS-1$
        md.update(bb.array());
        byte[] digest = md.digest();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            buffer.append(Integer.toHexString(digest[i] & 0xff));
        }
        md5 = buffer.toString();
        return md5;
    }

    /**
     * Calculates the md5-hash of input
     *
     * @param input String to be hashed
     * @return MD5-Hash from input
     * @throws NoSuchAlgorithmException
     */
    public static String md5(String input) throws NoSuchAlgorithmException {
        return digest(input, "MD5");
    }
}
