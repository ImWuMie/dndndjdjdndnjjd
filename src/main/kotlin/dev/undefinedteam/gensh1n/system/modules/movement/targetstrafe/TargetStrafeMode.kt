package dev.undefinedteam.gensh1n.system.modules.movement.targetstrafe

import dev.undefinedteam.gensh1n.Client
import dev.undefinedteam.gensh1n.settings.SettingGroup
import dev.undefinedteam.gensh1n.system.SettingAdapter
import net.minecraft.client.MinecraftClient

open class TargetStrafeMode(val name: String) : SettingAdapter {
    val mc: MinecraftClient = Client.mc;

    val sGroup = SettingGroup(name, false);

    open fun onActivate() {}
    open fun onDeactivate() {}
}
