package net.fabricmc.amar.blocks;

import java.util.Random;

import net.fabricmc.amar.util.EntityExt;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class Bonfire extends Block {
    public Bonfire(Settings settings) {
        super(settings);
    }

    public Bonfire() {
        super(FabricBlockSettings.of(Material.STONE).strength(1.0f).requiresTool().nonOpaque().luminance(12));
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
            player.sendMessage(new TranslatableText("gameplay.homeward.bonfire_updated"), true);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity player, ItemStack itemStack) {
        if (!world.isClient) {
            if (world.getRegistryKey().getValue().equals(DimensionType.OVERWORLD_ID)) {
                super.onPlaced(world, pos, state, player, itemStack);
            } else {
                ((PlayerEntity) player).sendMessage(new TranslatableText("gameplay.homeward.invalid_bonfire_location"), true);
                world.breakBlock(pos, player.canTakeDamage(), player, 2);
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var bonfire_base = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D);
        var bonFire_Sword = Block.createCuboidShape(5.0D, 0.0D, 7.5D, 11.0D, 20.0D, 8.5D);
        return VoxelShapes.union(bonfire_base, new VoxelShape[] { bonFire_Sword });
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
