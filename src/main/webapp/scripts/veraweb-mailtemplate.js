(function(){
    var executeLoadMailtemplate = function() {
        if($('#mail-draft').find(":selected").val() < 1) {
            $(".errormsg").remove();
            $(".successmsg").remove();
            var errorMsg = $("#selectmailtemplate-errormsg").data("errormsg");
            showWarning(errorMsg);
        } else {
            $.ajax({
                url: $("#loadmailtemplate").data("loadmailtemplate"),
                type: 'GET',
                data: {
                    templateId:$('#mail-draft').find(":selected").val(),
                    mandantId:$("#loadmailtemplate-orgunit").data("orgunit")
                },
                success: function(data){
                    $(".errormsg").remove();
                    $(".successmsg").remove();
                    $("#mail-subject").val(data.subject);
                    $("#mail-text").text(data.content);
                },
                error: function(data) {
                    $(".errormsg").remove();
                    $(".successmsg").remove();
                    var errorMsg = $("#loadmailtemplate-errormsg").data("errormsg");
                    showWarning(errorMsg);
                }
            });
        }
    };

    $(document).ready(function() {
        $("#loadMailtemplateButton").click(executeLoadMailtemplate);
    });
})();