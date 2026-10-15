package me.lortseam.sneaktweak;

import me.lortseam.sneaktweak.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(SneakTweak.MOD_ID)
public class SneakTweak {

    public static final String MOD_ID = "sneaktweak";

    public SneakTweak() {
        ModLoadingContext.get().registerConfig(
            Type.CLIENT,
            ModConfig.SPEC,
            "sneaktweak-client.toml"
        );

        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        mc.player.refreshDimensions();
    }
}
