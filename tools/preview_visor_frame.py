"""Preview of the annihilation-moment visor frame, matching the LoL PROJECT Renekton reference.

Reading the reference through the boxed regions: the frame is a closed contour inset from the
viewport with the four corners cut away by long diagonals - an octagon, not a solid bezel and not
lines running off the screen edge. It is drawn very faintly, in thin lines, with small red telemetry
markers pinned to it, and the middle of the screen stays untouched.

Note the reference screenshot includes window chrome (title bar and browser toolbar), so the game
viewport is smaller than the image; the inset fractions here are measured against the viewport.

Two insets are rendered over the same stand-in world so the depth of the frame can be picked.

Usage: python tools/preview_visor_frame.py
"""

from __future__ import annotations

import math
import os

from PIL import Image, ImageDraw

PANEL_W, PANEL_H = 880, 495  # 16:9, roughly a 1080p window at GUI scale 2
GAP = 20

# ------------------------------------------------------------------- frame spec

# (inset across, inset down) as fractions of the panel, per variant
INSETS = ((0.060, 0.100), (0.180, 0.140))
CHAMFER = 0.26        # corner cut length as a fraction of the panel height
LINE_WIDTH = 1
RIM_ALPHA = 0x66
ECHO_OFFSET = 16      # faint second contour just outside, for the reflected-HUD read
ECHO_ALPHA = 0x1E
TICK_HALF = 5
TICK_ALPHA = 0x74
MARKER_HALF = 4
MARKER_ALPHA = 0xA8

CYAN = (0x4F, 0xD8, 0xFF)
MARKER = (0xFF, 0x4A, 0x2B)


def rgba(rgb, alpha):
    return (rgb[0], rgb[1], rgb[2], alpha)


def build_world(width, height):
    """A rough stand-in for the game view, so the frame can be judged against something."""
    world = Image.new("RGBA", (width, height), (0, 0, 0, 255))
    draw = ImageDraw.Draw(world)
    horizon = int(height * 0.55)
    for row in range(horizon):
        t = row / max(horizon - 1, 1)
        top, bottom = (0x3E, 0x62, 0x96), (0x9C, 0xBE, 0xE0)
        draw.line([(0, row), (width, row)],
                  fill=tuple(round(top[c] + (bottom[c] - top[c]) * t) for c in range(3)) + (255,))
    for row in range(horizon, height):
        t = (row - horizon) / max(height - horizon - 1, 1)
        top, bottom = (0x3D, 0x5C, 0x33), (0x22, 0x2A, 0x22)
        draw.line([(0, row), (width, row)],
                  fill=tuple(round(top[c] + (bottom[c] - top[c]) * t) for c in range(3)) + (255,))
    draw.rectangle([110, horizon - 64, 238, horizon], fill=(0x4A, 0x3F, 0x33, 255))
    draw.rectangle([110, horizon - 64, 238, horizon - 55], fill=(0x5C, 0x4E, 0x3E, 255))
    draw.rectangle([586, horizon - 42, 696, horizon], fill=(0x3C, 0x45, 0x3A, 255))
    draw.rectangle([348, horizon + 36, 430, horizon + 88], fill=(0x2E, 0x36, 0x2C, 255))
    return world


def contour(width, height, inset_x, inset_y, chamfer):
    """Octagon: an inset rectangle with all four corners cut by a diagonal of the given length."""
    x0, x1 = int(width * inset_x), width - int(width * inset_x)
    y0, y1 = int(height * inset_y), height - int(height * inset_y)
    c = int(chamfer)
    points = [
        (x0 + c, y0), (x1 - c, y0),
        (x1, y0 + c), (x1, y1 - c),
        (x1 - c, y1), (x0 + c, y1),
        (x0, y1 - c), (x0, y0 + c),
    ]
    return points + [points[0]]


def draw_frame(width, height, accent, inset_x, inset_y, chamfer):
    layer = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    draw = ImageDraw.Draw(layer)
    points = contour(width, height, inset_x, inset_y, chamfer)

    draw.line([(x - ECHO_OFFSET, y - ECHO_OFFSET) for (x, y) in points],
              fill=rgba(accent, ECHO_ALPHA), width=1)
    draw.line(points, fill=rgba(accent, RIM_ALPHA), width=LINE_WIDTH)

    # graduation ticks, perpendicular to the contour, at a few points along each straight edge
    for index in range(len(points) - 1):
        a, b = points[index], points[index + 1]
        dx, dy = b[0] - a[0], b[1] - a[1]
        length = math.hypot(dx, dy) or 1.0
        nx, ny = -dy / length, dx / length
        for t in (0.34, 0.66):
            px = a[0] + dx * t
            py = a[1] + dy * t
            draw.line([(px - nx * TICK_HALF, py - ny * TICK_HALF),
                       (px + nx * TICK_HALF, py + ny * TICK_HALF)],
                      fill=rgba(accent, TICK_ALPHA), width=1)

    # red telemetry markers, pinned to the contour like the glyphs in the reference
    for index, t in ((0, 0.42), (4, 0.58), (2, 0.5)):
        a, b = points[index], points[index + 1]
        px = a[0] + (b[0] - a[0]) * t
        py = a[1] + (b[1] - a[1]) * t
        draw.polygon([(px, py - MARKER_HALF), (px - MARKER_HALF, py + MARKER_HALF - 1),
                      (px + MARKER_HALF, py + MARKER_HALF - 1)], fill=rgba(MARKER, MARKER_ALPHA))

    return layer


def main() -> None:
    canvas = Image.new("RGBA", (PANEL_W * 2 + GAP, PANEL_H), (0x10, 0x12, 0x18, 255))
    world = build_world(PANEL_W, PANEL_H)

    for index, (inset_x, inset_y) in enumerate(INSETS):
        panel = world.copy()
        panel.alpha_composite(
            draw_frame(PANEL_W, PANEL_H, CYAN, inset_x, inset_y, PANEL_H * CHAMFER))
        canvas.alpha_composite(panel, (index * (PANEL_W + GAP), 0))

    output = os.path.normpath(os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "..", "visor_frame_preview.png"))
    canvas.convert("RGB").save(output)
    print(f"wrote {output} ({canvas.width}x{canvas.height})  "
          f"left=inset {INSETS[0][0]:.0%} right=inset {INSETS[1][0]:.0%}")


if __name__ == "__main__":
    main()
