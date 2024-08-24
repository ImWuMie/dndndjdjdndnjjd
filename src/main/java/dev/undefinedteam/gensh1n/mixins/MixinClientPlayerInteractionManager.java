package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.mixin_interface.IClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {
    @Shadow
    protected abstract void syncSelectedSlot();

    @Override
    public void gensh1n$syncSelected() {
        syncSelectedSlot();
    }
}
