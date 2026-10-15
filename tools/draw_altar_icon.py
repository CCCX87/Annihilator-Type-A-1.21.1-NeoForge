"""Redraws the Dragon Survival altar icon: same resolution, same tile layout, nicer art.

Format: 49x294 holding six 49x49 tiles, i.e. a 147x98 picture sliced in row-major order and
stacked - tiles 0-2 are the picture's top row, tiles 3-5 its bottom row. Top row colour, bottom row
greyscale.

Proportions matter more than detail at this size. A long tapering muzzle plus one tall triangular
crest reads as a fish, so the head uses a short blunt snout, a pronounced brow, two short swept
horns and a jaw spike, which is what makes it read as a dragon. Only the upper plane is lit, so the
head reads as a volume rather than a flat plate.

Everything is composed at four times the final size and downsampled with LANCZOS, which is what
keeps the diagonals clean at 147x49. The original file is never written.

Usage: python tools/draw_altar_icon.py [output.png]
"""

from __future__ import annotations

import os
import sys

import numpy as np
from PIL import Image, ImageChops, ImageDraw, ImageFilter

FINAL_W, FINAL_H = 147, 49
SCALE = 4
W, H = FINAL_W * SCALE, FINAL_H * SCALE
TILE = 49

# ------------------------------------------------------------------------ palette

NAVY_DEEP = (0x06, 0x0B, 0x14)
NAVY = (0x0D, 0x1C, 0x30)
BLUE = (0x18, 0x46, 0x70)
BLUE_LIGHT = (0x3A, 0x86, 0xC0)
ICE = (0x8C, 0xDE, 0xF8)
PALE = (0xEE, 0xFC, 0xFF)
GLOW = (0x4F, 0xD8, 0xFF)

METAL_DEEP = (0x18, 0x1D, 0x27)
METAL_DARK = (0x33, 0x3C, 0x4A)
METAL = (0x60, 0x6C, 0x7E)
METAL_LIGHT = (0x99, 0xA6, 0xB8)
METAL_TOP = (0xCB, 0xD8, 0xE4)

OUTLINE = (0x05, 0x08, 0x0E)

# ------------------------------------------------------------------------ geometry

# Head and neck, clockwise from the top of the snout. The snout tip is a short vertical face - a
# long taper would make it read as a fish.
SILHOUETTE = [
    (146, 96),    # top of the snout tip
    (216, 82),    # snout top
    (276, 62),    # brow front
    (334, 46),    # brow
    (396, 44),    # skull top
    (452, 60),    # occiput
    (484, 94),    # cheek
    (476, 134),   # jaw hinge
    (502, 196),   # neck leaves the bottom edge
    (366, 196),
    (398, 152),   # under the jaw
    (302, 160),   # jaw
    (212, 152),   # chin
    (152, 122),   # bottom of the snout tip
]

TOP_FACE = [
    (146, 96), (216, 82), (276, 62), (334, 46), (396, 44), (452, 60), (484, 94),
    (424, 84), (360, 74), (292, 92), (222, 104),
]

# two short swept horns rather than one tall crest
HORN_BACK = [(352, 50), (410, 12), (436, 32), (386, 70)]
HORN_FRONT = [(288, 62), (322, 24), (346, 40), (316, 76)]
JAW_SPIKE = [(402, 128), (446, 112), (428, 150)]

CHEEK_PLATE = [(404, 78), (462, 92), (470, 126), (414, 132), (386, 104)]
JAW_UNDER = [(212, 152), (302, 160), (398, 152), (430, 196), (252, 196)]

MOUTH = [(150, 112), (392, 148)]
EYE_SOCKET = [(236, 104), (296, 84), (310, 112), (246, 124)]
EYE_LENS = [(246, 104), (290, 90), (302, 108), (252, 116)]

SEAMS = [
    ((276, 62), (302, 160), 3),
    ((334, 46), (398, 152), 3),
    ((396, 44), (430, 196), 4),
    ((452, 60), (446, 196), 3),
    ((302, 160), (330, 196), 3),
]

GREEBLES = [(430, 100, 26, 13), (336, 166, 30, 15), (378, 170, 22, 12)]

BACKGROUND_SHARDS = [
    ((22, 150), (36, 46), (58, 152)),
    ((72, 152), (86, 22), (110, 152)),
    ((124, 150), (138, 58), (160, 152)),
    ((0, 154), (8, 90), (26, 154)),
]


def vertical_gradient(size, top, bottom):
    width, height = size
    ramp = np.linspace(0.0, 1.0, height)[:, None]
    channels = [top[i] + (bottom[i] - top[i]) * ramp for i in range(3)]
    array = np.stack([np.repeat(c, width, axis=1) for c in channels], axis=-1)
    return Image.fromarray(array.astype(np.uint8), "RGB").convert("RGBA")


def vertical_fade_mask(size, solid_until, clear_at):
    """L mask: 255 down to `solid_until`, fading to 0 at `clear_at`."""
    _, height = size
    span = max(clear_at - solid_until, 1)
    rows = np.arange(height)[:, None]
    ramp = np.clip(1.0 - (rows - solid_until) / span, 0.0, 1.0)
    return Image.fromarray(np.repeat(ramp * 255.0, size[0], axis=1).astype(np.uint8), "L")


