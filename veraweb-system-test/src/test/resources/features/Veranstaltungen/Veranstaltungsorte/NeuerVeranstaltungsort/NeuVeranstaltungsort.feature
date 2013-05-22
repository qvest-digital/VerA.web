# language: de
Funktionalität: Als User mit entsprechenden Rechte möchte ich einen neuen Veranstaltungsort anlegen können.


Grundlage: Ich bin als Administrator angemeldet 
	Angenommen ich bin als Administrator angemeldet
	 Und ich bin in der Übersicht aller Veranstaltungsorte 
	 
Szenariogrundriss: Ich lege einen neuen Veranstaltungsort an
	Angenommen ich klicke auf "Neu"
	 Und ich sehe "Neuen Veranstaltungsort eingeben"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten														|	Meldung													|
		| "Veranstaltungsort mit leeren Feldern"						|	Pflichtfeld "Beschreibung/Titel" muss ausgefüllt sein	|
		| "Veranstaltungsort mit leerem Pflichtfeld"					|	Pflichtfeld "Beschreibung/Titel" muss ausgefüllt sein	|
		| "Beschreibung/Titel mit 249 Zeichen"							|	Aufgabe erfolgreich angelegt							|
		| "Beschreibung/Titel mit 250 Zeichen"							|	Aufgabe erfolgreich angelegt							|
		| "Beschreibung/Titel mit 251 Zeichen"							|	Maximale Zeichenlänge überschritten						|
		| "Beschreibung/Titel mit 250 Zeichen mit Sonderzeichen"		|	Aufgabe erfolgreich angelegt							|
		| "Beschreibung/Titel mit Javascript"							|	Valide Daten eingeben									|	
		| "Ansprechpartner mit leerem Feld"								|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "Ansprechpartner mit 249 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Ansprechpartner mit 250 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Ansprechpartner mit 251 Zeichen"								|	Maximale Zeichenlänge überschritten						|
		| "Ansprechpartner mit 250 Zeichen mit Sonderzeichen"			|	Aufgabe erfolgreich angelegt							|
		| "Ansprechpartner mit Javascript"								|	Valide Daten eingeben									|
		| "Straße mit leerem Feld"										|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "Straße mit 249 Zeichen"										|	Aufgabe erfolgreich angelegt							|
		| "Straße mit 250 Zeichen"										|	Aufgabe erfolgreich angelegt							|
		| "Straße mit 251 Zeichen"										|	Maximale Zeichenlänge überschritten						|
		| "Straße mit 250 Zeichen mit Sonderzeichen"					|	Aufgabe erfolgreich angelegt							|
		| "Straße mit Javascript"										|	Valide Daten eingeben									|
		| "PLZ mit leerem Feld"											|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "PLZ mit 4 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "PLZ mit 5 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "PLZ mit 6 Zeichen"											|	Maximale Zeichenlänge überschritten						|
		| "PLZ mit 5 Zeichen mit Sonderzeichen"							|	Aufgabe erfolgreich angelegt							|
		| "PLZ mit Javascript"											|	Valide Daten eingeben									|
		| "Ort mit leerem Feld"											|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "Ort mit 99 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "Ort mit 100 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "Ort mit 101 Zeichen"											|	Maximale Zeichenlänge überschritten						|
		| "Ort mit 100 Zeichen mit Sonderzeichen"						|	Aufgabe erfolgreich angelegt							|
		| "Ort mit Javascript"											|	Valide Daten eingeben									|
		| "Telefonnummer mit leerem Feld"								|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "Telefonnummer mit 49 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Telefonnummer mit 50 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Telefonnummer mit 51 Zeichen"								|	Maximale Zeichenlänge überschritten						|
		| "Telefonnummer mit 50 Zeichen mit Sonderzeichen"				|	Aufgabe erfolgreich angelegt							|
		| "Telefonnummer mit Javascript"								|	Valide Daten eingeben									|
		| "Faxnummer mit leerem Feld"									|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "Faxnummer mit 49 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "Faxnummer mit 50 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "Faxnummer mit 51 Zeichen"									|	Maximale Zeichenlänge überschritten						|
		| "Faxnummer mit 50 Zeichen mit Sonderzeichen"					|	Aufgabe erfolgreich angelegt							|
		| "Faxnummer mit Javascript"									|	Valide Daten eingeben									|
		| "Email-Adresse mit leerem Feld"								|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "Email-Adresse mit 99 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Email-Adresse mit 100 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Email-Adresse mit 101 Zeichen"								|	Maximale Zeichenlänge überschritten						|
		| "Email-Adresse mit 100 Zeichen mit Sonderzeichen"				|	Aufgabe erfolgreich angelegt							|
		| "Email-Adresse mit Javascript"								|	Valide Daten eingeben									|
		| "Bemerkungsfeld mit leerem Feld"								|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "Bemerkungsfeld mit 999 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Bemerkungsfeld mit 1000 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Bemerkungsfeld mit 1001 Zeichen"								|	Maximale Zeichenlänge überschritten						|
		| "Bemerkungsfeld mit 1000 Zeichen mit Sonderzeichen"			|	Aufgabe erfolgreich angelegt							|
		| "Bemerkungsfeld mit Javascript"								|	Valide Daten eingeben									|
		| "URL mit leerem Feld"											|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "URL mit 249 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "URL mit 250 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "URL mit 251 Zeichen"											|	Maximale Zeichenlänge überschritten						|
		| "URL mit 250 Zeichen mit Sonderzeichen"						|	Aufgabe erfolgreich angelegt							|
		| "URL mit Javascript"											|	Valide Daten eingeben									|
		| "GPS-Daten mit leerem Feld"									|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "GPS-Daten mit 249 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "GPS-Daten mit 250 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "GPS-Daten mit 251 Zeichen"									|	Maximale Zeichenlänge überschritten						|
		| "GPS-Daten mit 250 Zeichen mit Sonderzeichen"					|	Aufgabe erfolgreich angelegt							|
		| "GPS-Daten mit Javascript"									|	Valide Daten eingeben									|
		| "Raumnummer mit leerem Feld"									|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "Raumnummer mit 249 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "Raumnummer mit 250 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "Raumnummer mit 251 Zeichen"									|	Maximale Zeichenlänge überschritten						|
		| "Raumnummer mit 250 Zeichen mit Sonderzeichen"				|	Aufgabe erfolgreich angelegt							|
		| "Raumnummer mit Javascript"									|	Valide Daten eingeben									|
