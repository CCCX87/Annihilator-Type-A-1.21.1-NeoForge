package com.ccx87.annihilator_type_a.common;

import net.neoforged.neoforge.common.ModConfigSpec;


public final class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue MARK_ADJUDICATE_DESTROY_BLOCKS = BUILDER
            .comment(new String[]{
                "Whether Mark Adjudication's orbital beam destroys blocks.",
                "When false the beam still deals its damage and plays every effect, but it leaves",
                "the terrain untouched: no crater under the impact and no shaft burned through",
                "the ceiling above it."
            })
            .translation("annihilator_type_a.configuration.mark_adjudicate.destroy_blocks")
            .define("mark_adjudicate.destroy_blocks", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ServerConfig() {}

    public static boolean markAdjudicateDestroysBlocks() {
        return !SPEC.isLoaded() || MARK_ADJUDICATE_DESTROY_BLOCKS.get();
    }
}
