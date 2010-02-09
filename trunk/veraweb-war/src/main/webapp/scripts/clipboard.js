
function isNetscape() {
	return !!document.layers;
}

function isInternetExplorer() {
	return !!document.all;
}

/**
 * Check if XPCOM is supported.
 */
function isMozilla() {
	return !!window.Components;
}

/**
 * A clipboard that can write and read from the system clipboard.
 */
function Clipboard() {
	if (isNetscape()) {
		netscape.security.PrivilegeManager.enablePrivilege('UniversalSystemClipboardAccess');
		var tmp = new java.awt.Frame();
		this.clipboard = fr.getToolkit().getSystemClipboard();
    } else if (isInternetExplorer()) {
		this.clipboard = document.createElement( 'INPUT' );
		with (this.clipboard.style) {
			position = 'absolute';
			left = '0px';
			top = '0px';
			visibility = 'hidden';
		}
		document.body.appendChild(this.clipboard);
	} else if (isMozilla()) {
		netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
		this.clipboardid = Components.interfaces.nsIClipboard;
		this.clipboard = Components.classes['@mozilla.org/widget/clipboard;1'].getService(this.clipboardid);
		this.clipboardstring = Components.classes['@mozilla.org/supports-string;1'].createInstance(Components.interfaces.nsISupportsString);
	}
	this.copy = ClipboardCopyX;
	this.paste = ClipboardPasteX;
}

/**
 * Copies a text to the system clipboard. Priveleges are necessary.
 * @param text the text
 */
function ClipboardCopyX(text) {
	if (isNetscape()) {
		field.select();
		this.clipboard.setContents(new java.awt.datatransfer.StringSelection( text ), null);
	} else if (isInternetExplorer()) {
		this.clipboard.value = text;
		this.clipboard.select();
		var textRange = this.clipboard.createTextRange();
		textRange.execCommand('copy');
	} else if (isMozilla()) {
		netscape.security.PrivilegeManager.enablePrivilege( 'UniversalXPConnect' );
		this.clipboardstring.data = text;
		var transfer = Components.classes['@mozilla.org/widget/transferable;1'].createInstance( Components.interfaces.nsITransferable );
		transfer.setTransferData( 'text/unicode', this.clipboardstring, text.length*2 );
		this.clipboard.setData( transfer, null, this.clipboardid.kGlobalClipboard );
	}
}

/**
 * Gets the current text in the system clipboard.
 * @return the text
 */
function ClipboardPasteX() {
	if (isNetscape()) {
		var content = this.clipboard.getContents(null);
		if (content != null) {
			return content.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
		}
	} else if (isInternetExplorer()) {
		this.clipboard.value = '';
		var textRange = this.clipboard.createTextRange();
		textRange.execCommand('paste');
		return this.clipboard.value;
	} else if (isMozilla()) {
		netscape.security.PrivilegeManager.enablePrivilege( 'UniversalXPConnect' );
		var transfer = Components.classes['@mozilla.org/widget/transferable;1'].createInstance( Components.interfaces.nsITransferable );
		transfer.addDataFlavor( 'text/unicode' );
		this.clipboard.getData( transfer, this.clipboardid.kGlobalClipboard );
		var str = new Object();
		var strLength = new Object();
		transfer.getTransferData( 'text/unicode', str, strLength );
		str = str.value.QueryInterface( Components.interfaces.nsISupportsString );
		return str.data.substring( 0, strLength.value / 2 );
	}
}

function setClipboard(text) {
	if (window.clipboardData) {
		window.clipboardData.setData('Text', text);
	} else {
		if (!window.clipboard) {
			window.clipboard = new Clipboard();
		}
		window.clipboard.copy(text)
	}
}

function getClipboard() {
	if (window.clipboardData) {
		return window.clipboardData.getData('Text');
	} else {
		if (!window.clipboard) {
			window.clipboard = new Clipboard();
		}
		return window.clipboard.paste();
	}
}
