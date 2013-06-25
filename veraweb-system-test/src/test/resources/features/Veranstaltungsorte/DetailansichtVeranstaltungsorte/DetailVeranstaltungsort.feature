# language: de
Funktionalität: Als User mit entsprechenden Rechte möchte ich Details eines Veranstaltungsortes aus der Listenansicht sehen können und die Details der Veranstaltungsorte bearbeiten oder löschen können.


Grundlage: Ich bin als Administrator angemeldet und bin in der Liste der Veranstaltungsorte und habe mind. einen Veranstaltungsort angelegt
	Angenommen ich bin als Administrator angemeldet
	 Und es existieren folgende Veranstaltungsorte mit den Werten:
		|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Ort 1	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|
	 Und ich befinde mich auf der Listenansicht aller Veranstaltungsorte 

	 

Szenariogrundriss: Ich bearbeite das Pflichtfeld Beschreibung/Titel
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten												|	Meldung									|
		| "Beschreibung/Titel mit leerem Pflichtfeld"			|	Pflichtfeld muss ausgefüllt sein		|
		| "Beschreibung/Titel mit 199 Zeichen"					|	Aufgabe erfolgreich geändert			|
		| "Beschreibung/Titel mit 200 Zeichen"					|	Aufgabe erfolgreich geändert			|
		| "Beschreibung/Titel mit 201 Zeichen"					|	Maximale Zeichenlänge überschritten		|
		| "Beschreibung/Titel mit 201 Zeichen und Sonderzeichen"|	Aufgabe erfolgreich geändert			|
		| "Beschreibung/Titel mit Javascript"					|	Valide Daten eingeben					|



Szenariogrundriss: Ich bearbeite das Feld Ansprechpartner
 	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten												|	Meldung								|
		| "Ansprechpartner mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "Ansprechpartner mit 249 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Ansprechpartner mit 250 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Ansprechpartner mit 251 Zeichen"						|	Maximale Zeichenlänge überschritten	|
		| "Ansprechpartner mit 250 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
		| "Ansprechpartner mit Javascript"						|	Valide Daten eingeben				|
		
		

	
		

Szenariogrundriss: Ich bearbeite das Feld Straße
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten										|	Meldung								|
		| "Straße mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "Straße mit 249 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Straße mit 250 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Straße mit 251 Zeichen"						|	Maximale Zeichenlänge überschritten	|
		| "Straße mit 250 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
		| "Straße mit Javascript"						|	Valide Daten eingeben				|



Szenariogrundriss: Ich bearbeite das Feld PLZ
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten									|	Meldung								|
		| "PLZ mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "PLZ mit 4 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "PLZ mit 5 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "PLZ mit 6 Zeichen"						|	Maximale Zeichenlänge überschritten	|
		| "PLZ mit 5 Zeichen mit Sonderzeichen"		|	Aufgabe erfolgreich geändert		|
		| "PLZ mit Javascript"						|	Valide Daten eingeben				|	
		
		
		
Szenariogrundriss: Ich bearbeite das Feld Ort
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten									|	Meldung								|
		| "Ort mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "Ort mit 99 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Ort mit 100 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Ort mit 101 Zeichen"						|	Maximale Zeichenlänge überschritten	|
		| "Ort mit 100 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
		| "Ort mit Javascript"						|	Valide Daten eingeben				|	
		


Szenariogrundriss: Ich bearbeite das Feld Telefonnummer
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten											|	Meldung								|
		| "Telefonnummer mit leerem Feld"					|	Aufgabe erfolgreich geändert		|
		| "Telefonnummer mit 49 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "Telefonnummer mit 50 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "Telefonnummer mit 51 Zeichen"					|	Maximale Zeichenlänge überschritten	|
		| "Telefonnummer mit 50 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
		| "Telefonnummer mit Javascript"					|	Valide Daten eingeben				|
		
		

Szenariogrundriss: Ich bearbeite das Feld Faxnummer
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten										|	Meldung								|
		| "Faxnummer mit leerem Feld"					|	Aufgabe erfolgreich geändert		|
		| "Faxnummer mit 49 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "Faxnummer mit 50 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "Faxnummer mit 51 Zeichen"					|	Maximale Zeichenlänge überschritten	|
		| "Faxnummer mit 50 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
		| "Faxnummer mit Javascript"					|	Valide Daten eingeben				|
		
		
		
Szenariogrundriss: Ich bearbeite das Feld Emailadresse
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten											|	Meldung								|
		| "Emailadresse mit leerem Feld"					|	Aufgabe erfolgreich geändert		|
		| "Emailadresse mit 99 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Emailadresse mit 100 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "Emailadresse mit 101 Zeichen"					|	Maximale Zeichenlänge überschritten	|
		| "Emailadresse mit 100 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
		| "Emailadresse mit Javascript"						|	Valide Daten eingeben				|
		
		
		
Szenariogrundriss: Ich bearbeite das Feld Bemerkungsfeld
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten												|	Meldung								|
		| "Bemerkungsfeld mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "Bemerkungsfeld mit 999 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Bemerkungsfeld mit 1000 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Bemerkungsfeld mit 1001 Zeichen"						|	Maximale Zeichenlänge überschritten	|
		| "Bemerkungsfeld mit 1000 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
		| "Bemerkungsfeld mit Javascript"						|	Valide Daten eingeben				|
		
		
		
Szenariogrundriss: Ich bearbeite das Feld URL
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten									|	Meldung								|
		| "URL mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "URL mit 249 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "URL mit 250 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "URL mit 251 Zeichen"						|	Maximale Zeichenlänge überschritten	|
		| "URL mit 250 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
		| "URL mit Javascript"						|	Valide Daten eingeben				|
		
		
		
Szenariogrundriss: Ich bearbeite das Feld GPS-Daten
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten										|	Meldung								|
		| "GPS-Daten mit leerem Feld"					|	Aufgabe erfolgreich geändert		|
		| "GPS-Daten mit 249 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "GPS-Daten mit 250 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "GPS-Daten mit 251 Zeichen"					|	Maximale Zeichenlänge überschritten	|
		| "GPS-Daten mit 250 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
		| "GPS-Daten mit Javascript"					|	Valide Daten eingeben				|
		
		
		
Szenariogrundriss: Ich bearbeite das Feld Raumnummer
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten											|	Meldung								|
		| "Raumnummer mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "Raumnummer mit 249 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Raumnummer mit 250 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Raumnummer mit 251 Zeichen"						|	Maximale Zeichenlänge überschritten	|
		| "Raumnummer mit 250 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
		| "Raumnummer mit Javascript"						|	Valide Daten eingeben				|				