# language: de
Funktionalität: Als User mit entsprechenden Rechte möchte ich einen neuen Verteiler anlegen können


Grundlage: Ich bin als Administrator angemeldet und habe mind. eine Veranstaltung angelegt und bin in der Aufgabenliste einer Veranstaltung 
	Angenommen ich bin als Administrator angemeldet
	 Und es existieren die Personen:
		|  Vorname	| Nachname 		| 
		|  Fred 	| Feuerstein	|
		|  Barny	| Geröllheimer	| 
	 Und es existiert eine Veranstaltung "Veranstaltung 1" mit folgenden Aufgaben:
		| ID	| Titel 		  | Beschreibung	 | Start 	  | Ende 		| Fertigstellungsgrad	| Verantwortlicher	 | Priorität | 
		| 1		| Veranstaltung 1 | Beschreibung 1   | heute(+1)  | heute(+2) 	| 		0				| Barny  			 | 1		 |
	 Und ich bin in der Ansicht der Gästeliste der Veranstaltung "Veranstaltung 1"



Szenariogrundriss: Neuen Verteiler anlegen und Pflichtfelder prüfen 
	Angenommen ich klicke auf "Verteiler erstellen"
	 Und ich sehe "Verteiler"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten							|	Meldung									|
		| "Verteiler mit leerem Namensfeld"	|	Pflichtfeld "Name" muss ausgefüllt sein	|
		| "Verteiler mit Namen"				|	#noch abzuklären						|
	 
	 
