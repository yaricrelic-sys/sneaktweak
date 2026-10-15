package me.lortseam.sneaktweak.mixin;

import com.mojang.authlib.GameProfile;
import me.lortseam.sneaktweak.config.ModConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@OnlyIn(Dist.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer {

    @Unique
    private float sneaktweak$smoothEyeHeight = 0f;

    @Unique
    private boolean sneaktweak$initialized = false;

    private ClientPlayerEntityMixin(ClientLevel level, GameProfile profile) {
        super(level, profile);
    }

    @Override
    public float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        float vanilla = super.getStandingEyeHeight(pose, dimensions);

        if (!ModConfig.isSmoothingEnabled()) {
            return vanilla;
        }

        // Целевая высота — с учётом конфига
        float target = pose == Pose.CROUCHING
            ? ModConfig.modifySneakingEyeHeight(vanilla)
            : vanilla;

        // Инициализация
        if (!sneaktweak$initialized) {
            sneaktweak$smoothEyeHeight = target;
            sneaktweak$initialized = true;
            return sneaktweak$smoothEyeHeight;
        }

        // Плавная интерполяция
        float diff = target - sneaktweak$smoothEyeHeight;
        if (Math.abs(diff) > 0.001f) {
            float speed = ModConfig.getSpeedModifier();
            // Скорость интерполяции: чем меньше speed — тем плавнее
            sneaktweak$smoothEyeHeight += diff * speed * 0.15f;
        } else {
            sneaktweak$smoothEyeHeight = target;
        }

        return sneaktweak$smoothEyeHeight;
    }
}
