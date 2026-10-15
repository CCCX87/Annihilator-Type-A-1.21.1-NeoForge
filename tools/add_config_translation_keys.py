"""Gives every config option an explicit translation key.

NeoForge resolves a value's title as `valueSpec.getTranslationKey()`, falling back to
`<modid>.configuration.<key>` built from whatever path the screen happens to hold. That fallback
depends on how the screen composes nested sections, and with flat dotted keys such as
`ring.maxScale` it did not match, so the client screen stayed untranslated. Setting the key
explicitly with `Builder#translation` removes the ambiguity: the screen uses it verbatim, and the
tooltip is looked up at `<key>.tooltip`.

Idempotent: an option that already carries its translation line is left alone.

Usage: python tools/add_config_translation_keys.py
"""

from __future__ import annotations

import os
import re
import sys

MOD_ID = "annihilator_type_a"
PROJECT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TARGET = os.path.join(PROJECT, "src", "main", "java", "com", "ccx87", "annihilator_type_a",
                      "client", "ClientConfig.java")

PATTERN = re.compile(r'^([ \t]*)\.((?:define|defineInRange|defineEnum|defineList'
                     r'|defineListAllowEmpty))\("([^"]+)"', re.MULTILINE)


def main() -> None:
    text = open(TARGET, encoding="utf-8").read()
    inserted = 0

    def replace(match: re.Match) -> str:
        nonlocal inserted
        indent, call, key = match.group(1), match.group(2), match.group(3)
        translation = f'.translation("{MOD_ID}.configuration.{key}")'
        if translation in text:
            return match.group(0)
        inserted += 1
        return f"{indent}{translation}\n{indent}.{call}(\"{key}\""

    updated = PATTERN.sub(replace, text)
    if inserted:
        with open(TARGET, "w", encoding="utf-8") as handle:
            handle.write(updated)

    keys = PATTERN.findall(updated)
    duplicates = {key for _, _, key in keys if [k for _, _, k in keys].count(key) > 1}
    print(f"{os.path.relpath(TARGET, PROJECT)}: {len(keys)} options, {inserted} translation keys added")
    if duplicates:
        print(f"duplicate keys: {', '.join(sorted(duplicates))}")
    missing = [key for _, _, key in keys
               if f'.translation("{MOD_ID}.configuration.{key}")' not in updated]
    print(f"options still without an explicit key: {len(missing)} {missing if missing else ''}")


if __name__ == "__main__":
    sys.exit(main())
