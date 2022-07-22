package net.fabricmc.amar.items;

import java.util.List;

import net.fabricmc.amar.EntityExt;
import net.fabricmc.amar.HomeAnchor;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
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
                .maxCount(16)
                .food(new FoodComponent.Builder().hunger(0).saturationModifier(0).snack().alwaysEdible().build()));
    }

    public HomewardBone(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1.0f, 1.0f);

        return super.use(world, player, hand);
    }

    private void TeleportPlayer(World world, LivingEntity player, Hand hand) {
        if (!world.isClient) {
            var anchorPos = ((EntityExt) player).GetHomeAnchorPos();
            var block = world.getBlockState(anchorPos).getBlock();

            if (world.getRegistryKey().getValue().equals(DimensionType.OVERWORLD_ID)) {
                if (block instanceof HomeAnchor) {

                    ((ServerPlayerEntity) player).networkHandler.requestTeleport(anchorPos.getX(), anchorPos.getY(),
                            anchorPos.getZ(), player.getYaw(), player.getPitch());

                    player.getStackInHand(hand).decrement(1);
                } else {
                    player.sendSystemMessage(Text.of("Home anchor was broken or blocked."), player.getUuid());
                }
            } else {
                player.sendSystemMessage(Text.of("Home anchor is in another dimension."), player.getUuid());
            }

        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.of("Use to return back to\n your attuned home anchor."));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void usageTick(World world, LivingEntity player, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks == 1) {
            TeleportPlayer(world, player, player.getActiveHand());

            player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 2.0f, 0.75f);
        }
    }
}
