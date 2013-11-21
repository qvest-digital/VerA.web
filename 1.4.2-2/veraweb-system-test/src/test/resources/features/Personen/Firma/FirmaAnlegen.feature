# language: de
Funktionalität: Als User mit entsprechenden Rechten möchte ich eine neue Firma ohne dedizierten Personenbezug anlegen können.

Grundlage: Ich bin als Administrator angemeldet und bin in der Maske Neue Person anlegen und im Reiter Personendaten
	Angenommen ich bin als Administrator angemeldet
	 Und ich befinde mich in der Maske "Neue Person anlegen" 
	 

Szenariogrundriss: Ich lege eine neue Firma ohne dedizierten Personenbezug an
	Angenommen ich bin im Reiter "Personendaten"
	 Und ich wähle die Checkbox "Ist Firma" aus
	 Und ich fülle die Maske mit <Testdaten> aus
	Aber ohne "Vorname"
	 Und ohne "Nachname"
	Wenn ich auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| 	Testdaten							|	Meldung													 |
		| 	"Neue Firma ohne Personenbezug"		|	Firma erfolgreich angelegt								 |
		|	"Neue Firma ohne Pflichtfeld"		|	Sie müssen einen Namen für die Firma/Institution angeben |
				

Szenario: Checkbox aktiviert neues Feld	
Beim Auswählen von "ist Firma" erscheint ein neues Feld "Firma/Institution 
	
	Angenommen ich bin im Reiter "Personendaten"
	 Und ich sehe kein Feld "Firma/Institution"
	Wenn ich die Checkbox "Ist Firma" auswähle
	Dann sehe ich das Feld "Firma/Institution"
	
	
Szenario: Referenzierung Firma
Der Eintrag in dieses Feld referenziert auf das Feld "Firma/Institution" unter Anschrift Geschäftlich	
	
	Angenommen ich bin im Reiter "Personendaten"
	 Und ich wähle die Checkbox "Ist Firma" aus
	 Und ich fülle das Feld "Firma/Institution" mit "tarent Solutions" aus
	 Und ich klicke auf "Speichern"
	Wenn ich den Reiter "Anschriften" auswähle
	Dann sehe ich im Reiter "Geschäftlich" das Feld "Firma/Institution"
	 Und ich sehe im Feld "Firma/Institution" den Eintrag "tarent Solutions"
	