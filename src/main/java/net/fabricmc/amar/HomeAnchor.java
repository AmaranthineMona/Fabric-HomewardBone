package net.fabricmc.amar;

import java.util.Random;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class HomeAnchor extends Block {
    public HomeAnchor(Settings settings) {
        super(settings);
    }

    public HomeAnchor() {
        super(FabricBlockSettings.of(Material.STONE).strength(1.0f).requiresTool().nonOpaque());
    };

    public void onTeleport(BlockPos pos, World world) {
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.AMBIENT,
                2.0f, 0.75f, true);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (!world.isClient) {
            ((EntityExt) player).UpdateAnchor(pos);
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

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var BASE_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D);
        var X_STEM_SHAPE = Block.createCuboidShape(4.0D, 0.0D, 5.0D, 8.0D, 16.0D, 10.0D);
        var X_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, new VoxelShape[] { X_STEM_SHAPE });
        return X_AXIS_SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(10) == 0) {
            world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                    SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + random.nextFloat(),
                    random.nextFloat() * 0.7F + 0.6F, false);
        }

        if (random.nextInt(5) == 0) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                world.addParticle(ParticleTypes.LAVA, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D,
                        (double) pos.getZ() + 0.5D, (double) (random.nextFloat() / 2.0F), 5.0E-5D,
                        (double) (random.nextFloat() / 2.0F));
            }
        }
    }
}
