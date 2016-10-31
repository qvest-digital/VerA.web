package org.evolvis.veraweb.onlinereg.auth;

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
