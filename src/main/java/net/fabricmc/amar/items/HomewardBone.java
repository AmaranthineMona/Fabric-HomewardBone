package net.fabricmc.amar.items;

import java.util.List;

import net.fabricmc.amar.blocks.Bonfire;
import net.fabricmc.amar.util.EntityExt;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AirBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class HomewardBone extends Item {
    final double PLAYER_BLOCK_OFFSET = 0.5D;

    public HomewardBone() {
        super(new FabricItemSettings()
                .group(ItemGroup.TRANSPORTATION)
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
            player.sendMessage(new TranslatableText("gameplay.homeward.bonfire_wrong_dimension"), true);

            return TypedActionResult.fail(player.getStackInHand(hand));
        } else if (!this.IsHomeAnchorAvailable(world, player)) {
            player.stopUsingItem();
            player.sendMessage(new TranslatableText("gameplay.homeward.bonfire_not_found"), true);

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

        return block instanceof Bonfire;
    }

    private int getFallDistance(BlockPos startPos, World world) {
        var fallDistance = 1;
        var currentBlock = startPos.add(0, -1, 0);
        while (world.getBlockState(currentBlock).getBlock() instanceof AirBlock) {
            fallDistance++;
            currentBlock = currentBlock.add(0, -1, 0);
        }

        return fallDistance;
    }

    private boolean isHalfBlockOrSmaller(VoxelShape shape) {
        var min = shape.getMin(Axis.Y);
        var max = shape.getMax(Axis.Y);

        if (Double.isNaN(min) || Double.isNaN(max))
            return true;

        return (max - min) <= 0.5D;
    }

    private boolean isFloorBlockTeleportSafe(World world, BlockPos footPos) {
        var footBlockState = world.getBlockState(footPos);
        var footBlockFluidState = footBlockState.getFluidState();

        if (footBlockFluidState.getFluid() instanceof LavaFluid) {
            return false;
        }

        if (!(footBlockState.getBlock() instanceof AirBlock)
                && !isHalfBlockOrSmaller(footBlockState.getCollisionShape(world, footPos))) {
            return false;
        }

        return true;
    }

    private BlockPos getTeleportLocation(BlockPos anchorPos, World world, PlayerEntity player) {
        for (int width = -1; width <= 1; width++) {
            for (int depth = -1; depth <= 1; depth++) {
                if (width == 0 && depth == 0) {
                    continue;
                }

                var foot = anchorPos.add(width, 0, depth);
                if (!isFloorBlockTeleportSafe(world, foot)) {
                    continue;
                }

                var head = anchorPos.add(width, 1, depth);
                if (!((world.getBlockState(head)).getBlock() instanceof AirBlock)) {
                    continue;
                }

                var floor = anchorPos.add(width, -1, depth);
                if ((world.getBlockState(floor)).getBlock() instanceof AirBlock && getFallDistance(floor, world) >= 3) {
                    continue;
                }

                return foot;
            }
        }

        return null;
    }

    private void TeleportPlayer(World world, LivingEntity player, Hand hand) {
        if (!world.isClient) {
            if (player.hasVehicle()) {
                player.stopRiding();
            }

            var anchorPos = ((EntityExt) player).GetHomeAnchorPos();
            var homeAnchor = (Bonfire) world.getBlockState(anchorPos).getBlock();
            var teleportLocation = getTeleportLocation(anchorPos, world, (PlayerEntity) player);

            if (teleportLocation != null) {
                ((ServerPlayerEntity) player).networkHandler.requestTeleport(teleportLocation.getX() + PLAYER_BLOCK_OFFSET,
                        teleportLocation.getY(), teleportLocation.getZ() + PLAYER_BLOCK_OFFSET, player.getYaw(), player.getPitch());

                homeAnchor.onTeleport(anchorPos, world);
                player.getStackInHand(hand).decrement(1);
            } else {
                ((PlayerEntity) player).sendMessage(new TranslatableText("gameplay.homeward.bonfire_blocked"), true);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("gameplay.homeward.homeward_bone_tooltip"));
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
