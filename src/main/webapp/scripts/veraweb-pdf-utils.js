$(document).ready(function() {
    $.ajax({
        url: $("#pdfetemplate-list").data("pdfexport"),
        data: {
            mandantid:$("#pdfetemplate-orgunit").data("orgunit")
        },
        type: 'GET',
        success: function(data){
            select = document.getElementById("pdftemplate-list")

            for (var i = 0; i < data.length; i++) {
                var at = data[i];
                var id = at.fk_orgunit;
                var name = at.name;
                var option = document.createElement("option");
                option.value = id;
                option.textContent = name;
                select.appendChild(option);
            };
        },
        error: function(data) {
            alert('woops!');
        }
    });
});