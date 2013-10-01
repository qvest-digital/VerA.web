# language: de
Funktionalität: Ich möchte mich als User in der Rolle als Administrator anmelden 


Grundlage:
	Angenommen ich bin nicht angemeldet


Szenariogrundriss: Fehlgeschlagene Anmeldung in der Rolle als Administrator an
	Wenn ich "<Anmeldedaten>" eingebe
	 Und ich auf "Anmelden" klicke
	Dann sehe ich als "Fehler-Text" die Meldung "Es ist ein Fehler bei der Anmeldung aufgetreten. Bitte überprüfen Sie Ihren Benutzernamen und Ihr Passwort."

	Beispiele:
	| 	Anmeldedaten 									|
	|	ungültige Anmeldedaten mit falschem Benutzer	|
	|	ungültige Anmeldedaten mit falschem Passwort	|


Szenario: Ich melde mich erfolgreich in der Rolle als Administrator an
	Wenn ich "gültige Anmeldedaten" eingebe
	 Und ich auf "Anmelden" klicke
	Dann sehe ich als "Header-Titel" die Meldung "Sie wurden erfolgreich mit der Rolle administrator am System angemeldet."