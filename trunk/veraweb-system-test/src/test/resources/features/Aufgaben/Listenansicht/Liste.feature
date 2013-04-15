# language: de

Funktionalität: Als User mit entsprechenden Rechte möchte ich eine Aufgabenliste zu einer Veranstaltung sehen und aus dieser Sicht die Aufgabe löschen können. 

Grundlage: Ich bin als Administrator angemeldet und bin in der Übersicht aller Veranstaltungen und habe mind. eine Veranstaltung angelegt
	Angenommen ich bin als Administrator angemeldet
	 Und ich bin in der Übersicht aller Veranstaltungen


Szenario: Liste mit Aufgaben - Tabelle
	Angenommen es existieren die Personen:
		|  Vorname	| Nachname 		| 
		|  Fred 	| Feuerstein	|
		|  Barny	| Geröllheimer	| 
	 Und es existiert eine Veranstaltung
	 Und die Veranstaltung hat folgende Aufgaben:
		| ID	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad	| 	Verantwortliche	| Priorität | 
		| 1		| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10% 			| 	Fred			|	 1		|
		| 2		| Aufgabe 2 | 13.04.2013 | 14.04.2013 	| 		20% 			|	Barny			|	 2		|
		| 3 	| Aufgabe 3 | 14.04.2013 | 15.04.2013 	| 		30% 			| 	Fred, Barny		| 	 3		|
		| 4 	| Aufgabe 4 | 15.04.2013 | 16.04.2013 	| 		40% 			| 	Fred			| 	 4		|
		| 5 	| Aufgabe 5 | 16.04.2013 | 17.04.2013 	| 		50% 			| 	Barny			|	 5 		|
	Wenn ich den Reiter "Aufgaben" aufrufe
	Dann sehe ich folgende Tabelle:
		| CheckboxOhneBezeichnung	| ID	| Titel			| Start 	 | Ende 		| Fertigstellungsgrad 	|	Verantwortliche | Priorität | 
		| Checkbox 					| 1 	| Aufgabe 1 	| 12.04.2013 | 13.04.2013 	| 		10% 			| 	Fred			| 	1	 	|
		| Checkbox 					| 2 	| Aufgabe 2 	| 13.04.2013 | 14.04.2013 	| 		20% 			|	Barny			| 	2 		|
		| Checkbox 					| 3 	| Aufgabe 3 	| 14.04.2013 | 15.04.2013 	| 		30%			 	| 	Fred, Barny		|	3		|
		| Checkbox 					| 4 	| Aufgabe 4 	| 15.04.2013 | 16.04.2013 	| 		40%				|	Fred			| 	4		|
		| Checkbox 					| 5 	| Aufgabe 5 	| 16.04.2013 | 17.04.2013 	| 		50% 			|	Barny			|	5 		|


Szenario: Ich bearbeite eine vorhandene Aufgabe
	Angenommen es existiert eine Veranstaltung 
	 Und die Veranstaltung hat folgende Aufgaben:
		| ID	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad	| Priorität | 
		| 1		| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10% 			|	 1		|
		| 2		| Aufgabe 2 | 13.04.2013 | 14.04.2013 	| 		20% 			|	 2		|
	 Und ich rufe den Reiter "Aufgaben" auf
	Wenn ich auf die Aufgabe mit der ID 1 klicke
	Dann sehe ich "Detailansicht Aufgabe"
	 Und ich sehe "Aufgabe Detailansicht"


Szenario: Ich prüfe den Button Neu
	Angenommen es existiert eine Veranstaltung 
	 Und die Veranstaltung hat folgende Aufgaben:
		| ID	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad	| Priorität | 
		| 1		| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10% 			|	 1		|
	 Und ich rufe den Reiter "Aufgaben" auf
	Wenn ich auf "Neu" klicke
	Dann sehe ich "Neue Aufgabe erstellen"
	 Und ich sehe folgende Felder:
		| Aufgaben-ID:			|	 
		| Kurzbezeichnung:		|
		|	Beschreibung:		|
		|	Beginn:				|
		|	um					|
		|	Ende:				|
		|	um					|
		|	Verantwortliche:	|
		|	Fertigstellung:		|
		|	Priorität:			|


