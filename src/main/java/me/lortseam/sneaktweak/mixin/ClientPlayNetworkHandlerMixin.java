package me.lortseam.sneaktweak.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonPacketListenerImpl {

    @Shadow private ClientLevel level;

    private ClientPlayNetworkHandlerMixin(Minecraft client, Connection connection, CommonListenerCookie cookie) {
        super(client, connection, cookie);
    }

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
        if (level.getEntity(packet.id()) != null
            && level.getEntity(packet.id()).equals(minecraft.player)) {
            packet.packedItems().removeIf(entry ->
                entry.serializer().equals(EntityDataSerializers.POSE)
            );
        }
        return packet.packedItems();
    }
}
