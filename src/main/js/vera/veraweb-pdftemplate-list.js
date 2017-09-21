// This file is part of VerA.web and published under the same licence.

(function(){
    var loadPdfTemplateList = function() {
        $.ajax({
            url: $("#pdfetemplate-list").data("pdflist"),
            data: {
                mandantid:$("#pdfetemplate-orgunit").data("orgunit")
            },
            type: 'GET',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                if (data !== null ) {
                    for (i = 0; i < data.length; i++) {
                        $("#pdftemplate-list-overview").before("<input type='hidden' name='list' value='"+ data[i][0] + "'/>");
                        $("#pdftemplate-list-overview").find("tbody").find("tr:last").before("<tr id='pdftemplate-" + data[i][0] + "'><td><input type='checkbox' id='" + data[i][0] + "' name='" + data[i][0] +"-select'></td><td style='cursor: pointer;' onclick='window.location.href=\"PdfTemplateDetail?id=" + data[i][0] + "\";' >" + data[i][0] + "</td><td style='cursor: pointer;' onclick='window.location.href=\"PdfTemplateDetail?id=" + data[i][0] + "\";' >" + data[i][1] + "</td></tr>");
                    }
                    $("#pdftemplates-count").text(data.length);
                } else {
                    $("#pdftemplates-count").text("Keine");
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

    var deletePdfTemplates = function(selectedCheckboxes) {
        $.ajax({
            url: $("#pdfetemplate-delete").data("pdfdelete"),
            data: {
                templateId:selectedCheckboxes
            },
            type: 'POST',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                $('#pdftemplate-list-overview tr:not(:first):not(:last)').remove();
                loadPdfTemplateList();
            },
            error: function(data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = $("#pdfetemplate-delete-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    var executePdfTemplateDeletion = function() {
        var selected = [];
        $('#pdftemplate-list-overview input:checked').each(function() {
            if($(this).attr('id') != "toggleAllSelect"){
                selected.push($(this).attr('id'));
            }
        });
        deletePdfTemplates(selected);
    };

    $(document).ready(function() {
        $("#deletePdfTemplateButton").click(executePdfTemplateDeletion);
        loadPdfTemplateList();
    });
})();
