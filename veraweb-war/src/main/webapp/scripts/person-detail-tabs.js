var tab = 1;
var person = 1;
var anschrift = 1;
var zeichensatz = 1;

var allblocks = new Array();
allblocks.push('default');
allblocks.push('person1-zs1');
allblocks.push('person1-zs2');
allblocks.push('person1-zs3');
allblocks.push('person2-zs1');
allblocks.push('person2-zs2');
allblocks.push('person2-zs3');
allblocks.push('anschrift1-zs1');
allblocks.push('anschrift1-zs2');
allblocks.push('anschrift1-zs3');
allblocks.push('anschrift2-zs1');
allblocks.push('anschrift2-zs2');
allblocks.push('anschrift2-zs3');
allblocks.push('anschrift3-zs1');
allblocks.push('anschrift3-zs2');
allblocks.push('anschrift3-zs3');

var allsubtabs = new Array();
allsubtabs.push('person-1');
allsubtabs.push('person-2');
allsubtabs.push('person-zs1');
allsubtabs.push('person-zs2');
allsubtabs.push('person-zs3');
allsubtabs.push('anschrift-1');
allsubtabs.push('anschrift-2');
allsubtabs.push('anschrift-3');
allsubtabs.push('anschrift-zs1');
allsubtabs.push('anschrift-zs2');
allsubtabs.push('anschrift-zs3');

function showTab(name) {
	if (!name) {
	} else if (name == 'default') {
		tab = 1;
	} else if (name == 'person') {
		tab = 2;
	} else if (name == 'anschrift') {
		tab = 3;
	} else if (name == 'dokumente') {
		tab = 4;
	} else if (name == 'kategorien') {
		tab = 5;
	} else if (name == 'person-1') {
		person = 1;
	} else if (name == 'person-2') {
		person = 2;
	} else if (name == 'person-zs1') {
		zeichensatz = 1;
	} else if (name == 'person-zs2') {
		zeichensatz = 2;
	} else if (name == 'person-zs3') {
		zeichensatz = 3;
	} else if (name == 'anschrift-1') {
		anschrift = 1;
	} else if (name == 'anschrift-2') {
		anschrift = 2;
	} else if (name == 'anschrift-3') {
		anschrift = 3;
	} else if (name == 'anschrift-zs1') {
		zeichensatz = 1;
	} else if (name == 'anschrift-zs2') {
		zeichensatz = 2;
	} else if (name == 'anschrift-zs3') {
		zeichensatz = 3;
	}
	
	if (person < 1 || person > 2)
		person = 1;
	if (anschrift < 1 || anschrift > 3)
		anschrift = 1;
	if (zeichensatz < 1 || zeichensatz > 3)
		zeichensatz = 1;
	
	document.getElementById('personTab').value = tab;
	document.getElementById('personMemberTab').value = person;
	document.getElementById('personAddresstypeTab').value = anschrift;
	document.getElementById('personLocaleTab').value = zeichensatz;
	
	if (tab == 1) {
		changeTab('default', new Array('person', 'anschrift'));
		changeBlock(null, new Array('subtab-person', 'subtab-anschrift'));
		changeBlock('default', allblocks);
	} else if (tab == 2) {
		changeTab('person', new Array('default', 'anschrift'));
		changeTab(null, allsubtabs);
		changeTab('person-' + person);
		changeTab('person-zs' + zeichensatz);
		changeBlock('subtab-person', new Array('subtab-anschrift'));
		changeBlock('person' + person + '-zs' + zeichensatz, allblocks);
	} else if (tab == 3) {
		changeBlock(null, allblocks);
		changeTab('anschrift', new Array('default', 'person'));
		changeTab(null, allsubtabs);
		changeTab('anschrift-' + anschrift);
		changeTab('anschrift-zs' + zeichensatz);
		changeBlock('subtab-anschrift', new Array('subtab-person'));
		changeBlock('anschrift' + anschrift + '-zs' + zeichensatz);
	}
}
