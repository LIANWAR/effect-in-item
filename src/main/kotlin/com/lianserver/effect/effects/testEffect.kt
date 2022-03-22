package com.lianserver.effect.effects

import com.lianserver.effect.interfaces.EffectInterface
import com.lianserver.effect.types.EffectMeta
import org.bukkit.ChatColor
import org.bukkit.Particle
import org.bukkit.entity.Player

class testEffect: EffectInterface {
    override val meta: EffectMeta = EffectMeta("test", "${ChatColor.GOLD}테스트 이펙트", listOf("테스트 이펙트입니다.", "푸틴 반동놈"))

    override fun effect(p: Player) {
        playerEffCheck(p)

        getInstance().playerEffTasks[p.uniqueId.toString()]!![meta.id]!!.add(
            getInstance().server.scheduler.scheduleSyncRepeatingTask(
                getInstance(),
                {
                    p.world.spawnParticle(Particle.ELECTRIC_SPARK, p.location, 10)
                },
                0L,
                2L
            )
        )
    }
}