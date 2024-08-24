package dev.undefinedteam.gensh1n.system.modules.movement
import dev.undefinedteam.gensh1n.system.modules.Categories
import dev.undefinedteam.gensh1n.system.modules.Module
import dev.undefinedteam.gensh1n.events.player.PlayerTickEvent
import meteordevelopment.orbit.EventHandler
// This class was coded by Yurnu, some may be shitty :(
class Sprint : Module(Categories.Movement, "sprint", "Keep sprint")
{
    val sg = settings.defaultGroup
    val onGround = bool(sg, "only-on-ground", false)

    fun setPlayerSprinting(isSprint: Boolean){  //给Wumie看的: Unit就相当于void
        mc.player?.isSprinting = isSprint //? 就是空安全，没有nullpointerexception
    }
    @EventHandler
    fun eventPlayerTick(e: PlayerTickEvent){
        if (mc.player?.isSprinting == false){
            when {
                mc.player?.isOnGround == true && onGround.get() -> setPlayerSprinting(true)
                !onGround.get() -> setPlayerSprinting(true)
                // 由于空安全限制，必须得写 == true，不是shit code
            }
        }
    }
}
