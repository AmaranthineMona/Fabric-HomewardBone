package net.fabricmc.amar.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.amar.PlayerEntityExt;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityExt {
    private BlockPos blockPos;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public void UpdateAnchor(BlockPos anchorPos) {
        if (anchorPos == null) {
            this.blockPos = null;
        } else {
            this.blockPos = new BlockPos(anchorPos.getX(), anchorPos.getY(), anchorPos.getZ());
        }
    }

    public BlockPos GetHomeAnchorPos() {
        return blockPos;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeCustomDataToTag(NbtCompound tag, CallbackInfo ci) {
        if (this.GetHomeAnchorPos() != null) {
            tag.putIntArray("homeAnchorPos", new int[] {
                    this.GetHomeAnchorPos().getX(),
                    this.GetHomeAnchorPos().getY(),
                    this.GetHomeAnchorPos().getZ()
            });
        } else {
            tag.putIntArray("homeAnchorPos", new int[] {});
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    public void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        var tagValue = tag.getIntArray("homeAnchorPos");
        if (tagValue != null && tagValue.length > 0) {
            var homeAnchorX = tagValue[0];
            var homeAnchorY = tagValue[1];
            var homeAnchorZ = tagValue[2];

            this.UpdateAnchor(new BlockPos(homeAnchorX, homeAnchorY, homeAnchorZ));
        } else {
            this.UpdateAnchor(null);
        }
    }
}
