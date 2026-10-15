"""Adds Chinese and English translations for every configuration option.

NeoForge looks a config option's title up as `<modid>.configuration.<path>` and its tooltip as
`<key>.tooltip` (see ConfigurationScreen#getTranslationKey). When the tooltip key is missing the
comment from the code is used instead, but the title has no fallback, so without these entries the
config screen shows raw keys.

The keys are discovered from the source, so nothing can be missed by hand. Existing entries are
kept; these are merged in.

Usage: python tools/add_config_translations.py
"""

from __future__ import annotations

import collections
import glob
import json
import os
import re
import sys

MOD_ID = "annihilator_type_a"
PROJECT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LANG_DIR = os.path.join(PROJECT, "src", "main", "resources", "assets", MOD_ID, "lang")
SOURCE_DIR = os.path.join(PROJECT, "src", "main", "java")

# ------------------------------------------------------------------ section + screen titles

TITLES = {
    f"{MOD_ID}.configuration.title": (
        "%s Configuration", "%s 配置"),
    f"{MOD_ID}.configuration.section.annihilator.type.a.client.toml": (
        "Client visuals", "客户端视觉"),
    f"{MOD_ID}.configuration.section.annihilator.type.a.client.toml.title": (
        "Client visuals", "客户端视觉"),
    f"{MOD_ID}.configuration.section.annihilator.type.a.common.toml": (
        "Common", "通用"),
    f"{MOD_ID}.configuration.section.annihilator.type.a.common.toml.title": (
        "Common", "通用"),
}

# ------------------------------------------------------------------ option translations
# key -> (english title, english tooltip, chinese title, chinese tooltip)

