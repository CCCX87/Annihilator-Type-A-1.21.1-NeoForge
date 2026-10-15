"""Generate the world-space effect textures for the annihilation moment skill.

Four mechanical layers are drawn in code (three dial passes plus a scan sweep), so this
script produces two textures:

  annihilation_ring.png     a machined instrument dial: gear teeth, encoder track, index
                            marks, socket-head bolts, a castellated lock ring and hub slots.
  annihilation_scanner.png  a radar-style scan sector with a bright leading beam.

Both feed RenderType.energySwirl(), whose fragment shader is:

    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) { discard; }
    fragColor = color * linear_fog_fade(...);

Consequences that drive how these images are authored:

1. The render type blends additively (blendFunc ONE, ONE), so the *RGB* channel is what
   the effect adds to the scene and is therefore stored pre-multiplied by intensity.
2. Alpha is only used for the `discard` test, so it stores the same intensity and anything
   below 0.11 is dropped to keep speckle out.
3. Additive blending cannot darken, so "shading" is expressed by adding *more* light to the
   lit facet of a part and less to the rest - that is how the bolts and teeth read as metal
   rather than as flat glyphs.

Usage:  python tools/generate_annihilation_ring.py
"""

from __future__ import annotations

import os

import numpy as np
from PIL import Image

SIZE = 512
SUPERSAMPLE = 2
RESOURCES = os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
    "src", "main", "resources", "assets", "annihilator_type_a", "textures", "effect",
)
DIAL_OUTPUT = os.path.join(RESOURCES, "annihilation_ring.png")
SCANNER_OUTPUT = os.path.join(RESOURCES, "annihilation_scanner.png")

TAU = np.pi * 2.0

# Palette sampled from the mod's existing icons (energy_bar, ignore_invincibility).
STEEL = (0.52, 0.80, 1.00)
STEEL_BRIGHT = (0.86, 0.96, 1.00)
STEEL_DIM = (0.34, 0.62, 0.86)
BOLT = (0.58, 0.76, 1.00)
BOLT_FACET = (0.92, 0.99, 1.00)
HUB = (0.46, 0.82, 1.00)


def smoothstep(edge0: float, edge1: float, x: np.ndarray) -> np.ndarray:
    t = np.clip((x - edge0) / (edge1 - edge0), 0.0, 1.0)
    return t * t * (3.0 - 2.0 * t)


def band(radius: np.ndarray, inner: float, outer: float, soft: float = 0.0035) -> np.ndarray:
    """Hard-edged annulus. `soft` is only large enough to antialias the edge."""
    return smoothstep(inner - soft, inner + soft, radius) * (
        1.0 - smoothstep(outer - soft, outer + soft, radius)
    )


def teeth(theta: np.ndarray, count: int, duty: float, phase: float = 0.0) -> np.ndarray:
    """Periodic square wave around the circle, duty = fraction of the pitch that is solid."""
    pitch = 1.0 / count
    position = np.mod(theta / TAU + phase, pitch) / pitch
    distance = np.abs(position - 0.5)
    return 1.0 - smoothstep(duty * 0.5, duty * 0.5 + 0.055, distance)


def teeth_edge(theta: np.ndarray, count: int, duty: float, flank: float,
               phase: float = 0.0) -> np.ndarray:
    """A thin sliver on the leading flank of each tooth, used as a machined highlight.

    `flank` is a fraction of the tooth pitch; it is converted to radians here because
    `teeth` measures its position in pitch units, not in radians.
    """
    offset = flank * TAU / count
    return np.clip(teeth(theta + offset, count, duty, phase) - teeth(theta, count, duty, phase),
                   0.0, 1.0)


def hex_distance(dx: np.ndarray, dy: np.ndarray) -> np.ndarray:
    """Pointy-top regular hexagon distance: <= 1 inside."""
    return np.maximum(np.abs(dx) * 0.8660254 + np.abs(dy) * 0.5, np.abs(dy))


class Paint:
    """Accumulates masks plus a weighted colour sum."""

    def __init__(self, shape) -> None:
        self.weight = np.zeros(shape)
        self.colour = np.zeros((*shape, 3))

    def add(self, mask: np.ndarray, rgb) -> None:
        self.weight += mask
        self.colour += mask[..., None] * np.asarray(rgb, dtype=np.float64)

    def result(self, peak: float) -> tuple[np.ndarray, np.ndarray]:
        tint = np.divide(
            self.colour,
            np.maximum(self.weight, 1e-6)[..., None],
            out=np.zeros_like(self.colour),
            where=self.weight[..., None] > 1e-6,
        )
        # Weights are absolute intensities, so a part keeps the brightness it was authored
        # with and only genuinely overlapping detail saturates. Normalising by the global
        # maximum instead would let one busy band dim every other part of the dial.
        intensity = np.clip(self.weight, 0.0, 1.0) * peak
        return intensity, tint


def downsample(array: np.ndarray) -> np.ndarray:
    shape = array.shape
    if array.ndim == 3:
        return array.reshape(SIZE, SUPERSAMPLE, SIZE, SUPERSAMPLE, shape[-1]).mean(axis=(1, 3))
    return array.reshape(SIZE, SUPERSAMPLE, SIZE, SUPERSAMPLE).mean(axis=(1, 3))


