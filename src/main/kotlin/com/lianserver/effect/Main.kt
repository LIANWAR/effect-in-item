/*
 * Copyright (c) 2022 AlphaGot
 *
 *  Licensed under the General Public License, Version 3.0. (https://opensource.org/licenses/gpl-3.0/)
 */

package com.lianserver.effect

import com.lianserver.effect.interfaces.EffectInterface
import com.lianserver.effect.interfaces.KommandInterface
import com.lianserver.effect.interfaces.PrefixedTextInterface
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import java.io.File

/***
 * @author AlphaGot
 */

class Main : JavaPlugin(), Listener, PrefixedTextInterface {

    companion object {
        lateinit var instance: Main
            private set
    }

    lateinit var userEffDB: YamlConfiguration
    lateinit var loadedEffects: MutableMap<String, EffectInterface>
    lateinit var playerEffectConfData: YamlConfiguration
    var playerEffTasks: MutableMap<String, MutableMap<String, MutableList<Int>>> = mutableMapOf()

    override fun onEnable() {
        instance = this
        loadedEffects = mutableMapOf()
        userEffDB = YamlConfiguration.loadConfiguration(File(dataFolder, "db.yml"))
        playerEffectConfData = YamlConfiguration.loadConfiguration(File(dataFolder, "pldb.yml"))

        var reflections = Reflections("com.lianserver.effect.commands")

        reflections.getSubTypesOf(
            KommandInterface::class.java
        )?.forEach { clazz ->
            logger.info(clazz.name)

            clazz.getDeclaredConstructor().trySetAccessible()
            clazz.getDeclaredConstructor().newInstance().kommand()
        }

        reflections = Reflections("com.lianserver.effect.effects")

        reflections.getSubTypesOf(
            EffectInterface::class.java
        )?.forEach { clazz ->
            logger.info(clazz.name)

            clazz.getDeclaredConstructor().trySetAccessible()

            val inst = clazz.getDeclaredConstructor().newInstance()
            if(clazz.interfaces.any { it.simpleName == "Listener" }) server.pluginManager.registerEvents(inst, this)
            else println(clazz.interfaces)

            loadedEffects[inst.meta.id] = inst
        }

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        userEffDB.save(File(dataFolder, "db.yml"))
        playerEffectConfData.save(File(dataFolder, "pldb.yml"))
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        if(playerEffectConfData.getStringList(e.player.uniqueId.toString()).isNotEmpty()){
            playerEffectConfData.getStringList(e.player.uniqueId.toString()).forEach {
                loadedEffects[it]!!.effect(e.player)
            }
        }
    }

    @EventHandler
    fun onUseEffectBook(e: PlayerInteractEvent){
        if(e.hasItem()){
            if(e.item!!.type == Material.ENCHANTED_BOOK){
                if(e.item!!.hasItemMeta()){
                    if(e.item!!.itemMeta.hasLore()){
                        val c = PlainTextComponentSerializer.plainText().serialize(e.item!!.itemMeta.lore()!![0]!!)

                        if(c.contains("(id=")){
                            val id = c.split("=")[1].replace(")", "")

                            if(userEffDB.isSet(e.player.uniqueId.toString())){
                                val uu = userEffDB.getStringList(e.player.uniqueId.toString())

                                if(uu.contains(id)) {
                                    e.player.sendMessage(userText("${loadedEffects[id]!!.meta.name}${ChatColor.WHITE} 이펙트가 이미 있습니다."))
                                    return
                                }

                                uu.add(id)

                                userEffDB.set(e.player.uniqueId.toString(), uu)
                            }
                            else {
                                userEffDB.set(e.player.uniqueId.toString(), listOf(id))
                            }

                            e.player.sendMessage(userText("${loadedEffects[id]!!.meta.name}${ChatColor.WHITE} 이펙트를 획득했습니다."))
                            e.item!!.subtract(e.item!!.amount)
                        }
                    }
                }
            }
        }
    }
}