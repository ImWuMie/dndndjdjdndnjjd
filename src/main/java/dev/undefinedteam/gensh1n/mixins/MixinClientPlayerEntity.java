package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.player.PlayerMoveEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void sendMovementPackets(CallbackInfo ci) {
        Client.EVENT_BUS.post(PlayerMoveEvent.INSTANCE);
        Client.EVENT_BUS.post(new PlayerMoveEvent());
    }
}
