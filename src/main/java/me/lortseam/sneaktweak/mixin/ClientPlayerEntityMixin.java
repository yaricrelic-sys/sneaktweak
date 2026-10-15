package me.lortseam.sneaktweak.mixin;

import com.mojang.authlib.GameProfile;
import me.lortseam.sneaktweak.config.ModConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer {

    private ClientPlayerEntityMixin(ClientLevel level, GameProfile profile) {
        super(level, profile);
    }

    @Override
    public float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        float height = super.getStandingEyeHeight(pose, dimensions);

        if (pose == Pose.CROUCHING && canChangeIntoPose(Pose.STANDING)) {
            height = ModConfig.modifySneakingEyeHeight(height);
        }
        return height;
    }
}
