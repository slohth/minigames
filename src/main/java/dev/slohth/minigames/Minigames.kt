package dev.slohth.minigames

import dev.slohth.minigames.arena.manager.ArenaManager
import dev.slohth.minigames.game.manager.GameManager
import dev.slohth.minigames.profile.manager.ProfileManager
import org.bukkit.plugin.java.JavaPlugin

class Minigames(private val plugin: JavaPlugin) {

    private val arenaManager: ArenaManager = ArenaManager(this)
    private val gameManager: GameManager = GameManager(this)
    private val profileManager: ProfileManager = ProfileManager(this)

    init {
        plugin.getCommand("test")!!.setExecutor(TestCommand())
    }

    fun plugin(): JavaPlugin { return plugin }

    fun arenaManager(): ArenaManager { return arenaManager }
    fun gameManager(): GameManager { return gameManager }
    fun profileManager(): ProfileManager { return profileManager }

}