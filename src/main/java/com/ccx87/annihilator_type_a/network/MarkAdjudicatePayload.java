package com.ccx87.annihilator_type_a.network;

import com.ccx87.annihilator_type_a.AnnihilatorTypeA;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MarkAdjudicatePayload(int casterId, double targetX, double targetY, double targetZ,
                                    double groundX, double groundY, double groundZ)
        implements CustomPacketPayload {
    public static final Type<MarkAdjudicatePayload> TYPE =
            new Type<>(AnnihilatorTypeA.id("mark_adjudicate"));

    public static final StreamCodec<FriendlyByteBuf, MarkAdjudicatePayload> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, payload) -> {
                        buffer.writeVarInt(payload.casterId);
                        buffer.writeDouble(payload.targetX);
                        buffer.writeDouble(payload.targetY);
                        buffer.writeDouble(payload.targetZ);
                        buffer.writeDouble(payload.groundX);
                        buffer.writeDouble(payload.groundY);
                        buffer.writeDouble(payload.groundZ);
                    },
                    buffer -> new MarkAdjudicatePayload(
                            buffer.readVarInt(),
                            buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                            buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
