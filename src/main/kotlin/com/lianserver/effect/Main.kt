/*
 * Copyright (c) 2022 AlphaGot
 *
 *  Licensed under the General Public License, Version 3.0. (https://opensource.org/licenses/gpl-3.0/)
 */

package com.lianserver.effect

import com.lianserver.effect.interfaces.EffectInterface
import com.lianserver.effect.interfaces.KommandInterface
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import java.io.File

/***
 * @author AlphaGot
 */

class Main : JavaPlugin() {

    companion object {
        lateinit var instance: Main
            private set
    }

    private val configFile = File(dataFolder, "config.yml")
    lateinit var config: YamlConfiguration
    lateinit var userEffDB: YamlConfiguration
    lateinit var loadedEffects: MutableMap<String, in EffectInterface>
    val playerEffects = mutableMapOf<String, String>()

    override fun onEnable() {
        instance = this
        loadedEffects = mutableMapOf()

        config = YamlConfiguration.loadConfiguration(configFile)
        userEffDB = YamlConfiguration.loadConfiguration(File(dataFolder, "db.yml"))

        var reflections = Reflections("com.lianserver.effect.command")

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
            server.pluginManager.registerEvents(inst, this)

            loadedEffects[inst.id] = inst
        }
    }

    override fun onDisable() {
        config.save(configFile)
        userEffDB.save(File(dataFolder, "db.yml"))
    }
}