def save(intensity: np.ndarray, tint: np.ndarray, path: str, label: str) -> None:
    rgb = np.clip(tint * intensity[..., None], 0.0, 1.0)
    alpha = np.clip(intensity, 0.0, 1.0)
    alpha[alpha < 0.11] = 0.0
    rgba = np.concatenate([rgb, alpha[..., None]], axis=-1)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    Image.fromarray((rgba * 255.0).round().astype(np.uint8), mode="RGBA").save(path)
    print(f"wrote {label}: {path} ({SIZE}x{SIZE}, peak {intensity.max():.3f})")


def build_dial() -> tuple[np.ndarray, np.ndarray]:
    n = SIZE * SUPERSAMPLE
    ys, xs = np.mgrid[0:n, 0:n].astype(np.float64)
    centre = (n - 1) / 2.0
    x = (xs - centre) / (n / 2.0)
    y = (ys - centre) / (n / 2.0)
    radius = np.hypot(x, y)
    theta = np.arctan2(y, x)
    paint = Paint(radius.shape)

    # --- outer gear ring: 32 trapezoidal teeth on a root circle -------------
    # Each band is given its own annulus so nothing overlaps and every part keeps its
    # authored brightness: teeth, root circle, encoder track, then index marks.
    tooth = teeth(theta, 32, 0.56)
    paint.add(band(radius, 0.878, 0.966) * tooth * 0.62, STEEL)
    paint.add(band(radius, 0.878, 0.966) * teeth_edge(theta, 32, 0.56, 0.035) * 0.45,
              STEEL_BRIGHT)
    paint.add(band(radius, 0.859, 0.874) * 0.55, STEEL)          # root circle

    # --- encoder track: 60 fine dashes -------------------------------------
    paint.add(band(radius, 0.820, 0.856) * teeth(theta, 60, 0.44) * 0.62, STEEL_DIM)

    # --- index marks: 12 long strokes every 30 degrees ---------------------
    paint.add(band(radius, 0.742, 0.806) * teeth(theta, 12, 0.24) * 0.92, STEEL_BRIGHT)

    # --- bolt circle: 8 socket-head cap screws -----------------------------
    for index in range(8):
        angle = index * TAU / 8.0 + TAU / 16.0
        dx = x - np.cos(angle) * 0.618
        dy = y - np.sin(angle) * 0.618
        distance = hex_distance(dx / 0.034, dy / 0.034)
        head = 1.0 - smoothstep(0.86, 1.0, distance)
        socket = 1.0 - smoothstep(0.42, 0.54, distance)
        ring = head * (1.0 - socket)
        paint.add(ring * 0.60, BOLT)
        # a brighter bevel right around the socket, so the head looks turned metal
        paint.add(ring * (1.0 - smoothstep(0.58, 0.74, distance)) * 0.45, BOLT_FACET)

    # --- castellated lock ring: a notched band -----------------------------
    notches = 1.0 - teeth(theta, 24, 0.34)
    paint.add(band(radius, 0.436, 0.458) * notches * 0.58, STEEL)

    # --- hub: four hard-edged slots along the axes -------------------------
    slot = 0.0115
    horizontal = band(radius, 0.165, 0.400) * (np.abs(y) < slot)
    vertical = band(radius, 0.165, 0.400) * (np.abs(x) < slot)
    paint.add(np.maximum(horizontal, vertical) * 0.55, HUB)
    paint.add(band(radius, 0.128, 0.140) * 0.40, STEEL_DIM)       # hub collar

    # --- outer silhouette: a hard rim so the dial reads as one part --------
    paint.add(band(radius, 0.982, 0.992, 0.002) * 0.45, STEEL)

    intensity, tint = paint.result(1.0)
    return downsample(intensity), downsample(tint)


def build_scanner() -> tuple[np.ndarray, np.ndarray]:
    n = SIZE * SUPERSAMPLE
    ys, xs = np.mgrid[0:n, 0:n].astype(np.float64)
    centre = (n - 1) / 2.0
    x = (xs - centre) / (n / 2.0)
    y = (ys - centre) / (n / 2.0)
    radius = np.hypot(x, y)
    theta = np.arctan2(y, x)
    # Re-centre so the leading edge sits at phi = 0 and the trail runs to -span.
    phi = np.mod(theta + np.pi, TAU) - np.pi
    paint = Paint(radius.shape)

    span = np.radians(36.0)
    behind = np.clip(-phi / span, 0.0, 1.0)
    inside = (phi <= 0.0) & (phi >= -span)
    trail = np.where(inside, (1.0 - behind) ** 1.7, 0.0)

    radial = smoothstep(0.085, 0.125, radius) * (1.0 - smoothstep(0.952, 0.992, radius))
    paint.add(radial * trail * 0.42, (0.34, 0.74, 1.00))

    # Leading beam: a hard, bright radial line.
    beam = 1.0 - smoothstep(0.0, np.radians(1.5), np.abs(phi))
    paint.add(radial * beam * 0.95, (0.78, 0.96, 1.00))

    # One faint range ring, as on a radar scope.
    paint.add(band(radius, 0.618, 0.626, 0.003) * 0.09, (0.40, 0.80, 1.00))

    intensity, tint = paint.result(0.85)
    return downsample(intensity), downsample(tint)


def main() -> None:
    save(*build_dial(), DIAL_OUTPUT, "mechanical dial")
    save(*build_scanner(), SCANNER_OUTPUT, "scan sector")


if __name__ == "__main__":
    main()