OPTIONS = {
    "ring.enabled": (
        "Machined dial",
        "Draw the machined dial around every entity affected by the annihilation moment.",
        "机械表盘",
        "为所有受「湮灭时刻」影响的实体绘制脚下的机械表盘。"),
    "ring.opacity": (
        "Dial brightness",
        "Brightness multiplier of the dial, 0.0 - 2.0.",
        "表盘亮度",
        "表盘亮度倍率，0.0 - 2.0。"),
    "ring.visibleDistance": (
        "Dial visible distance",
        "Affected entities farther away than this, in blocks, are not drawn.",
        "表盘可见距离",
        "超过该距离（格）的受影响实体不再绘制表盘。"),
    "ring.followEntitySize": (
        "Scale with body size",
        "Scale the dial and its sparks with the entity's body height, measured against one block."
        " Taller entities get a proportionally larger ring, shorter ones a smaller one.",
        "随体型缩放",
        "表盘与火花按实体身高缩放，以 1 格为基准：越高的实体光环越大，越小的越小。"),
    "ring.sizeMultiplier": (
        "Overall size multiplier",
        "Overall size multiplier applied on top of the body scaling, 0.25 - 4.0.",
        "整体尺寸倍率",
        "在体型缩放之上再乘的一个系数，0.25 - 4.0，用来整体微调光环大小。"),
    "ring.sizeFollowStrength": (
        "Body size follow strength",
        "How strongly the body height is followed, 0.0 - 1.0. 1.0 scales in proportion to the"
        " height measured against one block; lower values damp the change in either direction.",
        "体型跟随强度",
        "体型对光环大小的影响强度，0.0 - 1.0。1.0 表示完全按身高成比例，更小的值会双向抑制变化。"),
    "ring.maxScale": (
        "Maximum scale (performance)",
        "Hard cap on the final ring scale, 0.35 - 4.0. Cost grows with the square of this: going"
        " from 4.0 down to 2.5 removes about 60% of the blended pixels the dial covers, which"
        " matters because the dial is four large additive quads.",
        "缩放上限（性能）",
        "光环缩放的硬上限，0.35 - 4.0。开销与该值的平方成正比：从 4.0 降到 2.5 可少约 60% 的混合像素"
        "——因为表盘是 4 片很大的加法混合面片。"),
    "ring.distanceLod": (
        "Distance level of detail",
        "Draw fewer dial layers for distant rings: past 24 blocks the encoder and index dials are"
        " only a few pixels across, and past 40 only the outer gear is drawn.",
        "远处简化绘制",
        "远处只画部分表盘层：超过 24 格时编码盘与索引盘只有几个像素，直接跳过；超过 40 格只画外齿盘。"),
    "ring.cullOffscreen": (
        "Cull off-screen rings",
        "Skip rings whose area lies entirely outside the view frustum.",
        "剔除视野外光环",
        "完全不在视野内的光环直接跳过，不参与绘制。"),
    "hud.enabled": (
        "HUD panels",
        "Draw the annihilation moment HUD panels.",
        "HUD 面板",
        "绘制「湮灭时刻」的三块数据面板。"),
    "hud.decorations": (
        "Panel decorations",
        "Draw the decorative corner brackets and the sweeping highlight.",
        "面板装饰",
        "绘制面板的装饰角件与扫掠高光。"),
    "lightning.warning": (
        "Lightning storm warning",
        "Show the WARNING: LIGHTNING STORM CREATED banner the moment the storm is released. An"
        " interrupted cast never reaches that point, so it shows nothing.",
        "\u95ea\u7535\u98ce\u66b4\u8b66\u544a\u6a2a\u5e45",
        "\u5728\u98ce\u66b4\u771f\u6b63\u91ca\u653e\u7684\u77ac\u95f4\u663e\u793a WARNING: LIGHTNING STORM CREATED \u6a2a\u5e45\u3002\u8bfb\u6761\u88ab\u6253\u65ad\u65f6\u4e0d\u4f1a\u6709\u4efb\u4f55\u63d0\u793a\u3002"),
    "lightning.hud": (
        "Lightning storm data readout",
        "Show the column of scrolling values down the left of the screen while the storm charges.",
        "\u95ea\u7535\u98ce\u66b4\u6570\u636e\u5217",
        "\u8bfb\u6761\u671f\u95f4\u5728\u5c4f\u5e55\u5de6\u4fa7\u663e\u793a\u6eda\u52a8\u7684\u6570\u503c\u5217\u3002"),
    "hud.selfPanelOnlyWhileActive": (
        "Self panel only while active",
        "Only show the left self-status panel while the annihilation moment is active. Set to false to"
        " keep your own attributes on screen at all times. Either way the panel is exclusive to the"
        " mechanical dragon - it is identified by that species' permanent random fault marker - so no"
        " other species and no human ever sees it.",
        "自身面板仅在激活时显示",
        "只在技能激活期间显示左侧自身状态面板；改为 false 则随时显示自身属性。"
        "无论哪种设置，该面板都只属于机械龙（通过该龙种常驻的随机故障标记识别），"
        "其它龙种和人类都看不到。"),
    "hud.targetPanel": (
        "Target panel",
        "Show the right target panel for the creature under the crosshair.",
        "目标面板",
        "显示准星所指生物的信息面板。"),
    "hud.targetOnlyWhileActive": (
        "Target panel only while active",
        "Only show the right target panel while the annihilation moment is active. Set to false to keep"
        " it up whenever something is targeted. Either way the panel is exclusive to the mechanical"
        " dragon, so no other species and no human ever sees it.",
        "目标面板仅在激活时显示",
        "只在技能激活期间显示目标面板；改为 false 则只要选中生物就显示。"
        "无论哪种设置，该面板都只属于机械龙，其它龙种和人类都看不到。"),
    "hud.targetAttributes": (
        "Show target armour and attack",
        "Include the target's armour and attack damage in the right panel.",
        "显示目标护甲与攻击",
        "在目标面板中显示目标的护甲与攻击力。"),
    "hud.targetBuffs": (
        "Show target effects",
        "Include the target's active effects in the right panel.",
        "显示目标状态效果",
        "在目标面板中显示目标身上的增益/减益效果。"),
    "hud.targetRange": (
        "Target search range",
        "How far, in blocks, the crosshair looks for a creature to inspect, 4 - 64.",
        "目标搜索距离",
        "准星搜索可查看生物的距离（格），4 - 64。"),
    "hud.frame": (
        "Visor frame",
        "Draw the full-screen visor frame while the annihilation moment is running.",
        "全屏取景框",
        "技能激活期间绘制全屏取景框。"),
    "hud.frameInsetX": (
        "Frame inset, horizontal",
        "How far the frame sits in from the left and right edges, as a fraction, 0.0 - 0.45.",
        "取景框内缩（水平）",
        "取景框距左右边缘的内缩比例，0.0 - 0.45。"),
    "hud.frameInsetY": (
        "Frame inset, vertical",
        "How far it sits in from the top and bottom edges, 0.0 - 0.45.",
        "取景框内缩（垂直）",
        "取景框距上下边缘的内缩比例，0.0 - 0.45。"),
    "hud.frameChamfer": (
        "Frame corner cut",
        "Length of the diagonal corner cuts, as a fraction of the screen height, 0.0 - 0.60."
        " Larger values read more like a hexagon than a box.",
        "取景框切角",
        "四角斜切长度占屏幕高度的比例，0.0 - 0.60。越大越接近六边形，越小越接近方框。"),
    "hud.frameAlpha": (
        "Frame opacity",
        "Opacity of the frame's main contour, 0.0 - 1.0.",
        "取景框不透明度",
        "取景框主线的不透明度，0.0 - 1.0。"),
    "hud.frameColor": (
        "Frame colour",
        "Accent colour of the frame, as a hex RGB string such as 4FD8FF. The species' official"
        " primary colour is 66CCFF.",
        "取景框颜色",
        "取景框主色，十六进制 RGB 字符串（例如 4FD8FF）。本龙种的官方主色是 66CCFF。"),
    "hud.top.offsetX": (
        "Top panel X offset",
        "Horizontal offset of the top ability panel, in GUI pixels.",
        "顶部面板水平偏移",
        "顶部技能面板的水平偏移，单位为 GUI 像素。"),
    "hud.top.offsetY": (
        "Top panel Y offset",
        "Vertical offset of the top ability panel, in GUI pixels.",
        "顶部面板垂直偏移",
        "顶部技能面板的垂直偏移，单位为 GUI 像素。"),
    "hud.left.offsetX": (
        "Self panel X offset",
        "Horizontal offset of the left self-status panel, in GUI pixels.",
        "自身面板水平偏移",
        "左侧自身状态面板的水平偏移，单位为 GUI 像素。"),
    "hud.left.offsetY": (
        "Self panel Y offset",
        "Vertical offset of the left self-status panel, in GUI pixels.",
        "自身面板垂直偏移",
        "左侧自身状态面板的垂直偏移，单位为 GUI 像素。"),
    "hud.right.offsetX": (
        "Target panel X offset",
        "Horizontal offset of the right target panel, in GUI pixels.",
        "目标面板水平偏移",
        "右侧目标面板的水平偏移，单位为 GUI 像素。"),
    "hud.right.offsetY": (
        "Target panel Y offset",
        "Vertical offset of the right target panel, in GUI pixels.",
        "目标面板垂直偏移",
        "右侧目标面板的垂直偏移，单位为 GUI 像素。"),
    "debug.timings": (
        "Log render timings",
        "Log the average cost of each effect renderer once a second, to the game log. Use it to"
        " tell which part is responsible for a frame rate drop.",
        "输出渲染耗时日志",
        "每秒把各特效渲染器的平均耗时写入游戏日志，用来判断掉帧是哪一部分造成的。"),
}


