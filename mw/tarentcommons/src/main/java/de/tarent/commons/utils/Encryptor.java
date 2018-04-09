/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import sun.misc.BASE64Encoder;

/**
 * Singleton for encryption of string values (e.g. passwords)
 * Currently, always uses MD5 
 * @author pralph
 *
 */
public final class Encryptor {
    private static Encryptor instance = null;
    
    /** MD5 encrpytion algorithm */
    public static final String ALGORITHM_MD5 = "MD5";

    /** ASCII (7-bit) encoding charset, a.k.a. ISO646-US (Basic Latin block of the Unicode character set) */
    public static final String CHAR_SET_ASCII = "US-ASCII";
    
    /** ISO-8859-1 (8-bit) encoding charset, a.k.a. ISO-LATIN-1 (Western European Latin-1 block of the Unicode character set) */
    public static final String CHAR_SET_ISO_8859_1 = "ISO-8859-1";
    
    /** UCS Transformation Format encoding charset, 8-bit */
    public static final String CHAR_SET_UTF8 = "UTF-8";
    
    /** UCS Transformation Format encoding charset, 16-bit, byte order identified by an optional byte-order mark  */
    public static final String CHAR_SET_UTF16 = "UTF-16";
    
    /** UCS Transformation Format encoding charset, 16-bit, big-endian byte order  */
    public static final String CHAR_SET_UTF16BE = "UTF-16BE";
    
    /** UCS Transformation Format encoding charset, 16-bit, little-endian byte order  */
    public static final String CHAR_SET_UTF16LE = "UTF-16LE";

    /**
     * Constructor. USE THE getInstance() METHOD
     */
    protected Encryptor() {
    }
    
    /**
     * Get an instance of the Encryptor class
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
