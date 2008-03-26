var detail = 1;

var allblocks = new Array();
allblocks.push('detail-1');
allblocks.push('detail-2');
allblocks.push('detail-3');

var allsubtabs = new Array();
allsubtabs.push('detail-1');
allsubtabs.push('detail-2');
allsubtabs.push('detail-3');

function showTab(name) {
	if (!name) {
	} else if (name == 'detail-1') {
		detail = 1;
	} else if (name == 'detail-2') {
		detail = 2;
	} else if (name == 'detail-3') {
		detail = 3;
	}
	
	changeTab(null, new Array('detail-1', 'detail-2', 'detail-3'));
	changeBlock(null, new Array('detail-1', 'detail-2', 'detail-3'));
	
	window.setTimeout("changeTab('detail-' + detail);", 50);
	window.setTimeout("changeBlock('detail-' + detail);", 50);
}
