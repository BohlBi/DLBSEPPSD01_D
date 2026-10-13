# OnDeviceAI

Android-App, mit der sich per Chat in natürlicher Sprache Wecker und Timer stellen, Kalendereinträge anlegen und Anrufe starten lassen. Die Eingaben verarbeitet ein Sprachmodell (Gemma 4 E2B), das vollständig auf dem Smartphone läuft

Projekt im IU-Kurs DLBSEPPSD01_D – Projekt: Software Development.

## Funktionen

- Chat mit einem lokalen Sprachmodell (LiteRT-LM), Antworten gestreamt, Abbruch per Stop-Button
- Aktionen per Function Calling:
  - Wecker (Uhr-App des Systems, bis 24 Stunden im Voraus)
  - Timer (Uhr-App des Systems, bis 24 Stunden)
  - Kalendereintrag (Kalender-App mit vorausgefülltem Termin)
  - Anruf (Kontaktsuche, Telefon-App mit der Nummer)
- Modelle direkt in der App von Hugging Face herunterladen und im Chat wechseln
- Einstellung GPU/CPU für die Ausführung des Modells

## Voraussetzungen

- Android 15 (API 35) oder neuer
- ca. 3 GB freier Speicher für Gemma 4 E2B, empfohlen 8 GB RAM
- JDK 21

## Bauen und Testen

```bash
./gradlew :app:assembleDebug        # APK bauen
./gradlew :app:installDebug         # auf angeschlossenem Gerät installieren
./gradlew :app:testDebugUnitTest    # Unit-Tests
```

Nach dem ersten Start im Tab „Modelle“ ein Modell herunterladen, danach im Chat auswählen. Beim Öffnen des Chats fragt die App nach Zugriff auf die Kontakte (für Anrufe).

## Architektur

- Jetpack Compose, Material 3, Navigation 3, MVI
- Pakete pro Feature (`chat`, `models`, `settings`) mit den Schichten `domain`, `application`, `infrastructure`, `presentation`

## Modelle und Lizenzen

Die Modelle sind nicht Teil des Repositorys; die App lädt sie von Hugging Face (`litert-community`). Es gelten die Lizenzen der jeweiligen Modelle:

- Gemma 4 E2B (`litert-community/gemma-4-E2B-it-litert-lm`): Apache 2.0, siehe Modellkarte auf Hugging Face. Gemma ist eine Marke von Google; für frühere Gemma-Generationen gelten die [Gemma Terms of Use](https://ai.google.dev/gemma/terms).
- Qwen3 0.6B (`litert-community/Qwen3-0.6B`): Apache 2.0
