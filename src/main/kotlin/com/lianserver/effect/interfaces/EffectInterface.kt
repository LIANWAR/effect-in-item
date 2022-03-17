package com.lianserver.effect.interfaces

import com.lianserver.effect.Main
import org.bukkit.event.Listener

interface EffectInterface: Listener {
    val id: String
    val name: String
    val description: List<String>

    fun getInstance(): Main = Main.instance
}