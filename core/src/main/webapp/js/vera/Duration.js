/*
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
<script type="text/javascript">
var d = new Duration();
alert( d.toString() );

d = Duration.fromString( "P14Y5M" );
alert( d.toString() );

alert( d.toFormattedString( "Et duurt %Y, %M un %d bis het suwig is." ) );
</script>
*/
