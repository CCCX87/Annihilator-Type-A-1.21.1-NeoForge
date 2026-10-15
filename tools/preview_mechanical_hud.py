"""Render a side-by-side preview of the annihilation-moment HUD.

Row 1 reproduces what is currently implemented (translucent slab, hairline frame,
L corner brackets, continuous gradient bars, sweeping highlight).
Row 2 is the proposed mechanical treatment derived from the mod's own art:
opaque beveled plates, chamfered corners, rivets, segmented gauges, status lamps,
hazard stripes and the navy-violet / cyan / hazard-red palette of the existing icons.

Everything is drawn on the real 1x GUI pixel grid with Minecraft's own bitmap font,
then upscaled with nearest-neighbour so the preview shows true pixel shapes.

Usage: python tools/preview_mechanical_hud.py
"""

from __future__ import annotations

import io
import os
import zipfile

from PIL import Image, ImageDraw, ImageFont

PROJECT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ARTIFACTS = os.path.join(PROJECT, "build", "moddev", "artifacts")
CLIENT_JAR = os.path.join(ARTIFACTS, "neoforge-21.1.219-client-extra-aka-minecraft-resources.jar")
ABILITY_ICON = os.path.join(
    PROJECT, "src", "main", "resources", "assets", "dragonsurvival",
    "textures", "gui", "sprites", "abilities", "annihilator_type_a", "annihilation_formula.png",
)
CJK_FONT = "C:/Windows/Fonts/simhei.ttf"
OUTPUT = os.path.join(os.path.dirname(PROJECT), "hud_mechanical_proposal.png")

SCALE = 3

# --------------------------------------------------------------------------- palette

# current implementation
CUR_ACCENT = (0x6F, 0xE3, 0xFF)
CUR_ACCENT_DIM = (0x48, 0xA9, 0xC4)
CUR_WARN = (0xFF, 0x5A, 0x4D)
CUR_TEXT = (0xEA, 0xF6, 0xFF)
CUR_TEXT_DIM = (0x9F, 0xB6, 0xC4)
CUR_BG = (0x06, 0x0C, 0x14)
CUR_TRACK = (0x0E, 0x18, 0x20)

# proposed mechanical treatment, sampled from the mod's existing icons
MECH_PLATE = (0x16, 0x13, 0x26)
MECH_PLATE_HI = (0x2B, 0x25, 0x45)
MECH_PLATE_LO = (0x0A, 0x08, 0x14)
MECH_OUTLINE = (0x04, 0x03, 0x09)
MECH_SEAM = (0x3C, 0x35, 0x5E)
MECH_CYAN = (0x4F, 0xD8, 0xFF)
MECH_CYAN_DIM = (0x1E, 0x6B, 0x8C)
MECH_MAGENTA = (0xA8, 0x4F, 0xD8)
MECH_RED = (0xFF, 0x4A, 0x2B)
MECH_AMBER = (0xFF, 0xC2, 0x4A)
MECH_TEXT = (0xE8, 0xF4, 0xFF)
MECH_TEXT_DIM = (0x8A, 0x93, 0xB5)
MECH_TRACK = (0x07, 0x06, 0x0E)


# ------------------------------------------------------------------------------ font

def load_minecraft_font() -> dict[str, tuple[Image.Image, int]]:
    """Indexes ascii.png the way minecraft:include/default does.

    That provider's `chars` table lists the 16x16 sheet row by row, and its first two
    rows are entirely \\u0000 padding: U+0020 starts at row 2, column 0.
    """
    with zipfile.ZipFile(CLIENT_JAR) as jar:
        blob = jar.read("assets/minecraft/textures/font/ascii.png")
    sheet = Image.open(io.BytesIO(blob)).convert("RGBA")
    glyphs: dict[str, tuple[Image.Image, int]] = {}
    for code in range(32, 127):
        index = code - 32
        col, row = index % 16, index // 16 + 2
        cell = sheet.crop((col * 8, row * 8, col * 8 + 8, row * 8 + 8))
        alpha = cell.getchannel("A")
        width = 0
        for x in range(8):
            if any(alpha.getpixel((x, y)) > 24 for y in range(8)):
                width = x + 1
        glyphs[chr(code)] = (cell, width)
    return glyphs


GLYPHS = load_minecraft_font()
_CJK_CACHE: dict[tuple[str, int], Image.Image] = {}


