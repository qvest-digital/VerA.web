// This file is part of VerA.web and published under the same licence.

(function(){
    var loadSalutationsUnusedList = function() {
        $.ajax({
            url: $("#salutations-unused-list").data("rest-path"),
            type: 'GET',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                if (data !== null ) {
                    if (data.length > 0) { /* show/hide + Button */
                        $("#salutationsAlternativeListContent").append("<div class='clearSalutation' style='margin-top: 10px'><img id='addSalutation' style='vertical-align: middle; margin-right: 10px;' src='../images/add.png'/><span>" + $("#salutations-placeholder-add").data("translation") + "</span></div>");
                        $("img#addSalutation").click(function(){
                            showDialog(data);
                        });
                    }
                } else {
                    //FIXME: REST API ERROR
                }
            },
            error: function(data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = $("#pdftemplate-salutation-load-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    var showDialog = function(data) {
        var selectOptions = buildSelectTag(data);

        vex.dialog.open({
            message: $("#salutations-dialog-text").data("translation"),
            input: [
                selectOptions,
                "<span> " + $("#salutations-placeholder-to").data("translation") + "</span>",
                "<input name='salutationText' type='text' placeholder=" + $("#salutations-dialog-field").data("translation") + " required />"
            ].join(""),
            buttons: [
                $.extend({}, vex.dialog.buttons.YES, { text: $("#salutations-dialog-yes").data("translation") }),
                $.extend({}, vex.dialog.buttons.NO, { text: $("#salutations-dialog-no").data("translation") })
            ],
            callback: function (value) {
                if (value) {
                    saveSalutationsAlternative(value);
                } else {
                    // FIXME: ERROR
                    console.log("ERROR or CLOSED dialog popup");
                }
            }
        });
    };

    var buildSelectTag = function(data) {
        var selectTag = "<select name='salutationId'>";
        data.forEach(function(entry){
            selectTag += "<option value='" + entry.pk + "'>" + entry.salutation + "</option>";
        });
        selectTag += "</select>";

        return selectTag;
    };

    var saveSalutationsAlternative = function(value) {
        $.ajax({
            url: $("#salutations-save-link").data("rest-path"),
            data: {
                salutationId: value.salutationId,
                content: value.salutationText.trim()
            },
            type: 'POST',
            success: function(response){
                $(".errormsg").remove();
                $(".successmsg").remove();
                reloadSalutations();
            },
            error: function(response) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = "";
                if (response.status == 400){ /* Bad Request */
                    errorMsg = $("#pdftemplate-salutation-empty-errormsg").data("errormsg");
                } else if (response.status == 420){ /* Policy Not Fulfilled */
                    errorMsg = $("#pdftemplate-salutation-length-errormsg").data("errormsg");
                } else {
                    errorMsg = $("#pdftemplate-salutation-save-errormsg").data("errormsg");
                }
                showWarning(errorMsg);
            }
        });
    };

    var loadSalutationsAlternativeList = function() {
        $.ajax({
            url: $("#salutations-alternative-list").data("rest-path"),
            type: 'GET',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                if (data !== null) {
                    for (i = 0; i < data.length; i++) {
                        $("#salutationsAlternativeListContainer").append("<div class='clearSalutation' style='margin: 10px 10px 0 0'><label style='display:inline; margin-right: 10px; width: 40%;'>" + data[i][4] + "</label>" + $("#salutations-placeholder-to").data("translation") + "<label style='display:inline; margin-left: 10px; width: 40%;'>" + data[i][3] + "</label><img data-salutation-id=" + data[i][0] + " class='removeSalutation' style='vertical-align: middle; margin-left: 10px;' src='../images/remove.png'/></div>");
                    }
                    $("img.removeSalutation").click(function(){
                        deleteSalutationsAlternative($(this).data("salutation-id"));
                    });
                } else {
                    //FIXME: REST API ERROR
                }
            },
            error: function(data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = $("#pdftemplate-salutation-load-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    var deleteSalutationsAlternative = function(salutationId) {
        $.ajax({
            url: $("#salutations-delete-link").data("rest-path") + salutationId,
            type: 'DELETE',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                reloadSalutations();
            },
            error: function(data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = $("#pdftemplate-salutation-delete-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    var reloadSalutations = function(){
        $(".clearSalutation").remove();
        loadSalutations();
    };

    var loadSalutations = function(){
        loadSalutationsUnusedList();
        loadSalutationsAlternativeList();
    };

    $(document).ready(function() {
        loadSalutations();
    });
})();
