
/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2008 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/**
 * The namespace DurationInputDialog represents a widget
 * for input of arbitrary, valid durations.
 *
 * Usage is as follows:
 *
 * <pre>
 *  <link rel="stylesheet" type="text/css" href="${paths.staticWeb}styles/dialog.css">
 *  <script type="text/javascript" src="${paths.staticWeb}scripts/DurationInputDialog.js"></script>
 *  <input name="myDurationInputFieldValue" id="myDurationInputFieldValue" type="hidden" value="P1Y4M"></input>
 *  <input name="myDurationInputFieldDisplay" id="myDurationInputFieldDisplay" type="text" readonly="readonly" value="1 Jahr 4 Monate"></input>
 *  <span><img src="/images/duration.gif"></span>
 *  <script type="text/javascript">
 *    DurationInputDialog.setup(
 *      {
 *        value : "myDurationInputFieldValue",
 *        display : "myDurationInputFieldDisplay",
 *        button : "myDurationInputFieldButton",
 *        title : "Aufbewahrungsfrist:"
 *      }
 *    );
 *  </script>
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
					height: 140
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
		dialogBody.style.height = ( windowSpec.height - 30 ) + "px";
		dialogBody.style.width = ( windowSpec.width - 30 ) + "px";

		var label = document.createElement( "LABEL" );
		var labelText = document.createTextNode( "Jahre" );
		var br = document.createElement( "BR" );
		br.style.clear = "both";
		label.setAttribute( "for", "durationInYears" );
		label.style.width = "100px";
		var inputField = document.createElement( "INPUT" );
		inputField.type = "textbox";
		inputField.size = "5";
		inputField.style.float = "right";
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
		var okButton = document.createElement( "INPUT" );
		okButton.type = "button";
		okButton.name = "ok";
		okButton.value = "OK";
		okButton.style.width = "100px";
		okButton.onclick = DurationInputDialog.handlers.okButtonClickHandler;
		dialogFooter.appendChild( okButton );

		var cancelButton = document.createElement( "INPUT" );
		cancelButton.type = "button";
		cancelButton.name = "cancel";
		cancelButton.value = "Abbruch";
		cancelButton.style.width = "100px";
		cancelButton.onclick = DurationInputDialog.handlers.cancelButtonClickHandler;
		dialogFooter.appendChild( cancelButton );

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
