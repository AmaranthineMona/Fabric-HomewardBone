package net.fabricmc.amar;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class HomeAnchor extends Block {
    private PlayerEntity boundPlayer;

    public HomeAnchor(Settings settings) {
        super(settings);
    }

    public HomeAnchor() {
        super(FabricBlockSettings.of(Material.STONE).strength(1.0f).requiresTool());
    }

    public PlayerEntity getBoundPlayer() {
        return this.boundPlayer;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (!world.isClient) {
            if (this.boundPlayer == null) {
                ((EntityExt) player).UpdateAnchor(pos);
                boundPlayer = player;
            } else if (!this.boundPlayer.equals(player)) {
                player.sendMessage(Text.of("This home anchor is already in use"), true);
                return ActionResult.FAIL;
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity player, ItemStack itemStack) {
        if (!world.isClient) {
            if (world.getRegistryKey().getValue().equals(DimensionType.OVERWORLD_ID)) {
                super.onPlaced(world, pos, state, player, itemStack);
            } else {
                sendMessage(player, "Can only be placed in the overworld");
                world.breakBlock(pos, player.canTakeDamage(), player, 2);
            }
        }
    }

    private void sendMessage(LivingEntity player, String text) {
        player.sendSystemMessage(Text.of(text), player.getUuid());
    }
}
