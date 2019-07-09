package de.tarent.aa.veraweb.beans;
import junit.framework.Assert;
import junit.framework.TestCase;

public class DurationTest extends TestCase {

    public void testToString() {
        Duration d = new Duration();
        Assert.assertEquals("P0", d.toString());
    }

    public void testDuration() {
        Duration d = new Duration();
        Assert.assertNotNull(d);
    }

    public void testFromString() {
        Duration d = Duration.fromString("P1023Y4D");
        System.out.println(d.toString());
        Assert.assertEquals("P1023Y4D", d.toString());
        d = Duration.fromString("P-12Y214M");
        System.out.println(d.toString());
        Assert.assertEquals("P0", d.toString());
        d = Duration.fromString("P12Y214M");
        System.out.println(d.toString());
        Assert.assertEquals("P12Y214M", d.toString());
    }

    public void testToFormattedString() {
        Duration d = Duration.fromString("P1023Y4D");

        String t = d.toFormattedString("%y%m%d");
        System.out.println(t);
        Assert.assertEquals("1023 Jahre 0 Monate 4 Tage", t);

        d = Duration.fromString("P1023Y0M4D");
        t = d.toFormattedString("%Y%M%D");
        System.out.println(t);
        Assert.assertEquals("1023 Jahre 4 Tage", t);

        d = Duration.fromString("P0Y0M4D");
        t = d.toFormattedString("%Y%M%D");
        System.out.println(t);
        Assert.assertEquals("4 Tage", t);

        d = Duration.fromString("P1Y1M1D");
        t = d.toFormattedString("%Y%M%D");
        System.out.println(t);
        Assert.assertEquals("1 Jahr 1 Monat 1 Tag", t);

        d = Duration.fromString("P1Y0M1D");
        t = d.toFormattedString("%Y%m%D");
        System.out.println(t);
        Assert.assertEquals("1 Jahr 0 Monate 1 Tag", t);

        d = Duration.fromString("P1Y");
        t = d.toFormattedString("%Y%m%D");
        System.out.println(t);
        Assert.assertEquals("1 Jahr 0 Monate", t);
    }
}
