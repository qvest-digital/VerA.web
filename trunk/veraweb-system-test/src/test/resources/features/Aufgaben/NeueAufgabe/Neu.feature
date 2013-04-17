# language: de

Funktionalität: Als User mit entsprechenden Rechte möchte ich eine neue Aufgabe anlegen können.

Grundlage: Ich bin als Administrator angemeldet und habe mind. eine Veranstaltung angelegt und bin in der Aufgabenliste einer Veranstaltungen 
	Angenommen ich bin als Administrator angemeldet
	 Und es existieren die Personen:
		|  Vorname	| Nachname 		| 
		|  Fred 	| Feuerstein	|
		|  Barny	| Geröllheimer	| 
	 Und es existiert die Veranstaltung:
	 	| Name  		  | Beginn 	   |
	 	| Veranstaltung 1 | 31.12.2013 |
	 Und ich bin in der Übersicht aller Aufgaben der Veranstaltung
	 
	 
Szenariogrundriss: Ich lege eine neue Aufgabe an
	Angenommen ich klicke auf "Neu"
	 Und ich sehe "Neue Aufgabe erstellen"
	 Und ich fülle die Maske mit <Testdaten> aus
	Aber ohne "Verantwortlicher"
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>:
		| Testdaten														|	Meldung								|
		| "Aufgabe mit leeren Feldern"									|	Aufgabe nicht erfolgreich angelegt	|
		| "Titel mit leerem Pflichtfeld"								|	Pflichtfeld muss ausgefüllt sein	|
		| "Titel mit einhundertneun Zeichen Text"						|	Aufgabe nicht erfolgreich angelegt	|
		| "Titel mit hundert Zeichen Text"								|	Aufgabe erfolgreich angelegt		|
		| "Titel mit neunundneunzig Zeichen Text"						|	Aufgabe erfolgreich angelegt		|
		| "Titel mit Sonderzeichen"										|	Aufgabe erfolgreich angelegt		|
#muss noch geklärt werden		| "Titel mit Javascript"										|	Aufgabe erfolgreich angelegt		|	 
		| "Beschreibung mit leerem Feld"								|	Aufgabe erfolgreich angelegt		|
		| "Beschreibung mit neunhundertneunundneunzig Zeichen Text"		|	Aufgabe erfolgreich angelegt		|
		| "Beschreibung mit eintausend Zeichen Text"					|	Aufgabe erfolgreich angelegt		|
		| "Beschreibung mit eintausendundeinundzwanzig Zeichen Text"	|	Aufgabe nicht erfolgreich angelegt	|
		| "Beschreibung mit Sonderzeichen"								|	Aufgabe erfolgreich angelegt		|
		| "Beschreibung mit Javascript"									|	Aufgabe erfolgreich angelegt		|
		| "Startdatum mit leerem Feld"									|	Aufgabe nicht erfolgreich angelegt	|
		| "Startdatum valide"											|	Aufgabe erfolgreich angelegt		|
		| "Startdatum mit Sonderzeichen"								|	Aufgabe nicht erfolgreich angelegt	|
		| "Startdatum mit Javascript"									|	Aufgabe nicht erfolgreich angelegt	|
		| "Startdatum mit Monatsname"									|	Aufgabe nicht erfolgreich angelegt	|
		| "Enddatum mit leerem Feld"									|	Aufgabe nicht erfolgreich angelegt	|
		| "Enddatum valide"												|	Aufgabe erfolgreich angelegt		|
		| "Enddatum mit Sonderzeichen"									|	Aufgabe nicht erfolgreich angelegt	|
		| "Enddatum mit Javascript"										|	Aufgabe nicht erfolgreich angelegt	|
		| "Enddatum mit Monatsname"										|	Aufgabe nicht erfoglreich angelegt	|
		| "FERTIGSTELLUNG_GEANDERT_10"									|	Aufgabe erfolgreich angelegt		|
		| "FERTIGSTELLUNG_GEANDERT_50"									|	Aufgabe erfolgreich angelegt		|
		| "FERTIGSTELLUNG_GEANDERT_90"									|	Aufgabe erfolgreich angelegt		|
		| "PRIORITAET_GEANDERT_2"										|	Aufgabe erfolgreich angelegt		|
		| "PRIORITAET_GEANDERT_4" 										|	Aufgabe erfolgreich angelegt		|
		| "PRIORITAET_GEANDERT_6"										|	Aufgabe erfolgreich angelegt		|
	

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

	
