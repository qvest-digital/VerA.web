# language: de
Funktionalität: Als User mit entsprechenden Rechte möchte ich eine neue Veranstaltung anlegen können.


Grundlage: Ich bin als Administrator angemeldet und bin in der Maske Neue Veranstaltung anlegen  
	Angenommen ich bin als Administrator angemeldet
	 Und ich sehe "Neue Veranstaltung anlegen"

	 
Szenariogrundriss: Ich lege eine neue Veranstaltung an und teste ob Pflichtfelder ausgefüllt sein müssen
	Angenommen ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Veranstaltung speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten													|	Meldung														|
		| "Veranstaltung mit leeren Pflichtfeldern					|	Sie müssen eine Kurzbezeichnung und den Beginn angeben		|
		| "Veranstaltung mit leerem Pflichtfeld Kurzbezeichnung"	|	Sie müssen eine Kurzbezeichnung angeben						|
		| "Veranstaltung mit leerem Pflichtfeld Beginn"				|	Sie müssen den Beginn angebne								|
#Pflichtfelder sind Kurzbezeichnung und Beginn
