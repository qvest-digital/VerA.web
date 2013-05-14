# language: de
Funktionalität: Als User mit entsprechenden Rechten möchte ich eine neue Firma ohne dedizierten Personenbezug anlegen können.

Grundlage: Ich bin als Administrator angemeldet und bin in der Maske Neue Person anlegen und im Reiter Personendaten
	Angenommen ich bin als Administrator angemeldet
	 Und ich sehe "Neue Person anlegen" 
	 

Szenariogrundriss: Ich lege eine neue Firma ohne dedizierten Personenbezug an
	Angenommen ich bin im Reiter "Personendaten"
	 Und ich wähle die Checkbox "Ist Firma" aus
	 Und ich fülle die Maske mit <Testdaten> aus
	Aber ohne "Vorname"
	Aber ohne "Nachname"
	Wenn ich dann auf "Speichern" klicke
	Dann sehe ich <Meldung>
	
	Beispiele:
		| 	Testdaten									|	Meldung						|
		| 	"Neue Firma ohne Personenbezug"				|	Firma erfolgreich angelegt	|