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
            var select = document.getElementById("pdftemplate-list")
            for (var i = 0; i < data.length; i++) {
                var pk = data[i].pk;
                var name = data[i].name;
                var option = document.createElement("option");
                option.value = pk;
                option.textContent = name;
                select.appendChild(option);
            };
        },
        error: function(data) {
            $(".errormsg").remove();
            $(".successmsg").remove();
            var errorMsg = $("#pdfetemplate-list-errormsg").data("errormsg");
            showWarning(errorMsg);
        }
    });
});