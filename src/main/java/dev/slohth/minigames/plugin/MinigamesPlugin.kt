package dev.slohth.minigames.plugin

import dev.slohth.minigames.Minigames
import org.bukkit.plugin.java.JavaPlugin

class MinigamesPlugin : JavaPlugin() {

    override fun onEnable() {
        Minigames(this)
    }

}