def cjk_glyph(char: str, size: int) -> Image.Image:
    key = (char, size)
    if key not in _CJK_CACHE:
        mask = Image.new("L", (size + 6, size + 6), 0)
        ImageDraw.Draw(mask).text((-1, -1), char, font=ImageFont.truetype(CJK_FONT, size), fill=255)
        # Hard threshold: keeps the preview in the game's aliased pixel style.
        _CJK_CACHE[key] = mask.point(lambda v: 255 if v > 96 else 0)
    return _CJK_CACHE[key]


class Canvas:
    def __init__(self, width: int, height: int, background):
        self.image = Image.new("RGBA", (width, height), background)
        self.draw = ImageDraw.Draw(self.image)

    def rect(self, x: int, y: int, w: int, h: int, color) -> None:
        if w <= 0 or h <= 0:
            return
        self.draw.rectangle([x, y, x + w - 1, y + h - 1], fill=color)

    def frame(self, x: int, y: int, w: int, h: int, color) -> None:
        self.rect(x, y, w, 1, color)
        self.rect(x, y + h - 1, w, 1, color)
        self.rect(x, y, 1, h, color)
        self.rect(x + w - 1, y, 1, h, color)

    def hline(self, x: int, y: int, w: int, color) -> None:
        self.rect(x, y, w, 1, color)

    def vline(self, x: int, y: int, h: int, color) -> None:
        self.rect(x, y, 1, h, color)

    def gradient_v(self, x: int, y: int, w: int, h: int, top, bottom) -> None:
        for offset in range(h):
            t = offset / max(h - 1, 1)
            color = tuple(int(round(top[i] + (bottom[i] - top[i]) * t)) for i in range(3))
            self.rect(x, y + offset, w, 1, color)

    def text(self, x: int, y: int, value: str, color) -> int:
        """Draws ASCII with Minecraft's bitmap font, CJK with a thresholded system font."""
        pen = x
        for char in value:
            if char == " ":
                pen += 4
                continue
            if ord(char) < 127:
                glyph, width = GLYPHS.get(char, (None, 0))
                if glyph is None:
                    continue
                stamp = Image.new("RGBA", glyph.size, (*color, 255))
                stamp.putalpha(glyph.getchannel("A"))
                self.image.alpha_composite(stamp, (pen, y))
                pen += width + 1
            else:
                mask = cjk_glyph(char, 9)
                stamp = Image.new("RGBA", mask.size, (*color, 255))
                stamp.putalpha(mask)
                self.image.alpha_composite(stamp, (pen, y - 1))
                pen += 9
        return pen - x

    def text_width(self, value: str) -> int:
        total = 0
        for char in value:
            if char == " ":
                total += 4
            elif ord(char) < 127:
                total += GLYPHS.get(char, (None, 0))[1] + 1
            else:
                total += 9
        return total

    def paste_image(self, path: str, x: int, y: int, size: int) -> None:
        icon = Image.open(path).convert("RGBA")
        if icon.size != (size, size):
            icon = icon.resize((size, size), Image.NEAREST)
        self.image.alpha_composite(icon, (x, y))


# ------------------------------------------------------------------------ primitives

