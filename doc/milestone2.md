# Milestone 2 - Benutzer, Freunde & Gruppen

## Inhalte

Die Ziele für diesen Milestone in Kürze sind die Implementierung von:

- Benutzer
- Freunde
- Gruppen
- Statistik

Nachdem im ersten Milestone die Grundlagen gelegt wurden kann es sinnvoll sein, sich zu Beginn des 2. Milestones noch einmal die bisherige Architektur genauer anzuschauen. Da die weitere Programmierung auf diesen Grundlagen aufbauen wird lohnt es sich vielleicht, den Code und das Design noch etwas zu überarbeiten. Fühlt sich alles rund an, oder können hier und da Pakte, Klassen und Methoden noch besser strukturiert werden?

#### Benutzer

Das Anlegen von neuen Benutzern wurde bereits im Milestone 1 implementiert. Mit diesem Milestones kommen einige Funktionen und Ansichten dazu:

- Benutzer-Einstellungen
  - Sobald ein Benutzer angemeldet ist, soll er die Möglichkeit haben seine Attribute wie z.B. EMail, Name, Passwort oder Selbstdarstellung zu ändern. Einzig der Benutzername darf nicht verändert werden.
  - Später können hier noch bestimmte Programmeinstellungen hinzukommen, wie z.B. Regeln wer dem Benutzer Nachrichten schicken darf und wer nicht.
- Benutzer löschen
  - Ein Benutzer soll sich selbst aus dem System löschen können- aber nur  nach einer Sicherheitsabfrage ob er/sie das wirklich will.
  - Durch das Löschen des eigenen Accounts gelangt der Benutzer wieder zurück auf die Login-Seite
- Benutzer suchen und auflisten
  - Implementieren sie eine Ansicht um Benutzer nach bestimmten Attributen wie z.B. Name suchen zu können. Es sollte zumindest nach Name und EMail-Adresse gesucht werden können. Weitere Felder können, je nach gewählter Modellierung, natürlich auch sinnvoll sein.
  - Der Benutzer sollte verschiedene Sortierungen wählen können, z.B. nach Name.
  - Listen von Benutzern werden im Client immer wieder benötigt. Es macht daher Sinn, sich eine möglichst modular und flexibel einsetzbare Lösung zu überlegen.

#### Freunde

Benutzer sollen andere Benutzer einseitig als Freunde markieren können. Das Datenmodell sollte erfassen, zu welchem Zeitpunkt dies erfasst wurde. Natürlich sollen sich Benutzer auch wechselseitig als Freunde markieren können. Es macht aber Sinn, dies getrennt voneinander zu betrachten.

- Freundschaft setzen
  - Ein Benutzer soll in einer geeigneten Ansicht die Möglichkeit haben, einen anderen Benutzer als befreundet zu markieren. Im laufe des Projekts kann es mehr als eine Stelle geben wo man diese Funktion nutzen können möchte.
- Freunde anzeigen
  - Ein Benutzer soll sich eine Liste aller Freunde anzeigen lassen können.
  - Es sollten entweder über Filterkriterien oder über separate Ausgaben folgende Ansichten für den Benutzer möglichen sein:
    - mit wem bin ich wechselseitig befreundet?
    - mit wem bin ich nur einseitig befreundet?
    - wer ist mit mir befreundet, aber nicht umgekehrt?
- Freundschaft zurücknehmen
  - Ein Benutzer kann einseitig die Freundschaft zu einem anderen Benutzer löschen. Er kann jedoch nicht ändern, dass er von anderen als Freund markiert wird.

#### Gruppen

Gruppen haben im System die Funktion, Interessensgruppe abzubilden. Eine Gruppe ist also eine lose Verbindung von Benutzern. Wie in der Spezifikation dargestellt sind Gruppen selbstorganisiert: Jeder Benutzer kann Gruppen erstellen, ihnen beitreten und sie auch wieder verlassen. Eine leere Gruppe sollte automatisch wieder vom System gelöscht werden.

- Gruppe anlegen
  - Ein Benutzer soll die Möglichkeit haben, eine neue Gruppe unter Angabe der benötigten Felder anzulegen.
  - Der Ersteller einer Gruppe ist automatisch auch das erste Mitglied- auch wenn er sie später wieder verlassen kann.
- Gruppen auflisten und suchen
  - Analog zur Auflistung von Benutzern soll ein Benutzer Gruppen auflisten, durchsuchen und sortieren können
- Informationen zu einer gewählten Gruppe anzeigen
  - für eine gewählte Gruppe sollen Details sichtbar sein (z.B. eine Beschreibung sofern vorhanden)
  - Liste aller Mitglieder
- Gruppe beitreten
  - Ein Benutzer kann jeder beliebigen Gruppe beitreten
- Gruppe verlassen
  - Ein Benutzer kann eien Gruppe wieder verlassen

#### Statistik

Ein Benutzer soll in einer geeigneten Form folgende Informationen bekommen können:

- Wie viele Benutzer gibt es im System?
- Wie viele Gruppen gibt es?
- In wie vielen Gruppen bin ich?
- Wie viele wechselseitige Freunde habe ich?
- Wie viele habe ich nur einseitig als Freunde markiert?
- Wie viele haben mich einseitig als Freund markiert?
- Wie viele Benutzer sind zur Zeit am Server eingeloggt?

## Abgabe und Bewertung

Der Abgabetermin ist Freitag, 28. November 2014, 18 Uhr (per Olat). Für reine LA-Gruppen ist der Abgabetermin Freitag, 12. Dezember 2014, 18 Uhr.

Insgesamt können, wie bei jedem Milestone, 100 Punkte erreicht werden. Für die erfolgreiche Bearbeitung müssen mindestens 60 Punkte erreicht werden. Die Abgabe erfolgt per Olat. Bitte beachten Sie die Fristen!

- Benutzer (25 Punkte)
- Freunde (30 Punkte)
- Gruppen (30 Punkte)
- Statistik (15 Punkte)

Bitte Beachten Sie, dass eine angemessene Kommentierung der Quelltexte mit JavaDoc sowie eine gute Strukturierung des Softwareprojekts in die Bewertung eingeht.