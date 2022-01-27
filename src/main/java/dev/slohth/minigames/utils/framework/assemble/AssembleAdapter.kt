package dev.slohth.minigames.utils.framework.assemble

import org.bukkit.entity.Player

interface AssembleAdapter {
    fun getTitle(player: Player?): String?
    fun getLines(player: Player): List<String?>?
}