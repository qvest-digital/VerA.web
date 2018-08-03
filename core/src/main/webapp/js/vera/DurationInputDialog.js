/*
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017, 2018 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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
 * The namespace DurationInputDialog represents a widget
 * for input of arbitrary, valid durations.
 *
 * Usage is as follows:
 *
 * <pre>
 *  <link rel="stylesheet" type="text/css" href="${paths.staticWeb}styles/dialog.css">
 *  <script type="text/javascript" src="${paths.staticWeb}js/vera/DurationInputDialog.js"></script>
 *  <input name="myDurationInputFieldValue" id="myDurationInputFieldValue" type="hidden" value="P1Y4M"></input>
 *  <input name="myDurationInputFieldDisplay" id="myDurationInputFieldDisplay" type="text" readonly="readonly" value="1 Jahr 4 Monate"></input>
 *  <span><img src="/images/duration.gif"></span>
 *  <script type="text/javascript"><!--//--><![CDATA[//><!--
 *    DurationInputDialog.setup(
 *      {
 *        value : "myDurationInputFieldValue",
 *        display : "myDurationInputFieldDisplay",
 *        button : "myDurationInputFieldButton",
 *        title : "Aufbewahrungsfrist:"
 *      }
 *    );
 *  //--><!]]></script>
 * </pre>
 *
 * @author cklein
 * @see Duration.js
 */
var DurationInputDialog = {};

DurationInputDialog.instances = {};
DurationInputDialog.setup = function( spec )
{
	if ( ! DurationInputDialog.instances[ spec.button ] )
	{
		var dspelt = document.getElementById( spec.display );
		if ( ! dspelt )
		{
			//throw new "Display Element with id " + spec.inputField + " not found.";
		}
		var butelt = document.getElementById( spec.button );
		if ( ! butelt )
		{
			//throw new "Button Element with id " + spec.button + " not found.";
		}
		var valelt = document.getElementById( spec.value );
		if ( ! valelt )
		{
			//throw new "Value Element with id " + spec.value + " not found.";
		}
		DurationInputDialog.instances[ spec.button ] =
		{
			spec : spec,		// the dialog spec
			display : dspelt,	// the html input element used for display purposes
			button : butelt,	// the html span element representing a graphical image button
			value : valelt,		// the html input element for storing the serialized duration value
			isVisible : false,
			dialog : null		// the html element representing the dialog
		};
	}
	butelt.style.cursor = "default";
	DurationInputDialog.installHandlers( spec );
}

DurationInputDialog.destroy = function( spec )
{
	if ( DurationInputDialog.instances[ spec.button ] )
	{
		DurationInputDialog.removeHandlers( spec );
		delete DurationInputDialog.instances[ spec.button ];
	}
}

DurationInputDialog.removeHandlers = function( spec )
{
	var instance = DurationInputDialog.instances[ spec.button ];
	if ( instance )
	{
		instance.button.onclick = "";
	}
}

DurationInputDialog.installHandlers = function( spec )
{
	var instance = DurationInputDialog.instances[ spec.button ];
	if ( instance )
	{
		instance.button.onclick = DurationInputDialog.handlers.showButtonClickHandler;
	}
}

DurationInputDialog.handlers = {};
DurationInputDialog.handlers.showButtonClickHandler = function( event )
{
	event = window.event ? window.event : event;
	var target = ( event.target ? event.target : event.srcElement ).parentNode;

	var instance = DurationInputDialog.instances[ target.id ];
	if ( instance )
	{
		if ( instance.isVisible )
		{
			DurationInputDialog.hideModalInputDialog( instance );
		}
		else
		{
			DurationInputDialog.showModalInputDialog(
				instance,
				{
					width: 200,
					height: 100
				}
			);
		}
	}
}

DurationInputDialog.handlers.okButtonClickHandler = function( event )
{
	var elt = this;
	while( elt.className != "inputDialog" )
	{
		elt = elt.parentNode;
	}

	instance = elt.instance;
	var duration = new Duration();
	duration.years = instance.inputYears.value;
	duration.months = instance.inputMonths.value;
	duration.days = instance.inputDays.value;
	instance.value.value = duration.toString();
	instance.display.value = duration.toFormattedString();
	DurationInputDialog.hideModalInputDialog( instance );
}

DurationInputDialog.handlers.cancelButtonClickHandler = function( event )
{
	var elt = this;
	while( elt.className != "inputDialog" )
	{
		elt = elt.parentNode;
	}

	instance = elt.instance;
	DurationInputDialog.hideModalInputDialog( instance );
}

