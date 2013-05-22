# language: de
Funktionalität: Als User mit entsprechenden Rechte möchte ich eine neue Person anlegen können.


Grundlage: Ich bin als Administrator angemeldet und bin in der Maske Neue Person anlegen  
	Angenommen ich bin als Administrator angemeldet
	 Und ich sehe "Neue Person anlegen"

	 
Szenariogrundriss: Ich lege eine neue Person an und teste ob Pflichtfelder ausgefüllt sein müssen
	Angenommen ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Person speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten										|	Meldung																	|
		| "Person mit leeren Pflichtfeldern				|	Sie müssen einen Vornamen, einen Nachnamen oder eine Firma angeben		|
		| "Person mit leerem Pflichtfeld Vorname"		|	Es wurde ein neuer Datensatz angelegt									|
		| "Person mit leerem Pflichtfeld Nachname"		|	Es wurde ein neuer Datensatz angelegt									|
#Pflichtfelder sind entweder Vorname oder Nachname.
