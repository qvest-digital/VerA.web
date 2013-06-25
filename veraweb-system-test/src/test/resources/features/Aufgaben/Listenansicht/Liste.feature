# language: de
Funktionalität: Als User mit entsprechenden Rechten möchte ich eine Aufgabenliste zu einer Veranstaltung sehen und aus dieser Sicht die Aufgabe löschen können. 



Grundlage: Ich bin als Administrator angemeldet und bin in der Übersicht aller Veranstaltungen und habe mind. eine Veranstaltung angelegt
	Angenommen ich bin als Administrator angemeldet



Szenario: Liste mit Aufgaben - Tabelle
	Angenommen es existieren die Personen:
		|  Vorname	| Nachname 		| 
		|  Fred 	| Feuerstein	|
		|  Barny	| Geröllheimer	| 
	 Und es existiert eine Veranstaltung "Feier" mit folgenden Aufgaben:
		|	ID	|	Titel		|	Start 	 			|	Ende 			|	Fertigstellungsgrad	| 	Verantwortlicher	|	Priorität	| 
		|	1	|	Aufgabe 1	|	heute(-1)  			|	heute(+1) 		| 	10	 				| 	Fred				|	 1			|
		|	2	|	Aufgabe 2	|	heute(-2)  			|	heute(+2) 		| 	20 					|	Barny				|	 2			|
		|	3 	|	Aufgabe 3	|	heute(-3) 3:23 Uhr	|	heute(+3) 7 Uhr	| 	30 					| 	Fred				| 	 3			|
		|	4 	|	Aufgabe 4	|	heute(-4) 8:12		|	heute(+4) 9		| 	40 					| 	Fred				| 	 4			|
		|	5 	|	Aufgabe 5	|	heute(-5) 13:13 	|	heute(+5) 20	|	50 					| 	Barny				|	 5 			|
	 Und ich befinde mich auf der Detailansicht der Veranstaltung "Feier"
	Wenn ich den Reiter "Aufgaben" aufrufe
	Dann sehe ich eine Tabelle mit folgenenden Aufgaben:
		|	CheckboxOhneBezeichnung	|	ID	|	Titel		|	Start 				|	Ende 			|	Fertigstellungsgrad |	Verantwortlicher	| Priorität	| 
		|	nein 					|	1 	|	Aufgabe 1 	|	heute(-1)			|	heute(+1)		|	10					| 	Fred				| 	1	 	|
		|	nein 					|	2 	|	Aufgabe 2 	|	heute(-2)			|	heute(2)		|	20	 				|	Barny				| 	2 		|
		|	nein 					|	3	|	Aufgabe 3 	|	heute(-3) 3:23 Uhr	|	heute(+3) 7 Uhr	|	30			 		| 	Fred				|	3		|
		|	nein 					|	4	|	Aufgabe 4 	|	heute(-4) 8:12		|	heute(+4) 9		|	40					|	Fred				| 	4		|
		|	nein 					|	5	|	Aufgabe 5	|	heute(-5) 13:13		|	heute(+5) 20	|	50	 				|	Barny				|	5 		|



Szenario: Beim Löschen einer Aufgabe erwarte ich eine Löschbestätigung
	Angenommen es existiert eine Veranstaltung "Feier" mit folgenden Aufgaben:
		|	ID	|	Titel		|	Start		|	Ende 		|	Fertigstellungsgrad	|	Priorität	| 
		|	1	|	Aufgabe 1	|	heute(-1)	|	heute(+1)	|	10					|	1			|
	 Und ich befinde mich auf der Detailansicht der Veranstaltung "Feier"
	 Und ich rufe den Reiter "Aufgaben" auf
	Wenn ich die Checkbox zur Aufgabe "Aufgabe 1" auswähle
	 Und ich auf "Löschen" klicke
	Dann sehe ich eine Rückfrage zur Bestätigung des Löschens einer Aufgabe



