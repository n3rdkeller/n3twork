Milestone 1 - Teambildung, Datenmodellierung und Basis-System

## Inhalte

Die Ziele für diesen Milestone in Kürze sind:

* Teambildung
* Datenmodellierung
* Basis-System

Im Abnahmegespräch des Milestones sollte jedes Teammitglied zu Wort kommen und seinen bzw. ihren Teil vorstellen können.

### Teambildung

Gleich ob sie sich im Team vorher schon kannten oder erst durch das Praktikum zusammengefunden haben: Durch die intensive gemeinsame Arbeit an einem Projekt lernt man sich neu kennen.
Jeder bringt seine Stärken und Schwächen mit. Das es bei der Zusammenarbeit immer wieder einmal zu Reibungspunkten kommt ist normal. Daher sollten sie sich von Anfang an um eine gute und respektvolle Teamkultur bemühen.

Möglichst frühzeitig sollten sie sich darüber einigen, wer welche Aufgaben übernimmt. Die folgende Liste gibt einige Anregungen für mögliche Rollen:

* GUI Entwicklung
* Programmlogik und Ablaufsteuerung
* Datenmodellierung und Implementierung der Datenbankanbindung
* Qualitätssicherung
* Dokumentation (Benutzerdokumentation und Entwicklerdokumentation)
* "Außenminister", übernimmt die Kommunikation nach außen
* Teamleiter
* ...

Ob eine Gruppe einen Teamleiter oder einen Außenminister haben möchte ist ihr freigestellt. Bei Punkten wie Qualitätssicherung, Dokumentation oder Entwicklung bestimmter Komponenten ist es aber sehr hilfreich, Verantwortliche benennen zu können.

Verfassen sie für die Abnahme des Milestones ein kurzes Selbstportrait. Wie heisst das Team? Wie heisst das soziale Netzwerk an dem gearbeitet wird? Wer im Team ist für welche Aufgabenbereiche verantwortlich? Wie wird Zusammenarbeit strukturiert- soll z.B. ein System zur Projektverwaltung eingesetzt werden um Aufgaben oder Fehler zu verwalten? Wie werden Quelltexte und Ressourcen verwaltet?

Das Selbstportrait sollte alles in allem so etwa eine Seite lang sein. Letztlich kommt es auf den Inhalt an, nicht auf die Länge. Es ist uns wichtig, dass sie sich zu diesen Punkten Gedanken machen und es ausformuliert zu Papier bringen. Eine Liste von Stichpunkten wäre zu kurz gegriffen. Für die Abgabe erstellen Sie bitte eine Version in PDF.

### Datenmodellierung

Erstellen sie auf Grundlage der informellen Spezifikation der Aufgabenstellung ein Datenmodell. Damit soll nicht zuletzt sichergestellt werden, dass die Entwicklung in die richtige Richtung geht.

Im Rahmen der Abgabe für den Milestone soll ein schriftlich kommentiertes UML-Klassendiagramm des Datenmodells abgegeben werden. Das UML Diagramm soll die Klassen, evtl. Vererbungshierarchien, sowie Kompositionen, Aggregationen, Assoziationen und deren Multiplizitäten beinhalten. Darüber hinaus soll es auch Attribute enthalten. Der Begleittext soll das Klassendiagramm noch einmal informell beschreiben und ggf. auf Besonderheiten eingehen, die sich nicht unmittelbar aus dem Diagramm ergeben. Es geht hier nur um die Klassen des Datenmodells, nicht der Programmsteuerung oder GUI. Der Begleittext zum Diagramm sollte eine halbe bis eine Seite umfassen. Das UML Diagramm soll in einem gängigen Bild- oder Vektorformat oder PDF abgegeben werden. Der Begleittext in PDF.

### Basis-System

Sie haben viele Freiheiten darin, wie sie ihr System konzipieren und implementieren. In den begleitenden Plenarterminen werden einige Ansätze vorgestellt, mit denen das Projekt umgesetzt werden kann. Technologisch soll folgender Rahmen eingehalten werden:

* Als Datenbank wird MySQL verwendet. Wir stellen für alle Teams separate Datenbanken und Accounts bereits mit denen sie arbeiten können.
* Die Serverseite soll rein in Java implementiert werden.
* Bei der Wahl des User-Interface sind sie weitgehend frei in ihrer Entscheidung. Sie können eine Java-Applikation als Client schreiben, JavaScript, PHP oder auch JSP benutzen. Es sollen beliebig viele Clients parallel auf den (remote) Server zugreifen können. Natürlich können zum Testen und Entwickeln Client und Server auch auf einer Maschiene laufen. Sprechen sie die Wahl der Technologie für den Client auf jeden Fall kurz mit ihrer Tutorin bzw. Tutor ab.

Entscheiden sie sich im Team für eine Architektur und implementieren sie damit das Grundsystem als proof-of-concept. Das Basis-System soll für den Benutzer folgende Funktionen anbieten:

