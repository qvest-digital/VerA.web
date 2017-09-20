// This file is part of VerA.web and published under the same licence.

var tab = 1;
var person = 1;
var anschrift = 1;
var zeichensatz = 1;

var allblocks = [
    'default',
    'person1-zs1',
    'person1-zs2',
    'person1-zs3',
    'person2-zs1',
    'person2-zs2',
    'person2-zs3',
    'anschrift1-zs1',
    'anschrift1-zs2',
    'anschrift1-zs3',
    'anschrift2-zs1',
    'anschrift2-zs2',
    'anschrift2-zs3',
    'anschrift3-zs1',
    'anschrift3-zs2',
    'anschrift3-zs3'
];

var allsubtabs = [
    'person-1',
    'person-2',
    'person-zs1',
    'person-zs2',
    'person-zs3',
    'anschrift-1',
    'anschrift-2',
    'anschrift-3',
    'anschrift-zs1',
    'anschrift-zs2',
    'anschrift-zs3'
];

var showTab = function (name) {
	if (!name) {
	} else if (name === 'default') {
		tab = 1;
	} else if (name === 'person') {
		tab = 2;
	} else if (name === 'anschrift') {
		tab = 3;
	} else if (name === 'dokumente') {
		tab = 4;
	} else if (name === 'kategorien') {
		tab = 5;
	} else if (name === 'person-1') {
		person = 1;
	} else if (name === 'person-2') {
		person = 2;
	} else if (name === 'person-zs1') {
		zeichensatz = 1;
	} else if (name === 'person-zs2') {
		zeichensatz = 2;
	} else if (name === 'person-zs3') {
		zeichensatz = 3;
	} else if (name === 'anschrift-1') {
		anschrift = 1;
	} else if (name === 'anschrift-2') {
		anschrift = 2;
	} else if (name === 'anschrift-3') {
		anschrift = 3;
	} else if (name === 'anschrift-zs1') {
		zeichensatz = 1;
	} else if (name === 'anschrift-zs2') {
		zeichensatz = 2;
	} else if (name === 'anschrift-zs3') {
		zeichensatz = 3;
	}

	if (person < 1 || person > 2) {
		person = 1;
    }
    if (anschrift < 1 || anschrift > 3) {
		anschrift = 1;
    }
    if (zeichensatz < 1 || zeichensatz > 3) {
		zeichensatz = 1;
    }

    $('#personTab').val(tab);
    $('#personMemberTab').val(person);
    $('#personAddresstypeTab').val(anschrift);
    $('#personLocaleTab').val(zeichensatz);

	if (tab === 1) {
		changeTab('default', ['person', 'anschrift']);
		changeBlock(null, ['subtab-person', 'subtab-anschrift']);
		changeBlock('default', allblocks);
	} else if (tab === 2) {
		changeTab('person', ['default', 'anschrift']);
		changeTab(null, allsubtabs);
		changeTab('person-' + person);
		changeTab('person-zs' + zeichensatz);
		changeBlock('subtab-person', ['subtab-anschrift']);
		changeBlock('person' + person + '-zs' + zeichensatz, allblocks);
	} else if (tab === 3) {
		changeBlock(null, allblocks);
		changeTab('anschrift', ['default', 'person']);
		changeTab(null, allsubtabs);
		changeTab('anschrift-' + anschrift);
		changeTab('anschrift-zs' + zeichensatz);
		changeBlock('subtab-anschrift', ['subtab-person']);
		changeBlock('anschrift' + anschrift + '-zs' + zeichensatz);
	}
};
