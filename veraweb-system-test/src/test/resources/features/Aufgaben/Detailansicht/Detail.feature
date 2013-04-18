# language: de

Funktionalität: Als User mit entsprechenden Rechte möchte ich Details einer Aufgabe aus der Listenansicht sehen können und die Aufgabendetails bearbeiten oder löschen können.

Grundlage: Ich bin als Administrator angemeldet und bin in der Aufgabenliste einer Veranstaltungen und habe mind. eine Veranstaltung angelegt
	Angenommen ich bin als Administrator angemeldet
	 Und es existieren die Personen:
		|  Vorname	| Nachname 		| 
		|  Fred 	| Feuerstein	|
		|  Barny	| Geröllheimer	| 
	 Und es existiert die Veranstaltung:
	 	| Name  		  | Beginn 	   |
	 	| Veranstaltung 1 | 31.12.2013 |
	 Und die Veranstaltung hat folgende Aufgaben:
		| ID	| Titel 	| Beschreibung	 | Start 	  | Ende 		| Fertigstellungsgrad	| Verantwortliche	 | Priorität | 
		| 1		| Aufgabe 1 | Beschreibung 1 | 12.04.2013 | 13.04.2013 	| 		0				| Barny Geröllheimer | 1		 |
	 Und ich bin in der Übersicht aller Aufgaben der Veranstaltung	 
	 

Szenariogrundriss: Ich bearbeite das Pflichtfeld Titel
	Angenommen ich klicke auf die Aufgabe mit der ID 1
	 Und ich sehe "Detailansicht Aufgabe"
	 Und ich sehe "Aufgabe Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>:
		| Testdaten									|	Meldung								|
		| "Titel mit leerem Pflichtfeld"			|	Pflichtfeld muss ausgefüllt sein	|
		| "Titel mit einhundertneun Zeichen Text"	|	Maximale Zeichenlänge überschritten	|
		| "Titel mit hundert Zeichen Text"			|	Aufgabe erfolgreich geändert		|
		| "Titel mit neunundneunzig Zeichen Text"	|	Aufgabe erfolgreich geändert		|
		| "Titel mit Sonderzeichen"					|	Aufgabe erfolgreich geändert		|
		| "Titel mit Javascript"					|	Aufgabe erfolgreich geändert		| #muss noch geklärt werden	
	 

Szenariogrundriss: Ich bearbeite das Feld Beschreibung
 	Angenommen ich klicke auf die Aufgabe mit der ID 1
	 Und ich sehe "Detailansicht Aufgabe"
	 Und ich sehe "Aufgabe Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>:
		| Testdaten														|	Meldung								|
		| "Beschreibung mit leerem Feld"								|	Aufgabe erfolgreich geändert		|
		| "Beschreibung mit neunhundertneunundneunzig Zeichen Text"		|	Aufgabe erfolgreich geändert		|
		| "Beschreibung mit eintausend Zeichen Text"					|	Aufgabe erfolgreich geändert		|
		| "Beschreibung mit eintausendundeinundzwanzig Zeichen Text"	|	Maximale Zeichenlänge überschritten	|
		| "Beschreibung mit Sonderzeichen"								|	Aufgabe erfolgreich geändert		|
		| "Beschreibung mit Javascript"									|	Aufgabe erfolgreich geändert		|#muss noch geklärt werden


Szenariogrundriss: Ich bearbeite das Feld Startdatum
	Angenommen ich klicke auf die Aufgabe mit der ID 1
	 Und ich sehe "Detailansicht Aufgabe"
	 Und ich sehe "Aufgabe Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>:
		| Testdaten							|	Meldung								|
		| "Startdatum mit leerem Feld"		|	Valide Daten dd.mm.yyyy eingeben	|
		| "Startdatum valide"				|	Aufgabe erfolgreich geändert		|
		| "Startdatum mit Sonderzeichen"	|	Valide Daten dd.mm.yyyy eingeben	|
		| "Startdatum mit Javascript"		|	Valide Daten dd.mm.yyyy eingeben	|
		| "Startdatum mit Monatsname"		|	Valide Daten dd.mm.yyyy eingeben	|
		 
	 
