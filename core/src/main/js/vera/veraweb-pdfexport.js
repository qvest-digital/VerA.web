// This file is part of VerA.web and published under the same licence.

// loads the list of available pdf templates into the dropdown on the export tab

$(document).ready(function() {
    $.ajax({
        url: $("#pdfetemplate-list").data("pdfexport"),
        data: {
            mandantid:$("#pdfetemplate-orgunit").data("orgunit")
        },
        type: 'GET',
        success: function(data){
            $(".errormsg").remove();
            $(".successmsg").remove();
            var select = document.getElementById("pdftemplate-combo");
            for (var i = 0; i < data.length; i++) {
                var pk = data[i][0];
                var name = data[i][1];
                var option = document.createElement("option");
                option.value = pk;
                option.textContent = name;
                select.appendChild(option);
            }
        },
        error: function(data) {
            $(".errormsg").remove();
            $(".successmsg").remove();
            var errorMsg = $("#pdfetemplate-list-errormsg").data("errormsg");
            showWarning(errorMsg);
        }
    });
});
