// This file is part of VerA.web and published under the same licence.

(function(){
    var goToCreatePerson = function() {
        window.location.href='CreatePerson?action=guest';
    };

	$(document).ready(function() {
	    $("#addPersonFromGuestSearch").click(goToCreatePerson);
    });
})();
