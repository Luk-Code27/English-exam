"""Erzeugt die Folien-Texturen und Modelle fuer das Resourcepack.

Jede Folie ist ein 16:9-Bild (1024 x 576) oben in einer 1024 x 1024 PNG-Datei.
Aufruf:  python3 tools/make_slides.py
Eigene Folien: PNG-Dateien (16:9) als slides/slide_1.png, slides/slide_2.png ... ablegen,
dann werden diese statt der Testfolien benutzt.
"""
import json
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parent.parent
RP = ROOT / "resourcepack" / "exam_slides" / "assets" / "exam"
OWN = ROOT / "slides"
W, H = 1024, 576
FONT = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"
FONT_REG = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"

BG, FG, ACCENT, MUTED = (24, 26, 33), (240, 240, 240), (230, 57, 70), (160, 165, 175)

TEST_SLIDES = [
    {"title": "Logic Gates", "sub": "explained with Minecraft Redstone", "lines": ["Test slide 1 of 2"]},
    {"title": "NOT gate", "sub": "The output is the opposite of the input",
     "table": [("Input", "Output"), ("OFF", "ON"), ("ON", "OFF")]},
]


def draw_test(slide, n):
    img = Image.new("RGB", (W, H), BG)
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, W, 14], fill=ACCENT)
    d.text((60, 70), slide["title"], font=ImageFont.truetype(FONT, 88), fill=FG)
    d.text((62, 180), slide["sub"], font=ImageFont.truetype(FONT_REG, 40), fill=MUTED)
    y = 270
    for line in slide.get("lines", []):
        d.text((62, y), line, font=ImageFont.truetype(FONT_REG, 40), fill=FG)
        y += 60
    if "table" in slide:
        f = ImageFont.truetype(FONT, 44)
        for r, (a, b) in enumerate(slide["table"]):
            yy = 270 + r * 80
            color = MUTED if r == 0 else FG
            d.text((80, yy), a, font=f, fill=color)
            d.text((380, yy), b, font=f, fill=ACCENT if b == "ON" else color)
            d.line([60, yy + 68, 620, yy + 68], fill=(60, 63, 72), width=3)
    d.text((W - 90, H - 60), str(n), font=ImageFont.truetype(FONT_REG, 32), fill=MUTED)
    return img


def main():
    tex_dir, model_dir, item_dir = RP / "textures" / "item", RP / "models" / "item", RP / "items"
    for p in (tex_dir, model_dir, item_dir):
        p.mkdir(parents=True, exist_ok=True)
    own = sorted(OWN.glob("slide_*.png"), key=lambda p: int(p.stem.split("_")[1])) if OWN.exists() else []
    count = len(own) if own else len(TEST_SLIDES)
    for i in range(1, count + 1):
        slide = Image.open(own[i - 1]).convert("RGB").resize((W, H)) if own else draw_test(TEST_SLIDES[i - 1], i)
        canvas = Image.new("RGBA", (W, W), (0, 0, 0, 0))
        canvas.paste(slide, (0, 0))
        canvas.save(tex_dir / f"slide_{i}.png", optimize=True)
        tex = f"exam:item/slide_{i}"
        model = {
            "textures": {"slide": tex, "particle": tex},
            "elements": [{
                "from": [0, 3.5, 7.9], "to": [16, 12.5, 8.1], "shade": False,
                "faces": {
                    "north": {"uv": [0, 0, 16, 9], "texture": "#slide"},
                    "south": {"uv": [0, 0, 16, 9], "texture": "#slide"},
                },
            }],
        }
        (model_dir / f"slide_{i}.json").write_text(json.dumps(model, indent=2) + "\n")
        item = {"model": {"type": "minecraft:model", "model": f"exam:item/slide_{i}"}}
        (item_dir / f"slide_{i}.json").write_text(json.dumps(item, indent=2) + "\n")
    print(f"{count} Folien erzeugt")


if __name__ == "__main__":
    main()
