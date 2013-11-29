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
	 Und ich fülle "Beschreibung/Titel" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten												|	Meldung									|
		| "Beschreibung/Titel mit leerem Pflichtfeld"			|	Pflichtfeld muss ausgefüllt sein		|
		| "Beschreibung/Titel mit 199 Zeichen"					|	Aufgabe erfolgreich geändert			|
		| "Beschreibung/Titel mit 200 Zeichen"					|	Aufgabe erfolgreich geändert			|
		| "Beschreibung/Titel mit 201 Zeichen"					|	Maximale Zeichenlänge überschritten		| //kann man so nicht testen, da das Feld auf 200 gesetzt ist
		| "Beschreibung/Titel mit 200 Zeichen und Sonderzeichen"|	Aufgabe erfolgreich geändert			|
		| "Beschreibung/Titel mit Javascript"					|	Valide Daten eingeben					| //kann man so nicht testen, da javascript gespeichert wird, aber trotzdem nicht greifen soll 



Szenariogrundriss: Ich bearbeite das Feld Ansprechpartner
 	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle "Ansprechpartner" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten												|	Meldung								|
		| "Ansprechpartner mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "Ansprechpartner mit 249 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Ansprechpartner mit 250 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Ansprechpartner mit 250 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|

		

Szenariogrundriss: Ich bearbeite das Feld Straße
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle "Straße" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten										|	Meldung								|
		| "Straße mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "Straße mit 249 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Straße mit 250 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Straße mit 250 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|


Szenariogrundriss: Ich bearbeite das Feld PLZ
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle "PLZ" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten									|	Meldung								|
		| "PLZ mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "PLZ mit 49 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "PLZ mit 50 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "PLZ mit 50 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|

			
		
Szenariogrundriss: Ich bearbeite das Feld Ort
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle "Ort" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten									|	Meldung								|
		| "Ort mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "Ort mit 99 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Ort mit 100 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Ort mit 100 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
	

Szenariogrundriss: Ich bearbeite das Feld Telefonnummer
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle "Telefonnummer" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten											|	Meldung								|
		| "Telefonnummer mit leerem Feld"					|	Aufgabe erfolgreich geändert		|
		| "Telefonnummer mit 49 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "Telefonnummer mit 50 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "Telefonnummer mit 50 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
		
		

Szenariogrundriss: Ich bearbeite das Feld Faxnummer
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle "Faxnummer" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten										|	Meldung								|
		| "Faxnummer mit leerem Feld"					|	Aufgabe erfolgreich geändert		|
		| "Faxnummer mit 49 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "Faxnummer mit 50 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "Faxnummer mit 50 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
	
		
		
Szenariogrundriss: Ich bearbeite das Feld Emailadresse
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle "Emailadresse" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten											|	Meldung								|
		| "Emailadresse mit leerem Feld"					|	Aufgabe erfolgreich geändert		|
		| "Emailadresse mit 99 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Emailadresse mit 100 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "Emailadresse mit 100 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|

		
		
Szenariogrundriss: Ich bearbeite das Feld Bemerkungsfeld
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle "Bemerkungsfeld" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten												|	Meldung								|
		| "Bemerkungsfeld mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "Bemerkungsfeld mit 999 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Bemerkungsfeld mit 1000 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Bemerkungsfeld mit 1001 Zeichen"						|	Maximale Zeichenlänge überschritten	|
		| "Bemerkungsfeld mit 1000 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
	
		
Szenariogrundriss: Ich bearbeite das Feld URL
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle "URL" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten									|	Meldung								|
		| "URL mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "URL mit 249 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "URL mit 250 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "URL mit 250 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
	
				
Szenariogrundriss: Ich bearbeite das Feld GPS-Daten
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle "GPS-Daten" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten										|	Meldung								|
		| "GPS-Daten mit leerem Feld"					|	Aufgabe erfolgreich geändert		|
		| "GPS-Daten mit 249 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "GPS-Daten mit 250 Zeichen"					|	Aufgabe erfolgreich geändert		|
		| "GPS-Daten mit 250 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
	
		
		
