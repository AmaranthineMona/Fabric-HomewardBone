package net.fabricmc.amar.events;

import net.fabricmc.amar.EntityExt;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerEvents implements ServerPlayerEvents.CopyFrom{
    @Override
    public void copyFromPlayer(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        var original = (EntityExt) oldPlayer;
        var player = (EntityExt) newPlayer;

        player.UpdateAnchor(original.GetHomeAnchorPos());
    }
}