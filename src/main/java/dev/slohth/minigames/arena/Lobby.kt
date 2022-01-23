package dev.slohth.minigames.arena

import org.bukkit.Bukkit
import org.bukkit.Location

object Lobby {

    fun location(): Location {
        return Location(Bukkit.getWorld("world"), -46.5, 20.0, 237.5, -180f, 0f)
    }

}