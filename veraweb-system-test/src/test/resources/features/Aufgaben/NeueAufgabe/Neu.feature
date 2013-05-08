# language: de
Funktionalität: Als User mit entsprechenden Rechte möchte ich eine neue Aufgabe anlegen können.


Grundlage: Ich bin als Administrator angemeldet und habe mind. eine Veranstaltung angelegt und bin in der Aufgabenliste einer Veranstaltung 
	Angenommen ich bin als Administrator angemeldet
	 Und es existieren die Personen:
		|  Vorname	| Nachname 		| 
		|  Fred 	| Feuerstein	|
		|  Barny	| Geröllheimer	| 
	 Und es existiert eine Veranstaltung "Veranstaltung 1" mit folgenden Aufgaben:
		| ID	| Titel 	| Beschreibung	 | Start 	  | Ende 		| Fertigstellungsgrad	| Verantwortlicher	 | Priorität | 
		| 1		| Aufgabe 1 | Beschreibung 1 | heute(+1)  | heute(+2) 	| 		0				| Barny  			 | 1		 |
	 Und ich bin in der Übersicht aller Aufgaben der Veranstaltung "Veranstaltung 1"
	 
	 
	 
Szenariogrundriss: Ich lege eine neue Aufgabe an
	Angenommen ich klicke auf "Neu"
	 Und ich sehe "Neue Aufgabe erstellen"
	 Und ich fülle die Maske mit <Testdaten> aus
	Aber ohne "Verantwortlicher"
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten														|	Meldung										|
		| "Aufgabe mit leeren Feldern"									|	Pflichtfeld "Titel" muss ausgefüllt sein	|
		| "Titel mit leerem Pflichtfeld"								|	Pflichtfeld "Titel" muss ausgefüllt sein	|
		| "Titel mit einhundertneun Zeichen Text"						|	Maximale Zeichenlänge überschritten			|
		| "Titel mit hundert Zeichen Text"								|	Aufgabe erfolgreich angelegt				|
		| "Titel mit neunundneunzig Zeichen Text"						|	Aufgabe erfolgreich angelegt				|
		| "Titel mit Sonderzeichen"										|	Aufgabe erfolgreich angelegt				|
		| "Titel mit Javascript"										|	Aufgabe erfolgreich angelegt				|		 
		| "Beschreibung mit leerem Feld"								|	Aufgabe erfolgreich angelegt				|
		| "Beschreibung mit neunhundertneunundneunzig Zeichen Text"		|	Aufgabe erfolgreich angelegt				|
		| "Beschreibung mit eintausend Zeichen Text"					|	Aufgabe erfolgreich angelegt				|
		| "Beschreibung mit eintausendundeinundzwanzig Zeichen Text"	|	Maximale Zeichenlänge überschritten			|
		| "Beschreibung mit Sonderzeichen"								|	Aufgabe erfolgreich angelegt				|
		| "Beschreibung mit Javascript"									|	Aufgabe erfolgreich angelegt				|
		| "Startdatum mit leerem Feld"									|	Aufgabe erfolgreich angelegt				|
		| "Startdatum valide"											|	Aufgabe erfolgreich angelegt				|
		| "Startdatum mit Sonderzeichen"								|	Valide Daten dd.mm.yyyy eingeben			|
		| "Startdatum mit Javascript"									|	Valide Daten dd.mm.yyyy eingeben			|
		| "Startdatum mit Monatsname"									|	Valide Daten dd.mm.yyyy eingeben			|
		| "Enddatum mit leerem Feld"									|	Aufgabe erfolgreich angelegt				|
		| "Enddatum valide"												|	Aufgabe erfolgreich angelegt				|
		| "Enddatum mit Sonderzeichen"									|	Valide Daten dd.mm.yyyy eingeben			|
		| "Enddatum mit Javascript"										|	Valide Daten dd.mm.yyyy eingeben			|
		| "Enddatum mit Monatsname"										|	Valide Daten dd.mm.yyyy eingeben			|
		| "Enddatum vor Startdatum"										|	Startdatum muss vor Enddatum liegen			|
		| "Enddatum in Vergangenheit"									|	Enddatum muss in Zukunft liegen				|
		| "FERTIGSTELLUNG_GEANDERT_10"									|	Aufgabe erfolgreich angelegt				|
		| "FERTIGSTELLUNG_GEANDERT_50"									|	Aufgabe erfolgreich angelegt				|
		| "FERTIGSTELLUNG_GEANDERT_90"									|	Aufgabe erfolgreich angelegt				|
		| "PRIORITAET_GEANDERT_2"										|	Aufgabe erfolgreich angelegt				|
		| "PRIORITAET_GEANDERT_4" 										|	Aufgabe erfolgreich angelegt				|
		| "PRIORITAET_GEANDERT_6"										|	Aufgabe erfolgreich angelegt				|
#muss noch geklärt werden
	

Szenario: Ich lege eine neue Aufgabe an und teste die Weiterleitung zur Verantwortlichen-Suchmaske und zurück
	Angenommen ich klicke auf "Neu"
	 Und ich sehe "Neue Aufgabe erstellen"
	 Und ich klicke auf "Suchen"
	 Und ich sehe "Verantwortlichen für Aufgabe suchen" 
	 Und ich klicke auf "Suche starten"
	Wenn ich "Fred Feuerstein" auswähle
	Dann sehe ich "Neue Aufgabe erstellen"
	 Und ich sehe "Aufgabe Detailansicht"
	 Und ich sehe als verantwortliche Person "Fred Feuerstein"
	 
	 
Szenario: Ich lege eine neue Aufgabe an und teste, ob das Startdatum hintern dem Enddatum liegen kann
	Angenommen ich klicke auf "Neu"
	 Und ich sehe "Neue Aufgabe erstellen"
 

	
