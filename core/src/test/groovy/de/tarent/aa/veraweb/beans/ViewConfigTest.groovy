import de.tarent.aa.veraweb.beans.ViewConfig
import spock.lang.Specification

class ViewConfigTest extends Specification {
    Properties properties

    def setup() {
        properties = new Properties()
    }

    def 'if show global open tab property is not given the default will be used'() {
        when: 'no properties are given at all'
        ViewConfig testObject = new ViewConfig(properties)

        then:
        testObject.showGlobalOpenTab == Boolean.FALSE
    }

    def 'if show open tab property is given this value will be used'() {
        given: 'property is given and is not equal to default'
        properties.put(ViewConfig.SHOW_GLOBAL_OPENTAB_KEY, 'true')

        when: 'no properties are given at all'
        ViewConfig testObject = new ViewConfig(properties)

        then:
        testObject.showGlobalOpenTab == Boolean.TRUE
    }
}
