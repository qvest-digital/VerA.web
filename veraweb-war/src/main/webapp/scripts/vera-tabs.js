/* global $ */

$(function () { // on DOM ready
    'use strict';

    /**
     * @type {string}
     * @const
     */
    var ATTR_TAB = 'vera-tab';

    /**
     * @type {string}
     * @const
     */
    var ATTR_SELECT = 'vera-select';

    /**
     * @type {string}
     * @const
     */
    var ATTR_TAB_GROUP = 'vera-tab-group';

    /**
     * @type {string}
     * @const
     */
    var ATTR_CONTENT = 'vera-content';

    /**
     * @type {string}
     * @const
     */
    var ATTR_TAB_DEFAULT = 'vera-tab-default';


    /**
     * Only the content which is referenced by all active tabs and selected drop down fields is shown.
     */
    var updateContent = function () {
        // update visibility of content
        var contentIds = []; // sctive content ids
        $('[vera-tab].active').each(function () { // iterate active tabs
            contentIds.push($(this).attr(ATTR_TAB)); // add id of current active tab
        });
        $('select[vera-select] option:selected').each(function () { // iterate selected options of all selects
            contentIds.push($(this).val());  // add id of current active selection
        });
        var activeTabContent = $();
        $('[' + ATTR_CONTENT + ']').each(function () { // iterate all managed content
            var showContent = true; // true if all ids in the content field are active
            $.each($(this).attr(ATTR_CONTENT).split(' '), function () {
                if ($.inArray(this, contentIds) === -1) {  // content field contains an id which is not active
                    showContent = false;
                }
            });
            if (showContent) {
                activeTabContent = $(this).add(activeTabContent);
            }
        });
        // hide inactive content
        $('[' + ATTR_CONTENT + ']').not(activeTabContent).hide();
        // show active content
        activeTabContent.show();
    };

    /**
     * Activate the given tab elements and deactivate the other tabs in the same tab group.
     * Also only the content which is referenced by all active tabs is shown.
     *
     * @param tabs
     */
    var setTabs = function (tabs) {
        // activate tab
        tabs.each(function (i, tab) { // iterate Tabs which should be activated
            tab = $(tab); // create wrapping jQuery object
            // get all tabs in the tab group of the current tab
            var tabGroup = tab.parent('[' + ATTR_TAB_GROUP + ']').children('[' + ATTR_TAB + ']');
            // activate only current tab in tab group
            tabGroup.not(tab).removeClass("active").addClass("inactive");
            tab.removeClass("inactive").addClass("active");
        });
        updateContent();
    };

    // add click handler to all tabs
    $('[' + ATTR_TAB + ']').click(function () {
        setTabs($(this));
    });

    // add change handler to all content selects
    $('[' + ATTR_SELECT + ']').change(function () {
        updateContent();
    });

    // activate default tabs
    setTabs($('[' + ATTR_TAB_DEFAULT + ']'));
});