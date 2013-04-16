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
		| 1		| Aufgabe 1 | Beschreibung 1 | 12.04.2013 | 13.04.2013 	| 		0				| Barny Geröllheimer | 1			|
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
		| "Titel mit einhundertneun Zeichen Text"	|	Aufgabe nicht erfolgreich geändert	|
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
		| "Beschreibung mit eintausendundeinundzwanzig Zeichen Text"	|	Aufgabe nicht erfolgreich geändert	|
		| "Beschreibung mit Sonderzeichen"								|	Aufgabe erfolgreich geändert		|
		| "Beschreibung mit Javascript"									|	Aufgabe erfolgreich geändert		|


Szenariogrundriss: Ich bearbeite das Feld Startdatum
	Angenommen ich klicke auf die Aufgabe mit der ID 1
	 Und ich sehe "Detailansicht Aufgabe"
	 Und ich sehe "Aufgabe Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>:
		| Testdaten							|	Meldung								|
		| "Startdatum mit leerem Feld"		|	Aufgabe nicht erfolgreich geändert	|
		| "Startdatum valide"				|	Aufgabe erfolgreich geändert		|
		| "Startdatum mit Sonderzeichen"	|	Aufgabe nicht erfolgreich geändert	|
		| "Startdatum mit Javascript"		|	Aufgabe nicht erfolgreich geändert	|
		| "Startdatum mit Monatsname"		|	Aufgabe nicht erfolgreich geändert	|
		 
	 
Szenariogrundriss: Ich bearbeite das Feld Enddatum
	Angenommen ich klicke auf die Aufgabe mit der ID 1
	 Und ich sehe "Detailansicht Aufgabe"
	 Und ich sehe "Aufgabe Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>:
		| Testdaten							|	Meldung								|
		| "Enddatum mit leerem Feld"		|	Aufgabe nicht erfolgreich geändert	|
		| "Enddatum valide"					|	Aufgabe erfolgreich geändert		|
		| "Enddatum mit Sonderzeichen"		|	Aufgabe nicht erfolgreich geändert	|
		| "Enddatum mit Javascript"			|	Aufgabe nicht erfolgreich geändert	|
		| "Enddatum mit Monatsname"			|	Aufgabe nicht erfoglreich genändert	|


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
	 UUnd ich fülle die Maske mit <Testdaten> aus
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
	| 1							| Aufgabe 2 | Beschreibung 2 | 15.04.2013 | 16.04.2013 	| 20   					| Fred Feuerstein | 3		  |


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
	Dann sehe ich "Barny Gerölheimer" nicht
	
	
	
	
	 
	

		
# Es fehlt noch das Springen zwischen den Reitern


#
# Wenn das mit den Szenariogrundrissen oben so okay ist, dann können diese Szenarien gelöscht werden!
#
#Szenario: Ich bearbeite eine Aufgabe lasse das Pflichtfeld Titel leer 
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Titel mit leerem Pflichtfeld" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 
	 

#Szenario: Ich bearbeite eine Aufgabe und gebe 109 Zeichen in das Pflichtfeld Titel ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Titel mit einhundertneun Zeichen Text" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	 
	
	
#Szenario: Ich bearbeite eine Aufgabe und gebe 100 Zeichen in das Pflichtfeld Titel ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Titel mit hundert Zeichen Text" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	


#Szenario: Ich bearbeite eine Aufgabe und gebe 99 Zeichen in das Pflichtfeld Titel ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Titel mit neunundneunzig Zeichen Text" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 


#Szenario: Ich bearbeite eine Aufgabe und gebe 100 Zeichen und Sonderzeichen in das Pflichtfeld Titel ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Titel mit Sonderzeichen" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	 
	

#Szenario: Ich bearbeite eine Aufgabe und gebe JavaScript in das Pflichtfeld Titel ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Titel mit Javascript" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	
	

#Szenario: Ich bearbeite eine Aufgabe und lasse das Feld Beschreibung leer
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Beschreibung mit leerem Feld" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	
	

#Szenario: Ich bearbeite eine Aufgabe und gebe 999 Zeichen in Feld Beschreibung ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Beschreibung mit neunhundertneunundneunzig Zeichen Text" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	
	
	
#Szenario: Ich bearbeite eine Aufgabe und gebe 1000 Zeichen in Feld Beschreibung ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Beschreibung mit eintausend Zeichen Text" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	
	
	
#Szenario: Ich bearbeite eine Aufgabe und gebe 1021 Zeichen in Feld Beschreibung ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Beschreibung mit eintausendundeinundzwanzig Zeichen Text" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	
	

#Szenario: Ich bearbeite eine Aufgabe und gebe 100 Zeichen und Sonderzeichen in das Feld Beschreibung ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Beschreibung mit Sonderzeichen" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	
	
	
#Szenario: Ich bearbeite eine Aufgabe und gebe JavaScript in das Feld Beschreibung ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Beschreibung mit Javascript" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 
	
	
#Szenario: Ich bearbeite eine Aufgabe und lasse das Feld Startdatum leer
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Startdatum mit leerem Feld" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	
	

#Szenario: Ich bearbeite eine Aufgabe und gebe ein valides Datum in Feld Startdatum ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Startdatum valide" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	

	
#Szenario: Ich bearbeite eine Aufgabe und gebe ein valides Datum in Feld Enddatum ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Enddatum valide" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	


#Szenario: Ich bearbeite eine Aufgabe und gebe Javascript in Feld Enddatum ein
#	Angenommen ich klicke auf die Aufgabe mit der ID 1
#	 Und ich sehe "Detailansicht Aufgabe"
#	 Und ich sehe "Aufgabe Detailansicht"
#	 Und ich "Enddatum mit Javascript" eingebe
#	Wenn ich auf "Speichern" klicke
#	#Im Moment nicht feststellbar, welche Maske und welche Fehlermeldung dann angezeigt wird
#	Dann sehe ich ... 	