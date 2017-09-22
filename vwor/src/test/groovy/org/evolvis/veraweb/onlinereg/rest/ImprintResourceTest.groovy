package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader
import spock.lang.Specification


class ImprintResourceTest extends Specification {

    def resource
    VworPropertiesReader vworPropertiesReader = Mock(VworPropertiesReader)
    Properties prop = Mock(Properties)

    def setup() {
        Set set = new HashSet<String>();
        set.add("imprint.fr_FR.content.3");
        set.add("imprint.fr_FR.heading.1");
        set.add("imprint.fr_FR.heading.2");
        set.add("imprint.fr_FR.content.2");
        set.add("imprint.es_ES.heading.1");
        set.add("imprint.es_ES.content.1");

        vworPropertiesReader.getProperties() >> prop
        prop.stringPropertyNames() >> set
        vworPropertiesReader.getProperty(_) >> "value"

        resource = new ImprintResource(vworPropertiesReader: vworPropertiesReader)
    }

    void testGetImprint() {
        when:
            def result = resource.getImprintList("fr_FR");

        then:
            assert result.size() == 3
            assert result.get("1").getHeading() == "value"
            assert result.get("1").getContent() == null
            assert result.get("2").getHeading() == "value"
            assert result.get("2").getContent() == "value"
            assert result.get("3").getHeading() == null
            assert result.get("3").getContent() == "value"
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
