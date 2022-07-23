package net.fabricmc.amar.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class SoulEssence extends Item {
    public SoulEssence() {
        super(new FabricItemSettings().group(ItemGroup.MATERIALS));
    }

    public SoulEssence(Settings settings) {
        super(settings);
    }
}
