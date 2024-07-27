package com.thuverx.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface BedrockPacksAppliedCallback {
    Event<BedrockPacksAppliedCallback> EVENT = EventFactory.createArrayBacked(BedrockPacksAppliedCallback.class,
            (listeners) -> () -> {
                for (BedrockPacksAppliedCallback event : listeners) {
                    event.onPacksApplied();
                }
            });

    void onPacksApplied();
}