Szenario:  Löschen einer Aufgabe nur nach Bestätigung einer Rückfrage möglich
	Angenommen es existiert eine Veranstaltung "Feier" mit folgenden Aufgaben:
		|	ID	|	Titel 		|	Start 		|	Ende 		| 	Fertigstellungsgrad	|	Priorität	| 
		|	1	|	Aufgabe 1	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	2 	|	Aufgabe 2	|	heute(-2)	|	heute(+2) 	| 	20 					|  	2 			|
		|	3 	|	Aufgabe 3	|	heute(-3)	|	heute(+3) 	| 	30 					|  	3 			|
		|	4 	|	Aufgabe 4	|	heute(-4)	|	heute(+4) 	| 	40 					| 	4 			|
		|	5 	|	Aufgabe 5	|	heute(-5)	|	heute(+5) 	| 	50 					| 	5 			|
	 Und ich befinde mich auf der Detailansicht der Veranstaltung "Feier"
	 Und ich rufe den Reiter "Aufgaben" auf
	 Und ich wähle die Checkbox zur Aufgabe "Aufgabe 1" aus
	 Und ich klicke auf "Löschen"
	Wenn ich die Rückfrage mit "Ja" beantworte
	Dann wird die Aufgabe mit der ID 1 gelöscht
	 Und ich sehe die Meldung "Es wurde ein Datensatz gelöscht."
	 Und ich sehe eine Tabelle mit folgenenden Aufgaben:
		|	CheckboxOhneBezeichnung	|	ID	|	Titel 		|	Start		|	Ende 		|	Fertigstellungsgrad	|	Priorität	|
		|	nein					|	2 	|	Aufgabe 2	|	heute(-2)	|	heute(+2) 	| 	20	 				|  	2 			|
		|	nein					|	3 	|	Aufgabe 3	|	heute(-3)	|	heute(+3) 	| 	30					|  	3 			|
		|	nein					|	4 	|	Aufgabe 4	|	heute(-4)	|	heute(+4) 	| 	40	 				|  	4 			|
		|	nein					|	5 	|	Aufgabe 5	|	heute(-5)	|	heute(+5) 	| 	50	 				| 	5 			|



Szenario:  Löschen aller Aufgaben nur nach Bestätigung einer Rückfrage möglich
	Angenommen es existiert eine Veranstaltung "Feier" mit folgenden Aufgaben:
		|	ID	|	Titel 		|	Start 		|	Ende 		| 	Fertigstellungsgrad	|	Priorität	| 
		|	1	|	Aufgabe 1	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	2 	|	Aufgabe 2	|	heute(-2)	|	heute(+2) 	| 	20 					|  	2 			|
		|	3 	|	Aufgabe 3	|	heute(-3)	|	heute(+3) 	| 	30 					|  	3 			|
		|	4 	|	Aufgabe 4	|	heute(-4)	|	heute(+4) 	| 	40 					| 	4 			|
		|	5 	|	Aufgabe 5	|	heute(-5)	|	heute(+5) 	| 	50 					| 	5 			|
	 Und ich befinde mich auf der Detailansicht der Veranstaltung "Feier"
	 Und ich rufe den Reiter "Aufgaben" auf
	 Und ich klicke auf "Alle markieren"
	 Und ich klicke auf "Löschen"
	Wenn ich die Rückfrage mit "Ja" beantworte
	Dann werden alle Aufgaben gelöscht
	 Und ich sehe die Meldung "Es wurden 5 Datensätze gelöscht."
	 Und ich sehe eine leere Tabelle



Szenario: Löschen einer Aufgabe nicht möglich, wenn die Löschbestätigung verneint wird
	Angenommen es existiert eine Veranstaltung "Feier" mit folgenden Aufgaben:
		|	ID	|	Titel 		|	Start 		|	Ende 		| 	Fertigstellungsgrad	|	Priorität	| 
		|	1	|	Aufgabe 1	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
	 Und ich befinde mich auf der Detailansicht der Veranstaltung "Feier"
	 Und ich rufe den Reiter "Aufgaben" auf
	 Und ich wähle die Checkbox zur Aufgabe "Aufgabe 1" aus
	 Und ich klicke auf "Löschen"
	Wenn ich die Rückfrage mit "Nein" beantworte
	Dann wird die Aufgabe mit der ID 1 nicht gelöscht
	 Und ich sehe eine Tabelle mit folgenenden Aufgaben:
		| CheckboxOhneBezeichnung	|	ID 	|	Titel 		|	Start		|	Ende 		|	Fertigstellungsgrad	|	Priorität	| 
		| ja						|	1 	|	Aufgabe 1	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|



Szenario: Löschen einer Aufgabe nicht möglich wenn keine Checkbox aktiviert ist
	Angenommen es existiert eine Veranstaltung "Feier" mit folgenden Aufgaben:
		|	ID	|	Titel 		|	Start 		|	Ende 		| 	Fertigstellungsgrad	|	Priorität	| 
		|	1	|	Aufgabe 1	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
	 Und ich befinde mich auf der Detailansicht der Veranstaltung "Feier"
	 Und ich rufe den Reiter "Aufgaben" auf
	Wenn ich auf "Löschen" klicke
	 Und ich die Rückfrage mit "Ja" beantworte
	Dann wird die Aufgabe mit der ID 1 nicht gelöscht
	 Und ich sehe die Meldung "Es wurde kein Datensatz gelöscht."
	 Und ich sehe eine Tabelle mit folgenenden Aufgaben:
		| CheckboxOhneBezeichnung	|	ID 	|	Titel 		|	Start		|	Ende 		|	Fertigstellungsgrad	|	Priorität	| 
		| nein						|	1 	|	Aufgabe 1	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|



