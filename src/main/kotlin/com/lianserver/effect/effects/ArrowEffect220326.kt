package com.lianserver.effect.effects

import com.lianserver.effect.interfaces.EffectInterface
import com.lianserver.effect.types.EffectMeta
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.projectiles.ProjectileSource

class ArrowEffect220326: EffectInterface, Listener {
    override val meta: EffectMeta = EffectMeta("arrow_220326", "${ChatColor.GRAY}화살 1", listOf("삐슝빠슝뿌슝", "분광 화살에는 적용되지 않습니다."))

    private val arrowEffTMap: MutableMap<String, Int> = mutableMapOf()

    override fun effect(p: Player) {
        return
    }

    @EventHandler
    fun onArrowLaunch(e: ProjectileLaunchEvent){
        if(e.entityType == EntityType.ARROW){
            if(e.entity.shooter!! is Player){
                playerEffCheck(e.entity.shooter!! as Player)

                val ent = e.entity.shooter!! as Player

                if(getInstance().userEffDB.getStringList(ent.uniqueId.toString()).contains(this.meta.id)){
                    if(getInstance().playerEffectConfData.getStringList(ent.uniqueId.toString()).contains(this.meta.id)){
                        arrowEffTMap[e.entity.uniqueId.toString()] = getInstance().server.scheduler.scheduleSyncRepeatingTask(
                            getInstance(),
                            {
                                e.entity.world.spawnParticle(Particle.DUST_COLOR_TRANSITION, e.entity.location, 4, Particle.DustTransition(
                                    Color.RED,
                                    Color.NAVY,
                                    1f
                                ))
                            },
                            0L,
                            2L
                        )
                    }
                }
            }
        }
    }

    @EventHandler
    fun onArrowCollides(e: ProjectileHitEvent){
        if(e.entityType == EntityType.ARROW){
            if(arrowEffTMap.containsKey(e.entity.uniqueId.toString())){
                getInstance().server.scheduler.cancelTask(arrowEffTMap[e.entity.uniqueId.toString()]!!)
            }
        }
    }
}