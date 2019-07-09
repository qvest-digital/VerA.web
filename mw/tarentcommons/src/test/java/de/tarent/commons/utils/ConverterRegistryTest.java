package de.tarent.commons.utils;
import junit.framework.TestCase;

public class ConverterRegistryTest extends TestCase {

    Converter testConverter = new AbstractConverter() {
        public String getConverterName() {
            return "TestConverter";
        }

        public Object doConversion(Object sourceData) throws Exception {
            return "Just a test";
        }
    };

    protected void setUp() throws Exception {
        ConverterRegistry.getDefaultRegistry()
          .register(testConverter, false);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testConverterLookup() {
        assertEquals("Right converter",
          ConverterRegistry.getDefaultRegistry().getConverter(String.class, Boolean.class).getConverterName(),
          "StringToBoolean");
        assertEquals("Right converter",
          ConverterRegistry.getDefaultRegistry().getConverter(Boolean.class, String.class).getConverterName(),
          "BooleanToString");
        assertEquals("Right converter",
          ConverterRegistry.getDefaultRegistry().getConverter(Integer.class, String.class).getConverterName(),
          "ObjectToString");
        assertEquals("Right converter", testConverter, ConverterRegistry.getDefaultRegistry().getConverter("TestConverter"));
    }

    public void testSimpleConverions() {
        assertEquals("Right conversion", Boolean.TRUE, ConverterRegistry.convert(new Integer(1), Boolean.class));
        assertEquals("Right conversion", "Just a test", ConverterRegistry.convert("xxx", "TestConverter"));
    }

    public void testPrimitiveConverions() {
        assertEquals("Right conversion", Boolean.TRUE, ConverterRegistry.convert(new Integer(1), Boolean.TYPE));
    }

    public void testNullConverions() {
        assertEquals("Primitive conversion with null", Boolean.FALSE, ConverterRegistry.convert(null, Boolean.TYPE));
        assertEquals("Complex conversion with null", null, ConverterRegistry.convert(null, Boolean.class));
    }

    public void testErrorHandling()
      throws Exception {
        try {
            ConverterRegistry.convert(this, Boolean.class);
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new Exception("exception estimated");
    }

    public void testErrorHandling2()
      throws Exception {
        try {
            ConverterRegistry.convert("XXX", Integer.class);
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new Exception("exception estimated");
    }
}
