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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

import sun.misc.BASE64Encoder;

/**
 * Singleton for encryption of string values (e.g. passwords)
 * Currently, always uses MD5
 *
 * @author pralph
 */
public final class Encryptor {
    private static Encryptor instance = null;

    /**
     * MD5 encrpytion algorithm
     */
    public static final String ALGORITHM_MD5 = "MD5";

    /**
     * ASCII (7-bit) encoding charset, a.k.a. ISO646-US (Basic Latin block of the Unicode character set)
     */
    public static final String CHAR_SET_ASCII = "US-ASCII";

    /**
     * ISO-8859-1 (8-bit) encoding charset, a.k.a. ISO-LATIN-1 (Western European Latin-1 block of the Unicode character set)
     */
    public static final String CHAR_SET_ISO_8859_1 = "ISO-8859-1";

    /**
     * UCS Transformation Format encoding charset, 8-bit
     */
    public static final String CHAR_SET_UTF8 = "UTF-8";

    /**
     * UCS Transformation Format encoding charset, 16-bit, byte order identified by an optional byte-order mark
     */
    public static final String CHAR_SET_UTF16 = "UTF-16";

    /**
     * UCS Transformation Format encoding charset, 16-bit, big-endian byte order
     */
    public static final String CHAR_SET_UTF16BE = "UTF-16BE";

    /**
     * UCS Transformation Format encoding charset, 16-bit, little-endian byte order
     */
    public static final String CHAR_SET_UTF16LE = "UTF-16LE";

    /**
     * Constructor. USE THE getInstance() METHOD
     */
    protected Encryptor() {
    }

    /**
     * Get an instance of the Encryptor class
     *
     * @return
     */
    public static synchronized Encryptor getInstance() {
        if (instance == null) {
            instance = new Encryptor();
        }
        return instance;
    }

    /**
     * Returns an MD5 hash of the (plain text) input string
     *
     * @param input
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public synchronized String encrypt(String input)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String hash = null;
        MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
        md.update(input.getBytes(CHAR_SET_UTF8));

        byte raw[] = md.digest();
        hash = (new BASE64Encoder()).encode(raw);
        return hash;
    }

}