Szenario: Beim Löschen einer Aufgabe erwarte ich eine Löschbestätigung
	Angenommen es existiert eine Veranstaltung 
	 Und die Veranstaltung hat folgende Aufgaben:
		| ID	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad	| Priorität | 
		| 1		| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10% 			|	 1		|
	 Und ich rufe den Reiter "Aufgaben" auf
	 Und ich wähle die Checkbox zur Aufgabe aus
	Wenn ich auf "Löschen" klicke
	Dann sehe ich eine Rückfrage zur Bestätigung des Löschens einer Aufgabe


wip
Szenario:  Löschen einer Aufgabe nur nach Bestätigung einer Rückfrage möglich
Angenommen ich betrachte eine Veranstaltung mit folgenden Aufgaben
| ID 	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	| Verantwortliche 	| Priorität	| 
| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10% 			| 					| 	1 		|
Und ich rufe den Reiter "Aufgaben" auf
Und ich wähle die Checkbox zur Aufgabe aus
Und ich kliche auf "Löschen"
Wenn ich die Rückfrage mit "Ja" beantworte
Dann wird die Aufgabe gelöscht
Und ich sehe eine Meldung für Löschung eines Datensatzes
Und die Aufgabentabelle sieht wie folgt aus
| ID | Titel | Start | Ende | Fertigstellungsgrad | Verantwortliche | Priorität |

Szenario:  Löschen einer Aufgabe nur nach Bestätigung einer Rückfrage möglich
Angenommen ich betrachte eine Veranstaltung mit folgenden Aufgaben
| ID 	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	| Verantwortliche 	| Priorität	| 
| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10% 			| 					| 	1 		|
| 2 	| Aufgabe 2 | 13.04.2013 | 14.04.2013 	| 		20% 			| 					| 	2 		|
| 3 	| Aufgabe 3 | 14.04.2013 | 15.04.2013 	| 		30% 			| 					| 	3 		|
| 4 	| Aufgabe 4 | 15.04.2013 | 16.04.2013 	| 		40% 			| 					| 	4 		|
| 5 	| Aufgabe 5 | 16.04.2013 | 17.04.2013 	| 		50% 			| 					|	5 		|
Und ich rufe den Reiter "Aufgaben" auf
Und ich klicke auf "Alle markieren"
Und ich klicke auf "Löschen"
Wenn ich die Rückfrage mit "Ja" beantworte
Dann werden die Aufgaben gelöscht
Und ich sehe eine Meldung für Löschung eines Datensatzes
Und die Aufgabentabelle sieht wie folgt aus
| ID | Titel | Start | Ende | Fertigstellungsgrad | Verantwortliche | Priorität |

Szenario: Löschen einer Aufgabe nicht möglich, wenn die Löschbestätigung verneint wird
Angenommen ich betrachte eine Veranstaltung mit folgenden Aufgaben
| ID 	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	| Verantwortliche	| Priorität	| 
| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10% 			| 					| 	1 		|
Und ich rufe den Reiter "Aufgaben" auf
Und ich wähle die Checkbox zur Aufgabe aus
Und ich klicke auf "Löschen"
Wenn ich die Rückfrage mit "Nein" beantworte
Dann wird die Aufgabe nicht gelöscht
Und ich sehe eine Meldung "Es wurde kein Datensatz gelöscht."
Und die Aufgabentabelle sieht wie folgt aus
|			| ID 	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	| Verantwortliche 	| Priorität | 
| Checkbox	| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10% 			| 					| 	1 		|

Szenario: Löschen einer Aufgabe nicht möglich wenn keine Checkbox aktiviert ist
Angenommen ich betrachte eine Veranstaltung mit folgenden Aufgaben
| ID 	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	| Verantwortliche 	| Priorität | 
| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013	| 		10%				| 					| 	1 		|
Und ich rufe den Reiter "Aufgaben" auf
Wenn ich auf "Löschen" klicke
Dann wird die Aufgabe nicht gelöscht
Und ich sehe eine Meldung "Es wurde kein Datensatz gelöscht."

wip - Aus dem aktuellen Veraweb kann ich nicht sicher erkennen, wie die Seite heißen wird
Szenario: Ich prüfe den Button Zur Gästeliste
Angenommen ich betrachte eine Veranstaltung mit folgenden Aufgaben
| ID 	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	| Verantwortliche 	| Priorität | 
| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013	| 		10%				| 					| 	1 		|
Und ich rufe den Reiter "Aufgaben" auf
Wenn ich auf "Zur Gästeliste" klicke
Dann sehe ich "Gästeliste"

