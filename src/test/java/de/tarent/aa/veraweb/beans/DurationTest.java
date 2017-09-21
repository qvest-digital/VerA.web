package de.tarent.aa.veraweb.beans;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import junit.framework.Assert;
import junit.framework.TestCase;

public class DurationTest extends TestCase {

	public void testToString() {
		Duration d = new Duration();
		Assert.assertEquals( "P0", d.toString() );
	}

	public void testDuration() {
		Duration d = new Duration();
		Assert.assertNotNull( d );
	}

	public void testFromString() {
		Duration d = Duration.fromString( "P1023Y4D" );
		System.out.println( d.toString() );
		Assert.assertEquals( "P1023Y4D", d.toString() );
		d = Duration.fromString( "P-12Y214M" );
		System.out.println( d.toString() );
		Assert.assertEquals( "P0", d.toString() );
		d = Duration.fromString( "P12Y214M" );
		System.out.println( d.toString() );
		Assert.assertEquals( "P12Y214M", d.toString() );
	}

	public void testToFormattedString() {
		Duration d = Duration.fromString( "P1023Y4D" );

		String t = d.toFormattedString( "%y%m%d" );
		System.out.println( t );
		Assert.assertEquals( "1023 Jahre 0 Monate 4 Tage", t );

		d = Duration.fromString( "P1023Y0M4D" );
		t = d.toFormattedString( "%Y%M%D" );
		System.out.println( t );
		Assert.assertEquals( "1023 Jahre 4 Tage", t );

		d = Duration.fromString( "P0Y0M4D" );
		t = d.toFormattedString( "%Y%M%D" );
		System.out.println( t );
		Assert.assertEquals( "4 Tage", t );

		d = Duration.fromString( "P1Y1M1D" );
		t = d.toFormattedString( "%Y%M%D" );
		System.out.println( t );
		Assert.assertEquals( "1 Jahr 1 Monat 1 Tag", t );

		d = Duration.fromString( "P1Y0M1D" );
		t = d.toFormattedString( "%Y%m%D" );
		System.out.println( t );
		Assert.assertEquals( "1 Jahr 0 Monate 1 Tag", t );

		d = Duration.fromString( "P1Y" );
		t = d.toFormattedString( "%Y%m%D" );
		System.out.println( t );
		Assert.assertEquals( "1 Jahr 0 Monate", t );
	}

}
