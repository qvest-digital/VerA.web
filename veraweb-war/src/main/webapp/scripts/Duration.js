/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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