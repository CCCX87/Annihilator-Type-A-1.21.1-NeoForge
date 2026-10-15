"""Draws a Minecraft-style standing illustration of the Annihilator Type A dragon.

Vertical 1:3 composition, facing left: head at the upper left, the blade-wings fanning up and back
from the shoulders, legs below the torso, and the long segmented tail running down the right side.

Everything is axis-aligned or rotated rectangles - the model is a blocky mech dragon, so flat faces
with a lighter top and a darker underside read as voxel shading at this size.

Output is a transparent RGBA PNG.

Usage: python tools/draw_dragon_stand.py [output.png]
"""

from __future__ import annotations

import math
import os
import sys

from PIL import Image, ImageChops, ImageDraw, ImageFilter

W, H = 480, 1440

# ------------------------------------------------------------------------ palette

OUTLINE = (0x0B, 0x11, 0x18, 255)

STEEL_LIGHT = (0xCB, 0xD4, 0xDB)
STEEL = (0x9C, 0xA7, 0xB2)
STEEL_DARK = (0x6B, 0x77, 0x83)
STEEL_DEEP = (0x46, 0x50, 0x5B)

NAVY = (0x26, 0x36, 0x46)
NAVY_DEEP = (0x14, 0x1E, 0x2A)

CYAN = (0x4F, 0xC8, 0xE8)
CYAN_BRIGHT = (0xA6, 0xF0, 0xFF)
CYAN_DEEP = (0x28, 0x74, 0x9C)

GLOW = (0x6F, 0xDC, 0xFF)
PANEL_LIGHT = (0xE0, 0xE8, 0xEE, 255)


def blank():
    return Image.new("RGBA", (W, H), (0, 0, 0, 0))


def block(draw, x0, y0, x1, y1, base=STEEL, light=STEEL_LIGHT, dark=STEEL_DARK,
          top=5, bottom=7, outline=True):
    """A voxel face: flat fill, lit strip along the top, shaded strip along the bottom."""
    draw.rectangle([x0, y0, x1, y1], fill=base)
    draw.rectangle([x0, y0, x1, min(y0 + top, y1)], fill=light)
    draw.rectangle([x0, max(y1 - bottom, y0), x1, y1], fill=dark)
    if outline:
        draw.rectangle([x0, y0, x1, y1], outline=OUTLINE, width=2)


def plate(draw, root, tip, width, base=STEEL, edge=STEEL_DARK):
    """A long thin blade, like the wing plates on the model."""
    rx, ry = root
    tx, ty = tip
    dx, dy = tx - rx, ty - ry
    length = math.hypot(dx, dy) or 1.0
    nx, ny = -dy / length * width / 2, dx / length * width / 2
    quad = [(rx + nx, ry + ny), (tx + nx, ty + ny), (tx - nx, ty - ny), (rx - nx, ry - ny)]
    draw.polygon(quad, fill=base)
    draw.polygon([(rx, ry), (tx, ty), (tx - nx, ty - ny), (rx - nx, ry - ny)], fill=edge)
    draw.line(quad + [quad[0]], fill=OUTLINE, width=2, joint="curve")


def fin(draw, x, y, size, up=True, base=CYAN, edge=CYAN_BRIGHT):
    direction = -1 if up else 1
    draw.polygon([(x, y), (x + size, y + size * direction), (x + size * 2, y)], fill=base)
    draw.line([(x, y), (x + size, y + size * direction)], fill=edge, width=2)
    draw.line([(x + size, y + size * direction), (x + size * 2, y)], fill=edge, width=2)


