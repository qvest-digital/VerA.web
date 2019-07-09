(function(){
    var goToCreatePerson = function() {
        window.location.href='CreatePerson?action=guest';
    };

	$(document).ready(function() {
	    $("#addPersonFromGuestSearch").click(goToCreatePerson);
    });
})();
