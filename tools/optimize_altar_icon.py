"""Cleans up the Dragon Survival altar icon without changing its resolution.

The altar icon format, confirmed against all twenty icons shipped by Dragon Survival and its
addons, is a 49x294 PNG holding six 49x49 tiles. The six tiles are a 147x98 picture sliced in
row-major order and stacked: tiles 0-2 are the top row of the picture (left to right) and tiles 3-5
are the bottom row. Filtering tile by tile would leave seams, so this script reassembles the picture
first, works on it, and slices it back exactly the same way.

The source file is only ever read. The result is written elsewhere, defaulting to the workspace.

Usage: python tools/optimize_altar_icon.py [source.png] [output.png]
"""

from __future__ import annotations

import os
import sys

from PIL import Image, ImageEnhance, ImageFilter

TILE = 49
TILES_ACROSS = 3
TILES_DOWN = 2

DEFAULT_SOURCE = os.path.join(
    "src", "main", "resources", "assets", "dragonsurvival", "textures", "gui", "custom", "altar",
    "annihilator_type_a", "altar_icon.png")

SUPERSAMPLE = 4
DESPECKLE = 3          # median window at the supersampled size
SHARPEN_RADIUS = 3.0   # ~0.75px at the final size
SHARPEN_PERCENT = 80
COLOUR_BOOST = 1.12
CONTRAST_BOOST = 1.06


def reassemble(icon: Image.Image) -> Image.Image:
    """Six stacked tiles back into a TILES_ACROSS x TILES_DOWN picture."""
    width = TILE * TILES_ACROSS
    height = TILE * TILES_DOWN
    if icon.width != TILE or icon.height != TILE * TILES_ACROSS * TILES_DOWN:
        raise SystemExit(f"unexpected icon size {icon.size}; expected {TILE}x{TILE * 6}")
    picture = Image.new("RGBA", (width, height))
    for index in range(TILES_ACROSS * TILES_DOWN):
        column = index % TILES_ACROSS
        row = index // TILES_ACROSS
        tile = icon.crop((0, index * TILE, TILE, (index + 1) * TILE))
        picture.paste(tile, (column * TILE, row * TILE))
    return picture


def slice_back(picture: Image.Image) -> Image.Image:
    icon = Image.new("RGBA", (TILE, TILE * TILES_ACROSS * TILES_DOWN))
    for index in range(TILES_ACROSS * TILES_DOWN):
        column = index % TILES_ACROSS
        row = index // TILES_ACROSS
        tile = picture.crop((column * TILE, row * TILE, (column + 1) * TILE, (row + 1) * TILE))
        icon.paste(tile, (0, index * TILE))
    return icon


def enhance(row: Image.Image, colourful: bool) -> Image.Image:
    """Supersample, despeckle, sharpen, then come back down - all without changing the size."""
    big = row.resize((row.width * SUPERSAMPLE, row.height * SUPERSAMPLE), Image.LANCZOS)
    # Removes the one-pixel speckle scattered over the flat areas. At four times the size this only
    # touches features that were a single pixel across in the original.
    big = big.filter(ImageFilter.MedianFilter(size=DESPECKLE))
    big = big.filter(ImageFilter.UnsharpMask(radius=SHARPEN_RADIUS, percent=SHARPEN_PERCENT,
                                             threshold=0))
    if colourful:
        big = ImageEnhance.Color(big).enhance(COLOUR_BOOST)
    big = ImageEnhance.Contrast(big).enhance(CONTRAST_BOOST)
    return big.resize(row.size, Image.LANCZOS)


def main() -> None:
    source = sys.argv[1] if len(sys.argv) > 1 else DEFAULT_SOURCE
    output = sys.argv[2] if len(sys.argv) > 2 else os.path.normpath(os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "..", "altar_icon.png"))

    original = Image.open(source).convert("RGBA")
    picture = reassemble(original)

    row_height = TILE
    colour_row = picture.crop((0, 0, picture.width, row_height))
    grey_row = picture.crop((0, row_height, picture.width, picture.height))

    enhanced = Image.new("RGBA", picture.size)
    enhanced.paste(enhance(colour_row, True), (0, 0))
    enhanced.paste(enhance(grey_row, False), (0, row_height))

    slice_back(enhanced).save(output)

    # Side-by-side proof sheet: original above, enhanced below, at 6x.
    scale = 6
    sheet = Image.new("RGBA", (picture.width * scale, (picture.height * 2 + 4) * scale),
                      (0x18, 0x18, 0x20, 255))
    sheet.alpha_composite(picture.resize((picture.width * scale, picture.height * scale),
                                         Image.NEAREST), (0, 0))
    sheet.alpha_composite(enhanced.resize((picture.width * scale, picture.height * scale),
                                          Image.NEAREST), (0, (picture.height + 4) * scale))
    sheet_path = os.path.join(os.path.dirname(output), "altar_icon_optimised_compare.png")
    sheet.save(sheet_path)

    print(f"source     {source} ({original.width}x{original.height})")
    print(f"picture    {picture.width}x{picture.height} (reassembled, {TILES_ACROSS}x{TILES_DOWN} tiles)")
    print(f"wrote      {output} ({TILE}x{TILE * 6}, same layout as the source)")
    print(f"sheet      {sheet_path} (original above, enhanced below)")


if __name__ == "__main__":
    main()
