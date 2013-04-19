# language: de


Funktionalität: Als User mit entsprechenden Rechte möchte ich eine Aufgabenliste zu einer Veranstaltung sehen und aus dieser Sicht die Aufgabe löschen können. 

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
	Dann wird die Aufgabe "Aufgabe 1" gelöscht
	 Und ich sehe eine Meldung für Löschung eines Datensatzes
	 Und ich sehe folgende Tabelle:
		| ID	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	|  Priorität 	|
		| 2 	| Aufgabe 2 | 13.04.2013 | 14.04.2013 	| 		20	 			|  	2 			|
		| 3 	| Aufgabe 3 | 14.04.2013 | 15.04.2013 	| 		30				|  	3 			|
		| 4 	| Aufgabe 4 | 15.04.2013 | 16.04.2013 	| 		40	 			|  	4 			|
		| 5 	| Aufgabe 5 | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5 			|


Szenario:  Löschen aller Aufgaben nur nach Bestätigung einer Rückfrage möglich
	Angenommen es existiert eine Veranstaltung 
	 Und die Veranstaltung hat folgende Aufgaben:
		| ID 	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	|  Priorität	| 
		| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10	 			| 	 	1 		|
		| 2 	| Aufgabe 2 | 13.04.2013 | 14.04.2013 	| 		20	 			| 	 	2 		|
		| 3 	| Aufgabe 3 | 14.04.2013 | 15.04.2013 	| 		30	 			| 	 	3 		|
		| 4 	| Aufgabe 4 | 15.04.2013 | 16.04.2013 	| 		40	 			| 	 	4 		|
		| 5 	| Aufgabe 5 | 16.04.2013 | 17.04.2013 	| 		50	 			| 		5 		|
	 Und ich rufe den Reiter "Aufgaben" auf
	 Und ich klicke auf "Alle markieren"
	 Und ich klicke auf "Löschen"
	Wenn ich die Rückfrage mit "Ja" beantworte
	Dann werden die Aufgaben gelöscht
	 Und ich sehe eine Meldung für Löschung eines Datensatzes
	 Und ich sehe folgende Tabelle:
		| ID | Titel | Start | Ende | Fertigstellungsgrad | Priorität |


Szenario: Löschen einer Aufgabe nicht möglich, wenn die Löschbestätigung verneint wird
	Angenommen es existiert eine Veranstaltung 
	 Und die Veranstaltung hat folgende Aufgaben:
		| ID 	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	| Priorität	| 
		| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10	 			| 	1 		|
	 Und ich rufe den Reiter "Aufgaben" auf
	 Und ich wähle die Checkbox zur Aufgabe mit der ID 1 aus
	 Und ich klicke auf "Löschen"
	Wenn ich die Rückfrage mit "Nein" beantworte
	Dann wird die Aufgabe nicht gelöscht
	 Und ich sehe eine Meldung "Es wurde kein Datensatz gelöscht."
	 Und ich sehe folgende Tabelle:
		| CheckboxOhneBezeichnung	| ID 	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	| Priorität | 
		| Checkbox					| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10	 			| 	1 		|


Szenario: Löschen einer Aufgabe nicht möglich wenn keine Checkbox aktiviert ist
	Angenommen es existiert eine Veranstaltung 
	 Und die Veranstaltung hat folgende Aufgaben:
		| ID 	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	| Priorität | 
		| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013	| 		10				|  	1 		|
	 Und ich rufe den Reiter "Aufgaben" auf
	Wenn ich auf "Löschen" klicke
	Dann wird die Aufgabe nicht gelöscht
	 Und ich sehe eine Meldung "Es wurde kein Datensatz gelöscht."
	 Und ich sehe folgende Tabelle:
	 	| ID 	| Titel 	| Start 	 | Ende 		| Fertigstellungsgrad 	| Priorität | 
		| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10	 			| 	1 		|
		
