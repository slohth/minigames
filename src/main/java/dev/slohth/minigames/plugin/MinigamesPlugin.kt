package dev.slohth.minigames.plugin

import dev.slohth.minigames.Minigames
import org.bukkit.plugin.java.JavaPlugin

class MinigamesPlugin : JavaPlugin() {

    private lateinit var core: Minigames

    override fun onEnable() {
        core = Minigames(this)
    }

    fun core(): Minigames { return core }

}