package de.tarent.commons.datahandling;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests for {@link BeanMapTransformator}.
 *
 * @author Tim Steffens
 */
public class BeanMapTranformatorTest extends TestCase {

    BeanMapTransformator beanMapTransformator;

    /**
     * @param arg0
     */
    public BeanMapTranformatorTest(String arg0) {
        super(arg0);
        this.beanMapTransformator = new BeanMapTransformator();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link de.tarent.commons.datahandling.BeanMapTransformator#transformBeanToMap(java.lang.Object)}.
     */
    public void testTransformBeanToMapObject() {
        Object bean = new ConcreteBeanMap();
        assertEquals("anAttributeValue", this.beanMapTransformator.transformBeanToMap(bean).get("anAttribute"));
    }

    /**
     * Test method for
     * {@link de.tarent.commons.datahandling.BeanMapTransformator#transformMapToBean(java.util.Map, java.lang.Class)}.
     */
    public void testTransformMapToBeanMapClass() {
        Map map = new HashMap();
        map.put("anAttribute", "aNewAttributeValue");
        ConcreteBeanMap bean = (ConcreteBeanMap) this.beanMapTransformator.transformMapToBean(map, ConcreteBeanMap.class);
        assertEquals("aNewAttributeValue", bean.getAnAttribute());
    }

    /**
     * Test method for
     * {@link de.tarent.commons.datahandling.BeanMapTransformator#transformBeanToMap(java.lang.Object, java.util.Map)}.
     */
    public void testTransformBeanToMapObjectMap() {
        Object bean = new ConcreteBeanMap();
        Map map = new HashMap();
        this.beanMapTransformator.transformBeanToMap(bean, map);
        assertEquals("anAttributeValue", map.get("anAttribute"));
    }

    /**
     * Test method for
     * {@link de.tarent.commons.datahandling.BeanMapTransformator#transformMapToBean(java.util.Map, java.lang.Object)}.
     */
    public void testTransformMapToBeanMapObject() {
        Map map = new HashMap();
        map.put("anAttribute", "aNewAttributeValue");
        ConcreteBeanMap bean = new ConcreteBeanMap();
        this.beanMapTransformator.transformMapToBean(map, bean);
        assertEquals("aNewAttributeValue", bean.getAnAttribute());
    }
}