DurationInputDialog.showModalInputDialog = function( instance, windowSpec )
{
	var container = document.getElementById( "DurationInputDialogInstances" );
	if ( ! container )
	{
		container = document.createElement( "DIV" );
		container.id = "DurationInputDialogInstances";
		document.body.appendChild( container );
	}

	var dialog = instance.dialog;
	if ( ! dialog )
	{
		dialog = document.createElement( "DIV" );
		dialog.id = "DurationInputDialogInstance" + instance.spec.button;
		dialog.className = "inputDialog";
		dialog.style.visibility = "hidden";
		dialog.style.position = "absolute";
		dialog.style.zIndex = "1";

		var dialogBorder = document.createElement( "DIV" );
		dialogBorder.className = "dialogBorder";

		var titlebar = document.createElement( "DIV" );
		titlebar.className = "dialogTitlebar";

		var title = document.createElement( "DIV" );
		title.className = "dialogTitle";
		title.style.width = windowSpec.width + "px";

		var titleText = document.createTextNode( instance.spec.title );
		title.appendChild( titleText );
		titlebar.appendChild( title );
		dialogBorder.appendChild( titlebar );
		dialog.appendChild( dialogBorder );

		var dialogBody = document.createElement( "DIV" );
		dialogBody.className = "dialogBody";
		dialogBody.style.padding = "16px";
		dialogBody.style.height = ( windowSpec.height ) + "px";
		dialogBody.style.width = ( windowSpec.width - 30 ) + "px";

		var label = document.createElement( "LABEL" );
		var labelText = document.createTextNode( "Jahre" );
		var br = document.createElement( "BR" );
		br.style.clear = "both";
		label.setAttribute( "for", "durationInYears" );
		label.style.cssFloat = "left";

		var inputField = document.createElement( "INPUT" );
		inputField.type = "text";
		inputField.size = "5";
		inputField.style.cssFloat = "right";
		inputField.style.width = "50%";
		inputField.maxLength = "4";
		inputField.name = inputField.id = "durationInYears";
		dialogBody.appendChild( label );
		label.appendChild( labelText );
		dialogBody.appendChild( inputField );
		instance.inputYears = inputField;
		dialogBody.appendChild( br );
		br = br.cloneNode( true );
		label = label.cloneNode( false );
		label.setAttribute( "for", "durationInMonths" );
		labelText = labelText.cloneNode( false );
		labelText.data = "Monate";
		inputField = inputField.cloneNode( true );
		inputField.name = inputField.id = "durationInMonths";
		dialogBody.appendChild( label );
		label.appendChild( labelText );
		dialogBody.appendChild( inputField );
		instance.inputMonths = inputField;
		dialogBody.appendChild( br );
		br = br.cloneNode( true );
		label = label.cloneNode( false );
		label.setAttribute( "for", "durationInDays" );
		labelText = labelText.cloneNode( true );
		labelText.data = "Tage";
		inputField = inputField.cloneNode( true );
		inputField.name = inputField.id = "durationInDays";
		dialogBody.appendChild( label );
		label.appendChild( labelText );
		dialogBody.appendChild( inputField );
		instance.inputDays = inputField;

		dialogBorder.appendChild( dialogBody );

		var dialogFooter = document.createElement( "DIV" );
		dialogFooter.className = "dialogFooter";
		dialogFooter.style.width = windowSpec.width + "px";

		// TODO add ok / cancel buttons
		var cancelButton = document.createElement( "INPUT" );
		cancelButton.type = "button";
		cancelButton.name = "cancel";
		cancelButton.value = "Abbruch";
		cancelButton.style.width = "50%";
		cancelButton.onclick = DurationInputDialog.handlers.cancelButtonClickHandler;
		dialogFooter.appendChild( cancelButton );

		var okButton = document.createElement( "INPUT" );
		okButton.type = "button";
		okButton.name = "ok";
		okButton.value = "OK";
		okButton.style.width = "50%";
		okButton.onclick = DurationInputDialog.handlers.okButtonClickHandler;
		dialogFooter.appendChild( okButton );

		dialogBorder.appendChild( dialogFooter );
		container.appendChild( dialog );
	}

	var offsetLeft = DurationInputDialog.getOffsetLeftRecursively( instance.button );
	windowSpec.left = offsetLeft;
	if ( ( offsetLeft + dialog.offsetWidth ) > ( document.body.scrollLeft + document.body.clientWidth ) )
	{
		windowSpec.left -= dialog.offsetWidth;
	}

	var offsetTop = DurationInputDialog.getOffsetTopRecursively( instance.button );
	windowSpec.top = offsetTop;
	if ( ( offsetTop + dialog.offsetHeight ) > ( document.body.scrollTop + document.body.clientHeight ) )
	{
		windowSpec.top -= dialog.offsetHeight;
		windowSpec.top -= instance.button.firstChild.offsetHeight;
	}
	else
	{
		windowSpec.top += instance.button.firstChild.offsetHeight;
	}

	dialog.style.left = windowSpec.left + "px";
	dialog.style.top = windowSpec.top + "px";
	dialog.style.visibility = "visible";

	var duration = Duration.fromString( instance.value.value );
	instance.inputYears.value = duration.years;
	instance.inputMonths.value = duration.months;
	instance.inputDays.value = duration.days;

	instance.dialog = dialog;
	dialog.instance = instance;
	instance.isVisible = true;
}

DurationInputDialog.hideModalInputDialog = function( instance )
{
	if ( instance.dialog && instance.isVisible )
	{
		instance.isVisible = false;
		instance.dialog.style.visibility = "hidden";
	}
}

DurationInputDialog.getOffsetLeftRecursively = function( elt )
{
	var result = 0;
	while ( elt.tagName != "BODY" )
	{
		result += elt.offsetLeft;
		elt = elt.offsetParent;
	}
	return result;
}

DurationInputDialog.getOffsetTopRecursively = function( elt )
{
	var result = 0;
	while ( elt.tagName != "BODY" )
	{
		result += elt.offsetTop;
		elt = elt.offsetParent;
	}
	return result;
}
