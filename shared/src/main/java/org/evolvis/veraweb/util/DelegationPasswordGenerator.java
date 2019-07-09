package org.evolvis.veraweb.util;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j2
public class DelegationPasswordGenerator {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public String generatePassword(final String eventName, final Date eventBegin, final String companyName) {
        logger.trace("generating password for {}", eventName);
        return extractFirstThreeChars(eventName) +
          extractFirstThreeChars(companyName) +
          DATE_FORMAT.format(eventBegin);
    }

    private static String extractFirstThreeChars(String value) {
        return value.substring(0, Math.min(value.length(), 3));
    }
}
