package net.fabricmc.amar.util;

import net.minecraft.util.math.BlockPos;

public interface EntityExt {
    void UpdateAnchor(BlockPos anchorPos);

    BlockPos GetHomeAnchorPos();
}