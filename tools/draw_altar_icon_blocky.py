"""Draws the altar icon in the blocky style of the actual Annihilator Type A model.

Two rules make a blocky head read at this size, and both were learned the hard way here:

1. Leave background visible around the silhouette. A head built from wide rectangles that fill the
   frame becomes a stack of planks no matter how it is subdivided.
2. Build the masses as one union, outline that union once, then add the plates on top. Per-rectangle
   outlines merge into noise and the head never separates from the background.

Masses: a low thin snout, a step up to the brow, a tall skull behind it, the jaw hung below the
snout with a mouth gap, a crest blade sweeping up out of the silhouette, and the neck descending to
the lower right. The cyan eye sits in a dark recess at the front of the skull.

Format, unchanged: 49x294 holding six 49x49 tiles = a 147x98 picture sliced in row-major order and
stacked. Top row colour, bottom row greyscale. The original icon is never written.

Usage: python tools/draw_altar_icon_blocky.py [output.png]
"""

from __future__ import annotations

import os
import sys

from PIL import Image, ImageChops, ImageDraw, ImageFilter

FW, FH = 147, 49
TILE = 49

OUTLINE = (0x08, 0x0D, 0x14, 255)

PLATE_TOP = (0xD6, 0xDE, 0xE4)
PLATE = (0xA8, 0xB3, 0xBD)
PLATE_DARK = (0x74, 0x80, 0x8B)
PLATE_DEEP = (0x4E, 0x58, 0x63)

NAVY = (0x28, 0x38, 0x48)
NAVY_DEEP = (0x16, 0x20, 0x2B)
NAVY_BLACK = (0x0B, 0x12, 0x1A)

CYAN = (0x4F, 0xC8, 0xE8)
CYAN_BRIGHT = (0xB2, 0xF3, 0xFF)
CYAN_DEEP = (0x24, 0x6E, 0x96)

BG_TOP = (0x12, 0x25, 0x3A)
BG_BOTTOM = (0x07, 0x0C, 0x14)
SHARD = (0x1B, 0x47, 0x6E)
SHARD_LIGHT = (0x3C, 0x8A, 0xC4)

# ------------------------------------------------------------------ masses (x0, y0, x1, y1)

SKULL = (48, 17, 88, 40)
BROW = (38, 21, 64, 28)
SNOUT = (14, 27, 54, 35)
SNOUT_TIP = (7, 29, 16, 35)
JAW = (18, 36, 78, 43)
NECK_A = (80, 23, 112, 46)
NECK_B = (104, 30, 130, 46)
CREST = [(74, 20), (114, 5), (120, 12), (80, 30)]