Szenario: Ich prüfe den Button Zurück
	Angenommen es existiert eine Veranstaltung 
	 Und die Veranstaltung hat folgende Aufgaben:
		| ID 	| Titel 	| Start 	 | Ende			| Fertigstellungsgrad 	| Priorität | 
		| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10				| 	1		|
	 Und ich rufe den Reiter "Aufgaben" auf
	Wenn ich auf "Zurück" klicke
	Dann sehe ich "Veranstaltung auswählen"


Szenariogrundriss: Man kann alle Aufgaben aus- und abwählen 
	Angenommen es existiert eine Veranstaltung "Feier" mit den folgenden Aufgaben:
		| ID 	| Titel 	| Start 	 | Ende			| Fertigstellungsgrad 	| Priorität | 
		| 1 	| Aufgabe 1 | 12.04.2013 | 13.04.2013 	| 		10				|  	1		|
		| 2 	| Aufgabe 2 | 13.04.2013 | 14.04.2013 	| 		20	 			|  	2		|
		| 3 	| Aufgabe 3 | 14.04.2013 | 15.04.2013 	| 		30	 			|  	3		|
		| 4 	| Aufgabe 4 | 15.04.2013 | 16.04.2013 	| 		40	 			| 	4		|
		| 5		| Aufgabe 5 | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5		|
	 Und ich rufe den Reiter "Aufgaben" auf
	Wenn ich die folgende <Aktion> ausführe
	Dann wird der <Status> der Checkboxen wie folgt geändert
		| Aktion 			| Status	|
		| alle markieren 	| on 	 	|
		| alle demarkieren 	| off 		|


Szenariogrundriss: Ich prüfe die Vorwärts-Navigation der Ergebnisliste
#Hier müssen 21 Aufgaben angelegt werden, um die Navigation zu tesen. 
#Pro Seite werden immer 10 Aufgaben angezeigt, insgesamt existieren 3 Seiten.
	Angenommen es existiert eine Veranstaltung 
	 Und die Veranstaltung hat folgende Aufgaben:
		| ID 	| Titel 	 | Start 	 | Ende			| Fertigstellungsgrad 	| Priorität | 
		| 1 	| Aufgabe 1  | 12.04.2013 | 13.04.2013 	| 		10				| 	1		|
		| 2 	| Aufgabe 2  | 13.04.2013 | 14.04.2013 	| 		20	 			|  	2		|
		| 3 	| Aufgabe 3  | 14.04.2013 | 15.04.2013 	| 		30	 			|  	3		|
		| 4 	| Aufgabe 4  | 15.04.2013 | 16.04.2013 	| 		40	 			| 	4		|
		| 5		| Aufgabe 5  | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5		|
		| 6 	| Aufgabe 6  | 12.04.2013 | 13.04.2013 	| 		10				| 	1		|
		| 7 	| Aufgabe 7  | 13.04.2013 | 14.04.2013 	| 		20	 			|  	2		|
		| 8 	| Aufgabe 8  | 14.04.2013 | 15.04.2013 	| 		30	 			|  	3		|
		| 9 	| Aufgabe 9  | 15.04.2013 | 16.04.2013 	| 		40	 			| 	4		|
		| 10	| Aufgabe 10 | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5		|
		| 11	| Aufgabe 11 | 12.04.2013 | 13.04.2013	|		10				|	1		|
		| 12	| Aufgabe 12 | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5		|
		| 13	| Aufgabe 13 | 12.04.2013 | 13.04.2013 	| 		10				| 	1		|
		| 14	| Aufgabe 14 | 13.04.2013 | 14.04.2013 	| 		20	 			|  	2		|
		| 15	| Aufgabe 15 | 14.04.2013 | 15.04.2013 	| 		30	 			|  	3		|
		| 16	| Aufgabe 16 | 15.04.2013 | 16.04.2013 	| 		40	 			| 	4		|
		| 17	| Aufgabe 17 | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5		|
		| 18	| Aufgabe 18 | 12.04.2013 | 13.04.2013	|		10				|	1		|
		| 19	| Aufgabe 19 | 15.04.2013 | 16.04.2013 	| 		40	 			| 	4		|
		| 20	| Aufgabe 20 | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5		|
		| 21	| Aufgabe 21 | 12.04.2013 | 13.04.2013	|		10				|	1		|
	 Und ich rufe den Reiter "Aufgaben" auf
	 Und ich betrachte die erste Seite der Aufgabenliste
	Wenn ich auf <Seite> klicke
	Dann sehe ich <nächste Seite>
		| Seite 		| nächste Seite	|
		| 2     		| 2 			|
		| nächste Seite | 2 			| 
		| letzte Seite 	| 3 			|


