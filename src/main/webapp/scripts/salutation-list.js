(function(){
    var loadSalutationsUnusedList = function() {
        $.ajax({
            url: $("#salutations-unused-list").data("rest-path"),
            type: 'GET',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                if (data != null ) {
                    if (data.length > 0) { /* show/hide + Button */
                        $("#salutationsAlternativeListContent").append("<div style='margin-top: 10px'><img id='addSalutation' style='vertical-align: middle; margin-right: 10px;' src='../images/add.png'/><span>" + $("#salutations-placeholder-add").data("translation") + "</span></div>");
                        $("#addSalutation").click(function(){
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
            message: 'Are you absolutely sure you want to destroy the alien planet?ÜBERSETZUNG',
            input: [
                selectOptions,
                '<input name="salutationText" type="text" placeholder="Alternative AnredeÜBERSETZUNG" required />'
            ].join(''),
            buttons: [
                $.extend({}, vex.dialog.buttons.YES, { text: 'SpeichernÜBERSETZUNG' }),
                $.extend({}, vex.dialog.buttons.NO, { text: 'AbbrechenÜBERSETZUNG' })
            ],
            callback: function (value) {
                if (value) {
                    saveSalutationsAlternative(value);
                } else {
                    // FIXME: ERROR
                    console.log("ERROR or CLOSED dialog popup");
                }
            }
        })
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
                salutationId:value.salutationId,
                content:value.salutationText.trim()
            },
            type: 'POST',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                location.reload();
            },
            error: function(data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                if (data.status == 400){ /* Bad Request */
                    var errorMsg = $("#pdftemplate-salutation-empty-errormsg").data("errormsg");
                } else {
                    var errorMsg = $("#pdftemplate-salutation-save-errormsg").data("errormsg");
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
                if (data != null) {
                    for (i = 0; i < data.length; i++) {
                        $("#salutationsAlternativeListContainer").append("<div style='margin: 10px 10px 0 0'><label style='display:inline; margin-right: 10px; width: 40%;'>" + data[i][4] + "</label>" + $("#salutations-placeholder-to").data("translation") + "<label style='display:inline; margin-left: 10px; width: 40%;'>" + data[i][3] + "</label></div>");
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

    $(document).ready(function() {
        loadSalutationsUnusedList();
        loadSalutationsAlternativeList();
    });
})();