Szenariogrundriss: Ich bearbeite das Feld Enddatum
	Angenommen ich klicke auf die Aufgabe mit der ID 1
	 Und ich sehe "Detailansicht Aufgabe"
	 Und ich sehe "Aufgabe Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>:
		| Testdaten							|	Meldung								|
		| "Enddatum mit leerem Feld"		|	Aufgabe erfolgreich geändert		|# muss noch geklärt werden - kann das Enddatum wirklich offen bleiben nach der Bearbeitung?	
		| "Enddatum valide"					|	Aufgabe erfolgreich geändert		|
		| "Enddatum mit Sonderzeichen"		|	Valide Daten dd.mm.yyyy eingeben	|
		| "Enddatum mit Javascript"			|	Valide Daten dd.mm.yyyy eingeben	|
		| "Enddatum mit Monatsname"			|	Valide Daten dd.mm.yyyy eingeben	|


Szenariogrundriss: Ich bearbeite das Feld Fertigstellung
	Angenommen ich klicke auf die Aufgabe mit der ID 1
	 Und ich sehe "Detailansicht Aufgabe"
	 Und ich sehe "Aufgabe Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>:
		| Testdaten							|	Meldung								|
		| "FERTIGSTELLUNG_GEANDERT_10"		|	Aufgabe erfolgreich geändert		|
		| "FERTIGSTELLUNG_GEANDERT_50"		|	Aufgabe erfolgreich geändert		|
		| "FERTIGSTELLUNG_GEANDERT_90"		|	Aufgabe erfolgreich geändert		|

Szenariogrundriss: Ich bearbeite das Feld Priorität
	Angenommen ich klicke auf die Aufgabe mit der ID 1
	 Und ich sehe "Detailansicht Aufgabe"
	 Und ich sehe "Aufgabe Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich die <Meldung>:
		| Testdaten							|	Meldung								|
		| "PRIORITAET_GEANDERT_2"			|	Aufgabe erfolgreich geändert		|
		| "PRIORITAET_GEANDERT_4" 			|	Aufgabe erfolgreich geändert		|
		| "PRIORITAET_GEANDERT_6"			|	Aufgabe erfolgreich geändert		|


Szenario: Die Änderungen einer Aufgabe werden in der Aufgabenliste angezeigt
	Angenommen ich klicke auf die Aufgabe mit der ID 1
	 Und ich sehe "Detailansicht Aufgabe"
	 Und ich sehe "Aufgabe Detailansicht"
	 Und ich fülle die Maske mit "AUFGABE_DETAILANSICHT_GEÄNDERT" aus
	 Und ich klicke auf "Speichern"
	Wenn ich auf "Zurück" klicke
	Dann sieht die Tabelle folgendermaßen aus:
	| CheckboxOhneBezeichnung	| ID		| Titel			 | Start 	  | Ende 		| Fertigstellungsgrad 	| Verantwortliche | Priorität | 
	| Checkbox					| 1			| Aufgabe 2 	 | 15.04.2013 | 16.04.2013 	| 20   					| Fred Feuerstein | 3		  |


Szenario: ich prüfe die Weiterleitung auf die Personensuche
	Angenommen ich klicke auf die Aufgabe mit der ID 1
	Wenn ich auf "Suchen" klicke
	Dann sehe ich die Personensuchmaske 
	Und die Maske sieht folgendermaßen aus:
		| Vorname: 				| 	
		| Nachname:				| 	
		| Amtsbez./Funktion:	|	
		| Firma/Institution: 	|	
		| Straße: 				|
		| PLZ: 					|
		| Bundesland: 			|
		| Ort: 					|
		| Land: 				|
		| Kategorie: 			|
		| Arbeitsbereich: 		|
		| Ereignis 				|
		| Ist Firma: 			|
		| Datenherkunft: 		|	
		| Gültigkeitsdatum:		|
	

Szenario: Ich füge eine verantwortliche Person hinzu
	Angenommen ich klicke auf die Aufgabe mit der ID 1
	 Und ich klicke auf "Suchen"
	 Und ich sehe die Personensuchmaske 
	 Und ich klicke auf "Suche starten"
	Wenn ich "Fred Feuerstein" auswähle
	Dann sehe ich die Maske zur Bearbeitung der Aufgabe
	Und ich sehe als verantwortliche Person "Fred Feuerstein"
	
Szenario: Ich entferne eine verantwortliche Person
	Angenommen ich klicke auf die Aufgabe mit der ID 1
	Wenn ich auf "Entfernen" klicke
	Dann sehe ich "Barny Geröllheimer" nicht
	

		
# Es fehlt noch das Springen zwischen den Reitern
