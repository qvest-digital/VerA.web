# language: de
Funktionalität: Als User mit entsprechenden Rechte möchte ich einen Partner zu einer existierenden Person anlegen können.


Grundlage: Ich bin als Administrator angemeldet habe mind. eine Person angelegt und bin in der Maske Person bearbeiten  
	Angenommen ich bin als Administrator angemeldet
	  Und es existieren die Personen:
		|  Vorname	| Nachname 		| 
		|  Fred 	| Feuerstein	|
	 Und ich sehe "Person bearbeiten"
	 Und ich sehe "Personendaten"
	 Und ich bin im Tab "Partner"


Szenariogrundriss: Ich lege einen neuen Partner an und teste ob Pflichtfelder ausgefüllt sein müssen
	Angenommen ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Person speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten										|	Meldung																|
		| "Partner mit leeren Pflichtfeldern			|	Sie müssen einen Vornamen, einen Nachnamen oder eine Firma angeben	|
		| "Partner mit leerem Pflichtfeld Vorname"		|	Es wurde ein neuer Datensatz angelegt								|
		| "Partner mit leerem Pflichtfeld Nachname"		|	Es wurde ein neuer Datensatz angelegt								|
#Pflichtfelder sind entweder Vorname oder Nachname
