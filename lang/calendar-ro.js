// ** I18N
Calendar._DN = new Array
("Duminic�",
 "Luni",
 "Mar�i",
 "Miercuri",
 "Joi",
 "Vineri",
 "S�mb�t�",
 "Duminic�");
Calendar._SDN_len = 2;
Calendar._MN = new Array
("Ianuarie",
 "Februarie",
 "Martie",
 "Aprilie",
 "Mai",
 "Iunie",
 "Iulie",
 "August",
 "Septembrie",
 "Octombrie",
 "Noiembrie",
 "Decembrie");

// tooltips
Calendar._TT = {};

Calendar._TT["INFO"] = "Despre calendar";

Calendar._TT["ABOUT"] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2003\n" + // don't translate this this ;-)
"Pentru ultima versiune vizita�i: http://dynarch.com/mishoo/calendar.epl\n" +
"Distribuit sub GNU GPL.  See http://gnu.org/licenses/gpl.html for details." +
"\n\n" +
"Selec�ia datei:\n" +
"- Folosi�i butoanele \xab, \xbb pentru a selecta anul\n" +
"- Folosi�i butoanele " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " pentru a selecta luna\n" +
"- Tine�i butonul mouse-ului ap�sat pentru selec�ie mai rapid�.";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"Selec�ia orei:\n" +
"- Chick pe ora sau minut pentru a m�ri valoarea cu 1\n" +
"- Sau Shift-Click pentru a mic�ora valoarea cu 1\n" +
"- Sau Click �i drag pentru a selecta mai repede.";

Calendar._TT["PREV_YEAR"] = "Anul precedent (lung pt menu)";
Calendar._TT["PREV_MONTH"] = "Luna precedent� (lung pt menu)";
Calendar._TT["GO_TODAY"] = "Data de azi";
Calendar._TT["NEXT_MONTH"] = "Luna urm�toare (lung pt menu)";
Calendar._TT["NEXT_YEAR"] = "Anul urm�tor (lung pt menu)";
Calendar._TT["SEL_DATE"] = "Selecteaz� data";
Calendar._TT["DRAG_TO_MOVE"] = "Trage pentru a mi�ca";
Calendar._TT["PART_TODAY"] = " (ast�zi)";
Calendar._TT["MON_FIRST"] = "Prima zi -> Luni";
Calendar._TT["SUN_FIRST"] = "Prima zi -> Duminic�";
Calendar._TT["CLOSE"] = "�nchide";
Calendar._TT["TODAY"] = "Ast�zi";
Calendar._TT["TIME_PART"] = "(Shift-)Click sau drag pentru a selecta";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "%d-%m-%Y";
Calendar._TT["TT_DATE_FORMAT"] = "%A, %d %B";

Calendar._TT["WK"] = "spt";