Szenario: Ich prüfe den Button Zurück
	Angenommen es existiert eine Veranstaltung "Feier" mit folgenden Aufgaben:
		|	ID	|	Titel 		|	Start 		|	Ende 		| 	Fertigstellungsgrad	|	Priorität	| 
		|	1	|	Aufgabe 1	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
	 Und ich befinde mich auf der Detailansicht der Veranstaltung "Feier"
	 Und ich rufe den Reiter "Aufgaben" auf
	Wenn ich auf "Zurück" klicke
	Dann sehe ich als "Header-Titel" die Meldung "Veranstaltung auswählen"



Szenariogrundriss: Man kann alle Aufgaben aus- und abwählen
	Angenommen es existiert eine Veranstaltung "Feier" mit folgenden Aufgaben:
		|	ID	|	Titel 		|	Start 		|	Ende 		| 	Fertigstellungsgrad	|	Priorität	| 
		|	1	|	Aufgabe 1	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	2 	|	Aufgabe 2	|	heute(-2)	|	heute(+2) 	| 	20 					|  	2 			|
		|	3 	|	Aufgabe 3	|	heute(-3)	|	heute(+3) 	| 	30 					|  	3 			|
		|	4 	|	Aufgabe 4	|	heute(-4)	|	heute(+4) 	| 	40 					| 	4 			|
		|	5 	|	Aufgabe 5	|	heute(-5)	|	heute(+5) 	| 	50 					| 	5 			|
	 Und ich befinde mich auf der Detailansicht der Veranstaltung "Feier"
	 Und ich rufe den Reiter "Aufgaben" auf
	Wenn ich auf "<Aktion>" klicke
	Dann sind die Checkboxen zu allen Aufgaben <Auswahlstatus>
	
	Beispiele:
		| Aktion 			| Auswahlstatus	|
		| Alle markieren 	| ausgewählt 	|
		| Alle demarkieren 	| abgewählt 	|



Szenariogrundriss: Ich prüfe die Vorwärts-Navigation der Ergebnisliste
Hier werden 22 Aufgaben angelegt werden, um die Navigation zu tesen. 
Pro Seite werden immer 10 Aufgaben angezeigt, insgesamt existieren 3 Seiten.

	Angenommen es existiert eine Veranstaltung "Feier" mit folgenden Aufgaben:
		|	ID	|	Titel 		|	Start 		|	Ende 		| 	Fertigstellungsgrad	|	Priorität	| 
		|	1	|	Aufgabe 1	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	2 	|	Aufgabe 2	|	heute(-2)	|	heute(+2) 	| 	20 					|  	2 			|
		|	3 	|	Aufgabe 3	|	heute(-3)	|	heute(+3) 	| 	30 					|  	3 			|
		|	4 	|	Aufgabe 4	|	heute(-4)	|	heute(+4) 	| 	40 					| 	4 			|
		|	5 	|	Aufgabe 5	|	heute(-5)	|	heute(+5) 	| 	50 					| 	5 			|
		|	6	|	Aufgabe 6	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	7 	|	Aufgabe 7	|	heute(-2)	|	heute(+2) 	| 	20 					|  	2 			|
		|	8 	|	Aufgabe 8	|	heute(-3)	|	heute(+3) 	| 	30 					|  	3 			|
		|	9 	|	Aufgabe 9	|	heute(-4)	|	heute(+4) 	| 	40 					| 	4 			|
		|	10 	|	Aufgabe 10	|	heute(-5)	|	heute(+5) 	| 	50 					| 	5 			|
		|	11	|	Aufgabe 11	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	12 	|	Aufgabe 12	|	heute(-2)	|	heute(+2) 	| 	20 					|  	2 			|
		|	13 	|	Aufgabe 13	|	heute(-3)	|	heute(+3) 	| 	30 					|  	3 			|
		|	14 	|	Aufgabe 14	|	heute(-4)	|	heute(+4) 	| 	40 					| 	4 			|
		|	15 	|	Aufgabe 15	|	heute(-5)	|	heute(+5) 	| 	50 					| 	5 			|
		|	16	|	Aufgabe 16	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	17 	|	Aufgabe 17	|	heute(-2)	|	heute(+2) 	| 	20 					|  	2 			|
		|	18 	|	Aufgabe 18	|	heute(-3)	|	heute(+3) 	| 	30 					|  	3 			|
		|	19	|	Aufgabe 19	|	heute(-4)	|	heute(+4) 	| 	40 					| 	4 			|
		|	20 	|	Aufgabe 20	|	heute(-5)	|	heute(+5) 	| 	50 					| 	5 			|
		|	21	|	Aufgabe 21	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	22	|	Aufgabe 22	|	heute(-2)	|	heute(+2) 	| 	20	 				|	2			|
	 Und ich befinde mich auf der Detailansicht der Veranstaltung "Feier"
	 Und ich rufe den Reiter "Aufgaben" auf
	 Und ich betrachte die "Erste Seite" der Aufgabenliste
	 Und ich sehe als "Listen-Info" den Text "1 - 10 von 22"
	Wenn ich auf "<Seite>" klicke
	Dann sehe ich als "Listen-Info" den Text "<Text>"
	
	Beispiele:
		|	Seite			|	Text			|
		|	Seite 2     	|	11 - 20 von 22	|
		|	Nächste Seite	|	11 - 20 von 22	| 
		|	Letzte Seite 	|	21 - 22 von 22	|



