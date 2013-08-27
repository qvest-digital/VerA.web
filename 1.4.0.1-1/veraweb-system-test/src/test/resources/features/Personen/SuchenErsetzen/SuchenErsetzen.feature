# language: de
Funktionalität: Als User mit entsprechenden Rechte möchte ich Suchen und Ersetzen können.


#laut Dokumentation soll es in Suchen und Ersetzen keine Pflichtfelder geben, daher wird getestet, ob die bisher bestehenden Pflichtfelder
#Pflichtfelder mehr sind


Grundlage: Ich bin als Administrator angemeldet und bin in der Maske Personen Suchen und Ersetzen und prüfe ob es keine Pflichtfelder mehr gibt  
	Angenommen ich bin als Administrator angemeldet
	 Und es existieren die Personen:
		|  Vorname	| Nachname 		| 
		|  Fred 	| Feuerstein	|
	 Und ich sehe "Personen suchen und ersetzen"	
		
	
	
Szenariogrundriss: Ich mache keine Eingaben und klicke auf Suchen
	Angenommen ich fülle die Maske mit <Testdaten> aus 
	Wenn ich auf "Suchen" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| Testdaten				|	Meldung				|
		| "leere Testdaten"		|	#noch abzuklären	|