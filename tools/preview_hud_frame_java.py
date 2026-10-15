"""Simulates HudFrame.render stroke for stroke, at a real GUI-space size.

This is not a mock-up: it walks the same geometry, the same config defaults, the same run-based
fill logic and the same half-thickness rounding as the Java class, at 960x540 - the GUI space of a
1080p window at GUI scale 2 - so what it shows is what the layer will draw.

Blending note: overlapping runs inside the overlay replace rather than accumulate, so a few corner
pixels are marginally less opaque than the game's blended output. Geometry, thickness and colour are
exact.

Usage: python tools/preview_hud_frame_java.py
"""

from __future__ import annotations

import math
import os

from PIL import Image, ImageDraw

WIDTH, HEIGHT = 960, 540  # GUI space of a 1080p window at GUI scale 2

# ------------------------------------------------------- mirrors ClientConfig defaults

INSET_X = 0.14
INSET_Y = 0.12
CHAMFER_FRACTION = 0.26
ALPHA = 0.59
ACCENT = (0x4F, 0xD8, 0xFF)

# ------------------------------------------------------- mirrors HudFrame constants

CONTOUR_THICKNESS = 2
ECHO_THICKNESS = 1
TICK_THICKNESS = 1
MARKER_THICKNESS = 1
ECHO_OFFSET = 10
TICK_HALF = 5
MARKER_SIZE = 6
TICK_POSITIONS = (0.34, 0.66)
MARKER_RGB = (0xFF, 0x4A, 0x2B)
ECHO_ALPHA_FACTOR = 0.40
TICK_ALPHA_FACTOR = 0.80
MARKER_ALPHA_FACTOR = 1.15


def build_world(width, height):
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
    for (x, y, w, h, colour) in (
        (120, horizon - 70, 130, 70, (0x4A, 0x3F, 0x33)),
        (120, horizon - 70, 130, 10, (0x5C, 0x4E, 0x3E)),
        (640, horizon - 46, 120, 46, (0x3C, 0x45, 0x3A)),
        (380, horizon + 40, 90, 56, (0x2E, 0x36, 0x2C)),
    ):
        draw.rectangle([x, y, x + w, y + h], fill=colour + (255,))
    return world


class Layer:
    """Replicates GuiGraphics.fill: the end coordinates are exclusive."""

    def __init__(self, width, height):
        self.image = Image.new("RGBA", (width, height), (0, 0, 0, 0))
        self.draw = ImageDraw.Draw(self.image)

    def fill(self, x0, y0, x1, y1, colour):
        if x0 == x1 or y0 == y1:
            return
        self.draw.rectangle([x0, y0, x1 - 1, y1 - 1], fill=colour)


def argb(alpha, rgb):
    return (rgb[0], rgb[1], rgb[2], max(0, min(255, alpha)))


def draw_line(layer, x0, y0, x1, y1, thickness, colour):
    half = thickness // 2
    if y0 == y1:
        layer.fill(min(x0, x1), y0 - half, max(x0, x1) + 1, y0 - half + thickness, colour)
        return
    if x0 == x1:
        layer.fill(x0 - half, min(y0, y1), x0 - half + thickness, max(y0, y1) + 1, colour)
        return
    top, bottom = min(y0, y1), max(y0, y1)
    for y in range(top, bottom + 1):
        t = (y - y0) / (y1 - y0)
        x = round(x0 + (x1 - x0) * t)
        layer.fill(x - half, y, x - half + thickness, y + 1, colour)


def pushed_outward(point, centre_x, centre_y, distance):
    dx, dy = point[0] - centre_x, point[1] - centre_y
    length = math.hypot(dx, dy)
    if length < 1.0e-4:
        return point
    return (round(point[0] + dx / length * distance), round(point[1] + dy / length * distance))


def render(layer):
    inset_x = int(WIDTH * INSET_X)
    inset_y = int(HEIGHT * INSET_Y)
    x0, x1 = inset_x, WIDTH - inset_x - 1
    y0, y1 = inset_y, HEIGHT - inset_y - 1
    chamfer = int(HEIGHT * CHAMFER_FRACTION)
    chamfer = min(chamfer, min((x1 - x0) // 2 - 1, (y1 - y0) // 2 - 1))

    ring = [
        (x0 + chamfer, y0), (x1 - chamfer, y0),
        (x1, y0 + chamfer), (x1, y1 - chamfer),
        (x1 - chamfer, y1), (x0 + chamfer, y1),
        (x0, y1 - chamfer), (x0, y0 + chamfer),
    ]

    alpha = int(ALPHA * 255)
    contour = argb(alpha, ACCENT)
    echo_colour = argb(int(alpha * ECHO_ALPHA_FACTOR), ACCENT)
    tick_colour = argb(int(alpha * TICK_ALPHA_FACTOR), ACCENT)
    marker_colour = argb(int(min(255.0, alpha * MARKER_ALPHA_FACTOR)), MARKER_RGB)

    centre_x, centre_y = (x0 + x1) // 2, (y0 + y1) // 2
    echo = [pushed_outward(point, centre_x, centre_y, ECHO_OFFSET) for point in ring]

    def stroke(points, thickness, colour):
        for index in range(len(points)):
            a, b = points[index], points[(index + 1) % len(points)]
            draw_line(layer, a[0], a[1], b[0], b[1], thickness, colour)

    stroke(echo, ECHO_THICKNESS, echo_colour)
    stroke(ring, CONTOUR_THICKNESS, contour)

    for index in range(len(ring)):
        a, b = ring[index], ring[(index + 1) % len(ring)]
        if a[0] != b[0] and a[1] != b[1]:
            continue  # graduations belong on the straight edges only
        nx, ny = -(b[1] - a[1]), b[0] - a[0]
        length = math.hypot(nx, ny)
        if length < 1.0e-4:
            continue
        nx, ny = nx / length, ny / length
        for position in TICK_POSITIONS:
            px = round(a[0] + (b[0] - a[0]) * position)
            py = round(a[1] + (b[1] - a[1]) * position)
            draw_line(layer, px - round(nx * TICK_HALF), py - round(ny * TICK_HALF),
                      px + round(nx * TICK_HALF), py + round(ny * TICK_HALF),
                      TICK_THICKNESS, tick_colour)

    for (edge, position) in ((0, 0.42), (4, 0.58), (2, 0.50)):
        a, b = ring[edge], ring[(edge + 1) % len(ring)]
        cx = round(a[0] + (b[0] - a[0]) * position)
        cy = round(a[1] + (b[1] - a[1]) * position)
        draw_line(layer, cx, cy - MARKER_SIZE, cx - MARKER_SIZE, cy + MARKER_SIZE,
                  MARKER_THICKNESS, marker_colour)
        draw_line(layer, cx - MARKER_SIZE, cy + MARKER_SIZE, cx + MARKER_SIZE, cy + MARKER_SIZE,
                  MARKER_THICKNESS, marker_colour)
        draw_line(layer, cx + MARKER_SIZE, cy + MARKER_SIZE, cx, cy - MARKER_SIZE,
                  MARKER_THICKNESS, marker_colour)


def main() -> None:
    world = build_world(WIDTH, HEIGHT)
    layer = Layer(WIDTH, HEIGHT)
    render(layer)
    world.alpha_composite(layer.image)

    output = os.path.normpath(os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "..",
        "hud_frame_java_preview.png"))
    world.convert("RGB").save(output)
    print(f"wrote {output} ({WIDTH}x{HEIGHT})")


if __name__ == "__main__":
    main()
