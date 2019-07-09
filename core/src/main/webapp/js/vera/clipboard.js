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