Szenario: Ich prüfe den Button Zurück
Angenommen ich betrachte eine Veranstaltung mit folgenden Aufgaben
| ID 	| Titel 	| Start 	 | Ende			| Fertigstellungsgrad 	| Verantwortliche 	| Priorität | 
| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10%				| 					| 	1		|
Und ich rufe den Reiter "Aufgaben" auf
Wenn ich auf "Zurück" klicke
Dann sehe ich "Veranstaltung suchen"

Szenariogrundriss: Man kann alle Aufgaben aus- und abwählen 
Angenommen ich betrachte eine Veranstaltung mit folgenden Aufgaben
| ID 	| Titel 	| Start 	 | Ende			| Fertigstellungsgrad 	| Verantwortliche 	| Priorität | 
| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10%				| 					| 	1		|
| 2 	| Aufgabe 2 | 13.04.2013 | 14.04.2013 	| 		20% 			| 					| 	2		|
| 3 	| Aufgabe 3 | 14.04.2013 | 15.04.2013 	| 		30% 			| 					| 	3		|
| 4 	| Aufgabe 4 | 15.04.2013 | 16.04.2013 	| 		40% 			|					| 	4		|
| 5		| Aufgabe 5 | 16.04.2013 | 17.04.2013 	| 		50% 			| 					| 	5		|
Und ich rufe den Reiter "Aufgaben" auf
Wenn ich die folgende <Aktion> ausführe
Dann wird der <Status> der Checkboxen wie folgt geändert
| Aktion 			| Status	|
| alle markieren 	| on 	 	|
| alle demarkieren 	| off 		|

Szenariogrundriss: Ich prüfe die Navigation der Ergebnisliste
Angenommen ich betrachte eine Veranstaltung mit folgenden Aufgaben
| ID 	| Titel 	| Start 	 | Ende			| Fertigstellungsgrad 	| Verantwortliche 	| Priorität | 
| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10%				| 					| 	1		|
| 2 	| Aufgabe 2 | 13.04.2013 | 14.04.2013 	| 		20% 			| 					| 	2		|
| 3 	| Aufgabe 3 | 14.04.2013 | 15.04.2013 	| 		30% 			| 					| 	3		|
| 4 	| Aufgabe 4 | 15.04.2013 | 16.04.2013 	| 		40% 			|					| 	4		|
| 5		| Aufgabe 5 | 16.04.2013 | 17.04.2013 	| 		50% 			| 					| 	5		|
Und ich rufe den Reiter "Aufgaben" auf
Und ich betrachte die erste Seite der Aufgabenliste
Wenn ich auf <Seite> klicke
Dann sehe ich <nächste Seite>
| Seite 		| nächste Seite	|
| 2     		| 2 			|
| nächste Seite | 2 			| 
| letzte Seite 	| 2 			|

Szenariogrundriss: Ich prüfe die Navigation der Ergebnisliste
Angenommen ich betrachte eine Veranstaltung mit folgenden Aufgaben
| ID 	| Titel 	| Start  	 | Ende			| Fertigstellungsgrad 	| Verantwortliche 	| Priorität | 
| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10%				| 					| 	1		|
| 2 	| Aufgabe 2 | 13.04.2013 | 14.04.2013 	| 		20% 			| 					| 	2		|
| 3 	| Aufgabe 3 | 14.04.2013 | 15.04.2013 	| 		30% 			| 					| 	3		|
| 4 	| Aufgabe 4 | 15.04.2013 | 16.04.2013 	| 		40% 			|					| 	4		|
| 5		| Aufgabe 5 | 16.04.2013 | 17.04.2013 	| 		50% 			| 					| 	5		|
Und ich rufe den Reiter "Aufgaben" auf
Und ich betrachte die letzte Seite der Aufgabenliste
Wenn ich auf <Seite> klicke
Dann sehe ich <nächste Seite>
| Seite 			| nächste Seite |
| 1     			| 1				|
| vorherige Seite 	| 1 			| 
| erste Seite 		| 1 			|