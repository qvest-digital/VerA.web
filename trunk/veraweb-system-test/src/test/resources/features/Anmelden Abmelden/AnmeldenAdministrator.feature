Funktionalität: Ich möchte mich als User in der Rolle als Administrator anmelden 

Szenario: Ich melde mich nicht erfolgreich in der Rolle als Administrator an
Angenommen ich bin nicht angemeldet
Wenn ich folgende Daten eintrage
| 	Benutzername:	|	Passwort:		|
|	 falscherUser	|	ohnePasswort	|
Und ich auf "Anmelden" klicke
Dann sehe ich die Meldung "Es ist ein Fehler bei der Anmeldung aufgetreten. Bitte überprüfen Sie Ihren Benutzernamen und Ihr Passwort."

Szenario: Ich melde mich erfolgreich in der Rolle als Administrator an
Angenommen ich bin nicht angemeldet
Wenn ich folgende Daten eintrage
| 	Benutzername:	|	Passwort:	|
|	administrator	|	mySecret2$	|
Und ich auf "Anmelden" klicke
Dann sehe ich die Meldung "Sie wurden erfolgreich mit der Rolle administrator am System angemeldet."