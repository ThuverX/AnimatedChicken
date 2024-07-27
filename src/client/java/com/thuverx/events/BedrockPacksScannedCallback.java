package com.thuverx.events;

import com.thuverx.resource.BedrockResourcePack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.List;

public interface BedrockPacksScannedCallback {
    Event<BedrockPacksScannedCallback> EVENT = EventFactory.createArrayBacked(BedrockPacksScannedCallback.class,
            (listeners) -> (packs) -> {
                for (BedrockPacksScannedCallback event : listeners) {
                    event.onPacksScanned(packs);
                }
            });

    void onPacksScanned(List<BedrockResourcePack> packs);
}