def segmented_bar(canvas: Canvas, x: int, y: int, w: int, h: int,
                  progress: float, lit, dim, segments: int = 24) -> None:
    """A fuel-gauge style bar: discrete blocks with 1px gaps, never a smooth gradient."""
    canvas.rect(x - 1, y - 1, w + 2, h + 2, MECH_OUTLINE)
    canvas.rect(x, y, w, h, MECH_TRACK)
    canvas.hline(x, y, w, MECH_PLATE_LO)
    gap = 1
    seg_w = max(1, (w - (segments - 1) * gap) // segments)
    filled = int(round(progress * segments))
    for index in range(segments):
        seg_x = x + index * (seg_w + gap)
        if seg_x + seg_w > x + w:
            break
        canvas.rect(seg_x, y + 1, seg_w, h - 2, lit if index < filled else dim)


def current_bar(canvas: Canvas, x: int, y: int, w: int, h: int, progress: float, start, end) -> None:
    canvas.rect(x, y, w, h, CUR_TRACK)
    filled = int(round(w * progress))
    if filled > 0:
        for offset in range(h):
            t = offset / max(h - 1, 1)
            color = tuple(int(round(start[i] + (end[i] - start[i]) * t)) for i in range(3))
            canvas.rect(x, y + offset, filled, 1, color)
        canvas.rect(x + filled - 1, y, 1, h, (255, 255, 255))
    canvas.frame(x - 1, y - 1, w + 2, h + 2, (0x20, 0x30, 0x40))


def mech_plate(canvas: Canvas, x: int, y: int, w: int, h: int, chamfer: int = 3) -> None:
    """Opaque machined plate: chamfered corners, 1px near-black outline, bevel lighting."""
    canvas.rect(x, y, w, h, MECH_PLATE)
    # chamfer the four corners
    for step in range(chamfer):
        span = chamfer - step
        canvas.draw.rectangle([x, y + step, x + span - 1, y + step], fill=(0, 0, 0, 0))
        canvas.draw.rectangle([x + w - span, y + step, x + w - 1, y + step], fill=(0, 0, 0, 0))
        canvas.draw.rectangle([x, y + h - 1 - step, x + span - 1, y + h - 1 - step], fill=(0, 0, 0, 0))
        canvas.draw.rectangle([x + w - span, y + h - 1 - step, x + w - 1, y + h - 1 - step], fill=(0, 0, 0, 0))
    # bevel: light top/left, dark bottom/right
    canvas.hline(x + chamfer, y, w - 2 * chamfer, MECH_PLATE_HI)
    canvas.vline(x, y + chamfer, h - 2 * chamfer, MECH_PLATE_HI)
    canvas.hline(x + chamfer, y + h - 1, w - 2 * chamfer, MECH_PLATE_LO)
    canvas.vline(x + w - 1, y + chamfer, h - 2 * chamfer, MECH_PLATE_LO)
    # outline, skipping the cut corners
    canvas.hline(x + chamfer, y - 1, w - 2 * chamfer, MECH_OUTLINE)
    canvas.hline(x + chamfer, y + h, w - 2 * chamfer, MECH_OUTLINE)
    canvas.vline(x - 1, y + chamfer, h - 2 * chamfer, MECH_OUTLINE)
    canvas.vline(x + w, y + chamfer, h - 2 * chamfer, MECH_OUTLINE)
    for step in range(chamfer):
        canvas.draw.point((x + chamfer - 1 - step, y - 1 + step), fill=MECH_OUTLINE)
        canvas.draw.point((x + w - chamfer + step, y - 1 + step), fill=MECH_OUTLINE)
        canvas.draw.point((x + chamfer - 1 - step, y + h - step), fill=MECH_OUTLINE)
        canvas.draw.point((x + w - chamfer + step, y + h - step), fill=MECH_OUTLINE)


def rivet(canvas: Canvas, x: int, y: int) -> None:
    canvas.rect(x, y, 2, 2, MECH_OUTLINE)
    canvas.draw.point((x, y), fill=MECH_SEAM)


def hazard_stripe(canvas: Canvas, x: int, y: int, w: int) -> None:
    canvas.rect(x, y, w, 3, (0x1A, 0x14, 0x08))
    for index in range(0, w, 4):
        canvas.rect(x + index, y, 2, 3, MECH_AMBER)


def lamp(canvas: Canvas, x: int, y: int, color) -> None:
    canvas.rect(x - 1, y - 1, 5, 5, MECH_OUTLINE)
    canvas.rect(x, y, 3, 3, color)
    canvas.draw.point((x, y), fill=(255, 255, 255))


def vent(canvas: Canvas, x: int, y: int, w: int = 12) -> None:
    for index in range(3):
        canvas.rect(x, y + index * 2, w, 1, MECH_PLATE_LO)


# ------------------------------------------------------------------------ hud panels

def current_top(canvas: Canvas, x: int, y: int) -> None:
    w, h = 196, 46
    canvas.rect(x, y, w, h, CUR_BG)
    canvas.gradient_v(x + 1, y + 1, w - 2, h // 2, (0x38, 0x44, 0x50), CUR_BG)
    canvas.frame(x, y, w, h, CUR_ACCENT_DIM)
    for corner in ((x, y), (x + w - 5, y), (x, y + h - 5), (x + w - 5, y + h - 5)):
        cx, cy = corner
        canvas.rect(cx, cy, 5, 1, CUR_ACCENT)
        canvas.rect(cx, cy, 1, 5, CUR_ACCENT)
    canvas.paste_image(ABILITY_ICON, x + 7, y + 7, 22)
    canvas.text(x + 34, y + 8, "湮灭时刻", CUR_TEXT)
    canvas.text(x + 34, y + 20, "Lv.7", CUR_ACCENT)
    remaining = "剩余42.5s"
    canvas.text(x + w - 9 - canvas.text_width(remaining), y + 8, remaining, CUR_TEXT)
    current_bar(canvas, x + 9, y + 34, w - 18, 6, 0.71, CUR_ACCENT, (0x9B, 0xF0, 0xFF))
    for step in range(1, 10):
        canvas.rect(x + 9 + (w - 18) * step // 10, y + 34, 1, 4, (0x50, 0x60, 0x70))
    canvas.rect(x + 1, y + 1 + int(0.4 * (h - 2)), w - 2, 1, (0x30, 0x30, 0x30))


def current_left(canvas: Canvas, x: int, y: int) -> None:
    w, h = 124, 78
    canvas.rect(x, y, w, h, CUR_BG)
    canvas.gradient_v(x + 1, y + 1, w - 2, h // 2, (0x38, 0x44, 0x50), CUR_BG)
    canvas.frame(x, y, w, h, CUR_ACCENT_DIM)
    for corner in ((x, y), (x + w - 5, y), (x, y + h - 5), (x + w - 5, y + h - 5)):
        cx, cy = corner
        canvas.rect(cx, cy, 5, 1, CUR_ACCENT)
        canvas.rect(cx, cy, 1, 5, CUR_ACCENT)
    canvas.text(x + 6, y + 6, "自身状态", CUR_ACCENT)
    canvas.text(x + w - 6 - canvas.text_width("18.5/20.0"), y + 6, "18.5/20.0", CUR_TEXT)
    current_bar(canvas, x + 6, y + 17, w - 12, 6, 0.92, (0xFF, 0x4D, 0x4D), (0xFF, 0xA3, 0x6B))
    canvas.text(x + 6, y + 25, "护甲 14", CUR_TEXT_DIM)
    rows = [("力量", "28.4s", (0xFF, 0x4D, 0x4D)), ("急迫", "28.4s", (0xD9, 0xC0, 0x43)),
            ("迅捷", "28.4s", (0x4D, 0xA6, 0xFF)), ("吸收", "28.4s", (0xFF, 0xC2, 0x4A))]
    row_y = y + 34
    for name, value, color in rows:
        canvas.rect(x + 6, row_y + 2, 7, 7, color)
        canvas.frame(x + 6, row_y + 2, 7, 7, (0x60, 0x60, 0x60))
        canvas.text(x + 16, row_y + 1, name, CUR_TEXT_DIM)
        canvas.text(x + w - 6 - canvas.text_width(value), row_y + 1, value, color)
        row_y += 11


def current_right(canvas: Canvas, x: int, y: int) -> None:
    w, h = 124, 34
    canvas.rect(x, y, w, h, CUR_BG)
    canvas.gradient_v(x + 1, y + 1, w - 2, h // 2, (0x38, 0x44, 0x50), CUR_BG)
    canvas.frame(x, y, w, h, CUR_ACCENT_DIM)
    for corner in ((x, y), (x + w - 5, y), (x, y + h - 5), (x + w - 5, y + h - 5)):
        cx, cy = corner
        canvas.rect(cx, cy, 5, 1, CUR_ACCENT)
        canvas.rect(cx, cy, 1, 5, CUR_ACCENT)
    canvas.text(x + 6, y + 6, "僵尸", CUR_TEXT)
    canvas.text(x + w - 6 - canvas.text_width("12.0/20.0"), y + 6, "12.0/20.0", CUR_WARN)
    current_bar(canvas, x + 6, y + 19, w - 12, 6, 0.6, (0xFF, 0x3B, 0x30), (0xFF, 0x9A, 0x6B))


def mech_top(canvas: Canvas, x: int, y: int) -> None:
    w, h = 210, 58
    mech_plate(canvas, x, y, w, h, chamfer=4)
    # model plate: recessed strip carrying the designation, like a machine nameplate
    canvas.rect(x + 48, y + 5, w - 56, 13, MECH_PLATE_LO)
    canvas.frame(x + 48, y + 5, w - 56, 13, MECH_SEAM)
    canvas.text(x + 52, y + 8, "ATA-01 湮灭型-A", MECH_CYAN)
    lamp(canvas, x + w - 12, y + 7, MECH_CYAN)
    canvas.paste_image(ABILITY_ICON, x + 9, y + 12, 32)  # native 32px: no fractional scaling
    vent(canvas, x + 52, y + 22, 14)
    canvas.text(x + 70, y + 21, "状态 过载协议", MECH_TEXT_DIM)
    lamp(canvas, x + 52, y + 33, MECH_RED)
    canvas.text(x + 60, y + 32, "输出 141%", MECH_RED)
    lamp(canvas, x + 132, y + 33, MECH_AMBER)
    canvas.text(x + 140, y + 32, "剩余42.5s", MECH_AMBER)
    segmented_bar(canvas, x + 52, y + 45, w - 64, 8, 0.71, MECH_CYAN, MECH_CYAN_DIM, segments=26)
    rivet(canvas, x + 3, y + 5)
    rivet(canvas, x + w - 6, y + 5)
    rivet(canvas, x + 3, y + h - 7)
    rivet(canvas, x + w - 6, y + h - 7)
    hazard_stripe(canvas, x + 6, y + h - 4, w - 12)


def mech_left(canvas: Canvas, x: int, y: int) -> None:
    w, h = 132, 100
    mech_plate(canvas, x, y, w, h, chamfer=3)
    canvas.text(x + 6, y + 5, "自身状态", MECH_CYAN)
    lamp(canvas, x + w - 14, y + 5, MECH_AMBER)
    segmented_bar(canvas, x + 6, y + 17, w - 12, 8, 0.92, (0xFF, 0x5A, 0x3C), (0x5A, 0x1E, 0x14), segments=20)
    canvas.text(x + 6, y + 28, "18.5/20.0", MECH_TEXT)
    canvas.text(x + w - 6 - canvas.text_width("护甲 14"), y + 28, "护甲 14", MECH_TEXT_DIM)
    hazard_stripe(canvas, x + 6, y + 38, w - 12)
    rows = [("力量", "28.4", MECH_RED), ("急迫", "28.4", MECH_AMBER),
            ("迅捷", "28.4", MECH_CYAN), ("吸收", "28.4", MECH_MAGENTA)]
    row_y = y + 45
    for index, (name, value, color) in enumerate(rows):
        canvas.text(x + 6, row_y + 1, name, MECH_TEXT)
        dim = tuple(int(channel * 0.22) for channel in color)
        segmented_bar(canvas, x + 28, row_y + 3, 58, 5, 0.8 - 0.16 * index, color, dim, segments=12)
        canvas.text(x + w - 6 - canvas.text_width(value), row_y + 1, value, MECH_TEXT_DIM)
        row_y += 11
    rivet(canvas, x + 3, y + 4)
    rivet(canvas, x + w - 6, y + 4)
    rivet(canvas, x + 3, y + h - 6)
    rivet(canvas, x + w - 6, y + h - 6)


def mech_right(canvas: Canvas, x: int, y: int) -> None:
    w, h = 132, 48
    mech_plate(canvas, x, y, w, h, chamfer=3)
    canvas.text(x + 6, y + 5, "目标锁定", MECH_RED)
    lamp(canvas, x + w - 14, y + 5, MECH_RED)
    canvas.text(x + 6, y + 17, "僵尸 #0412", MECH_TEXT)
    segmented_bar(canvas, x + 6, y + 28, w - 12, 8, 0.6, MECH_RED, (0x4A, 0x14, 0x10), segments=20)
    canvas.text(x + 6, y + 38, "12.0/20.0", MECH_TEXT_DIM)
    canvas.text(x + w - 6 - canvas.text_width("护甲 2"), y + 38, "护甲 2", MECH_TEXT_DIM)
    rivet(canvas, x + 3, y + 4)
    rivet(canvas, x + w - 6, y + 4)


# ------------------------------------------------------------------------------ main

def render_row(canvas: Canvas, origin_y: int, mechanical: bool) -> None:
    """Top panel first, then the two side panels below it so nothing overlaps."""
    row_w = 300
    if mechanical:
        mech_top(canvas, (row_w - 210) // 2, origin_y)
        mech_left(canvas, 6, origin_y + 64)
        mech_right(canvas, row_w - 6 - 132, origin_y + 64)
    else:
        current_top(canvas, (row_w - 196) // 2, origin_y)
        current_left(canvas, 6, origin_y + 64)
        current_right(canvas, row_w - 6 - 124, origin_y + 64)


def main() -> None:
    row_w, row_h, label_h = 300, 168, 14
    canvas = Canvas(row_w, (label_h + row_h) * 2 + 8, (0x1B, 0x1E, 0x26))

    canvas.text(6, 2, "当前实现  全息玻璃感", (0x9F, 0xB6, 0xC4))
    render_row(canvas, label_h, False)

    canvas.hline(0, label_h + row_h + 2, row_w, (0x33, 0x38, 0x44))
    canvas.text(6, label_h + row_h + 5, "机械感方案  金属面板", MECH_CYAN)
    render_row(canvas, label_h + row_h + 6 + label_h, True)

    preview = canvas.image.resize((canvas.image.width * SCALE, canvas.image.height * SCALE), Image.NEAREST)
    preview.save(OUTPUT)
    print(f"wrote {OUTPUT} ({preview.width}x{preview.height})")


if __name__ == "__main__":
    main()
