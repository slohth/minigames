package dev.slohth.minigames.utils

import org.bukkit.ChatColor

object CC {
    fun color(msg: String): String {
        return ChatColor.translateAlternateColorCodes('&', msg)
    }

    fun color(lines: List<String?>): List<String> {
        val toReturn: MutableList<String> = ArrayList()
        for (line in lines) if (line != null) toReturn.add(ChatColor.translateAlternateColorCodes('&', line))
        return toReturn
    }

    fun color(lines: Array<String?>): List<String> {
        val toReturn: MutableList<String> = ArrayList()
        for (line in lines) if (line != null) toReturn.add(ChatColor.translateAlternateColorCodes('&', line))
        return toReturn
    }

    fun getLastColor(msg: String): ChatColor {
        var color: ChatColor = ChatColor.WHITE
        if (msg.isEmpty()) return color
        for (i in 0..msg.length) {
            if ((i + 1) < msg.length && msg[i] == ChatColor.COLOR_CHAR) color = ChatColor.getByChar(msg[i+1])!!
        }
        return color
    }
}