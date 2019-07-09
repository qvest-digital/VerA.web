$(function() {
  var form = $("#guestListFilter");
  var action = form.attr("action");
  var doSubmit = function() {
    $.post(action, form.serialize()) //Serialize looks good name=textInNameInput&&telefon=textInPhoneInput---etc
      .done(function(data) {
        var html = $.parseHTML(data);
        $("#guestsOnPages").replaceWith($(html).find("#guestsOnPages"));
        $("#guestListWrapper").replaceWith($(html).find("#guestListWrapper"));
        $("#guestSummary").replaceWith($(html).find("#guestSummary"));
      });
    return false;
  };
  //based on an example from the Bacon.js site

  var text = $("#search-keywords")
    .asEventStream('input')
    .debounce(300)
    .map(function(event) {
      return event.target.value;
    })
    .skipDuplicates()
    .onValue(function() {
      //form.get(0).submit();
      doSubmit();
    });

});