Szenariogrundriss: Ich prüfe die Rückwärts-Navigation der Ergebnisliste
Hier werden 22 Aufgaben angelegt werden, um die Navigation zu tesen. 
Pro Seite werden immer 10 Aufgaben angezeigt, insgesamt existieren 3 Seiten.

	Angenommen es existiert eine Veranstaltung "Feier" mit folgenden Aufgaben:
		|	ID	|	Titel 		|	Start 		|	Ende 		| 	Fertigstellungsgrad	|	Priorität	| 
		|	1	|	Aufgabe 1	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	2 	|	Aufgabe 2	|	heute(-2)	|	heute(+2) 	| 	20 					|  	2 			|
		|	3 	|	Aufgabe 3	|	heute(-3)	|	heute(+3) 	| 	30 					|  	3 			|
		|	4 	|	Aufgabe 4	|	heute(-4)	|	heute(+4) 	| 	40 					| 	4 			|
		|	5 	|	Aufgabe 5	|	heute(-5)	|	heute(+5) 	| 	50 					| 	5 			|
		|	6	|	Aufgabe 6	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	7 	|	Aufgabe 7	|	heute(-2)	|	heute(+2) 	| 	20 					|  	2 			|
		|	8 	|	Aufgabe 8	|	heute(-3)	|	heute(+3) 	| 	30 					|  	3 			|
		|	9 	|	Aufgabe 9	|	heute(-4)	|	heute(+4) 	| 	40 					| 	4 			|
		|	10 	|	Aufgabe 10	|	heute(-5)	|	heute(+5) 	| 	50 					| 	5 			|
		|	11	|	Aufgabe 11	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	12 	|	Aufgabe 12	|	heute(-2)	|	heute(+2) 	| 	20 					|  	2 			|
		|	13 	|	Aufgabe 13	|	heute(-3)	|	heute(+3) 	| 	30 					|  	3 			|
		|	14 	|	Aufgabe 14	|	heute(-4)	|	heute(+4) 	| 	40 					| 	4 			|
		|	15 	|	Aufgabe 15	|	heute(-5)	|	heute(+5) 	| 	50 					| 	5 			|
		|	16	|	Aufgabe 16	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	17 	|	Aufgabe 17	|	heute(-2)	|	heute(+2) 	| 	20 					|  	2 			|
		|	18 	|	Aufgabe 18	|	heute(-3)	|	heute(+3) 	| 	30 					|  	3 			|
		|	19	|	Aufgabe 19	|	heute(-4)	|	heute(+4) 	| 	40 					| 	4 			|
		|	20 	|	Aufgabe 20	|	heute(-5)	|	heute(+5) 	| 	50 					| 	5 			|
		|	21	|	Aufgabe 21	|	heute(-1)	|	heute(+1) 	| 	10	 				|	1			|
		|	22	|	Aufgabe 22	|	heute(-2)	|	heute(+2) 	| 	20	 				|	2			|
	 Und ich befinde mich auf der Detailansicht der Veranstaltung "Feier"
	 Und ich rufe den Reiter "Aufgaben" auf
	 Und ich betrachte die "Letzte Seite" der Aufgabenliste
	 Und ich sehe als "Listen-Info" den Text "21 - 22 von 22"
	Wenn ich auf "<Seite>" klicke
	Dann sehe ich als "Listen-Info" den Text "<Text>"
	
	Beispiele:
		| Seite 			|	Text			|
		| Seite 1     		|	1 - 10 von 22	|
		| Vorherige Seite 	|	11 - 20 von 22	| 
		| Erste Seite 		|	1 - 10 von 22	|
