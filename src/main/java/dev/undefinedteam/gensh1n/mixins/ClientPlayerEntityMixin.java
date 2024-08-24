package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.player.PlayerTickEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
        shift = At.Shift.BEFORE,
        ordinal = 0),
        cancellable = true)
    private void hookTickEvent(CallbackInfo ci) {
        var tickEvent = Client.EVENT_BUS.post(PlayerTickEvent.get());

        if (tickEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