Szenariogrundriss: Ich bearbeite das Feld Raumnummer
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle "Raumnummer" mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten											|	Meldung								|
		| "Raumnummer mit leerem Feld"						|	Aufgabe erfolgreich geändert		|
		| "Raumnummer mit 249 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Raumnummer mit 250 Zeichen"						|	Aufgabe erfolgreich geändert		|
		| "Raumnummer mit 250 Zeichen mit Sonderzeichen"	|	Aufgabe erfolgreich geändert		|
				
				
Szenariogrundriss: Javascript-Injektion greift nicht
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die <Felder> mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann greift die JS-Injektion nicht //wird so formuliert, da für manuellen Test verwendet
	     | Felder   			| Testdaten												|
         | Beschreibung/Titel 	| <script type="text/javascript">alert("huhu")</script> |					|
         | Ansprechpartner 		| <script type="text/javascript">alert("huhu")</script> |
         | Straße 				| <script type="text/javascript">alert("huhu")</script> |
         | PLZ 					| <script type="text/javascript">alert("huhu")</script> |
         | Ort                  | <script type="text/javascript">alert("huhu")</script> |
         | Telefonnummer        | <script type="text/javascript">alert("huhu")</script> |
         | Faxnummer            | <script type="text/javascript">alert("huhu")</script> |
         | Emailadresse         | <script type="text/javascript">alert("huhu")</script> |
         | Bemerkungsfeld       | <script type="text/javascript">alert("huhu")</script> |
         | URL                  | <script type="text/javascript">alert("huhu")</script> |
         | GPS-Daten            | <script type="text/javascript">alert("huhu")</script> |
         | Raumnummer           | <script type="text/javascript">alert("huhu")</script> |
   
   
   Szenariogrundriss: Eingabeeinschränkungen
   Die meisten Felder lassen nut eine bestimmte Zeichenlänge zu, dadurch keine Meldungen zur Längeüberschreitung
   Längere Einträge werden abgeschnitten.
	Angenommen ich klicke auf den Veranstaltungsort mit der ID 1
 	 Und ich sehe "Detailansicht Veranstaltungsorte"
	 Und ich sehe "Veranstaltungsort Detailansicht"
	 Und ich fülle die <Felder> mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann werden folgende "Eingaben" gespeichert 
	     | Felder   			| Testdaten								| Eigaben								|
         | Beschreibung/Titel 	| "Beschreibung/Titel mit 201 Zeichen"	| "Beschreibung/Titel mit 200 Zeichen"	|						
         | Ansprechpartner 		| "Ansprechpartner mit 251 Zeichen"		| "Ansprechpartner mit 250 Zeichen"		|
         | Straße 				| "Straße mit 251 Zeichen"				| "Straße mit 250 Zeichen"				|
         | PLZ 					| "PLZ mit 51 Zeichen"					| "PLZ mit 50 Zeichen"					|
         | Ort                  | "Ort mit 101 Zeichen"					| "Ort mit 100 Zeichen"					|
         | Telefonnummer        | "Telefonnummer mit 51 Zeichen" 		| "Telefonnummer mit 50 Zeichen" 		|
         | Faxnummer            | "Faxnummer mit 51 Zeichen"			| "Faxnummer mit 50 Zeichen"			|
         | Emailadresse         | "Emailadresse mit 101 Zeichen"		| "Emailadresse mit 100 Zeichen"		|
         | URL                  | "URL mit 251 Zeichen" 				| "URL mit 250 Zeichen" 				|
         | GPS-Daten            | "GPS-Daten mit 251 Zeichen" 			| "GPS-Daten mit 250 Zeichen" 			|
         | Raumnummer           | "Raumnummer mit 251 Zeichen" 			| "Raumnummer mit 250 Zeichen" 			|      
         
                               
                               