package net.fabricmc.amar.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.amar.EntityExt;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExt {
    private int[] blockPos;

    public void UpdateAnchor(BlockPos anchorPos) {
        this.blockPos = new int[] { anchorPos.getX(), anchorPos.getY(), anchorPos.getZ() };
    }

    public BlockPos GetHomeAnchorPos() {
        if (this.blockPos != null) {
            return new BlockPos(this.blockPos[0], this.blockPos[1], this.blockPos[2]);
        } else {
            return null;
        }
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    public void writeNbt(NbtCompound tag, CallbackInfoReturnable ci) {
        if (this.GetHomeAnchorPos() != null) {
            tag.putIntArray("homeAnchorPos", new int[] {
                    this.GetHomeAnchorPos().getX(),
                    this.GetHomeAnchorPos().getY(),
                    this.GetHomeAnchorPos().getZ()
            });
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    public void readNbt(NbtCompound tag, CallbackInfo ci) {
        var tagValue = tag.getIntArray("homeAnchorPos");
        if (tagValue != null && tagValue.length > 0) {
            this.UpdateAnchor(new BlockPos(tagValue[0], tagValue[1], tagValue[2]));
        }
    }
}