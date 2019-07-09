package de.tarent.aa.veraweb.utils;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelperTest extends TestCase {
    public void testEmptyOrder() {
        assertNull(DatabaseHelper.getOrder(null));
        assertNull(DatabaseHelper.getOrder(new ArrayList()));
    }

    public void testSingleOrder() {
        List order = new ArrayList();
        order.add("column1");
        assertEquals(" ORDER BY column1 ASC", DatabaseHelper.getOrder(order).clauseToString());

        order.add("DESC");
        assertEquals(" ORDER BY column1 DESC", DatabaseHelper.getOrder(order).clauseToString());

        order.set(1, "ASC");
        assertEquals(" ORDER BY column1 ASC", DatabaseHelper.getOrder(order).clauseToString());

        order.remove(1);
        assertEquals(" ORDER BY column1 ASC", DatabaseHelper.getOrder(order).clauseToString());
    }

    public void testMultiOrder() {
        List order = new ArrayList();
        order.add("column1");
        order.add("column2");
        order.add("DESC");

        assertEquals(" ORDER BY column1 ASC, column2 DESC", DatabaseHelper.getOrder(order).clauseToString());
    }
}
