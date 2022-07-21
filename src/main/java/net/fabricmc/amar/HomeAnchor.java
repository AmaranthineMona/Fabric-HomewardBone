package net.fabricmc.amar;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class HomeAnchor extends Block {
    public HomeAnchor(Settings settings) {
        super(settings);
    }

    public HomeAnchor(){
        super(FabricBlockSettings.of(Material.STONE).strength(1.0f).requiresTool());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (!world.isClient){
            ((PlayerEntityExt) player).UpdateAnchor(pos);

            player.sendMessage(Text.of("awwoooo"), false);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!world.isClient){
            var heck = ((PlayerEntityExt) player).GetHomeAnchorPos();

            if (heck != null){

                player.sendMessage(Text.of(heck.toShortString()), false);
            }else{
                player.sendMessage(Text.of("oh no"), false);

            }
        }
    }
}

