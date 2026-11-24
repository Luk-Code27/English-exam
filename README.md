# English exam: Logic gates explained with Minecraft Redstone

Präsentation in Minecraft Java (26.3, später 26.4). Folien werden per Knopfdruck auf einer
großen Leinwand in der Welt angezeigt. Ohne Mods: ein Datapack und ein Resourcepack.

Stand: **Prototyp mit 2 Testfolien.**

## Inhalt

| Ordner | Was |
| --- | --- |
| `datapack/exam_slides` | Datapack: Leinwand, Knöpfe, Fernbedienung, Reset |
| `resourcepack/exam_slides` | Resourcepack: die Folien als Bilder |
| `tools/make_slides.py` | Baut die Folien-Bilder und Modelle neu |
| `slides/` | (später) eigene Folien als `slide_1.png`, `slide_2.png` ... im Format 16:9 |

## Installieren (Modrinth-Instanz)

1. Ordner `datapack/exam_slides` (oder `exam_slides_datapack.zip`) kopieren nach
   `<Instanz>/saves/<Weltname>/datapacks/`.
2. `exam_slides_resourcepack.zip` kopieren nach `<Instanz>/resourcepacks/`.
   Im Spiel: Optionen → Ressourcenpakete → „Exam slides“ aktivieren.
3. Welt öffnen (Cheats müssen an sein). Im Chat `/reload` eingeben.

## Im Spiel benutzen

| Befehl | Was passiert |
| --- | --- |
| `/function exam:screen/place` | Leinwand 8 Blöcke vor dir aufstellen (schau dabei auf die Wand) |
| `/function exam:screen/size {s:10}` | Leinwand größer/kleiner (Zahl = Breite in Blöcken, nur ganze Zahlen) |
| `/function exam:screen/move {x:0,y:1,z:0}` | Leinwand verschieben |
| `/function exam:screen/remove` | Leinwand entfernen |
| `/function exam:kit` | Fernbedienung (Vor/Zurück) und fertige Befehlsblöcke für jede Folie |
| `/function exam:goto {n:2}` | Folie 2 zeigen (das steht auch in den Befehlsblöcken) |
| `/function exam:next` / `exam:prev` | Nächste / vorherige Folie |
| `/function exam:reset` | Vor der Prüfung: Folie 1, Mittag, klares Wetter, friedlich |

**Knopf an einer Station:** Befehlsblock aus `/function exam:kit` hinstellen, Knopf drankleben, fertig.
Jeder Knopf zeigt immer *seine* Folie, so kommt nichts durcheinander.

**Fernbedienung:** Grüner Farbstoff = nächste Folie, roter Farbstoff = vorherige (Rechtsklick).

## Tipps für die Welt

Spielregeln beim Erstellen der Welt einstellen: Tag-Nacht-Wechsel aus, Wetter aus, keine Mobs.
F1 blendet die Leiste aus. Für den schwachen Laptop: Sichtweite 6–8 Chunks.

## Folien ändern

`python3 tools/make_slides.py` baut die Bilder neu. Liegen PNGs in `slides/`, werden die benutzt.
Wenn sich die Anzahl der Folien ändert, auch `#max` in `datapack/.../load.mcfunction` anpassen.
