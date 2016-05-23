| Funktionalität                                | Client | Backend |Kommentar                                                                       |
|-----------------------------------------------+--------+---------+--------------------------------------------------------------------------------|
| Login / Authentifizierung                     | 95%    | 95%     |Gültigkeitsdauer der Osiam sowie der HMAC-Tokens wird noch nicht berücksichtigt |
| Authorisierung (ad hoc [1])                   | 90%    | 70%     |(1)                                                                             |
| Registrierung                                 | 100%   | 100%    |
| Account aktivieren / Registrierung bestätigen | 100%   | 100%    |
| Password Reset                                | 100%   | 90%     | zufällige neue  uuids beim verwenden des reset links? warum?
| Offene Veranstaltungen                        | 100%   | 100%    |
| Meine Veranstaltungen                         | 100%   | 100%    |
| Kontaktdaten                                  | 100%   | 100%    |
| Delegation anmelden                           | 100%   | 100%    |
| Pressevertreter anmelden                      | 100%   | 100%    |
| Zusagestatus bearbeiten                       | 100%   | 100%    |
| Anmeldung zu nicht-öffentlicher Veranstaltung | ?      | ?       |XXX 


(1) Authorisierung heißt idR: meine Daten darf ich sehen / ändern (Zu- und Absagen, Kontaktdaten), sonst nichts.
    In den meisten Fällen ist das damit erledigt, dass wir die Backend API anpassen und hier einfach die jeweils
    die Identität des Users aus dem HMAC-Token lesen, anstatt sie in der URI zu kodieren.
    Eine gesonderte Authorisierung ist notwendig:

      - Beim Anmelden einer Delegation: ist der Firmenaccount zu dieser Veranstaltung eingeladen?


