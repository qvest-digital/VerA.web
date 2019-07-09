(function () {
    var loadPdfTemplateList = function () {
        $.ajax({
            url: $("#pdfetemplate-list").data("pdflist"),
            data: {
                mandantid: $("#pdfetemplate-orgunit").data("orgunit")
            },
            type: 'GET',
            success: function (data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                if (data !== null) {
                    for (i = 0; i < data.length; i++) {
                        $("#pdftemplate-list-overview").before("<input type='hidden' name='list' value='" + data[i][0] + "'/>");
                        $("#pdftemplate-list-overview").find("tbody").find("tr:last").before("<tr id='pdftemplate-" + data[i][0] + "'><td><input type='checkbox' id='" + data[i][0] + "' name='" + data[i][0] + "-select'></td><td style='cursor: pointer;' onclick='window.location.href=\"PdfTemplateDetail?id=" + data[i][0] + "\";' >" + data[i][0] + "</td><td style='cursor: pointer;' onclick='window.location.href=\"PdfTemplateDetail?id=" + data[i][0] + "\";' >" + data[i][1] + "</td></tr>");
                    }
                    $("#pdftemplates-count").text(data.length);
                } else {
                    $("#pdftemplates-count").text("Keine");
                }
            },
            error: function (data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = $("#pdfetemplate-list-load-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    var deletePdfTemplates = function (selectedCheckboxes) {
        $.ajax({
            url: $("#pdfetemplate-delete").data("pdfdelete"),
            data: {
                templateId: selectedCheckboxes
            },
            type: 'POST',
            success: function (data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                $('#pdftemplate-list-overview tr:not(:first):not(:last)').remove();
                loadPdfTemplateList();
            },
            error: function (data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = $("#pdfetemplate-delete-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    var executePdfTemplateDeletion = function () {
        var selected = [];
        $('#pdftemplate-list-overview input:checked').each(function () {
            if ($(this).attr('id') != "toggleAllSelect") {
                selected.push($(this).attr('id'));
            }
        });
        deletePdfTemplates(selected);
    };

    var comparer = function (index) {
        return function (a, b) {
            var valA = getCellValue(a, index), valB = getCellValue(b, index);
            return $.isNumeric(valA) && $.isNumeric(valB) ? valA - valB : valA.toString().localeCompare(valB)
        }
    };

    var initHide = function () {
        $('.none').show();
        $('#upID').hide();
        $('#downID').hide();
        $('#upTemplateName').hide();
        $('#downTemplateName').hide();
    };

    var hideTemplateName = function () {
        $('.noneTemplateName').show();
        $('#upTemplateName').hide();
        $('#downTemplateName').hide();

    };

    var hideID = function () {
        $('.noneID').show();
        $('#upID').hide();
        $('#downID').hide();
    };

    var getCellValue = function (row, index) {
        return $(row).children('td').eq(index).text()
    };

    $(document).ready(function () {
        $("#deletePdfTemplateButton").click(executePdfTemplateDeletion);
        loadPdfTemplateList();
        initHide();

        $('#ID, #templateName').click(function () {
            var table = $(this).parents('table').eq(0);
            var rows = table.find('tr:gt(0)').toArray().sort(comparer($(this).index()));
            this.asc = !this.asc;
            if (!this.asc) {
                rows = rows.reverse()
            }
            for (var i = 0; i < rows.length; i++) {
                table.append(rows[i])
            }
        });

        var flagID = false;
        $('#ID').click(function () {
            $('.noneID').hide();
            hideTemplateName();
            if (flagID === false) {
                $('#upID').show();
                $('#downID').hide();
                flagID = true;
            }
            else {
                $('#upID').hide();
                $('#downID').show();
                flagID = false;
            }
        });

        var flagTemplateNate = false;
        $('#templateName').click(function () {
            $('.noneTemplateName').hide();
            hideID();
            if (flagTemplateNate === false) {
                $('#upTemplateName').show();
                $('#downTemplateName').hide();
                flagTemplateNate = true;
            }
            else {
                $('#upTemplateName').hide();
                $('#downTemplateName').show();
                flagTemplateNate = false;
            }
        });
    });
})();