Szenariogrundriss: Ich prüfe die Rückwärts-Navigation der Ergebnisliste
#Hier müssen 21 Aufgaben angelegt werden, um die Navigation zu tesen. 
#Pro Seite werden immer 10 Aufgaben angezeigt, insgesamt existieren 3 Seiten.
	Angenommen es existiert eine Veranstaltung 
	 Und die Veranstaltung hat folgende Aufgaben:
		| ID 	| Titel 	 | Start  	  | Ende		| Fertigstellungsgrad 	| Priorität | 
		| 1 	| Aufgabe 1  | 12.04.2013 | 13.04.2013 	| 		10				| 	1		|
		| 2 	| Aufgabe 2  | 13.04.2013 | 14.04.2013 	| 		20	 			| 	2		|
		| 3 	| Aufgabe 3  | 14.04.2013 | 15.04.2013 	| 		30	 			| 	3		|
		| 4 	| Aufgabe 4  | 15.04.2013 | 16.04.2013 	| 		40	 			| 	4		|
		| 5		| Aufgabe 5  | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5		|
		| 6 	| Aufgabe 6  | 12.04.2013 | 13.04.2013 	| 		10				| 	1		|
		| 7 	| Aufgabe 7  | 13.04.2013 | 14.04.2013 	| 		20	 			|  	2		|
		| 8 	| Aufgabe 8  | 14.04.2013 | 15.04.2013 	| 		30	 			|  	3		|
		| 9 	| Aufgabe 9  | 15.04.2013 | 16.04.2013 	| 		40	 			| 	4		|
		| 10	| Aufgabe 10 | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5		|
		| 11	| Aufgabe 11 | 12.04.2013 | 13.04.2013	|		10				|	1		|
		| 12	| Aufgabe 12 | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5		|
		| 13	| Aufgabe 13 | 12.04.2013 | 13.04.2013 	| 		10				| 	1		|
		| 14	| Aufgabe 14 | 13.04.2013 | 14.04.2013 	| 		20	 			|  	2		|
		| 15	| Aufgabe 15 | 14.04.2013 | 15.04.2013 	| 		30	 			|  	3		|
		| 16	| Aufgabe 16 | 15.04.2013 | 16.04.2013 	| 		40	 			| 	4		|
		| 17	| Aufgabe 17 | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5		|
		| 18	| Aufgabe 18 | 12.04.2013 | 13.04.2013	|		10				|	1		|
		| 19	| Aufgabe 19 | 15.04.2013 | 16.04.2013 	| 		40	 			| 	4		|
		| 20	| Aufgabe 20 | 16.04.2013 | 17.04.2013 	| 		50	 			| 	5		|
		| 21	| Aufgabe 21 | 12.04.2013 | 13.04.2013	|		10				|	1		|
	 Und ich rufe den Reiter "Aufgaben" auf
	 Und ich betrachte die letzte Seite der Aufgabenliste
	Wenn ich auf <Seite> klicke
	Dann sehe ich <nächste Seite>
		| Seite 			| nächste Seite |
		| 1     			| 1				|
		| vorherige Seite 	| 2 			| 
		| erste Seite 		| 1 			|