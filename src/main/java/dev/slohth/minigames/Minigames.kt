package dev.slohth.minigames

import dev.slohth.minigames.arena.manager.ArenaManager
import dev.slohth.minigames.game.manager.GameManager
import org.bukkit.plugin.java.JavaPlugin

class Minigames(private val plugin: JavaPlugin) {

    private val arenaManager: ArenaManager = ArenaManager(this)
    private val gameManager: GameManager = GameManager(this)

    init {
        plugin.getCommand("test")!!.setExecutor(TestCommand())
    }

    fun plugin(): JavaPlugin { return plugin }

    fun arenaManager(): ArenaManager { return arenaManager }
    fun gameManager(): GameManager { return gameManager }

}