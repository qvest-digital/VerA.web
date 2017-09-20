// This file is part of VerA.web and published under the same licence.

(function(){

	function addPlatzhalter(s) {
		var sel = document.getElementById('platzhalter');
		var text = sel.options[sel.selectedIndex].value;
		if (text.length > 0) {
			CKEDITOR.instances.maildrafttext.insertText('<' + text + '>');
		}
	}

    function setEmailContent() {
        var data = CKEDITOR.instances.maildrafttext.getData();
        $("#maildrafttext").val(data);
    }

    function executeSave() {
        setEmailContent();
        $('#formlist').submit();
    }

    $(document).ready(function() {
        CKEDITOR.replace('maildrafttext');
        $("input[name='send']").click(executeSave);
        $("input[name='add']").click(addPlatzhalter);
    });
})();
