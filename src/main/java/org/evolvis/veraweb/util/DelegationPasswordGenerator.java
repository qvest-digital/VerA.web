package org.evolvis.veraweb.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DelegationPasswordGenerator {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public String generatePassword(String eventName, Date eventBegin, String companyName) {
        return generateDelegationPassword(eventName, eventBegin, companyName);
    }
    private static String generateDelegationPassword(final String shortName, final Date begin, String companyName) {
        final StringBuilder passwordBuilder = new StringBuilder();
        final SimpleDateFormat dateFormat = DATE_FORMAT;
        passwordBuilder.append(extractFirstXChars(shortName, 3));
        passwordBuilder.append(extractFirstXChars(companyName, 3));
        passwordBuilder.append(dateFormat.format(begin));
        return passwordBuilder.toString();
    }

    private static String extractFirstXChars(String value, int x) {
        return value.substring(0, Math.min(value.length(), x));
    }

}
