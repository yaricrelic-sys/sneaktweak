package me.lortseam.sneaktweak.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Shadow private ClientLevel level;

    @Shadow private net.minecraft.client.Minecraft minecraft;

    // Fixes MC-159163
    @Redirect(
        method = "handleSetEntityData",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;packedItems()Ljava/util/List;",
            ordinal = 0
        )
    )
    public List<SynchedEntityData.DataValue<?>> sneaktweak$modifyPackedItems(
        ClientboundSetEntityDataPacket packet
    ) {
        Entity entity = level.getEntity(packet.id());
        if (entity != null && entity.equals(minecraft.player)) {
            packet.packedItems().removeIf(entry ->
                entry.serializer().equals(EntityDataSerializers.POSE)
            );
        }
        return packet.packedItems();
    }
}
