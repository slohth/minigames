package dev.slohth.minigames

import dev.slohth.minigames.arena.manager.ArenaManager
import dev.slohth.minigames.game.manager.GameManager
import dev.slohth.minigames.profile.manager.ProfileManager
import dev.slohth.minigames.utils.framework.assemble.Assemble
import dev.slohth.minigames.utils.framework.assemble.adapter.ScoreboardAdapter
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Minigames(private val plugin: JavaPlugin) {

    private lateinit var assemble: Assemble

    private val arenaManager: ArenaManager = ArenaManager(this)
    private val gameManager: GameManager = GameManager(this)
    private val profileManager: ProfileManager = ProfileManager(this)

    init {
        plugin.getCommand("test")!!.setExecutor(TestCommand())
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { assemble = Assemble(plugin, ScoreboardAdapter()) }, 1)
    }

    fun plugin(): JavaPlugin { return plugin }

    fun arenaManager(): ArenaManager { return arenaManager }
    fun gameManager(): GameManager { return gameManager }
    fun profileManager(): ProfileManager { return profileManager }

}