package net.fabricmc.amar;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public interface EntityExt {
    void UpdateAnchor(BlockPos anchorPos);

    BlockPos GetHomeAnchorPos();
}