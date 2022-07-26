package net.fabricmc.amar.util;

import net.fabricmc.amar.events.PlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class ModEventsRegister {
    public static void registerEvents() {
        ServerPlayerEvents.COPY_FROM.register(new PlayerEvents());
    }
}