def discover_keys() -> list:
    """Every config path defined in the source, in declaration order."""
    pattern = re.compile(r'\.define(?:InRange|Enum|List)?\(\s*"([^"]+)"')
    keys = []
    for path in sorted(glob.glob(os.path.join(SOURCE_DIR, "**", "*.java"), recursive=True)):
        for key in pattern.findall(open(path, encoding="utf-8").read()):
            if key not in keys:
                keys.append(key)
    return keys


def merge(path: str, option_title: int, option_tooltip: int, section_index: int) -> int:
    with open(path, encoding="utf-8") as handle:
        data = json.load(handle, object_pairs_hook=collections.OrderedDict)
    added = 0
    # The dictionaries above are the source of truth, so an existing entry is updated rather than
    # skipped - otherwise rewording a comment in the code would never reach the language files.
    for key, texts in TITLES.items():
        full = key
        if data.get(full) != texts[section_index]:
            data[full] = texts[section_index]
            added += 1
    for key, texts in OPTIONS.items():
        base = f"{MOD_ID}.configuration.{key}"
        for suffix, value in (("", texts[option_title]), (".tooltip", texts[option_tooltip])):
            if data.get(base + suffix) != value:
                data[base + suffix] = value
                added += 1

    # Drop entries for options that no longer exist, so removing a config option also removes its
    # translations instead of leaving them behind forever.
    expected = set(TITLES)
    for key in OPTIONS:
        expected.add(f"{MOD_ID}.configuration.{key}")
        expected.add(f"{MOD_ID}.configuration.{key}.tooltip")
    stale = [key for key in data if key.startswith(f"{MOD_ID}.configuration.") and key not in expected]
    for key in stale:
        del data[key]
    with open(path, "w", encoding="utf-8") as handle:
        json.dump(data, handle, ensure_ascii=False, indent=2)
        handle.write("\n")
    if stale:
        print(f"  pruned {len(stale)} obsolete keys: {', '.join(stale[:4])}"
              f"{' ...' if len(stale) > 4 else ''}")
    return added


def main() -> None:
    keys = discover_keys()
    missing = [key for key in keys if key not in OPTIONS]
    print(f"config keys found in source: {len(keys)}")
    if missing:
        print(f"NOT TRANSLATED: {', '.join(missing)}")
    extra = [key for key in OPTIONS if key not in keys]
    if extra:
        print(f"translated but no longer defined: {', '.join(extra)}")

    # OPTIONS tuples are (english title, english tooltip, chinese title, chinese tooltip);
    # TITLES tuples are (english, chinese).
    for name, title_index, tooltip_index, section_index in (("en_us.json", 0, 1, 0),
                                                             ("zh_cn.json", 2, 3, 1)):
        path = os.path.join(LANG_DIR, name)
        added = merge(path, title_index, tooltip_index, section_index)
        with open(path, encoding="utf-8") as handle:
            total = len(json.load(handle))
        print(f"{name}: +{added} entries, {total} total")


if __name__ == "__main__":
    sys.exit(main())
