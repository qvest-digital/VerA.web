# language: de
Funktionalität: Als User mit entsprechenden Rechte möchte ich einen neuen EMail-Entwurf anlegen können.


Grundlage: Administrator legt neuen E-Mail-Entwurf an
	Angenommen ich bin als Administrator angemeldet
	  Und es existiert eine Veranstaltung "Veranstaltung 1":
		| ID	| Titel 		  | Beschreibung	 | Start 	  | Ende 		| Fertigstellungsgrad	| Verantwortlicher	 | Priorität | 
		| 1		| Veranstaltung 1 | Beschreibung 1 | heute(+1)  | heute(+2) 	| 		0				| Barny  			 | 1		 |
	  Und es existiert ein Verteiler "Verteiler 1":
		| ID	| Bezeichnung 	| Benutzer	 	| 
		| 1		| Aufgabe 1 	| administrator | 
	  Und ich sehe "Adressen"
	  
	  
	  
Szenariogrundriss: E-Mail-Entwurf anlegen und Pflichtfelder prüfen 
	Angenommen ich klicke auf "Versenden"
	 Und ich sehe "E-Mail Entwürfe"
	 Und ich fülle die Maske mit <Testdaten> aus
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten									|	Meldung											|
		| "E-Mail Vorlage mit leerem Namensfeld"	|	Pflichtfeld "Vorlagenname" muss ausgefüllt sein	|
		| "E-Mail Vorlage mit Namen"				|	#noch abzuklären						|
	 
	 