def radial_glow(size, centre, radius, colour, strength):
    layer = Image.new("RGBA", size, (0, 0, 0, 0))
    cx, cy = centre
    ImageDraw.Draw(layer).ellipse([cx - radius, cy - radius, cx + radius, cy + radius],
                                  fill=colour + (strength,))
    return layer.filter(ImageFilter.GaussianBlur(radius * 0.4))


def polygon_mask(size, points):
    mask = Image.new("L", size, 0)
    ImageDraw.Draw(mask).polygon(points, fill=255)
    return mask


def clip_to(layer, mask):
    out = layer.copy()
    out.putalpha(ImageChops.multiply(out.getchannel("A"), mask))
    return out


def blank():
    return Image.new("RGBA", (W, H), (0, 0, 0, 0))


def draw_background():
    background = vertical_gradient((W, H), NAVY, NAVY_DEEP)
    background.alpha_composite(radial_glow((W, H), (300, 60), 260, GLOW, 60))
    background.alpha_composite(radial_glow((W, H), (120, 20), 190, BLUE_LIGHT, 70))

    shards = blank()
    shard_draw = ImageDraw.Draw(shards)
    for (a, b, c) in BACKGROUND_SHARDS:
        shard_draw.polygon([a, b, c], fill=BLUE + (255,))
        shard_draw.line([b, (a[0] * 0.35 + b[0] * 0.65, a[1] * 0.35 + b[1] * 0.65)],
                        fill=BLUE_LIGHT + (255,), width=5)
        shard_draw.line([b, (c[0] * 0.35 + b[0] * 0.65, c[1] * 0.35 + b[1] * 0.65)],
                        fill=ICE + (170,), width=3)
    background.alpha_composite(shards)

    sparks = blank()
    spark_draw = ImageDraw.Draw(sparks)
    for (x, y) in ((186, 30), (228, 46), (152, 66), (420, 24), (470, 44), (52, 72)):
        spark_draw.point((x, y), fill=PALE + (255,))
        spark_draw.point((x + 1, y), fill=PALE + (140,))
        spark_draw.point((x, y + 1), fill=PALE + (140,))
    background.alpha_composite(sparks)
    return background


def draw_head():
    body_mask = polygon_mask((W, H), SILHOUETTE)
    metal = vertical_gradient((W, H), METAL, METAL_DEEP)

    head = blank()
    head.alpha_composite(clip_to(metal, body_mask))
    head.alpha_composite(clip_to(radial_glow((W, H), (330, 116), 150, METAL_LIGHT, 110), body_mask))

    top_face = blank()
    ImageDraw.Draw(top_face).polygon(TOP_FACE, fill=METAL_TOP + (230,))
    head.alpha_composite(clip_to(top_face, body_mask))
    head.alpha_composite(clip_to(radial_glow((W, H), (300, 64), 120, PALE, 70), body_mask))

    details = blank()
    detail_draw = ImageDraw.Draw(details)
    detail_draw.polygon(CHEEK_PLATE, fill=METAL_LIGHT + (110,))
    for (a, b, width) in SEAMS:
        detail_draw.line([a, b], fill=METAL_DEEP + (200,), width=width)
    for (x, y, w, h) in GREEBLES:
        detail_draw.rectangle([x, y, x + w, y + h], fill=METAL_DEEP + (150,))
        detail_draw.rectangle([x, y, x + w, y + 2], fill=METAL_LIGHT + (110,))
    head.alpha_composite(clip_to(details, body_mask))

    under = blank()
    ImageDraw.Draw(under).polygon(JAW_UNDER, fill=METAL_DEEP + (190,))
    head.alpha_composite(clip_to(under, body_mask))

    lit = blank()
    lit_draw = ImageDraw.Draw(lit)
    lit_draw.polygon([(222, 150), (330, 156), (396, 148), (390, 158), (300, 164), (224, 158)],
                     fill=GLOW + (150,))
    lit_draw.polygon([(404, 106), (466, 116), (456, 128), (400, 118)], fill=GLOW + (120,))
    lit_draw.line([(334, 46), (398, 152)], fill=GLOW + (110,), width=3)
    head.alpha_composite(clip_to(lit, body_mask))

    mouth = blank()
    mouth_draw = ImageDraw.Draw(mouth)
    mouth_draw.line(MOUTH, fill=(0x0C, 0x10, 0x18, 255), width=6)
    steps = 6
    for step in range(steps):
        t = (step + 0.5) / steps
        x = MOUTH[0][0] + (MOUTH[1][0] - MOUTH[0][0]) * t
        y = MOUTH[0][1] + (MOUTH[1][1] - MOUTH[0][1]) * t
        mouth_draw.polygon([(x, y - 3), (x + 12, y - 6), (x + 6, y + 9)], fill=METAL_TOP + (255,))
    head.alpha_composite(clip_to(mouth, body_mask))

    nostril = blank()
    ImageDraw.Draw(nostril).polygon([(162, 100), (180, 96), (182, 106), (164, 110)],
                                    fill=METAL_DEEP + (220,))
    head.alpha_composite(clip_to(nostril, body_mask))

    # Horns, jaw spike and border all sit outside the head outline, so nothing may be clipped to the
    # silhouette after this point - clipping the finished head is what erased the horn earlier.
    spikes = blank()
    spike_draw = ImageDraw.Draw(spikes)
    spike_draw.polygon(HORN_BACK, fill=METAL_DARK + (255,))
    spike_draw.polygon(HORN_FRONT, fill=METAL_DARK + (255,))
    spike_draw.polygon(JAW_SPIKE, fill=METAL_DARK + (255,))
    spike_draw.line([HORN_BACK[0], HORN_BACK[1]], fill=ICE + (240,), width=5, joint="curve")
    spike_draw.line([HORN_FRONT[0], HORN_FRONT[1]], fill=ICE + (230,), width=5, joint="curve")
    spike_draw.line([JAW_SPIKE[0], JAW_SPIKE[1]], fill=ICE + (180,), width=4, joint="curve")

    border = blank()
    border_draw = ImageDraw.Draw(border)
    for shape in (SILHOUETTE, HORN_BACK, HORN_FRONT, JAW_SPIKE):
        border_draw.line(list(shape) + [shape[0]], fill=OUTLINE + (255,), width=10, joint="curve")

    rim = blank()
    ImageDraw.Draw(rim).line(SILHOUETTE + [SILHOUETTE[0]], fill=ICE + (255,), width=6,
                             joint="curve")
    redraw = blank()
    redraw.paste(metal, (0, 0), polygon_mask((W, H), [(x, y + 6) for (x, y) in SILHOUETTE]))
    rim.alpha_composite(redraw)
    rim = clip_to(rim, vertical_fade_mask((W, H), int(H * 0.25), int(H * 0.72)))

    eye = blank()
    eye.alpha_composite(radial_glow((W, H), (272, 100), 54, GLOW, 150))
    eye_draw = ImageDraw.Draw(eye)
    eye_draw.polygon(EYE_SOCKET, fill=(0x08, 0x1C, 0x28, 255))
    eye_draw.polygon(EYE_LENS, fill=GLOW + (255,))
    eye_draw.ellipse([258, 92, 280, 110], fill=PALE + (255,))
    eye.alpha_composite(clip_to(radial_glow((W, H), (268, 100), 28, PALE, 130),
                                polygon_mask((W, H), EYE_SOCKET)))

    result = Image.alpha_composite(border, head)
    result.alpha_composite(rim)
    result.alpha_composite(spikes)
    result.alpha_composite(eye)
    return result