* Login-Seite
 * Diese Ansicht ist nach dem Start den Clients oder dem Aufruf des Servers im Browser sichtbar (je nach gewähltem Ansatz)
 * Die Login-Seite enthält zunächst einmal Informationen über das System- also den Titel, den Namen des Teams und optional auch der Entwickler
 * Darüber hinaus bietet die Login-Seite Funktionen um sich anzumelden, einen Account anzulegen und das Programm ggf. zu beenden (wenn es als Applikation läuft)
* Account anlegen
 * Benutzer können sich eigenständig einen Account anlegen. Dazu müssen sie wie üblich einen Benutzernamen und ein Passwort wählen. Je nach gewähltem Datenmodell sind noch weitere Attribute wie z.B. EMail, Name usw. relevant.
 * Die Anfrage wird an den Server geschickt welche die Daten prüft- insbesondere ob der Benutzername nicht schon vergeben ist. Wenn alles ok ist wird in der Datenbank ein neuer Benutzer angelegt. In jedem Fall soll der Client erfahren ob alles geklappt hat oder ob ein Fehler aufgetreten ist. Bitte achten sie darauf, nie Passwörter im Klartext zu verschicken. Stattdessen sollten die Passwörter sofort mit einer Hashfunktion (z.B. MD5) verschlüsselt werden. Sie sollen auch so in der Datenbank gespeichert werden. Im Klartext liegen sie einzig im Client bei der Eingabe vor.
* Anmelden
 * Ein Benutzer soll sich mit Angabe von Benutzername und Passwort am System anmelden können.
 * Beim erfolgreichen Login wechselt die Ansicht zur Start-Seite. Andernfalls erfolgt eine Fehlermeldung.
 * Durch die erfolgreiche Anmeldung wird eine neue Sitzung (neudeutsch: Session) angelegt wofür der Client eine eindeitige SessionID erhält. Alle weiteren Anfragen vom Client an den Server müssen mit dieser SessionID als Berechtungsnachweis (aka Authentication) erfolgen. Die Session ID ist so lange gültig bis sich der Benutzer vom Server abmeldet, oder aber der Server die Session nach Ablauf einer bestimmten Zeit von Inaktivität selbst beendet (Session-Timeout).
* Start-Seite
 * Die Startseite soll im Laufe des Projekts Zugriff auf alle Programmfunktionen bieten. Für den ersten Milestone ist die Ansicht aber noch minimalistisch.
 * Der Benutzer sollte sehen können, mit welchem Benutzernamen er angemeldet ist. Stattdessen könnte auch der volle Name oder Nickname verwendet werden der nicht eindeutig sein muss.
 * Die Startseite bietet Funktionen um sich abzumelden. Dadurch gelangt man wieder zurück zur Startseite. Diese Funktion sollte auch dazu führen, dass eine ggf. geöffnete Session auf dem Server wieder geschlossen wird. Das ausloggen ist also nicht einfach nur ein umschalten auf der GUI. Auch der Server soll darüber informiert werden, dass sich ein Benutzer wieder ausgeloggt hat.

Ist der Client als Applikation umgesetzt sollte auch auf der Startseite eine Funktion existieren um das Programm zu beenden (ggf. verbunden mit einem gleichzeitig ausloggen)

Dieser Milestone deckt also bereits alle wesentlichen Bausteine der Client/Server Architektur ab: Es gibt einen Client der Anfragen an den Server schickt. Der Server schreibt in die Datenbank um Benutzer anzulegen oder führt Abfragen durch um Benutzername und Passwort bei einem Login zu prüfen. Schließlich gibt der Server eine Antwort an den Client zurück die dort angezeigt wird- etwa in Form eines Logins (Wechsel zur Start-Seite).

Mit diesem Milestone muss die Datenbank und die Datenbankanbindung zumindest soweit umgesetzt sein, dass Benutzer angelegt und abgefragt werden können. Das Datenmodell sollte vom Konzept her zwar stehen- es muss aber noch nicht vollständig implementiert sein.

## Abgabe und Bewertung
Der Abgabetermin ist Freitag, 7. November 2014, 18 Uhr (per Olat). Für reine LA-Gruppen ist der Abgabetermin Freitag, 14. November 2014, 18 Uhr. Da Olat keine Gruppenabgaben kennt genügt es, wenn einer der Teilnehmer die Abgabe im Namen der Gruppe durchführt. Bitte sorgen sie dafür, dass der Tutor bzw. die Tutorin ihr System schnell und ohne großen Aufwand testen kann um die Funktionalität zu prüfen!

Insgesamt können, wie bei jedem Milestone, 100 Punkte erreicht werden. Für die erfolgreiche Bearbeitung müssen mindestens 60 Punkte erreicht werden. Die Abgabe erfolgt per Olat. Bitte beachten Sie die Fristen!

* Selbstportrait des Teams (15 Punkte)
* Kommentiertes UML-Klassendiagramm des Datenmodells (15 Punkte)
* Basis-System (70 Punkte)

Bitte Beachten Sie, dass eine angemessene Kommentierung der Quelltexte mit JavaDoc in die Bewertung eingeht.
