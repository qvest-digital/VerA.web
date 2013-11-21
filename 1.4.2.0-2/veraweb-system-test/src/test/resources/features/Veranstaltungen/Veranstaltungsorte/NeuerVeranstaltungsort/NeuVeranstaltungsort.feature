# language: de
Funktionalität: Als User mit entsprechenden Rechte möchte ich einen neuen Veranstaltungsort anlegen können.


Grundlage: Ich bin als Administrator angemeldet 
	Angenommen ich bin als Administrator angemeldet
	 Und ich bin in der Übersicht aller Veranstaltungsorte 
	 
Szenariogrundriss: Ich lege einen neuen Veranstaltungsort an
Angenommen ich klicke auf "Neu"
	 Und ich sehe "Neuen Veranstaltungsort eingeben"
	 Und ich fülle die <Felder> mit <Testdaten> aus
	| Felder   				| Testdaten                |
	| Beschreibung/Titel 	| Abcd                     |
	| Ansprechpartner 		| Fred Feuerstein	       |            
	| Straße 				| Steinstraße 4            |       
	| PLZ 					| 53123                    |
	| Ort                   | Rockhausen               |    
	| Telefonnummer         | 0228/123456              |     
	| Faxnummer             | 0228/123654              |     
	| Emailadresse          | c.nuessle@tarent.de      |
	| Bemekrungsfeld		| Bemerkung1               |
	| URL                   | www.tarent.de            |       
	| GPS-Daten             | 50.73743°N 7.09821°E     |              
	| Raumnummer            | 123                      |
	

	Wenn ich auf "Speichern" klicke
	Dann wird die Aufgabe angelegt
	 Und in der Übersicht der Veranstaltungsorte sehe ich folgende Tabelle
	 |	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| URL 			| GPS-Daten 			| Raumnummer |
	 |	1	|	Abcd 	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	| www.tarent.de | 50.73743°N 7.09821°E	| 123		 |
	

Szenariogrundriss: Validierungscheck
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
		| "Beschreibung/Titel mit 250 Zeichen mit Sonderzeichen"		|	Aufgabe erfolgreich angelegt							|
		| "Ansprechpartner mit leerem Feld"								|	Aufgabe erfolgreich angelegt							|
		| "Ansprechpartner mit 249 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Ansprechpartner mit 250 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Ansprechpartner mit 250 Zeichen mit Sonderzeichen"			|	Aufgabe erfolgreich angelegt							|
		| "Straße mit leerem Feld"										|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "Straße mit 249 Zeichen"										|	Aufgabe erfolgreich angelegt							|
		| "Straße mit 250 Zeichen"										|	Aufgabe erfolgreich angelegt							|
		| "Straße mit 250 Zeichen mit Sonderzeichen"					|	Aufgabe erfolgreich angelegt							|
		| "PLZ mit leerem Feld"											|	#noch zu klären, ob Pflichtfeld oder nicht				|
		| "PLZ mit 49 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "PLZ mit 50 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "PLZ mit 50 Zeichen mit Sonderzeichen"						|	Aufgabe erfolgreich angelegt							|
		| "Ort mit leerem Feld"											|	Aufgabe erfolgreich angelegt							|
		| "Ort mit 99 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "Ort mit 100 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "Ort mit 100 Zeichen mit Sonderzeichen"						|	Aufgabe erfolgreich angelegt							|
		| "Telefonnummer mit leerem Feld"								|	Aufgabe erfolgreich angelegt							|
		| "Telefonnummer mit 49 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Telefonnummer mit 50 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Telefonnummer mit 50 Zeichen mit Sonderzeichen"				|	Aufgabe erfolgreich angelegt							|
		| "Faxnummer mit leerem Feld"									|	Aufgabe erfolgreich angelegt							|
		| "Faxnummer mit 49 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "Faxnummer mit 50 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "Faxnummer mit 50 Zeichen mit Sonderzeichen"					|	Aufgabe erfolgreich angelegt							|
		| "Email-Adresse mit leerem Feld"								|	Aufgabe erfolgreich angelegt							|
		| "Email-Adresse mit 99 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Email-Adresse mit 100 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Email-Adresse mit 100 Zeichen mit Sonderzeichen"				|	Aufgabe erfolgreich angelegt							|
		| "Bemerkungsfeld mit leerem Feld"								|	Aufgabe erfolgreich angelegt							|
		| "Bemerkungsfeld mit 999 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Bemerkungsfeld mit 1000 Zeichen"								|	Aufgabe erfolgreich angelegt							|
		| "Bemerkungsfeld mit 1001 Zeichen"								|	Maximale Zeichenlänge überschritten						|
		| "Bemerkungsfeld mit 1000 Zeichen mit Sonderzeichen"			|	Aufgabe erfolgreich angelegt							|
		| "URL mit leerem Feld"											|	Aufgabe erfolgreich angelegt							|
		| "URL mit 249 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "URL mit 250 Zeichen"											|	Aufgabe erfolgreich angelegt							|
		| "URL mit 250 Zeichen mit Sonderzeichen"						|	Aufgabe erfolgreich angelegt							|
		| "GPS-Daten mit leerem Feld"									|	Aufgabe erfolgreich angelegt							|
		| "GPS-Daten mit 249 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "GPS-Daten mit 250 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "GPS-Daten mit 250 Zeichen mit Sonderzeichen"					|	Aufgabe erfolgreich angelegt							|
		| "Raumnummer mit leerem Feld"									|	Aufgabe erfolgreich angelegt							|
		| "Raumnummer mit 249 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "Raumnummer mit 250 Zeichen"									|	Aufgabe erfolgreich angelegt							|
		| "Raumnummer mit 250 Zeichen mit Sonderzeichen"				|	Aufgabe erfolgreich angelegt							|
		
		
		
