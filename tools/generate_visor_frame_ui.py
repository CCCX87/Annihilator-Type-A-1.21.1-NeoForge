"""Generate the visor frame as a transparent-background UI asset.

Line art only: every element is a stroked path (1-3px) on a fully transparent canvas. There are no
filled rectangles, no bezel bands and no background, so the file can be dropped straight onto the
game view.

Output: <workspace>/visor_frame_ui.png

Usage: python tools/generate_visor_frame_ui.py
"""

from __future__ import annotations

import math
import os

from PIL import Image, ImageDraw

WIDTH, HEIGHT = 1920, 1080

# ------------------------------------------------------------------- frame spec

INSET_X = 0.140       # how far the contour sits in from the left/right edges
INSET_Y = 0.120       # and from the top/bottom edges
CHAMFER = 0.26        # corner cut length, as a fraction of the height

CONTOUR_WIDTH = 3
ECHO_WIDTH = 2
ECHO_OFFSET = 24
TICK_WIDTH = 2
TICK_HALF = 9
MARKER_SIZE = 13
MARKER_WIDTH = 2

CYAN = (0x4F, 0xD8, 0xFF)
RED = (0xFF, 0x4A, 0x2B)

CONTOUR_ALPHA = 0x96
ECHO_ALPHA = 0x3C
TICK_ALPHA = 0x78
MARKER_ALPHA = 0xAA


def rgba(rgb, alpha):
    return (rgb[0], rgb[1], rgb[2], alpha)


def contour(inset_x, inset_y, chamfer):
    """Octagon: an inset rectangle with all four corners cut by a diagonal."""
    x0, x1 = int(WIDTH * inset_x), WIDTH - int(WIDTH * inset_x)
    y0, y1 = int(HEIGHT * inset_y), HEIGHT - int(HEIGHT * inset_y)
    c = int(chamfer)
    points = [
        (x0 + c, y0), (x1 - c, y0),
        (x1, y0 + c), (x1, y1 - c),
        (x1 - c, y1), (x0 + c, y1),
        (x0, y1 - c), (x0, y0 + c),
    ]
    return points + [points[0]]


def offset_outward(points, distance):
    """Pushes every point away from the shape's centre, so the echo stays concentric."""
    ring = points[:-1]
    cx = sum(p[0] for p in ring) / len(ring)
    cy = sum(p[1] for p in ring) / len(ring)
    out = []
    for (x, y) in points:
        dx, dy = x - cx, y - cy
        length = math.hypot(dx, dy) or 1.0
        out.append((x + dx / length * distance, y + dy / length * distance))
    return out


def stroke_triangle(draw, cx, cy, size, colour):
    """Outlined, not filled: the marker is part of the line art."""
    draw.line([(cx, cy - size), (cx - size, cy + size), (cx + size, cy + size), (cx, cy - size)],
              fill=colour, width=MARKER_WIDTH, joint="curve")


def main() -> None:
    image = Image.new("RGBA", (WIDTH, HEIGHT), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    points = contour(INSET_X, INSET_Y, HEIGHT * CHAMFER)

    # faint concentric echo just outside, then the contour itself
    draw.line(offset_outward(points, ECHO_OFFSET),
              fill=rgba(CYAN, ECHO_ALPHA), width=ECHO_WIDTH)
    draw.line(points, fill=rgba(CYAN, CONTOUR_ALPHA), width=CONTOUR_WIDTH)

    # graduation ticks perpendicular to each straight edge
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
                      fill=rgba(CYAN, TICK_ALPHA), width=TICK_WIDTH)

    # red telemetry markers pinned to the contour
    for index, t in ((0, 0.42), (4, 0.58), (2, 0.5)):
        a, b = points[index], points[index + 1]
        stroke_triangle(draw, a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t,
                        MARKER_SIZE, rgba(RED, MARKER_ALPHA))

    output = os.path.normpath(os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "..", "visor_frame_ui.png"))
    image.save(output)

    opaque = sum(1 for pixel in image.getdata() if pixel[3] > 0)
    print(f"wrote {output} ({WIDTH}x{HEIGHT}, alpha channel, "
          f"{opaque} of {WIDTH * HEIGHT} pixels drawn = {opaque / (WIDTH * HEIGHT):.2%})")


if __name__ == "__main__":
    main()
