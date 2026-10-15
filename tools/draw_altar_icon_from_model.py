"""Builds the altar icon from the actual model render instead of hand-drawn shapes.

The reference is the left-facing side view of the Annihilator Type A render. A wide slice across the
head, neck and shoulder is keyed off its flat background, scaled into the icon's 147x49 banner and
laid over a dark gradient with a cyan haze and rim light, so the icon shows the real model rather
than an approximation of it.

Format, unchanged: 49x294 holding six 49x49 tiles = a 147x98 picture sliced in row-major order and
stacked. Top row colour, bottom row greyscale. The original icon is never written.

Usage: python tools/draw_altar_icon_from_model.py [output.png]
"""

from __future__ import annotations

import os
import sys
from collections import deque

import numpy as np
from PIL import Image, ImageChops, ImageDraw, ImageFilter

FW, FH = 147, 49
TILE = 49

REFERENCE = (r"C:\Users\Windows 10\.dsh\attachments\v1\objects\01"
             r"\015c16b4b63414a9cdc1c89a9703a22e0374962146ffe6a3b30ad6d4eb52d2af")
CROP = (15, 175, 430, 325)      # head, neck and shoulder, left of the side view
RENDER_BG = (16, 19, 22)        # the flat background colour of the render
KEY_THRESHOLD = 8       # how close to the background a pixel must be to count as background

BG_TOP = (0x11, 0x24, 0x39)
BG_BOTTOM = (0x06, 0x0B, 0x13)
SHARD = (0x1B, 0x47, 0x6E)
SHARD_LIGHT = (0x3C, 0x8A, 0xC4)
CYAN_BRIGHT = (0xB2, 0xF3, 0xFF)


def background_mask(array: np.ndarray, tolerance: int) -> np.ndarray:
    """Flood fills the render's background inwards from the crop border.

    A plain colour threshold is wrong here: the neck is dark navy (#16202B) and sits within a few
    levels of the render's background (#101316), so keying by colour deletes it and the head ends up
    floating free of the body. Only pixels actually connected to the border are background.
    """
    height, width = array.shape[:2]
    reference = np.array(RENDER_BG, dtype=np.int16)
    close = np.abs(array - reference).max(axis=2) <= tolerance

    reached = np.zeros((height, width), dtype=bool)
    queue: deque[tuple[int, int]] = deque()

    def push(x: int, y: int) -> None:
        if close[y, x] and not reached[y, x]:
            reached[y, x] = True
            queue.append((x, y))

    for x in range(width):
        push(x, 0)
        push(x, height - 1)
    for y in range(height):
        push(0, y)
        push(width - 1, y)

    while queue:
        x, y = queue.popleft()
        if x > 0:
            push(x - 1, y)
        if x < width - 1:
            push(x + 1, y)
        if y > 0:
            push(x, y - 1)
        if y < height - 1:
            push(x, y + 1)

    return ~reached


def keyed_slice():
    """Crops the reference and makes only its connected background transparent."""
    source = Image.open(REFERENCE).convert("RGB").crop(CROP)
    array = np.asarray(source).astype(np.int16)

    corner = np.asarray(source)[:8, :8].reshape(-1, 3)
    print(f"background sample: mean {corner.mean(axis=0).round(1)} "
          f"spread {corner.max(axis=0) - corner.min(axis=0)}")

    mask = Image.fromarray((background_mask(array, KEY_THRESHOLD) * 255).astype(np.uint8), "L")
    mask = mask.filter(ImageFilter.MedianFilter(3))
    mask = mask.filter(ImageFilter.MinFilter(3))
    print(f"kept {np.count_nonzero(np.asarray(mask)) / mask.size:.1%} of the crop")

    keyed = source.convert("RGBA")
    keyed.putalpha(mask)

    scale = min(FW / keyed.width, FH / keyed.height)
    size = (max(int(keyed.width * scale), 1), max(int(keyed.height * scale), 1))
    return keyed.resize(size, Image.LANCZOS)


def background():
    art = Image.new("RGBA", (FW, FH), (0, 0, 0, 255))
    draw = ImageDraw.Draw(art)
    for y in range(FH):
        t = y / (FH - 1)
        draw.line([(0, y), (FW, y)],
                  fill=tuple(round(BG_TOP[i] + (BG_BOTTOM[i] - BG_TOP[i]) * t) for i in range(3))
                  + (255,))

    for (x, base, height, width) in ((6, 47, 22, 6), (18, 48, 30, 7), (32, 47, 16, 5),
                                     (46, 48, 24, 6), (60, 48, 12, 4)):
        draw.polygon([(x, base), (x + width // 2, base - height), (x + width, base)], fill=SHARD)
        draw.line([(x + width // 2, base - height), (x + width // 2, base - height + 8)],
                  fill=SHARD_LIGHT)

    haze = Image.new("RGBA", (FW, FH), (0, 0, 0, 0))
    ImageDraw.Draw(haze).ellipse([40, 6, 130, 44], fill=(0x4F, 0xD8, 0xFF, 85))
    art.alpha_composite(haze.filter(ImageFilter.GaussianBlur(8)))
    return art


def main() -> None:
    output = sys.argv[1] if len(sys.argv) > 1 else os.path.normpath(os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "..", "altar_icon.png"))

    art = background()
    model = keyed_slice()
    art.alpha_composite(model, ((FW - model.width) // 2, (FH - model.height) // 2))

    # cyan rim light on the outside of the model only
    ring = ImageChops.subtract(model.getchannel("A").filter(ImageFilter.MaxFilter(3)),
                               model.getchannel("A"))
    halo = Image.new("RGBA", model.size, CYAN_BRIGHT + (120,))
    art.alpha_composite(Image.composite(halo, Image.new("RGBA", model.size, (0, 0, 0, 0)), ring),
                        ((FW - model.width) // 2, (FH - model.height) // 2))

    colour = art
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
    sheet.save(os.path.join(os.path.dirname(output), "altar_icon_model_preview.png"))

    print(f"model slice {model.size} -> {output}")
    print(f"wrote {output} ({TILE}x{TILE * 6}); sheet altar_icon_model_preview.png")


if __name__ == "__main__":
    main()
