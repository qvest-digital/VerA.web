# language: de
Funktionalität: Als User mit entsprechenden Rechten möchte ich eine Liste aller Veranstaltungsorte sehen und aus dieser Sicht Veranstaltungsorte löschen können. 



Grundlage: Ich bin als Administrator angemeldet und bin in der Übersicht aller Veranstaltungsorte und habe mind. einen Veranstaltungsort angelegt
	Angenommen ich bin als Administrator angemeldet
	 Und ich bin in der Übersicht aller Veranstaltungsorte 
	
	
	
Szenario: Beim Löschen einer Aufgabe erwarte ich eine Löschbestätigung
	Angenommen es existieren folgende Veranstaltungsorte mit den Werten:
		|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Ort 1	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|
	Wenn ich die Checkbox zum Veranstaltungsort "Ort 1" auswähle
	 Und ich auf "Löschen" klicke
	Dann sehe ich eine Rückfrage zur Bestätigung des Löschens einer Aufgabe
	


Szenario:  Löschen einer Aufgabe nur nach Bestätigung einer Rückfrage möglich
	Angenommen es existieren folgende Veranstaltungsorte mit den Werten:
		|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Ort 1	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|
	 Und ich wähle die Checkbox zum Veranstaltungsort "Ort 1" aus
	 Und ich klicke auf "Löschen"
	Wenn ich die Rückfrage mit "Ja" beantworte
	Dann wird die Aufgabe mit der ID 1 gelöscht
	 Und ich sehe die Meldung "Es wurde ein Datensatz gelöscht."
	 Und ich sehe eine leere Tabelle 



Szenario:  Löschen aller Veranstaltungsorte nur nach Bestätigung einer Rückfrage möglich
	Angenommen es existieren folgende Veranstaltungsort mit den Werten:
		|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Ort 1	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|
		|	2	|	Ort 2	|	Barny Geröllheimer	|	Felsstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung2	|
	 Und ich klicke auf "Alle markieren"
	 Und ich klicke auf "Löschen"
	Wenn ich die Rückfrage mit "Ja" beantworte
	Dann werden alle Aufgaben gelöscht
	 Und ich sehe die Meldung "Es wurden 2 Datensätze gelöscht."
	 Und ich sehe eine leere Tabelle



Szenario: Löschen eines Veranstaltungsortes nicht möglich, wenn die Löschbestätigung verneint wird
	Angenommen es existieren folgende Veranstaltungsorte mit den Werten:
		|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Ort 1	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|
	 Und ich wähle die Checkbox zum Veranstaltungsort "Ort 1" aus
	 Und ich klicke auf "Löschen"
	Wenn ich die Rückfrage mit "Nein" beantworte
	Dann wird die Aufgabe mit der ID 1 nicht gelöscht
	 Und ich sehe eine Tabelle mit folgenden Veranstaltungsorten:
	 	|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Ort 1	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|



Szenario: Löschen eines Veranstaltungsortes nicht möglich wenn keine Checkbox aktiviert ist
	Angenommen es existieren folgende Veranstaltungsorte mit den Werten:
		|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Ort 1	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|
	Wenn ich auf "Löschen" klicke
	 Und ich die Rückfrage mit "Ja" beantworte
	Dann wird die Aufgabe mit der ID 1 nicht gelöscht
	 Und ich sehe die Meldung "Es wurde kein Datensatz gelöscht."
	 Und ich sehe eine Tabelle mit folgenden Veranstaltungsorten:
	 	|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Ort 1	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|
	


Szenario: Ich prüfe den Button Zurück
	Angenommen ich klicke auf "Neu"
	 Und ich sehe "Neuen Veranstaltungsort eingeben"
	Wenn ich auf "Zurück" klicke
	Dann sehe ich im Header den Titel "Veranstaltungsorte"

	

Szenariogrundriss: Man kann alle Veranstaltungsorte aus- und abwählen
	Angenommen es existieren folgende Veranstaltungsorte mit den Werten:
		|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Ort 1	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|
		|	2	|	Ort 2	|	Barny Geröllheimer	|	Felsstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung2	|	
	Wenn ich auf <Aktion> klicke
	Dann sind die Checkboxen zu allen Veranstaltungsorten  <Auswahlstatus>
	
	Beispiele:
		| Aktion 			| Auswahlstatus	|
		| Alle markieren 	| ausgewählt 	|
		| Alle demarkieren 	| abgewählt 	|
		
		
		
