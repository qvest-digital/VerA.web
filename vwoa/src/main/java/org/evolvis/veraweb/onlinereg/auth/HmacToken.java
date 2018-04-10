package org.evolvis.veraweb.onlinereg.auth;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import org.apache.commons.codec.binary.Base64;
import org.evolvis.veraweb.onlinereg.utils.VerawebConstants;

import javax.crypto.Mac;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HmacToken {
    final private String osiamAccessToken;
    final private long timestamp;

    private final static byte[] key = new byte[16];

    /**
     * Layout is: MAC (20 Bytes) + timestamp (8 Bytes) + Osiam token (the rest)
     */
    private final byte[] token;

    static { // TODO: there should be a way to fix the signing key.
        new SecureRandom().nextBytes(key);
    }

    public HmacToken(String osiamAccessToken, long timestamp) {

        final Mac mac;
        mac = createMac();
        if (osiamAccessToken == null || osiamAccessToken.isEmpty()) {
            throw new IllegalArgumentException("Osiam Access Token must not be empty.");
        }
        this.osiamAccessToken = osiamAccessToken;
        this.timestamp = timestamp;
        byte[] osiamTokenBytes;
        try {
            osiamTokenBytes = osiamAccessToken.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        final int messageLength = osiamTokenBytes.length + VerawebConstants.LONG_BYTE;
        final int digestLength = mac.getMacLength();
        token = new byte[messageLength + digestLength];

        final ByteBuffer buf = ByteBuffer.wrap(token);
        buf.position(digestLength);
        buf.putLong(timestamp);
        buf.put(osiamTokenBytes);

        mac.update(token, digestLength, messageLength);
        try {
            mac.doFinal(token, 0);
        } catch (ShortBufferException | IllegalStateException e) {
            throw new RuntimeException(e);
        }

    }

    public HmacToken(byte[] token) throws InvalidTokenException {

        Mac mac;
        mac = createMac();

        this.token = token;
        final ByteBuffer buf = ByteBuffer.wrap(token);
        final int digestLength = mac.getMacLength();
        final int messageLength = token.length - digestLength;
        final int osiamTokenLength = messageLength - VerawebConstants.LONG_BYTE;
        if (osiamTokenLength <= 0) {
            throw new InvalidTokenException();
        }
        buf.position(digestLength);
        final byte[] osiamTokenBytes = new byte[osiamTokenLength];
        mac.update(token, digestLength, messageLength);
        final byte[] digest = mac.doFinal();

        // important: constant-time comparison, otherwise timing could be
        // used to guess key
        byte x = 0;
        for (int i = 0; i < digest.length; i++) {
            x |= digest[i] - token[i];
        }
        if (x != 0) {
            throw new InvalidTokenException();
        }
        timestamp = buf.getLong();
        buf.get(osiamTokenBytes);
        try {
            osiamAccessToken = new String(osiamTokenBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public HmacToken(String base64encodedToken) throws InvalidTokenException {
        this(Base64.decodeBase64(base64encodedToken));
    }

    private Mac createMac() {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            return mac;
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getOsiamAccessToken() {
        return osiamAccessToken;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String toString() {
        return Base64.encodeBase64String(token);
    }

}
