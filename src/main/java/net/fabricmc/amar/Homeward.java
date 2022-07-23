package net.fabricmc.amar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.amar.items.BonfireCharm;
import net.fabricmc.amar.items.HomewardBone;
import net.fabricmc.amar.items.SoulEssence;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Homeward implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	public static final HomeAnchor HOME_ANCHOR = new HomeAnchor();
	public static final HomewardBone HOMEWARD_BONE = new HomewardBone();
	public static final SoulEssence SOUL_ESSENCE = new SoulEssence();
	public static final BonfireCharm BONFIRE_CHARM = new BonfireCharm();

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("homeward", "home_anchor"), HOME_ANCHOR);
		Registry.register(Registry.ITEM, new Identifier("homeward", "home_anchor"),
				new BlockItem(HOME_ANCHOR, new FabricItemSettings().group(ItemGroup.MISC)));
		Registry.register(Registry.ITEM, new Identifier("homeward", "homeward_bone"), HOMEWARD_BONE);
		Registry.register(Registry.ITEM, new Identifier("homeward", "soul_essence"), SOUL_ESSENCE);
		Registry.register(Registry.ITEM, new Identifier("homeward", "bonfire_charm"), BONFIRE_CHARM);

		ModEventsRegister.registerEvents();
	}
}