Szenariogrundriss: Ich prüfe die Vorwärts-Navigation der Veranstaltungsortliste
Hier werden 22 Veranstaltungsorte angelegt, um die Navigation zu tesen. 
Pro Seite werden immer 10 Veranstaltungsorte angezeigt, insgesamt existieren 3 Seiten.

	Angenommen es existieren folgende Veranstaltungsorte mit den Werten:
		|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Ort 1	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|
		|	2	|	Ort 2	|	Barny Geröllheimer	|	Felsstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung2	|
		|	3	|	Ort 3	|	Wilma Feuerstein	|	Kieselstraße 4	| 	4000	| 	Steinhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung3	|
		|	4	|	Ort 4	|	Betty Geröllheimer	|	Bergstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung4	|
		|	5	|	Ort 5	|	Bambam Geröllheimer	|	Sandstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung5	|
		|	6	|	Ort 6	|	Dino Geröllheimer	|	Stonestraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung6	|
		|	7	|	Ort 7	|	Mr Schiefer			|	Rockstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung7	|
		|	8	|	Ort 8	|	Pebbels Feuerstein	|	Granitstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung8	|
		|	9	|	Ort 9	|	Joe Rockhead		|	Teerstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung9	|
		|	10	|	Ort 10	|	Pearl Slaghoople	|	Pebblestraße 5	|	2000	|	Stonehausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung10	|
		|	11	|	Ort 11	|	Great Gazoo			|	Bambamstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung11	|
		|	12	|	Ort 12	|	Arnold Paperboy		|	Feuerstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung12	|
		|	13	|	Ort 13	|	Tex Hardrock		|	Schiefstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung13	|
		|	14	|	Ort 14	|	Sam Slagheap		|	Heapstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung14	|
		|	15	|	Ort 15	|	Eppy Brianstone		|	Slagstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung15	|
		|	16	|	Ort 16	|	Gary Granite		|	Brianstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung16	|
		|	17	|	Ort 17	|	Ann Margrock		|	Eppystraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung17	|
		|	18	|	Ort 18	|	Perry Masonry		|	Marrockstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung18	|
		|	19	|	Ort 19	|	Beau Brumelstone	|	Masonrystraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung19	|
		|	20	|	Ort 20	|	Hoppy Feuerstein	|	Beaustraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung20	|
		|	21	|	Ort 21	|	Water Buffalo		|	Lodgestraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung21	|
		|	22	|	Ort 22	|	Texarock Ranger		|	Rangerstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung22	|
	 Und ich betrachte die "Erste Seite" der Veranstaltungsortliste
	 Und ich sehe als "Listen-Info" den Text "1 - 10 von 22"
	Wenn ich auf <Seite> klicke
	Dann sehe ich als "Listen-Info" den Text "<Text>"
	
	Beispiele:
		|	Seite			|	Text			|
		|	Seite 2     	|	11 - 20 von 22	|
		|	Nächste Seite	|	11 - 20 von 22	| 
		|	Letzte Seite 	|	21 - 22 von 22	|

		
		
		
Szenariogrundriss: Ich prüfe die Rückwärts-Navigation der Veranstaltungsortliste
Hier werden 22 Veranstaltungsorte angelegt werden, um die Navigation zu tesen. 
Pro Seite werden immer 10 Veranstaltungsorte angezeigt, insgesamt existieren 3 Seiten.

	Angenommen es existieren folgende Veranstaltungsort mit den Werten:
		|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Ort 1	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|
		|	2	|	Ort 2	|	Barny Geröllheimer	|	Felsstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung2	|
		|	3	|	Ort 3	|	Wilma Feuerstein	|	Kieselstraße 4	| 	4000	| 	Steinhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung3	|
		|	4	|	Ort 4	|	Betty Geröllheimer	|	Bergstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung4	|
		|	5	|	Ort 5	|	Bambam Geröllheimer	|	Sandstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung5	|
		|	6	|	Ort 6	|	Dino Geröllheimer	|	Stonestraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung6	|
		|	7	|	Ort 7	|	Mr Schiefer			|	Rockstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung7	|
		|	8	|	Ort 8	|	Pebbels Feuerstein	|	Granitstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung8	|
		|	9	|	Ort 9	|	Joe Rockhead		|	Teerstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung9	|
		|	10	|	Ort 10	|	Pearl Slaghoople	|	Pebblestraße 5	|	2000	|	Stonehausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung10	|
		|	11	|	Ort 11	|	Great Gazoo			|	Bambamstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung11	|
		|	12	|	Ort 12	|	Arnold Paperboy		|	Feuerstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung12	|
		|	13	|	Ort 13	|	Tex Hardrock		|	Schiefstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung13	|
		|	14	|	Ort 14	|	Sam Slagheap		|	Heapstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung14	|
		|	15	|	Ort 15	|	Eppy Brianstone		|	Slagstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung15	|
		|	16	|	Ort 16	|	Gary Granite		|	Brianstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung16	|
		|	17	|	Ort 17	|	Ann Margrock		|	Eppystraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung17	|
		|	18	|	Ort 18	|	Perry Masonry		|	Marrockstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung18	|
		|	19	|	Ort 19	|	Beau Brumelstone	|	Masonrystraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung19	|
		|	20	|	Ort 20	|	Hoppy Feuerstein	|	Beaustraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung20	|
		|	21	|	Ort 21	|	Water Buffalo		|	Lodgestraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung21	|
		|	22	|	Ort 22	|	Texarock Ranger		|	Rangerstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung22	|
	 Und ich betrachte die "Letzte Seite" der Veranstaltungsortliste
	 Und ich sehe als "Listen-Info" den Text "21 - 22 von 22"
	Wenn ich auf <Seite> klicke
	Dann sehe ich als "Listen-Info" den Text "<Text>"
	
	Beispiele:
		| Seite 			|	Text			|
		| Seite 1     		|	1 - 10 von 22	|
		| Vorherige Seite 	|	11 - 20 von 22	| 
		| Erste Seite 		|	1 - 10 von 22	|
		
		
