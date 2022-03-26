package com.lianserver.effect.effects

import com.lianserver.effect.interfaces.EffectInterface
import com.lianserver.effect.types.EffectMeta
import org.bukkit.ChatColor
import org.bukkit.Particle
import org.bukkit.entity.Player

class WalkEffect220326: EffectInterface {
    override val meta: EffectMeta = EffectMeta("walking_220326", "${ChatColor.GRAY}발자취 1", listOf("그냥 걸어보세요!"))

    override fun effect(p: Player) {
        playerEffCheck(p)

        getInstance().playerEffTasks[p.uniqueId.toString()]!![meta.id]!!.add(
            getInstance().server.scheduler.scheduleSyncRepeatingTask(
                getInstance(),
                {
                    p.world.spawnParticle(Particle.CLOUD, p.location, 10)
                },
                0L,
                2L
            )
        )
    }
}