def head(art):
    draw = ImageDraw.Draw(art)

    # horns sweep back and up from the skull
    plate(draw, (232, 198), (372, 104), 24, STEEL, STEEL_DARK)
    plate(draw, (226, 220), (340, 152), 18, STEEL_DARK, STEEL_DEEP)

    block(draw, 132, 156, 252, 182, STEEL_LIGHT, PANEL_LIGHT, STEEL, top=4, bottom=4)
    block(draw, 146, 178, 254, 268, STEEL, STEEL_LIGHT, STEEL_DARK, top=5, bottom=6)
    block(draw, 186, 212, 258, 254, STEEL_LIGHT, PANEL_LIGHT, STEEL, top=3, bottom=4)

    block(draw, 44, 204, 152, 254, STEEL_LIGHT, PANEL_LIGHT, STEEL, top=4, bottom=5)
    block(draw, 28, 212, 46, 250, STEEL, STEEL_LIGHT, STEEL_DARK, top=3, bottom=4)
    block(draw, 56, 252, 240, 288, STEEL_DARK, STEEL, STEEL_DEEP, top=4, bottom=6)

    # energy seam along the jaw
    draw.rectangle([62, 248, 236, 258], fill=CYAN_DEEP)
    draw.rectangle([70, 249, 228, 256], fill=CYAN)
    draw.rectangle([96, 250, 204, 255], fill=CYAN_BRIGHT)

    # eye
    draw.rectangle([158, 192, 202, 228], fill=NAVY_DEEP)
    draw.rectangle([164, 198, 198, 222], fill=CYAN)
    draw.rectangle([170, 202, 190, 216], fill=CYAN_BRIGHT)
    draw.rectangle([158, 192, 202, 228], outline=OUTLINE, width=2)

    # neck, running back and down from the skull
    segments = [
        (238, 276, 326, 320),
        (250, 312, 342, 358),
        (264, 350, 358, 398),
        (278, 390, 372, 440),
        (290, 432, 384, 484),
    ]
    for (x0, y0, x1, y1) in segments:
        block(draw, x0, y0, x1, y1, STEEL, STEEL_LIGHT, STEEL_DARK, top=4, bottom=5)
        draw.rectangle([x0 + 10, y1 - 14, x1 - 10, y1 - 8], fill=CYAN)
    return art


def wing(art):
    draw = ImageDraw.Draw(art)
    shoulder = (336, 512)

    # translucent membranes behind the blades
    draw.polygon([(300, 168), (392, 132), (470, 250), (476, 402), (438, 486), (300, 486), (250, 380),
                  (246, 250)], fill=GLOW + (58,))
    draw.polygon([(320, 240), (396, 214), (452, 306), (452, 412), (410, 470), (322, 462), (286, 372),
                  (288, 292)], fill=CYAN + (52,))

    blades = [
        ((334, 500), (306, 168), 26),
        ((340, 506), (382, 128), 26),
        ((346, 514), (446, 186), 24),
        ((352, 522), (474, 288), 24),
        ((356, 530), (478, 402), 22),
        ((342, 542), (438, 480), 22),
        ((322, 520), (252, 414), 20),
        ((316, 532), (232, 486), 20),
    ]
    for (root, tip, width) in blades:
        plate(draw, root, tip, width, STEEL_LIGHT, STEEL_DARK)
        mid = ((root[0] + tip[0]) / 2, (root[1] + tip[1]) / 2)
        draw.line([root, mid], fill=CYAN + (225,), width=5)
    return art


def tail(art):
    draw = ImageDraw.Draw(art)

    segments = [
        (404, 706, 452, 796),
        (418, 786, 464, 882),
        (430, 872, 472, 968),
        (438, 958, 476, 1054),
        (442, 1044, 476, 1140),
        (444, 1130, 474, 1216),
    ]
    for index, (x0, y0, x1, y1) in enumerate(segments):
        block(draw, x0, y0, x1, y1, NAVY, NAVY, NAVY_DEEP, top=5, bottom=7)
        block(draw, x0 + 6, y0 + 8, x0 + 24, y1 - 8, STEEL, STEEL_LIGHT, STEEL_DARK,
              top=3, bottom=4)
        draw.rectangle([x0 + 8, y0 + 18, x1 - 8, y0 + 28], fill=CYAN)
        fin(draw, x1 - 2, y0 + 34, 14 + index, up=False)
        fin(draw, x0 + 4, y0 + 40, 12 + index, up=True, base=CYAN_DEEP, edge=CYAN)

    draw.polygon([(444, 1212), (474, 1212), (459, 1300)], fill=STEEL_LIGHT)
    draw.line([(444, 1212), (459, 1300)], fill=OUTLINE, width=2)
    draw.line([(474, 1212), (459, 1300)], fill=OUTLINE, width=2)
    draw.rectangle([452, 1230, 468, 1250], fill=CYAN)

    # spine fins along the back
    for index in range(6):
        fin(draw, 404 + index * 5, 500 + index * 40, 16, up=True)
    return art


