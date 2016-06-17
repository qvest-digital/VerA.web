$(document).ready(function() {
    $.ajax({
        url: $("#pdfetemplate-list").data("pdflist"),
        data: {
            mandantid:$("#pdfetemplate-orgunit").data("orgunit")
        },
        type: 'GET',
        success: function(data){
            $(".errormsg").remove();
            $(".successmsg").remove();
            for (i = 0; i < data.length; i++) {
                $("#pdftemplate-list-overview").before("<input type='hidden' name='list' value='"+ data[i].pk + "'/>");
                $("#pdftemplate-list-overview").find("tbody").find("tr:last").before("<tr id='pdftemplate-" + data[i].pk + "'><td><input type='checkbox' id='" + data[i].pk + "' name='" + data[i].pk +"-select'></td><td>" + data[i].pk + "</td><td>" + data[i].name + "</td></tr>");
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