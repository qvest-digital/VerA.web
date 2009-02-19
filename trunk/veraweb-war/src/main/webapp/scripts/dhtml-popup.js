function openPopup(field, url, w, h, p) {
	var t = (screen.availHeight / 2) - (h / 2);
	var l = (screen.availWidth - w - 200);
	var params = "width=" + w + ",height=" + h + ",top=" + t + ",left=" + l;
	params += "," + p;
	
	var newwindow = window.open(url, field + 'window', params);
	newwindow.opener = window;
	newwindow.focus();
}

function closePopup() {
	if (window.opener) {
/*		window.opener.focus(); */
	}
	window.close();
}

function newLocation(url) {
	openPopup('location', url + 'popupNewLocation', 320, 180, "status=no,resizable=no,scrollbars=no,dependent=yes,alwaysRaised=yes");
}

function selectFunction(url, field) {
	openPopup('function', url + 'popupSelectFunction/field=' + field + '?limit=5', 520, 380, "status=no,resizable=no,scrollbars=yes,dependent=yes,alwaysRaised=yes");
}

function selectCompany(url, field) {
	openPopup('company', url + 'popupSelectCompany/field=' + field, 520, 380, "status=no,resizable=no,scrollbars=yes,dependent=yes,alwaysRaised=yes");
}

function selectPersonCategory(url) {
	openPopup('function', url, 320, 220, "status=no,resizable=yes,scrollbars=yes,dependent=yes,alwaysRaised=yes");
}

function selectPersonWorkarea(url) {
	openPopup('function', url, 320, 220, "status=no,resizable=yes,scrollbars=yes,dependent=yes,alwaysRaised=yes");
}

function selectGuestCategory(url) {
	openPopup('function', url, 320, 220, "status=no,resizable=yes,scrollbars=yes,dependent=yes,alwaysRaised=yes");
}

function exportPerson(url, id) {
	openPopup('person', url + 'popupPersonExport?id=' + id, 520, 380, "status=no,resizable=no,scrollbars=no,dependent=yes,alwaysRaised=yes");
}

function returnCity() {
	if (window.opener) {
		var city = document.getElementById('city').value;
		var select = window.opener.document.getElementById('event-location');
		
		if (city.length == 0) {
			alert('Bitte geben Sie einen Ort ein.');
			return false;
		} else {
			var exists = false;
			for (var i = 0; i < select.length; i++) {
				if (select.options[i].text == city) {
					exists = true;
					break;
				}
			}
			if (exists) {
/*				alert('Der Ort existiert bereits in der Liste und wird nun ausgew�hlt.'); */
			}
			window.opener.document.getElementById('addcity').value = city;
			window.opener.addCity();
			return true;
		}
	}
}

function returnFunction(url, id) {
	if (window.opener) {
		var field = getParameter('field');
		var content = document.getElementById('function_' + id).innerHTML;
		window.opener.document.getElementById(field).value = content;
		window.opener.document.getElementById(field).focus();
		return true;
	}
}

function returnCompany(url, id) {
	if (window.opener) {
		var field = getParameter('field');
		var form = window.opener.document.getElementById('PersonForm');
		window.opener.document.getElementById('company').value = id;
		window.opener.document.getElementById('companyfield').value = field;
		form.action = url + 'LoadPersonCompany/tab=anschrift';
		form.submit();
		return true;
	}
}
