package com.lianserver.effect.commands

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.lianserver.effect.interfaces.KommandInterface
import io.github.monun.kommand.Kommand.Companion.register
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class EffectMenuKommand: KommandInterface {
    override fun kommand() {
        register(getInstance(), "effects") {
            executes {
                val guiEffectList = ChestGui(4, "이펙트 목록")
                guiEffectList.setOnGlobalClick { e: InventoryClickEvent ->
                    e.isCancelled = true
                }
                val paneItem = PaginatedPane(1, 1, 7, 2)
                paneItem.populateWithGuiItems(
                    getInstance().userEffDB.getStringList(player.uniqueId.toString()).map {
                        val eff = getInstance().loadedEffects[it]!!

                        GuiItem(
                            namedItemStack(Material.ENCHANTED_BOOK, text(eff.meta.name).color(NamedTextColor.GOLD), eff.meta.description.map {
                                text(it).color(NamedTextColor.AQUA)
                            })
                        ){
                            val l = getInstance().playerEffectConfData.getStringList(player.uniqueId.toString())
                            if(!l.contains(eff.meta.id)){
                                l.add(eff.meta.id)
                                player.sendMessage(userText("${eff.meta.name}${ChatColor.WHITE} 효과를 켰습니다."))

                                getInstance().loadedEffects[eff.meta.id]?.effect(player)
                            }
                            else {
                                l.remove(eff.meta.id)
                                getInstance().playerEffTasks[player.uniqueId.toString()]!![eff.meta.id]!!.forEach {
                                    getInstance().server.scheduler.cancelTask(it)
                                }

                                player.sendMessage(userText("${eff.meta.name}${ChatColor.WHITE} 효과를 껐습니다."))
                            }

                            getInstance().playerEffectConfData.set(player.uniqueId.toString(), l)
                            it.isCancelled = true
                        }
                    }
                )
                val navigation = StaticPane(1, 3, 7, 1)

                val rw = ItemStack(Material.RED_WOOL)
                var meta = rw.itemMeta
                meta.displayName(text("이전").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                rw.itemMeta = meta

                navigation.addItem(
                    GuiItem(
                        rw
                    ) { event: InventoryClickEvent ->
                        event.isCancelled = true

                        if (paneItem.page > 0) {
                            paneItem.page = paneItem.page - 1
                            guiEffectList.update()
                        }
                    }, 0, 0
                )

                val gw = ItemStack(Material.GREEN_WOOL)
                meta = gw.itemMeta
                meta.displayName(Component.text("다음").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                gw.itemMeta = meta

                navigation.addItem(
                    GuiItem(
                        gw
                    ) { event: InventoryClickEvent ->
                        event.isCancelled = true

                        if (paneItem.page < paneItem.pages - 1) {
                            paneItem.page = paneItem.page + 1
                            guiEffectList.update()
                        }
                    }, 6, 0
                )

                val br = ItemStack(Material.BARRIER)
                meta = br.itemMeta
                meta.displayName(Component.text("닫기").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false))
                br.itemMeta = meta

                navigation.addItem(
                    GuiItem(
                        br
                    ) { event: InventoryClickEvent ->
                        event.isCancelled = true

                        event.whoClicked.closeInventory()
                    }, 3, 0
                )

                guiEffectList.addPane(navigation)
                val background = OutlinePane(0, 0, 9, 4)
                val stack = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
                meta = stack.itemMeta
                meta.displayName(text(""))
                stack.itemMeta = meta

                background.addItem(GuiItem(stack))
                background.setRepeat(true)
                background.priority = Pane.Priority.LOWEST
                background.setOnClick { event: InventoryClickEvent ->
                    event.isCancelled = true
                }
                guiEffectList.addPane(paneItem)
                guiEffectList.addPane(background)

                guiEffectList.update()
                guiEffectList.show(player)
            }
        }
    }

    init {}
}