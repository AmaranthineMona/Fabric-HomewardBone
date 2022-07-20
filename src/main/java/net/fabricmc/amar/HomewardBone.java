package net.fabricmc.amar;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomewardBone implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	public static final Block HOME_ANCHOR = new Block(FabricBlockSettings.of(Material.STONE).strength(4.0f).requiresTool());

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("homeward", "home_anchor"), HOME_ANCHOR);
		Registry.register(Registry.ITEM, new Identifier("homeward", "home_anchor"),
				new BlockItem(HOME_ANCHOR, new FabricItemSettings().group(ItemGroup.MISC)));
	}
}
