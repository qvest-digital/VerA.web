// ** I18N
Calendar._DN = new Array
("Domingo",
 "Lunes",
 "Martes",
 "Mi�rcoles",
 "Jueves",
 "Viernes",
 "S�bado",
 "Domingo");
Calendar._MN = new Array
("Enero",
 "Febrero",
 "Marzo",
 "Abril",
 "Mayo",
 "Junio",
 "Julio",
 "Agosto",
 "Septiembre",
 "Octubre",
 "Noviembre",
 "Diciembre");

// tooltips
Calendar._TT = {};
Calendar._TT["INFO"] = "Acerca del calendario";

Calendar._TT["ABOUT"] =
"Control DHTML de Fecha/Hora\n" +
"(c) dynarch.com 2002-2003\n" + // don't translate this this ;-)
"Puedes encontrar la �ltima versi�n en: http://dynarch.com/mishoo/calendar.epl\n" +
"Distribuido bajo GNU LGPL.  M�s detalles en http://gnu.org/licenses/lgpl.html ." +
"\n\n" +
"Selecci�n de fecha:\n" +
"- Usa los botones \xab y \xbb para seleccionar el a�o\n" +
"- Usa los botones " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " para seleccionar el mes\n" +
"- Mant�n el bot�n del rat�n en cualquiera de los botones anteriores para una selecci�n m�s r�pida.";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"Selecci�n de hora:\n" +
"- Haz click en una de las partes de la hora para incrementarla\n" +
"- o Mays-click para decrementarla\n" +
"- o haz click y arrastra para una selecci�n m�s r�pida.";

Calendar._TT["PREV_YEAR"] = "A�o anterior (mantener para menu)";
Calendar._TT["PREV_MONTH"] = "Mes anterior (mantener para menu)";
Calendar._TT["GO_TODAY"] = "Ir a hoy";
Calendar._TT["NEXT_MONTH"] = "Mes siguiente (mantener para menu)";
Calendar._TT["NEXT_YEAR"] = "A�o siguiente (mantener para menu)";
Calendar._TT["SEL_DATE"] = "Seleccionar fecha";
Calendar._TT["DRAG_TO_MOVE"] = "Arrastrar para mover";
Calendar._TT["PART_TODAY"] = " (hoy)";
Calendar._TT["MON_FIRST"] = "Mostrar lunes primero";
Calendar._TT["SUN_FIRST"] = "Mostrar domingo primero";
Calendar._TT["CLOSE"] = "Cerrar";
Calendar._TT["TODAY"] = "Hoy";
Calendar._TT["TIME_PART"] = "(Mays-)Click o arrastra para cambiar la hora";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "dd-mm-yy";
Calendar._TT["TT_DATE_FORMAT"] = "D, M d";

Calendar._TT["WK"] = "sem";
