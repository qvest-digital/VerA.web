package org.evolvis.veraweb.onlinereg.mail;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class MailDispatcherTest {

    @Test@Ignore
    public void testSend() throws Exception {
        final EmailConfiguration emailConfiguration = new EmailConfiguration("localhost", 2525, null, null, null, "noreply@tarent.de", "Subject mit Umlat ü", "Plain text content mit Umlaut ü", "HTML");
        final MailDispatcher mailDispatcher = new MailDispatcher(emailConfiguration);
        mailDispatcher.sendVerificationEmail("from", "to@fa.ke", "Subject mit Umlaut ä", "Plain text content mit Umlaut ä", "http://link.de", emailConfiguration.getContentType());
    }
}