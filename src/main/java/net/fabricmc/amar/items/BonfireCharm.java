package net.fabricmc.amar.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class BonfireCharm extends Item {
    public BonfireCharm() {
        super(new FabricItemSettings().group(ItemGroup.MATERIALS));
    }

    public BonfireCharm(Settings settings) {
        super(settings);
    }
}
