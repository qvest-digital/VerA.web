// ** I18N
// short day names

/*
Calendar._SDN = new Array
("Dim",
 "Lun",
 "Mar",
 "Mer",
 "Jeu",
 "Ven",
 "Sam",
 "Dim");
Calendar._SMN = new Array
("Jan",
 "Fév",
 "Mar",
 "Avr",
 "Mai",
 "Jun",
 "Jul",
 "Aoû",
 "Sep",
 "Oct",
 "Nov",
 "Déc");
*/

Calendar._DN = new Array
("Dimanche",
 "Lundi",
 "Mardi",
 "Mercredi",
 "Jeudi",
 "Vendredi",
 "Samedi",
 "Dimanche");

Calendar._MN = new Array
("Janvier",
 "Février",
 "Mars",
 "Avril",
 "Mai",
 "Juin",
 "Juillet",
 "Août",
 "Septembre",
 "Octobre",
 "Novembre",
 "Décembre");

Calendar._TT = {};
// tooltips
Calendar._TT["TOGGLE"] = "Changer le premier jour de la semaine";
Calendar._TT["PREV_YEAR"] = "Année préc. (maintenir pour menu)";
Calendar._TT["PREV_MONTH"] = "Mois préc. (maintenir pour menu)";
Calendar._TT["GO_TODAY"] = "Atteindre date du jour";
Calendar._TT["NEXT_MONTH"] = "Mois suiv. (maintenir pour menu)";
Calendar._TT["NEXT_YEAR"] = "Année suiv. (maintenir pour menu)";
Calendar._TT["SEL_DATE"] = "Choisir une date";
Calendar._TT["DRAG_TO_MOVE"] = "Déplacer";
Calendar._TT["PART_TODAY"] = " (Aujourd'hui)";
Calendar._TT["MON_FIRST"] = "Commencer par lundi";
Calendar._TT["SUN_FIRST"] = "Commencer par dimanche";
Calendar._TT["CLOSE"] = "Fermer";
Calendar._TT["TODAY"] = "Aujourd'hui";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "y-mm-dd";
Calendar._TT["TT_DATE_FORMAT"] = "D, M d";

Calendar._TT["WK"] = "wk";

// tooltips
Calendar._TT["INFO"] = "A propos du calendrier";

Calendar._TT["ABOUT"] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2003\n" + // don't translate this this ;-)
"Pour la dernière version allez sur http://dynarch.com/mishoo/calendar.epl\n" +
"Distribuée sous license GNU LGPL.  Voir http://gnu.org/licenses/lgpl.html pour détails." +
"\n\n" +
"Sélection d'une date:\n" +
"- Utilisez les boutons \xab et \xbb pour sélectionner l'année\n" +
"- Utilisez les boutons " + String.fromCharCode(0x2039) + " et " + String.fromCharCode(0x203a) + " pour sélectionner le mois\n" +
"- Gardez le bouton de la souris appuyée sur n'importe quel bouton pour une sélection plus rapide.";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"Sélection d'une heure:\n" +
"- Cliquez sur une partie de l'heure pour la faire augmenter\n" +
"- ou Shift-click pour la faire diminuer\n" +
"- or laissez votre bouton appuyé, puis bougez votre souris vers le haut ou le bas.";