def staircase(draw, root, tip, width, colours):
    for step in range(9):
        t0, t1 = step / 9, (step + 1) / 9
        x0 = round(root[0] + (tip[0] - root[0]) * t0)
        y0 = round(root[1] + (tip[1] - root[1]) * t0)
        x1 = round(root[0] + (tip[0] - root[0]) * t1) + 1
        y1 = round(root[1] + (tip[1] - root[1]) * t1) + 1
        draw.rectangle([min(x0, x1), min(y0, y1), max(x0, x1), max(y0, y1) + width // 2],
                       fill=colours[step % 2])


def background(art):
    d = ImageDraw.Draw(art)
    for y in range(FH):
        t = y / (FH - 1)
        d.line([(0, y), (FW, y)],
               fill=tuple(round(BG_TOP[i] + (BG_BOTTOM[i] - BG_TOP[i]) * t) for i in range(3))
               + (255,))

    for (x, base, height, width) in ((6, 46, 24, 7), (20, 47, 32, 8), (36, 46, 18, 6),
                                     (52, 47, 26, 7), (74, 47, 14, 5)):
        d.polygon([(x, base), (x + width // 2, base - height), (x + width, base)], fill=SHARD)
        d.line([(x + width // 2, base - height), (x + width // 2, base - height + 9)],
               fill=SHARD_LIGHT)

    haze = Image.new("RGBA", (FW, FH), (0, 0, 0, 0))
    ImageDraw.Draw(haze).ellipse([50, 8, 120, 44], fill=(0x4F, 0xD8, 0xFF, 80))
    art.alpha_composite(haze.filter(ImageFilter.GaussianBlur(7)))
    return art


def dragon(art):
    # ---- one union for the whole body, so it can be outlined and separated from the background
    body = Image.new("RGBA", (FW, FH), (0, 0, 0, 0))
    bd = ImageDraw.Draw(body)
    bd.rectangle(SNOUT, fill=PLATE)
    bd.rectangle(SNOUT_TIP, fill=PLATE_DARK)
    bd.rectangle(BROW, fill=PLATE_TOP)
    bd.rectangle(SKULL, fill=PLATE)
    bd.rectangle(JAW, fill=NAVY_DEEP)
    bd.rectangle(NECK_A, fill=NAVY)
    bd.rectangle(NECK_B, fill=NAVY_DEEP)
    staircase(bd, CREST[0], CREST[1], 8, (PLATE_TOP, PLATE))
    bd.polygon([(70, 43), (82, 43), (74, 48)], fill=NAVY)

    ring = ImageChops.subtract(body.getchannel("A").filter(ImageFilter.MaxFilter(3)),
                               body.getchannel("A"))
    halo = Image.new("RGBA", (FW, FH), OUTLINE)
    art.alpha_composite(Image.composite(halo, Image.new("RGBA", (FW, FH), (0, 0, 0, 0)), ring))
    art.alpha_composite(body)

    d = ImageDraw.Draw(art)

    # ---- lit top faces
    d.rectangle([SKULL[0] + 2, SKULL[1] + 2, SKULL[2] - 2, SKULL[1] + 6], fill=PLATE_TOP)
    d.rectangle([SNOUT[0] + 2, SNOUT[1] + 1, SNOUT[2] - 2, SNOUT[1] + 4], fill=PLATE_TOP)
    d.rectangle([BROW[0], BROW[1], BROW[2], BROW[1] + 2], fill=(0xE6, 0xEC, 0xF1, 255))
    d.rectangle([NECK_A[0], NECK_A[1], NECK_A[2], NECK_A[1] + 3], fill=PLATE_DARK)
    d.rectangle([NECK_B[0], NECK_B[1], NECK_B[2], NECK_B[1] + 3], fill=PLATE_DEEP)

    # ---- panel seams
    d.rectangle([66, 20, 67, 38], fill=OUTLINE)
    d.rectangle([30, 27, 31, 34], fill=OUTLINE)
    d.rectangle([44, 17, 45, 27], fill=OUTLINE)

    # ---- mouth gap and the energy seam inside it
    d.rectangle([12, 35, 76, 36], fill=NAVY_BLACK)
    d.rectangle([18, 35, 68, 35], fill=CYAN)
    d.rectangle([28, 35, 54, 35], fill=CYAN_BRIGHT)

    # ---- eye: cyan lens sunk into a dark recess
    d.rectangle([52, 22, 72, 33], fill=NAVY_BLACK)
    d.rectangle([55, 24, 69, 31], fill=CYAN_DEEP)
    d.rectangle([56, 25, 68, 30], fill=CYAN)
    d.rectangle([59, 26, 65, 29], fill=CYAN_BRIGHT)
    d.rectangle([52, 22, 72, 23], fill=PLATE_DARK)

    # ---- vents and seams
    d.rectangle([78, 26, 80, 44], fill=CYAN_DEEP)
    for y in (30, 36, 42):
        d.rectangle([84, y, 108, y], fill=CYAN)
    for x in (20, 30, 40, 50):
        d.rectangle([x, 39, x + 6, 41], fill=CYAN_DEEP)
    d.rectangle([84, 19, 92, 21], fill=CYAN_DEEP)
    d.rectangle([85, 19, 90, 20], fill=CYAN)
    return art


def main() -> None:
    output = sys.argv[1] if len(sys.argv) > 1 else os.path.normpath(os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "..", "altar_icon.png"))

    colour = Image.new("RGBA", (FW, FH), (0, 0, 0, 255))
    background(colour)
    dragon(colour)
    grey = colour.convert("L").convert("RGBA")

    picture = Image.new("RGBA", (FW, FH * 2))
    picture.paste(colour, (0, 0))
    picture.paste(grey, (0, FH))

    icon = Image.new("RGBA", (TILE, TILE * 6))
    for index in range(6):
        column, row = index % 3, index // 3
        icon.paste(picture.crop((column * TILE, row * TILE, (column + 1) * TILE,
                                 (row + 1) * TILE)), (0, index * TILE))
    icon.save(output)

    sheet = Image.new("RGBA", (FW * 6, FH * 8 + 20), (0x14, 0x16, 0x1C, 255))
    sheet.alpha_composite(picture.resize((FW * 6, FH * 6), Image.NEAREST), (0, 0))
    sheet.alpha_composite(picture.resize((FW, FH * 2), Image.NEAREST), (0, FH * 6 + 10))
    sheet.alpha_composite(picture.resize((FW * 3, FH * 6), Image.NEAREST), (FW + 14, FH * 6 + 10))
    sheet.save(os.path.join(os.path.dirname(output), "altar_icon_blocky_preview.png"))

    print(f"wrote {output} ({TILE}x{TILE * 6}); {FW}x{FH} colour row plus greyscale row")
    print("sheet altar_icon_blocky_preview.png")


if __name__ == "__main__":
    main()
