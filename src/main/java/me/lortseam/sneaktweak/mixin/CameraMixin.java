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

    @Unique private Pose[] lastPoses = new Pose[2];

    @Inject(method = "tick", at = @At("HEAD"))
    public void sneaktweak$storePose(CallbackInfo ci) {
        if (this.entity != null) {
            Pose pose = this.entity.getPose();
            if (lastPoses[0] == null || pose != lastPoses[0]) {
                lastPoses[1] = lastPoses[0];
                lastPoses[0] = pose;
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
        if (lastPoses[0] == null || lastPoses[1] == null) return false;
        return (lastPoses[1] == Pose.STANDING && lastPoses[0] == Pose.CROUCHING)
            || (lastPoses[1] == Pose.CROUCHING && lastPoses[0] == Pose.STANDING);
    }
}
