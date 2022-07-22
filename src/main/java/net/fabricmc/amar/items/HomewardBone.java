package net.fabricmc.amar.items;

import java.util.List;

import net.fabricmc.amar.EntityExt;
import net.fabricmc.amar.HomeAnchor;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class HomewardBone extends Item {
    public HomewardBone() {
        super(new FabricItemSettings()
                .group(ItemGroup.BREWING)
                .maxCount(16));
    }

    public HomewardBone(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1.0f, 1.0f);

        if (!world.isClient) {
            var anchorPos = ((EntityExt) player).GetHomeAnchorPos();
            var block = world.getBlockState(anchorPos).getBlock();

            if (block instanceof HomeAnchor) {
                if (world.getRegistryKey().getValue() == DimensionType.OVERWORLD_ID) {
                    ((ServerPlayerEntity) player).networkHandler.requestTeleport(anchorPos.getX(), anchorPos.getY(),
                            anchorPos.getZ(), player.getYaw(), player.getPitch());
                            
                    player.getStackInHand(hand).decrement(1);
                } else {
                    player.sendSystemMessage(Text.of("Home anchor is in another dimension."), player.getUuid());
                }
            } else {
                player.sendSystemMessage(Text.of("Home anchor was broken or blocked."), player.getUuid());
            }

        }
        return super.use(world, player, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.of("Use to return back to\n your attuned home anchor."));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }
}
