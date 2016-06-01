package org.evolvis.veraweb.onlinereg.rest

import spock.lang.Specification

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class ForgotPasswordResourceTest extends Specification {

    def forgotPasswordResource

    public void setup() {
        forgotPasswordResource = new ForgotPasswordResource();
    }

    public void "request reset password link"() {
        when:
            def result = forgotPasswordResource.requestResetPasswordLink("tarentuser")

        then:
            assert result == "tarentuser"
    }

}
