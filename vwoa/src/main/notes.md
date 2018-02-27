Lukas’ Notizen zu webroot-src/
(gehören da aber nicht rein, da sie dann installiert würden)
────────────────────────────────────────────────────────────

| Funktionalität                                | Client | Backend |Kommentar                                                                       |
|-----------------------------------------------+--------+---------+--------------------------------------------------------------------------------|
| Login / Authentifizierung                     | 95%    | 95%     |Gültigkeitsdauer der Osiam sowie der HMAC-Tokens wird noch nicht berücksichtigt |
| Authorisierung (ad hoc [1])                   | 90%    | 70%     |(1)                                                                             |
| Registrierung                                 | 100%   | 100%    | j
| Account aktivieren / Registrierung bestätigen | 100%   | 100%    | j
| Password Reset                                | 100%   | 90%     | j  zufällige neue  uuids beim verwenden des reset links? warum?
| Offene Veranstaltungen                        | 100%   | 100%    | j
| Meine Veranstaltungen                         | 100%   | 100%    | j
| Kontaktdaten                                  | 100%   | 100%    | j
| Delegation anmelden                           | 100%   | 100%    |
| Pressevertreter anmelden                      | 100%   | 100%    | j
| Zusagestatus bearbeiten                       | 100%   | 100%    | j
| Anmeldung zu nicht-öffentlicher Veranstaltung | 100%   | 100%    | j

(1) Authorisierung heißt idR: meine Daten darf ich sehen / ändern (Zu- und Absagen, Kontaktdaten), sonst nichts.
    In den meisten Fällen ist das damit erledigt, dass wir die Backend API anpassen und hier einfach die jeweils
    die Identität des Users aus dem HMAC-Token lesen, anstatt sie in der URI zu kodieren.
    Eine gesonderte Authorisierung ist notwendig:

      - Beim Anmelden einer Delegation: ist der Firmenaccount zu dieser Veranstaltung eingeladen?

65c88590-45c1-4adc-9112-ffa1a068e5d7

Authorisierung:
---------------

  - in den meisten Fällen trivial: ich darf sehen/ändern was (zu) mir gehört.
  - Lösung:
    - Whitlist für Resourcen, die öffentlich sind.
    - User-ID *NICHT* in URI kodieren, sondern aus dem hmac-Token nehmen.
  - Bislang einzige Ausnahme: Delegationsanmeldungen:
    - hier prüft die Resource selbst, ob der angemeldete Benutzer mit der
      jeweiligen Delegation assoziiert ist.
    - mir kommt das unnötig kompliziert vor!
