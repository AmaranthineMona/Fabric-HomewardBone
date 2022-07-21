package net.fabricmc.amar;

import net.minecraft.util.math.BlockPos;

public interface PlayerEntityExt {
    void UpdateAnchor(BlockPos anchorPos);

    BlockPos GetHomeAnchorPos();
}