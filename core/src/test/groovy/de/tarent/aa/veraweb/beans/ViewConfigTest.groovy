import de.tarent.aa.veraweb.beans.ViewConfig
import spock.lang.Specification

class ViewConfigTest extends Specification {

    Properties properties

    def setup() {
        properties = new Properties()
    }

    def 'if show internal id property is not given the default will be used'() {
        when: 'no properties are given at all'
        ViewConfig testObject = new ViewConfig(properties)

        then:
        testObject.SHOW_INTERNAL_ID == Boolean.FALSE
    }

    def 'if show veraweb id property is not given the default will be used'() {
        when: 'no properties are given at all'
        ViewConfig testObject = new ViewConfig(properties)

        then:
        testObject.SHOW_VERAWEB_ID == Boolean.TRUE
    }

    def 'if show veraweb id property is given this value will be used'() {
        given: 'property is given and is not equal to default'
        properties.put(ViewConfig.SHOW_VERWEB_ID_KEY, 'false')

        when: 'no properties are given at all'
        ViewConfig testObject = new ViewConfig(properties)

        then:
        testObject.SHOW_VERAWEB_ID == Boolean.FALSE
    }

    def 'if show internal id property is given this value will be used'() {
        given: 'property is given and is not equal to default'
        properties.put(ViewConfig.SHOW_INTERNAL_ID_KEY, 'true')

        when: 'no properties are given at all'
        ViewConfig testObject = new ViewConfig(properties)

        then:
        testObject.SHOW_VERAWEB_ID == Boolean.TRUE
    }
}