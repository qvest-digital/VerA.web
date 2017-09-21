// This file is part of VerA.web and published under the same licence.

(function () {
	$(document).ready(function () {
		CKEDITOR.replace('mailtext'); // Enable CKEditor
		$("input[name='send']").click(executeAllActions);
		$("input[name='add']").click(addPlatzhalter);
	});

	function executeAllActions() {
		rewriteAction();
		setEmailContent();
		executeSubmit();
	}

	function executeSubmit() {
		$('#formlist').submit();
	}

	function setEmailContent() {
		var data = CKEDITOR.instances.mailtext.getData();
		$("#mailtext").val(data);
	}

	function rewriteAction() {
		$("#formlist").attr("action", $("#formaction").data("formaction"));
	}

	function addPlatzhalter(s) {
		var sel = document.getElementById('platzhalter');
		var text = sel.options[sel.selectedIndex].value;
		if (text.length > 0) {
			CKEDITOR.instances.mailtext.insertText('<' + text + '>');
		}
	}

	function disableSubmitButton() {
		$('[type="button"]').attr('disabled', 'disabled').addClass('disabled');
	}

	function enableSubmitButton() {
		$('[type="button"]').removeAttr('disabled').removeClass('disabled');
	}

	function displaySpinner() {
		$('#spinner').show();
	}

	function hideSpinner() {
		$('#spinner').hide();
	}
	$(document).ready(function () {
		$('#formlist').ajaxForm({
			beforeSubmit: function (arr, $form, options) {
				$("#formlist").attr("action", "${paths.staticWeb}mailing");
				displaySpinner();
				disableSubmitButton();
			},
			error: function (response) {
				$(".errormsg").remove();
				if (response.status === 400) {
					var arr = response.responseText.split("\n");
					var text = '$errorMsgAddress' + "<br><br>";
					for (i = 0; i < arr.length; i++) {
						var syntax = arr[i].indexOf("ADDRESS_SYNTAX_NOT_CORRECT:");
						if (syntax != -1) {
							var mail = arr[i].indexOf(":");
							text += arr[i].substring(mail + 1) + "<br>";
						}
					}
					showWarning(text);
				} else if (response.status === 502) {
					showWarning('$errorMsgVerteiler');
				} else {
					showWarning('$errorMsg');
				}
				hideSpinner();
				enableSubmitButton();
			},
			success: function () {
				$(".errormsg").remove();
				hideSpinner();
				enableSubmitButton();
				$('#main_content').hide();
				$('#success').show();
			}
		});
	});
})();