def compose() -> Image.Image:
    picture = draw_background()
    picture.alpha_composite(draw_head())

    vignette = Image.new("L", (W, H), 0)
    ImageDraw.Draw(vignette).ellipse([-W * 0.3, -H * 0.8, W * 1.3, H * 1.8], fill=255)
    vignette = vignette.filter(ImageFilter.GaussianBlur(H * 0.2))
    return Image.composite(picture, Image.new("RGBA", (W, H), NAVY_DEEP + (255,)), vignette)


def slice_icon(picture: Image.Image) -> Image.Image:
    """147x98 picture -> the 49x294 tile layout the game expects."""
    icon = Image.new("RGBA", (TILE, TILE * 6), (0, 0, 0, 0))
    for index in range(6):
        column, row = index % 3, index // 3
        icon.paste(picture.crop((column * TILE, row * TILE, (column + 1) * TILE,
                                 (row + 1) * TILE)), (0, index * TILE))
    return icon


def main() -> None:
    output = sys.argv[1] if len(sys.argv) > 1 else os.path.normpath(os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "..", "altar_icon.png"))

    art = compose()
    colour_row = art.resize((FINAL_W, FINAL_H), Image.LANCZOS)
    grey_row = colour_row.convert("L").convert("RGBA")

    picture = Image.new("RGBA", (FINAL_W, FINAL_H * 2))
    picture.paste(colour_row, (0, 0))
    picture.paste(grey_row, (0, FINAL_H))
    slice_icon(picture).save(output)

    sheet = Image.new("RGBA", (FINAL_W * 6, FINAL_H * 8 + 24), (0x14, 0x16, 0x1C, 255))
    sheet.alpha_composite(picture.resize((FINAL_W * 6, FINAL_H * 6), Image.NEAREST), (0, 0))
    sheet.alpha_composite(picture.resize((FINAL_W, FINAL_H * 2), Image.NEAREST),
                          (0, FINAL_H * 6 + 12))
    sheet.alpha_composite(picture.resize((FINAL_W * 3, FINAL_H * 6), Image.NEAREST),
                          (FINAL_W + 16, FINAL_H * 6 + 12))
    sheet_path = os.path.join(os.path.dirname(output), "altar_icon_redraw_preview.png")
    sheet.save(sheet_path)

    print(f"wrote {output} ({TILE}x{TILE * 6}, {FINAL_W}x{FINAL_H} picture sliced row-major)")
    print(f"sheet {sheet_path}")


if __name__ == "__main__":
    main()
