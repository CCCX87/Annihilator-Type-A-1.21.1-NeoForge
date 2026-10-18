package com.ccx87.annihilator_type_a.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class MarkAdjudicateClientHandler {
    private MarkAdjudicateClientHandler() {}

    public static void handle(MarkAdjudicatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> com.ccx87.annihilator_type_a.client.MarkAdjudicateRemote.add(payload));
    }
}
