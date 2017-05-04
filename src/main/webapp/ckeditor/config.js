/**
 * @license Copyright (c) 2003-2017, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';

	config.toolbar = 'CustomToolbar';

	config.toolbar_CustomToolbar =
	[
		{ name: 'style', items : [ 'Bold','Italic','Strike','RemoveFormat','-' ] },
		{ name: 'clipboard', items : [ 'Cut','Copy','Paste','-' ] },
		{ name: 'font', items : [ 'Font', 'FontSize', 'Format','-' ] },
		{ name: 'justify', items : [ 'JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-' ] },
		{ name: 'extras', items : [ 'Link','ImageButton' ] },
	];
};
