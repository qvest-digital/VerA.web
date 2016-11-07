(function(){
    var loadSalutationsList = function() {
        $.ajax({
            url: $("#salutations-list").data("salutations"),
            type: 'GET',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                if (data != null ) {
                    if (data.length > 0) {
                        $('#salutationsAlternativeListContent').append("<div style='margin-top: 10px'><img id='addSalutation' style='vertical-align: middle; margin-right: 5px;' src='../images/add.png'/><span>${placeholderWithTranslation.ADD_ALTERNATIVE_SALUTATION}</span></div>");
                    }
                    $("img#addSalutation").click(function(){
                        showDialog(data);
                    });
                } else {
                    // FIXME: ERROR
                    console.log('Chicken.');
                    console.log('HINWEIS alle Anreden haben alternativen');
                }
            },
            error: function(data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = $("#pdfetemplate-list-load-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    var showDialog = function(data) {
        var selectOptions = '<select name="salutationPk">';
        data.forEach(function(entry){
            selectOptions += "<option value='" + entry.pk + "'>" + entry.salutation + "</option>";
        });
        selectOptions += '</select>';

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
                    console.log('Chicken.');
                    console.log('ERROR with dialog popup');
                }
            }
        })
    };

    var saveSalutationsAlternative = function(value) {
        $.ajax({
            url: $("#salutations-save-link").data("salutations-save"),
            data: {
                salutationId:value.salutationPk,
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
                var errorMsg = $("#pdfetemplate-save-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    var loadSalutationsAlternativeList = function() {
        $.ajax({
            url: $("#salutations-alternative-list").data("salutations-alternative"),
            type: 'GET',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                if (data != null) {
                    for (i = 0; i < data.length; i++) {
                        $('#salutationsAlternativeListContent').append("<div style='margin: 20px 10px 0 0'><label style='display:inline; margin-right: 10px; width: 40%;'>" + data[i][4] + "</label>${placeholderWithTranslation.SALUTATION_TO_SALUTATION} <span>" + data[i][3] + "</span></div>");
                    }
                } else {
                }
            },
            error: function(data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = $("#pdfetemplate-list-load-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    $(document).ready(function() {
        loadSalutationsList();
        loadSalutationsAlternativeList();
    });
})();