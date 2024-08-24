package dev.undefinedteam.gensh1n.system.modules.movement.targetstrafe

import dev.undefinedteam.gensh1n.system.modules.Categories
import dev.undefinedteam.gensh1n.system.modules.Module
import dev.undefinedteam.gensh1n.system.modules.movement.targetstrafe.modes.GrimCollide

class TargetStrafe : Module(Categories.Movement, "target-strafe", "Strafe towards a target") {
    private val sgDefault = settings.defaultGroup
    private val mode = choice(sgDefault, "mode", Modes.GrimSpeed)

    enum class Modes(val obj: TargetStrafeMode) {
        GrimSpeed(GrimCollide())
    }

    init {
        Modes.entries.forEach {
            mode.registerChild(it, it.obj.sGroup)
        }
    }

    private var subscribed = false

    override fun onSubscribe() {
        if (!subscribed) {
            subscribed = true
            EVENT_BUS.subscribe(mode.get().obj)
        }
    }

    override fun onUnSubscribe() {
        if (subscribed) {
            subscribed = false
            EVENT_BUS.unsubscribe(mode.get().obj)
        }
    }
}
