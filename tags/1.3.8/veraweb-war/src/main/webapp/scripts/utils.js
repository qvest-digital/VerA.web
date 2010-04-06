function getPosX(e) {
	var x = 0;
	while (e) {
		x += e.offsetLeft;
		e = e.offsetParent;
	}
	return x;
}

function getPosY(e) {
	var x = 0;
	while (e) {
		x += e.offsetTop;
		e = e.offsetParent;
	}
	return x;
}

function isOver(layer1, layer2) {
	return (getPosY(layer1) + layer1.offsetHeight > getPosY(layer2));
}

function fixLayer() {
	var fm = document.getElementById('fixme')
	var sl = document.getElementsByTagName('select');
	for (var i = 0; i < sl.length; i++) {
		if (isOver(sl[i], fm)) {
			sl[i].style.visibility = 'hidden';
		} else {
			sl[i].style.visibility = 'visible';
		}
	}
	return 0;
}

function setModified(id) {
	id = (id == null) ? 'modified' : id + '-modified';
	document.getElementById(id).value = 'true';
}

function checkModified(id) {
	if (isModified(id)) {
		return confirm('Bearbeitung verwerfen?');
	} else {
		return true;
	}
}

function isModified(id) {
	id = (id == null) ? 'modified' : id + '-modified';
	if (document.getElementById(id).value == 'true') {
		return true;
	} else {
		return false;
	}
}

function navigateList(start) {
	var form = document.getElementById('formlist');
	if (form && form.elements['start']) {
		form.elements['start'].value = start;
		form.submit();
	} else {
		alert('form or field start NOT FOUND!');
	}
}

function navigateSelectAll() {
	var form = document.getElementById('formlist');
	if (form && form.elements['selectAll']) {
		form.elements['selectAll'].value = 'true';
		form.submit();
	} else {
		alert('form or field selectAll NOT FOUND!');
	}
}

function navigateSelectNone() {
	var form = document.getElementById('formlist');
	var fields = form.elements;
	for (var i = 0; i < fields.length; i++) {
		if (fields[i].type == 'checkbox') {
			fields[i].checked = false;
		}
	}
	if (form && form.elements['selectNone']) {
		form.elements['selectNone'].value = 'true';
		form.submit();
	} else {
		alert('form or field selectNone NOT FOUND!');
	}
}

function smallResolution() {
	if (window.innerWidth) {
		return (window.innerWidth < 912);
	} else {
		return (document.body.offsetWidth < 912);
	}
}

function writeResolution(small, large) {
	document.writeln(smallResolution() ? small : large);
}

function selectAll(inp) {
	inp.focus();
	if (document.selection) {
		/*
		var sel = document.body.createRange();
		sel.select();
		*/
	} else {
		inp.selectionStart = 0;
		inp.selectionEnd = inp.value.length;
	}
}

function disableForm(frm) {
	var fields = frm.elements;
	for (var i = 0; i < fields.length; i++) {
		if (fields[i].type == 'text') {
			disableFormInput(fields[i]);
		} else if (fields[i].type == 'textarea') {
			disableFormTextarea(fields[i]);
		} else if (fields[i].type == 'radio') {
			disableFormRadiobox(fields[i]);
		} else if (fields[i].type == 'checkbox') {
			disableFormCheckbox(fields[i]);
		} else if (fields[i].type == 'select-one') {
			disableFormSelect(fields[i]);
/*
		} else if (fields[i].type == 'submit') {
		} else if (fields[i].type == 'button') {
		} else if (fields[i].type == 'hidden') {
		} else {
			alert(fields[i].type);
*/
		}
	}
}

function disableFormInput(fld) {
	var attr = document.createAttribute('readonly');
	attr.nodeValue = 'readonly';
	fld.setAttributeNode(attr);
	fld.style.color = "#808080";
	fld.style.background = "#f0f0f0";
}

function enableFormInput(fld) {
	fld.removeAttribute("readonly", 0);
	fld.style.color = "#000000";
	fld.style.background = "#ffffff";
}

function disableFormTextarea(fld) {
	var attr = document.createAttribute('readonly');
	attr.nodeValue = 'readonly';
	fld.setAttributeNode(attr);
	fld.style.color = "#808080";
	fld.style.background = "#f0f0f0";
}

function disableFormSelect(fld) {
	var attr = document.createAttribute('disabled');
	attr.nodeValue = 'true';
	fld.setAttributeNode(attr);
	fld.style.color = "#808080";
	fld.style.background = "#f0f0f0";
}

function enableFormSelect(fld) {
	fld.removeAttribute("disabled", 0);
	fld.style.color = "#000000";
	fld.style.background = "#ffffff";
}

function disableFormRadiobox(fld) {
	var attr = document.createAttribute('disabled');
	attr.nodeValue = 'true';
	fld.setAttributeNode(attr);
	fld.style.color = "#808080";
	fld.style.background = "#f0f0f0";
}

function disableFormCheckbox(fld) {
	var attr = document.createAttribute('disabled');
	attr.nodeValue = 'true';
	fld.setAttributeNode(attr);
	fld.style.color = "#808080";
	fld.style.background = "#f0f0f0";
}

function enableFormCheckbox(fld) {
	fld.removeAttribute("disabled", 0);
	fld.style.color = "#000000";
	fld.style.background = "#ffffff";
}

function onMouseOverList(line) {
	line.style.backgroundColor = '#ffff80';
}

function onMouseOutList(line) {
	line.style.backgroundColor = '';
}

function insertAtCursor(fld, text) {
	// IE
	if (document.selection) {
		fld.focus();
		sel = document.selection.createRange();
		sel.text = text;
	}
	// Mozilla, Firefox
	else if (fld.selectionStart || fld.selectionStart == '0') {
		var startPos = fld.selectionStart;
		var endPos = fld.selectionEnd;
		fld.value = fld.value.substring(0, startPos)
				+ text
				+ fld.value.substring(endPos, fld.value.length);
	// Alle anderen
	} else {
		fld.value += text;
	}
}
