package me.lortseam.sneaktweak.mixin;

import me.lortseam.sneaktweak.config.ModConfig;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow private float eyeHeight;
    @Shadow private Entity entity;

    @Unique private Pose lastPose = null;
    @Unique private Pose previousPose = null;

    @Inject(method = "tick", at = @At("HEAD"))
    public void sneaktweak$storePose(CallbackInfo ci) {
        if (this.entity != null) {
            Pose currentPose = this.entity.getPose();
            if (lastPose == null || currentPose != lastPose) {
                previousPose = lastPose;
                lastPose = currentPose;
            }
        }
    }

    @Inject(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/Camera;eyeHeight:F",
            ordinal = 0
        )
    )
    public void sneaktweak$modifyCameraY(CallbackInfo ci) {
        if (!ModConfig.isSmoothingEnabled() && isValidPose()) {
            eyeHeight = entity.getEyeHeight();
        }
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.5f))
    public float sneaktweak$modifyCameraYSpeed(float modifier) {
        if (!isValidPose()) return modifier;
        return ModConfig.isSmoothingEnabled() ? modifier * ModConfig.getSpeedModifier() : 0;
    }

    @Unique
    private boolean isValidPose() {
        if (lastPose == null || previousPose == null) return false;
        return (previousPose == Pose.STANDING && lastPose == Pose.CROUCHING)
            || (previousPose == Pose.CROUCHING && lastPose == Pose.STANDING);
    }
}
