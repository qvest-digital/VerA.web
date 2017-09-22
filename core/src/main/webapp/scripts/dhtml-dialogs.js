/** counter fuer dialoge */
var dialogCount = 0;

/** @return fenster breite */
function getWindowWidth() {
	if (window.innerWidth) {
		return window.innerWidth;
	} else {
		return document.body.offsetWidth;
	}
}

/** @return fenster hoehe */
function getWindowHeight() {
	if (window.innerHeight) {
		return window.innerHeight;
	} else {
		return document.body.offsetHeight;
	}
}

/** @return dialog top */
function getDialogTop(dw, dh) {
	return (getWindowHeight() - dh) / 2 - 2;
}

/** @return dialog left */
function getDialogLeft(dw, dh) {
	return (getWindowWidth() - dw) / 2 - 2;
}

/** @return dialog width */
function getDialogWidth(dw, dh) {
	return dw;
}

/** @return dialog height */
function getDialogHeight(dw, dh) {
	return dh;
}

/** @return dialog groesse als style-string */
function getDialogSize(dw, dh) {
	if (!dw) dw = 420;
	if (!dh) dh = 200;
	dt = 'top: ' + getDialogTop(dw, dh) + 'px; ';
	dl = 'left: ' + getDialogLeft(dw, dh) + 'px; ';
	dw = 'width: ' + getDialogWidth(dw, dh) + 'px; ';
	dh = 'height: ' + getDialogHeight(dw, dh) + 'px; ';
	return dt + dl + dw;
}

/** erstellt einen dialog */
function htmlDialog(styleName, text) {
	var dw = 420;
	var dh = 200;
	window.scrollTo(0, 0);
	hideComboboxes();
	hideDialogs();

	var innerId = document.createAttribute('id');
		innerId.nodeValue = 'dialogId' + dialogCount++;
	var innerClass = document.createAttribute('class');
		innerClass.nodeValue = styleName;
	var innerDiv = document.createElement('div');
		innerDiv.innerHTML = text;
		innerDiv.setAttributeNode(innerId);
		innerDiv.setAttributeNode(innerClass);
	if (window.innerWidth) {
		var innerStyle = document.createAttribute('style');
			innerStyle.nodeValue = getDialogSize(dw, dh);
			innerDiv.setAttributeNode(innerStyle);
	} else {
		innerDiv.style.top = getDialogTop(dw, dh);
		innerDiv.style.left = getDialogLeft(dw, dh);
		innerDiv.style.width = getDialogWidth(dw, dh);
	}

	var b = document.getElementsByTagName("body")[0];
	b.appendChild(innerDiv);
}

/** erstellt einen abfrage dialog */
function htmlConfirm(question, answer, links) {
	var text = '';
	text += '<div style="height: 120px;">';
	text += '<strong>' + question + '</strong>';
	text += '</div>';

	text += '<div style="text-align: right;">';
	for (var i = 0; i < answer.length; i++) {
		var t = answer[i];
		var c = links[i];
		text += '<input type="button" value="' + t + '" class="button" onclick="' + c + '"> &nbsp;';
	}
	text += '</div>';

	htmlDialog('dialogInfo', text);
}

/** erstellt ein bitte-warten dialog */
function htmlWait(pleaseWaitHeader, pleaseWaitMessage) {
	var text = '';
	text += '<div style="height: 140px;">';
	text += '<strong>' + pleaseWaitHeader + '</strong><br><br>';
	text += pleaseWaitMessage;
	text += '</div>';

	htmlDialog('dialogInfo', text);
}

/** blendet alle select-boxen aus, behebt einen bug im IE */
function hideComboboxes() {
	var sel = document.getElementsByTagName("select");
	for (var i = 0; i < sel.length; i++) {
		hideBlock(sel[i]);
	}
}

function showComboboxes() {
	var sel = document.getElementsByTagName("select");
	for (var i = 0; i < sel.length; i++) {
		showBlock(sel[i]);
	}
}

/** blendet alle dialoge aus */
function hideDialogs() {
	for (var i = 0; i < dialogCount; i++) {
		hideBlock(document.getElementById('dialogId' + i));
	}
}
