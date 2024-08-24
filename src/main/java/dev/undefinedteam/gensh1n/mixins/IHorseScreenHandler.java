package dev.undefinedteam.gensh1n.mixins;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.screen.HorseScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HorseScreenHandler.class)
public interface IHorseScreenHandler {
    @Accessor("entity")
    AbstractHorseEntity getEntity();
}