def torso(art):
    draw = ImageDraw.Draw(art)

    block(draw, 226, 478, 424, 772, NAVY, NAVY, NAVY_DEEP, top=6, bottom=8)
    block(draw, 232, 476, 376, 574, STEEL, STEEL_LIGHT, STEEL_DARK, top=6, bottom=8)
    block(draw, 302, 560, 428, 668, STEEL_LIGHT, PANEL_LIGHT, STEEL, top=5, bottom=7)
    block(draw, 248, 654, 368, 770, STEEL, STEEL_LIGHT, STEEL_DARK, top=5, bottom=7)

    block(draw, 266, 448, 398, 516, STEEL_LIGHT, PANEL_LIGHT, STEEL, top=5, bottom=7)
    draw.rectangle([300, 464, 364, 498], fill=CYAN_DEEP)
    draw.rectangle([308, 470, 356, 492], fill=CYAN)

    for (x0, y0, x1, y1) in ((238, 586, 298, 598), (238, 612, 282, 622), (390, 584, 422, 648),
                             (354, 684, 422, 696)):
        draw.rectangle([x0, y0, x1, y1], fill=CYAN)

    draw.ellipse([320, 706, 388, 774], fill=STEEL_DARK)
    draw.ellipse([332, 718, 376, 762], fill=STEEL)
    draw.ellipse([346, 732, 362, 748], fill=CYAN_BRIGHT)
    draw.ellipse([320, 706, 388, 774], outline=OUTLINE, width=3)
    return art


def legs(art):
    draw = ImageDraw.Draw(art)

    block(draw, 238, 622, 302, 708, STEEL, STEEL_LIGHT, STEEL_DARK, top=4, bottom=6)
    draw.ellipse([244, 692, 298, 746], fill=STEEL_DARK)
    draw.ellipse([254, 702, 288, 736], fill=CYAN)
    draw.ellipse([244, 692, 298, 746], outline=OUTLINE, width=3)
    block(draw, 232, 748, 298, 898, STEEL_LIGHT, PANEL_LIGHT, STEEL, top=5, bottom=7)
    draw.rectangle([262, 748, 284, 884], fill=CYAN_DEEP)
    draw.rectangle([266, 752, 280, 880], fill=CYAN)
    block(draw, 212, 892, 316, 934, STEEL, STEEL_LIGHT, STEEL_DARK, top=4, bottom=6)
    for claw in range(3):
        x = 218 + claw * 34
        draw.polygon([(x, 934), (x + 24, 934), (x + 9, 962)], fill=CYAN_DEEP)
        draw.line([(x, 934), (x + 9, 962)], fill=OUTLINE, width=2)

    block(draw, 348, 748, 414, 844, STEEL, STEEL_LIGHT, STEEL_DARK, top=4, bottom=6)
    draw.ellipse([354, 828, 410, 884], fill=STEEL_DARK)
    draw.ellipse([364, 838, 400, 874], fill=CYAN)
    draw.ellipse([354, 828, 410, 884], outline=OUTLINE, width=3)
    block(draw, 340, 884, 410, 1010, STEEL_LIGHT, PANEL_LIGHT, STEEL, top=5, bottom=7)
    draw.rectangle([366, 884, 384, 996], fill=CYAN)
    block(draw, 318, 1004, 432, 1046, STEEL, STEEL_LIGHT, STEEL_DARK, top=4, bottom=6)
    for claw in range(3):
        x = 326 + claw * 36
        draw.polygon([(x, 1046), (x + 26, 1046), (x + 10, 1076)], fill=CYAN_DEEP)
        draw.line([(x, 1046), (x + 10, 1076)], fill=OUTLINE, width=2)
    return art


def compose():
    art = blank()
    wing(art)
    tail(art)
    torso(art)
    legs(art)
    head(art)

    # cyan rim light on the outside of the silhouette only: the ring is the dilated alpha minus the
    # alpha itself. Compositing the dilated mask directly would flood the whole shape instead.
    alpha = art.getchannel("A")
    ring = ImageChops.subtract(alpha.filter(ImageFilter.MaxFilter(5)), alpha)
    halo = Image.new("RGBA", (W, H), CYAN_BRIGHT + (150,))
    art.alpha_composite(Image.composite(halo, Image.new("RGBA", (W, H), (0, 0, 0, 0)), ring))
    return art


def main() -> None:
    output = sys.argv[1] if len(sys.argv) > 1 else os.path.normpath(os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "..", "dragon_stand.png"))
    art = compose()
    art.save(output)
    print(f"wrote {output} ({W}x{H}, ratio 1:{H // W})")


if __name__ == "__main__":
    main()
