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
        if (((EntityExt) player).GetHomeAnchorPos() == null) {
            // This prevents non-player use events from being processed
            return TypedActionResult.fail(player.getStackInHand(hand));
        }

        if (!this.isInOverworld(world)) {
            player.stopUsingItem();
            player.sendMessage(Text.of("Home anchor is in another dimension"), true);

            return TypedActionResult.fail(player.getStackInHand(hand));
        } else if (!this.IsHomeAnchorAvailable(world, player)) {
            player.stopUsingItem();
            player.sendMessage(Text.of("Home anchor was broken or blocked"), true);

            return TypedActionResult.fail(player.getStackInHand(hand));
        } else {
            player.playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1.0f, 1.0f);

            return super.use(world, player, hand);
        }
    }

    private boolean isInOverworld(World world) {
        return world.getRegistryKey().getValue().equals(DimensionType.OVERWORLD_ID);
    }

    private boolean IsHomeAnchorAvailable(World world, PlayerEntity player) {
        var anchorPos = ((EntityExt) player).GetHomeAnchorPos();
        if (anchorPos == null)
            return false;

        var block = world.getBlockState(anchorPos).getBlock();

        if (block instanceof HomeAnchor) {
            var homeAnchor = (HomeAnchor) block;
            var boundPlayer = homeAnchor.getBoundPlayer();
            return boundPlayer != null ? boundPlayer.equals(player) : false;
        } else {
            return false;
        }
    }

    private void TeleportPlayer(World world, LivingEntity player, Hand hand) {
        if (!world.isClient) {
            var anchorPos = ((EntityExt) player).GetHomeAnchorPos();
            var homeAnchor = (HomeAnchor) world.getBlockState(anchorPos).getBlock();
            ((ServerPlayerEntity) player).networkHandler.requestTeleport(anchorPos.getX(), anchorPos.getY(),
                    anchorPos.getZ(), player.getYaw(), player.getPitch());

            homeAnchor.onTeleport(anchorPos, world);
            player.getStackInHand(hand).decrement(1);
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
        if (remainingUseTicks == this.getMaxUseTime(stack) - 2) {
            player.playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1.0f, 1.0f);
        }

        if (remainingUseTicks <= this.getMaxUseTime(stack) - 16) {
            player.stopUsingItem();
            TeleportPlayer(world, player, player.getActiveHand());

            player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 2.0f, 0.75f);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        // Set sufficiently high so that the food animation never finishes, allowing us
        // to override behavior
        return 300;
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return false;
    }
}
