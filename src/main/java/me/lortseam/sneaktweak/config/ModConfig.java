package me.lortseam.sneaktweak.config;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ModConfig INSTANCE;

    public final ForgeConfigSpec.BooleanValue smoothingEnabled;
    public final ForgeConfigSpec.IntValue speedPercentage;
    public final ForgeConfigSpec.EnumValue<SneakingEyeHeightType> sneakingEyeHeightPreset;
    public final ForgeConfigSpec.DoubleValue customSneakingEyeHeight;
    public final ForgeConfigSpec.BooleanValue modifyThirdPersonSneakingEyeHeight;

    static {
        Pair<ModConfig, ForgeConfigSpec> pair =
            new ForgeConfigSpec.Builder().configure(ModConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    private ModConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Sneak Tweak Configuration").push("general");

        smoothingEnabled = builder
            .comment("Enable smooth sneak animation")
            .define("smoothingEnabled", true);

        speedPercentage = builder
            .comment("Smoothing speed percentage (25-300, 100 = vanilla)")
            .defineInRange("speedPercentage", 100, 25, 300);

        sneakingEyeHeightPreset = builder
            .comment("Sneaking eye height preset")
            .defineEnum("sneakingEyeHeightPreset", SneakingEyeHeightType.DEFAULT);

        customSneakingEyeHeight = builder
            .comment("Custom sneaking eye height")
            .defineInRange("customSneakingEyeHeight", 1.27, 0.0, 1.8);

        modifyThirdPersonSneakingEyeHeight = builder
            .comment("Also modify in third person view")
            .define("modifyThirdPersonSneakingEyeHeight", false);

        builder.pop();
    }

    public static boolean isSmoothingEnabled() {
        return INSTANCE.smoothingEnabled.get();
    }

    public static float getSpeedModifier() {
        return INSTANCE.speedPercentage.get() / 100f;
    }

    public static float modifySneakingEyeHeight(float height) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.getCameraType().isFirstPerson()
            || INSTANCE.modifyThirdPersonSneakingEyeHeight.get()) {

            return switch (INSTANCE.sneakingEyeHeightPreset.get()) {
                case DEFAULT -> height;
                case PRE_1_14 -> 1.42f;
                case PRE_1_9 -> 1.54f;
                case CUSTOM -> INSTANCE.customSneakingEyeHeight.get().floatValue();
            };
        }
        return height;
    }

    public enum SneakingEyeHeightType {
        DEFAULT,
        PRE_1_14,
        PRE_1_9,
        CUSTOM
    }
}
