// ** I18N

// Calendar PL language
// Author: Dariusz Pietrzak, <eyck@ghost.anime.pl>
// Author: Janusz Piwowarski, <jpiw@go2.pl>
// Encoding: iso-8859-2
// Distributed under the same terms as the calendar itself.

Calendar._DN = new Array
("Niedziela",
 "Poniedzia³ek",
 "Wtorek",
 "¦roda",
 "Czwartek",
 "Pi±tek",
 "Sobota",
 "Niedziela");
Calendar._SDN = new Array
("Nie",
 "Pn",
 "Wt",
 "¦r",
 "Cz",
 "Pt",
 "So",
 "Nie");
Calendar._MN = new Array
("Styczeñ",
 "Luty",
 "Marzec",
 "Kwiecieñ",
 "Maj",
 "Czerwiec",
 "Lipiec",
 "Sierpieñ",
 "Wrzesieñ",
 "Pa¼dziernik",
 "Listopad",
 "Grudzieñ");
Calendar._SMN = new Array
("Sty",
 "Lut",
 "Mar",
 "Kwi",
 "Maj",
 "Cze",
 "Lip",
 "Sie",
 "Wrz",
 "Pa¼",
 "Lis",
 "Gru");

// tooltips
Calendar._TT = {};
Calendar._TT["INFO"] = "O kalendarzu";

Calendar._TT["ABOUT"] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2003\n" + // don't translate this this ;-)
"Aby pobraæ najnowsz± wersjê, odwied¼: http://dynarch.com/mishoo/calendar.epl\n" +
"Dostêpny na licencji GNU LGPL. Zobacz szczegó³y na http://gnu.org/licenses/lgpl.html." +
"\n\n" +
"Wybór daty:\n" +
"- U¿yj przycisków \xab, \xbb by wybraæ rok\n" +
"- U¿yj przycisków " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " by wybraæ miesi±c\n" +
"- Przytrzymaj klawisz myszy nad jednym z powy¿szych przycisków dla szybszego wyboru.";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"Wybór czasu:\n" +
"- Kliknij na jednym z pól czasu by zwiêkszyæ jego warto¶æ\n" +
"- lub kliknij trzymaj±c Shift by zmiejszyæ jego warto¶æ\n" +
"- lub kliknij i przeci±gnij dla szybszego wyboru.";

//Calendar._TT["TOGGLE"] = "Zmieñ pierwszy dzieñ tygodnia";
Calendar._TT["PREV_YEAR"] = "Poprzedni rok (przytrzymaj dla menu)";
Calendar._TT["PREV_MONTH"] = "Poprzedni miesi±c (przytrzymaj dla menu)";
Calendar._TT["GO_TODAY"] = "Id¼ do dzisiaj";
Calendar._TT["NEXT_MONTH"] = "Nastêpny miesi±c (przytrzymaj dla menu)";
Calendar._TT["NEXT_YEAR"] = "Nastêpny rok (przytrzymaj dla menu)";
Calendar._TT["SEL_DATE"] = "Wybierz datê";
Calendar._TT["DRAG_TO_MOVE"] = "Przeci±gnij by przesun±æ";
Calendar._TT["PART_TODAY"] = " (dzisiaj)";
Calendar._TT["MON_FIRST"] = "Wy¶wietl poniedzia³ek jako pierwszy";
Calendar._TT["SUN_FIRST"] = "Wy¶wietl niedzielê jako pierwsz±";
Calendar._TT["CLOSE"] = "Zamknij";
Calendar._TT["TODAY"] = "Dzisiaj";
Calendar._TT["TIME_PART"] = "(Shift-)Kliknij lub przeci±gnij by zmieniæ warto¶æ";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "%Y-%m-%d";
Calendar._TT["TT_DATE_FORMAT"] = "%e %B, %A";

Calendar._TT["WK"] = "ty";
