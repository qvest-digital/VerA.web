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
                if (data != null ) {
                    for (i = 0; i < data.length; i++) {
                        $("#pdftemplate-list-overview").before("<input type='hidden' name='list' value='"+ data[i].pk + "'/>");
                        $("#pdftemplate-list-overview").find("tbody").find("tr:last").before("<tr id='pdftemplate-" + data[i].pk + "'><td><input type='checkbox' id='" + data[i].pk + "' name='" + data[i].pk +"-select'></td><td style='cursor: pointer;' onclick='window.location.href=\"PdfTemplateDetail?id=" + data[i].pk + "\";' >" + data[i].pk + "</td><td style='cursor: pointer;' onclick='window.location.href=\"PdfTemplateDetail?id=" + data[i].pk + "\";' >" + data[i].name + "</td></tr>");
                    }
                    $("span.amount").append("<span id=\"pdftemplates-count\">" + data.length + "</span> PDF-Vorlage(n)");
                } else {
                    $("span.amount").append("<span id=\"pdftemplates-count\">Keine</span> PDF-Vorlage(n)")
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
    }

    var executePdfTemplateDeletion = function() {
        var selected = [];
        $('#pdftemplate-list-overview input:checked').each(function() {
            if($(this).attr('id') != "toggleAllSelect"){
                selected.push($(this).attr('id'));
            }
        });
        deletePdfTemplates(selected);
    }

    $(document).ready(function() {
        $("#deletePdfTemplateButton").click(executePdfTemplateDeletion);
        loadPdfTemplateList();
    });
})();