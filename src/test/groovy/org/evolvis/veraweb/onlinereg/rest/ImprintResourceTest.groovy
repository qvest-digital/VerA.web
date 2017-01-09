package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader
import spock.lang.Specification


class ImprintResourceTest extends Specification {

    def resource
    VworPropertiesReader vworPropertiesReader = Mock(VworPropertiesReader)
    Properties prop = Mock(Properties)

    def setup() {
        Set set = new HashSet<String>();
        set.add("imprint.fr_FR.asdf");
        set.add("imprint.fr_FR.qwert");
        set.add("imprint.es_ES.yxcvb");

        vworPropertiesReader.getProperties() >> prop
        prop.stringPropertyNames() >> set
        vworPropertiesReader.getProperty() >> "value"

        resource = new ImprintResource(vworPropertiesReader: vworPropertiesReader)
    }

    void testGetImprint() {
        when:
            def result = resource.getImprintList("fr_FR");

        then:
            assert result.size() == 2
    }

    void testGetImprintNull() {
        when:
            def result = resource.getImprintList(null);

        then:
            assert result == null
    }

    void testGetImprintEmpty() {
        when:
        def result = resource.getImprintList("");

        then:
        assert result == null
    }
}
