
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
			var offsetLeft = DurationInputDialog.getOffsetLeftRecursively( target );
			if ( ( offsetLeft + 200 ) > ( document.body.scrollLeft + document.body.scrollWidth ) )
			{
				offsetLeft -= 200;
			}
			var offsetTop = DurationInputDialog.getOffsetTopRecursively( target );
			if ( ( offsetTop + 200 ) > ( document.body.scrollTop + document.body.scrollHeight ) )
			{
				offsetTop -= 250;
			}
			DurationInputDialog.showModalInputDialog(
				instance,
				{
					left : offsetLeft,
					top : offsetTop,
					width: 200,
					height: 140
				}
			);
		}
	}
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
		dialog.style.display = "none";
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
		dialogBody.style.height = windowSpec.height + "px";
		dialogBody.style.width = windowSpec.width + "px";

		// TODO add input fields

		dialogBorder.appendChild( dialogBody );

		var dialogFooter = document.createElement( "DIV" );
		dialogFooter.className = "dialogFooter";
		dialogFooter.style.width = windowSpec.width + "px";

		// TODO add ok / cancel buttons
		var okButton = document.createElement( "INPUT" );
		okButton.type = "button";
		okButton.name = "ok";
		okButton.value = "OK";
		okButton.style.float = "right";
		okButton.style.width = "100px";
		okButton.onclick = function( event ) 
		{
			var elt = this;
			while( elt.className != "inputDialog" )
			{
				elt = elt.parentNode;
			}

			instance = elt.instance;
			DurationInputDialog.hideModalInputDialog( instance );
		};
		dialogFooter.appendChild( okButton );

		var cancelButton = document.createElement( "INPUT" );
		cancelButton.type = "button";
		cancelButton.name = "cancel";
		cancelButton.value = "Abbruch";
		cancelButton.style.float = "left";
		cancelButton.style.width = "100px";
		cancelButton.onclick = function( event )
		{
			var elt = this;
			while( elt.className != "inputDialog" )
			{
				elt = elt.parentNode;
			}

			instance = elt.instance;
			DurationInputDialog.hideModalInputDialog( instance );
		};
		dialogFooter.appendChild( cancelButton );

		dialogBorder.appendChild( dialogFooter );
		container.appendChild( dialog );
	}

	dialog.style.left = windowSpec.left + "px";
	dialog.style.top = windowSpec.top + "px";
	dialog.style.visibility = "visible";
	dialog.style.display = "inline";

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
		instance.dialog.style.display = "none";
	}
}

DurationInputDialog.getOffsetLeftRecursively = function( elt )
{
	var result = 0;
	while ( elt.tagName != "HTML" )
	{
		result += elt.offsetLeft;
		elt = elt.parentNode;
	}
	return result;
}

DurationInputDialog.getOffsetTopRecursively = function( elt )
{
	var result = 0;
	while ( elt.tagName != "HTML" )
	{
		result += elt.offsetTop;
		elt = elt.parentNode;
	}
	return result;
}
