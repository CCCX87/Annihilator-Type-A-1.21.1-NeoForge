"""Compose a preview of the in-world ring the same way AnnihilationRingRenderer draws it.

Each layer is an additive (blendFunc ONE, ONE) textured quad, so the preview simply adds
`texture.rgb * vertexColor` per layer, honouring the shader's alpha < 0.1 discard. The
particle ring is scattered on top for realism.

This is an off-line mock-up, not a game screenshot - it cannot show fog, depth sorting or
the surrounding terrain.

Usage: python tools/preview_annihilation_ring.py
"""

from __future__ import annotations

import os
import random

import numpy as np
from PIL import Image

PROJECT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
EFFECTS = os.path.join(PROJECT, "src", "main", "resources", "assets",
                       "annihilator_type_a", "textures", "effect")
PARTICLES = os.path.join(PROJECT, "src", "main", "resources", "assets",
                         "annihilator_type_a", "textures", "particle")
DIAL = os.path.join(EFFECTS, "annihilation_ring.png")
SCANNER = os.path.join(EFFECTS, "annihilation_scanner.png")
FIRE = os.path.join(PARTICLES, "a_small_fire_0.png")
OUTPUT = os.path.join(os.path.dirname(PROJECT), "ring_mechanical_preview.png")

CANVAS = 720
BLOCKS_ACROSS = 9.0
PX_PER_BLOCK = CANVAS / BLOCKS_ACROSS
TAU = 6.283185307179586

# texture, radius in blocks, angle, brightness, (r, g, b), layer opacity
# Must mirror the drawLayer calls in AnnihilationRingRenderer (baseline doubled).
LAYERS = [
    (DIAL, 3.90, 46.0, 1.00, (0.55, 0.88, 1.00), 0.85),
    (DIAL, 3.00, -78.0, 0.50, (0.62, 0.90, 1.00), 0.85),
    (DIAL, 2.00, 132.0, 0.32, (0.45, 0.72, 1.00), 0.85),
    (SCANNER, 3.70, -128.0, 0.85, (0.40, 0.86, 1.00), 0.85),
]


def layer_pixels(path: str, diameter: int, angle: float, tint, gain: float) -> np.ndarray:
    image = Image.open(path).convert("RGBA")
    image = image.rotate(angle, resample=Image.BILINEAR)
    image = image.resize((diameter, diameter), Image.BILINEAR)
    array = np.asarray(image, dtype=np.float64) / 255.0
    visible = (array[..., 3:4] >= 0.1).astype(np.float64)   # shader discards below 0.1
    return array[..., :3] * visible * np.asarray(tint) * gain


def add_centered(accumulator: np.ndarray, patch: np.ndarray, cx: float, cy: float) -> None:
    top = int(round(cy - patch.shape[0] / 2.0))
    left = int(round(cx - patch.shape[1] / 2.0))
    height, width = patch.shape[0], patch.shape[1]
    target = accumulator[top:top + height, left:left + width]
    if target.shape[:2] == (height, width):
        target += patch


def main() -> None:
    centre = CANVAS / 2.0
    accumulator = np.zeros((CANVAS, CANVAS, 3), dtype=np.float64)

    for path, radius, angle, brightness, tint, opacity in LAYERS:
        diameter = int(round(radius * PX_PER_BLOCK * 2.0))
        patch = layer_pixels(path, diameter, angle, tint, brightness * opacity)
        add_centered(accumulator, patch, centre, centre)

    # Particle ring, placed on the same reference circle the emitter uses.
    fire = Image.open(FIRE).convert("RGBA")
    rng = random.Random(20240607)
    for index in range(48):
        angle = index / 48.0 * TAU + rng.uniform(-0.05, 0.05)
        radius = 3.10 + rng.uniform(-0.18, 0.18)
        size = rng.randint(26, 44)
        sprite = fire.rotate(rng.uniform(0.0, 360.0), resample=Image.BILINEAR)
        sprite = sprite.resize((size, size), Image.BILINEAR)
        array = np.asarray(sprite, dtype=np.float64) / 255.0
        gain = rng.uniform(0.35, 0.85)
        patch = array[..., :3] * array[..., 3:4] * gain
        add_centered(
            accumulator, patch,
            centre + np.cos(angle) * radius * PX_PER_BLOCK,
            centre + np.sin(angle) * radius * PX_PER_BLOCK,
        )

    # Lit ground, so the additive result is judged against terrain rather than pure black.
    grid_y, grid_x = np.mgrid[0:CANVAS, 0:CANVAS].astype(np.float64)
    distance = np.hypot(grid_x - centre, grid_y - centre) / (CANVAS / 2.0)
    background = np.zeros((CANVAS, CANVAS, 3), dtype=np.float64)
    for channel, (inner, outer) in enumerate([(0.215, 0.070), (0.225, 0.075), (0.250, 0.090)]):
        background[..., channel] = inner + (outer - inner) * np.clip(distance, 0.0, 1.0)

    result = np.clip(background + accumulator, 0.0, 1.0)
    Image.fromarray((result * 255.0).round().astype(np.uint8), mode="RGB").save(OUTPUT)
    print(f"wrote {OUTPUT} ({CANVAS}x{CANVAS}, {len(LAYERS)} additive layers)")


if __name__ == "__main__":
    main()