Szenariogrundriss: Ich prüfe den Direkteinsprung
Hier werden 22 Veranstaltungsorte angelegt werden, um den Direkteinsprung zu tesen. 
Pro Seite werden immer 10 Veranstaltungsorte angezeigt, insgesamt existieren 3 Seiten.

	Angenommen es existieren folgende Veranstaltungsort mit den Werten:
		|	ID	|	Titel	|	Ansprechpartner		|	Straße 			|	PLZ		|	Ort			|	Telefon		|	Fax			|	E-Mail				|	Bemerkung	| 
		|	1	|	Abcd 	|	Fred Feuerstein		|	Steinstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung1	|
		|	2	|	Bcde 	|	Barny Geröllheimer	|	Felsstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung2	|
		|	3	|	Cdef	|	Wilma Feuerstein	|	Kieselstraße 4	| 	4000	| 	Steinhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung3	|
		|	4	|	Defg	|	Betty Geröllheimer	|	Bergstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung4	|
		|	5	|	Efgh	|	Bambam Geröllheimer	|	Sandstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung5	|
		|	6	|	Fghi	|	Dino Geröllheimer	|	Stonestraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung6	|
		|	7	|	Ghij	|	Mr Schiefer			|	Rockstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung7	|
		|	8	|	Hijk	|	Pebbels Feuerstein	|	Granitstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung8	|
		|	9	|	Ijkl	|	Joe Rockhead		|	Teerstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung9	|
		|	10	|	Jklm	|	Pearl Slaghoople	|	Pebblestraße 5	|	2000	|	Stonehausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung10	|
		|	11	|	Klmn	|	Great Gazoo			|	Bambamstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung11	|
		|	12	|	Lmno	|	Arnold Paperboy		|	Feuerstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung12	|
		|	13	|	Mnop	|	Tex Hardrock		|	Schiefstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung13	|
		|	14	|	Nopq	|	Sam Slagheap		|	Heapstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung14	|
		|	15	|	Opqr	|	Eppy Brianstone		|	Slagstraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung15	|
		|	16	|	Pqrs	|	Gary Granite		|	Brianstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung16	|
		|	17	|	Qrst	|	Ann Margrock		|	Eppystraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung17	|
		|	18	|	Rstu	|	Perry Masonry		|	Marrockstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung18	|
		|	19	|	Stuv	|	Beau Brumelstone	|	Masonrystraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung19	|
		|	20	|	Tuvw	|	Hoppy Feuerstein	|	Beaustraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung20	|
		|	21	|	Uvwx	|	Water Buffalo		|	Lodgestraße 4	| 	53123	| 	Rockhausen	|	0228/123456	|	0228/123654	|	c.nuessle@tarent.de |	Bemerkung21	|
		|	22	|	Vwxy	|	Texarock Ranger		|	Rangerstraße 5	|	53123	|	Rockhausen	|	0228/123456	|	0228/123653	|	c.nuessle@tarent.de	|	Bemerkung22	|
	 Und ich betrachte die "Erste Seite" der Veranstaltungsortliste
	Wenn ich auf <Buchstabe> klicke
	Dann sehe ich <Seite>
	 Und ich sehe als "Listen-Info" den <Text>
	
	Beispiele:
		| Buchstabe			|	Seite			| Text 				|
		| K		     		|	2				| 11 - 20 von 22	|
		| 0		     		|	2				| 11 - 20 von 22	|
		| T		     		|	2				| 11 - 20 von 22	|
		| U			 		|	3				| 21 - 22 von 22	|
		| V			 		|	3				| 21 - 22 von 22	|
		| B		     		|	1				| 1 - 10 von 22		|
		| E		     		|	1				| 1 - 10 von 22		|
		| J		     		|	1				| 1 - 10 von 22		|
