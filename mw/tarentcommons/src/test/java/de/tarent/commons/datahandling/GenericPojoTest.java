package de.tarent.commons.datahandling;
import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * TestCase for class GenericPojo
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public class GenericPojoTest extends TestCase {

    PersonI personDefault;
    PersonI personCustomStorage;
    PersonI personWithManager;

    protected void setUp() throws Exception {
        super.setUp();
        personDefault = (PersonI) GenericPojo.implementPojo(PersonI.class);
        personCustomStorage = (PersonI) GenericPojo.implementPojo(PersonI.class, new HashMapPojoStorage() {
            /** serialVersionUID */
            private static final long serialVersionUID = -6022561434143473039L;

            public Object get(Object key) {
                return key;
            }
        });
        personWithManager =
          (PersonI) GenericPojo.implementPojo(PersonI.class, new HashMapPojoStorage(), new GenericPojoManager() {
              public Object methodCalled(Object pojo, GenericPojo theGenericPojo, Method method, Object[] args) {
                  return args[0];
              }
          });
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSimpleValues() {
        personDefault.setName("Sebastian");
        assertEquals("Propertie matches", "Sebastian", personDefault.getName());

        personDefault.setAge(10);
        assertEquals("Propertie matches", 10, personDefault.getAge());

        personDefault.setCondition(true);
        assertEquals("Propertie matches", true, personDefault.isCondition());

        personDefault.setCondition(false);
        assertEquals("Propertie matches", false, personDefault.isCondition());

        personDefault.setA(42);
        assertEquals("Propertie matches", 42, personDefault.getA());
    }

    public void testNullForPrimitive() {
        try {
            personDefault.isCondition();
            fail("No Exception");
        } catch (NullPointerException npe) {
        }
    }

    public void testWrongType() {
        try {
            personCustomStorage.getA();
            fail("No Exception");
        } catch (ClassCastException npe) {
        }
    }

    public void testMethodCall() {
        assertEquals("method call response matches", "Test", personWithManager.echoMethod("Test"));
    }

    public void testCustomStorage() {
        assertEquals("custom storage works", "name", personCustomStorage.getName());
    }
}
