/*
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * Simple duration datatype with rudimentary support for
 * w3c compatible duration types.
 *
 * This class does not support fractional durations and it
 * also does not support any operations other than to and
 * from string operations.
 *
 * It is mainly used for presentational purposes, so any
 * domain specific logic other than that used for presentation
 * is missing.
 *
 * @author cklein
 */
function Duration()
{
	this.years = 0;
	this.months = 0;
	this.days = 0;
}

Duration.fromString = function( sval )
{
	var regex = /^P([0-9]+Y)?([0-9]+M)?([0-9]+D)?$/;
	var result = new Duration();
	if ( regex.test( sval ) )
	{
		var amount = 0;
		var cc = 0;
		var nval = 0;
		for( var i = 1; i < sval.length; i++ )
		{
			cc = sval.charCodeAt( i );
			if ( cc >= 48 && cc <= 57 )
			{
				amount *= 10;
				amount += cc - 48;
			}
			else
			{
				nval = amount;
				amount = 0;
				switch( sval.charAt( i ) )
				{
					case 'Y' :
					{
						result.years = nval;
						break;
					}
					case 'M' :
					{
						result.months = nval;
						break;
					}
					case 'D' :
					{
						result.days = nval;
						break;
					}
				}
			}
		}
	}
	return result;
}

Duration.prototype.toString = function()
{
	var result = "P";
	if ( this.years != 0 )
	{
		result += this.years;
		result += 'Y';
	}
	if ( this.months != 0 )
	{
		result += this.months;
		result += 'M';
	}
	if ( this.days != 0 )
	{
		result += this.days;
		result += 'D';
	}
	// is this a zero length duration?
	if ( result.length == 1 )
	{
		result += '0';
	}
	return result;
}

Duration.prototype.toFormattedString = function( fmt )
{
	fmt = fmt || "%Y%M%D";
	var result = "";
	var c = 0;
	var val = 0;
	var s = "";
	for( var i = 0; i < fmt.length; i++ )
	{
		c = fmt.charAt( i );
		switch( c )
		{
			case '%' :
			{
				i++;
				c = fmt.charAt( i );
				switch( c )
				{
					case '%' :
					{
						result += "%";
						break;
					}
					case 'y' :
					case 'Y' :
					case 'm' :
					case 'M' :
					case 'd' :
					case 'D' :
					{
						if ( c == 'y' || c == 'Y' )
						{
							val = this.years;
							s = " Jahr";
						}
						else if ( c == 'm' || c == 'M' )
						{
							val = this.months;
							s = " Monat";
						}
						else
						{
							val = this.days;
							s = " Tag";
						}
						if ( val != 0 || c == 'y' || c == 'm' || c == 'd' )
						{
							if ( result.length > 1 && result.charAt( result.length - 1 ) != ' ' )
							{
								result += " ";
							}
							result += val;
							result += s;
							if ( val > 1 || val == 0 )
							{
								result += "e";
							}
						}
						break;
					}
					default :
					{
						// unsupported
						result += '%';
						result += c;
						break;
					}
				}
				break;
			}
			default :
			{
				result += c;
				break;
			}
		}
	}
	return result;
}

/*
Test Cases
<script type="text/javascript"><!--//--><![CDATA[//><!--
var d = new Duration();
alert( d.toString() );

d = Duration.fromString( "P14Y5M" );
alert( d.toString() );

alert( d.toFormattedString( "Et duurt %Y, %M un %d bis het suwig is." ) );
//--><!]]></script>
*/
