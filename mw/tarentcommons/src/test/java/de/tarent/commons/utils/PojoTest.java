package de.tarent.commons.utils;
import junit.framework.TestCase;

import java.lang.reflect.Method;

public class PojoTest extends TestCase {

    SimplePersonPojo pojo;
    SimplePersonPojoExt extPojo;

    protected void setUp() throws Exception {
        pojo = new SimplePersonPojo();
        pojo.setName("Mancke");
        pojo.setMale(true);
        pojo.setAge(26);

        extPojo = new SimplePersonPojoExt();
        extPojo.setName("Mancke");
        extPojo.setMale(true);
        extPojo.setAge(26);
    }

    protected void tearDown() throws Exception {
    }

    public void testGetter() {
        Method m = Pojo.getGetMethod(pojo, "name");
        assertEquals("Right value", "Mancke", Pojo.get(pojo, m));

        Method m2 = Pojo.getGetMethod(pojo, "nAmE");
        assertNull("No method for wrong case", m2);

        Method m3 = Pojo.getGetMethod(pojo, "nAmE", true);
        assertEquals("Right value", "Mancke", Pojo.get(pojo, m3));
    }

    public void testFastGetter() {
        assertEquals("Right value", "Mancke", Pojo.get(pojo, "name"));
        assertEquals("Right value", Boolean.TRUE, Pojo.get(pojo, "male"));
        assertEquals("Right value", new Integer(26), Pojo.get(pojo, "age"));
    }

    public void testFastGetterOnExtendedPojos() {
        assertEquals("Right value", "Mancke", Pojo.get(extPojo, "name"));
        assertEquals("Right value", Boolean.TRUE, Pojo.get(extPojo, "male"));
        assertEquals("Right value", new Integer(26), Pojo.get(extPojo, "age"));
    }

    public void testSetter() {
        Method m = Pojo.getSetMethod(pojo, "name");
        Pojo.set(pojo, m, "Meyer");
        assertEquals("Right value set", "Meyer", pojo.getName());

        Method m2 = Pojo.getSetMethod(pojo, "nAmE");
        assertNull("No method for wrong case", m2);

        Method m3 = Pojo.getSetMethod(pojo, "nAmE", true);
        assertEquals("Right setter method", m, m3);
    }

    public void testFastSetter() {
        Pojo.set(pojo, "name", "Meyer");
        Pojo.set(pojo, "male", Boolean.FALSE);
        Pojo.set(pojo, "age", new Integer(18));

        assertEquals("Right value set", "Meyer", pojo.getName());
        assertEquals("Right value set", false, pojo.isMale());
        assertEquals("Right value set", 18, pojo.getAge());
    }

    public void testWithConversion() {
        Pojo.set(pojo, "name", new AnyObject("Meyer"));
        Pojo.set(pojo, "male", "false");
        Pojo.set(pojo, "age", "18");
        assertEquals("Right value set", "Meyer", pojo.getName());
        assertEquals("Right value set", false, pojo.isMale());
        assertEquals("Right value set", 18, pojo.getAge());

        Pojo.set(pojo, "male", new Integer(0));
        Pojo.set(pojo, "age", new Float(18f));
        assertEquals("Right value set", false, pojo.isMale());
        assertEquals("Right value set", 18, pojo.getAge());

        Pojo.set(pojo, "male", null);
        Pojo.set(pojo, "age", null);
        assertEquals("Right value set", false, pojo.isMale());
        assertEquals("Right value set", 0, pojo.getAge());
    }

    public void testErrorHandlingWrongProperty()
      throws Exception {
        try {
            Pojo.set(pojo, "sdcsdcsd", null);
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new Exception("exception estimated");
    }

    public void testErrorHandlingMissingConverter()
      throws Exception {
        try {
            Pojo.set(pojo, "age", new AnyObject("23"));
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new Exception("exception estimated");
    }

    public void testErrorHandlingTargetException()
      throws Exception {
        try {
            Pojo.set(pojo, "ExceptionDummy", null);
        } catch (RuntimeException e) {
            return;
        }
        throw new Exception("exception estimated");
    }

    public class AnyObject {
        String value;

        public AnyObject(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    public class SimplePersonPojo {
        String name;
        int age;
        boolean male;

        public void setExeptionDummy(Object o) {
            throw new RuntimeException("Have fun");
        }

        public boolean isMale() {
            return male;
        }

        public void setMale(boolean newMale) {
            this.male = newMale;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int newAge) {
            this.age = newAge;
        }

        public String getName() {
            return name;
        }

        public void setName(String newName) {
            this.name = newName;
        }
    }

    public class SimplePersonPojoExt extends SimplePersonPojo {

    }
}
