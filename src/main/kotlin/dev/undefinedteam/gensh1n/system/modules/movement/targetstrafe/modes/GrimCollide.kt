package dev.undefinedteam.gensh1n.system.modules.movement.targetstrafe.modes

import dev.undefinedteam.gensh1n.events.player.PlayerTickEvent
import dev.undefinedteam.gensh1n.rotate.Rotations
import dev.undefinedteam.gensh1n.system.modules.movement.targetstrafe.TargetStrafeMode
import meteordevelopment.orbit.EventHandler
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.sin

class GrimCollide : TargetStrafeMode("Grim") {
    private val speed = doubleN(sGroup,"speed",0.08,0.01,0.08)
    private val behindBlocks = doubleN(sGroup,"behind-blks",0.3,0.00,2.0)

    @EventHandler
    private fun eventPlayerTick(e: PlayerTickEvent) {
        if (mc.player!!.input.movementForward == 0.0f && mc.player!!.input.movementSideways == 0.0f) return
        var closestEntity: Entity? = null

        var collisions = 0
        val box: Box = mc.player!!.getBoundingBox().expand(1.0)
        for (entity in mc.world!!.getEntities()) {
            val entityBox = entity.boundingBox
            if (canCauseSpeed(entity) && box.intersects(entityBox)) {
                collisions++
                closestEntity = entity
            }
        }

        var yaw = Math.toRadians(mc.player!!.yaw.toDouble())

        // Grim gives 0.08 leniency per entity which is customizable by speed.
        if (closestEntity != null) {
            if (behindBlocks.get() == 0.0)
                yaw = Math.toRadians(Rotations.getYaw(closestEntity))
            else {
                val forward = Vec3d.fromPolar(0f, closestEntity.yaw).normalize()
                val target = Vec3d(closestEntity.x + forward.x * behindBlocks.get(), closestEntity.y, closestEntity.z + forward.z * behindBlocks.get());
                yaw = Math.toRadians(Rotations.getYaw(target))
            }
        }
        val boost: Double = this.speed.get() * collisions
        mc.player!!.addVelocity(-sin(yaw) * boost, 0.0, cos(yaw) * boost)
    }

    private fun canCauseSpeed(entity: Entity): Boolean {
        return entity !== mc.player && entity is LivingEntity && entity !is ArmorStandEntity
    }
}
