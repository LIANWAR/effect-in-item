package com.lianserver.effect.interfaces

import com.lianserver.effect.Main
import com.lianserver.effect.types.EffectMeta
import org.bukkit.entity.Player
import org.bukkit.event.Listener

interface EffectInterface: Listener {
    val meta: EffectMeta

    fun getInstance(): Main = Main.instance

    fun effect(p: Player)
    fun playerEffCheck(p: Player){
        if(!getInstance().playerEffTasks.containsKey(p.uniqueId.toString())) getInstance().playerEffTasks[p.uniqueId.toString()] = mutableMapOf(this.meta.id to mutableListOf())
    }
}