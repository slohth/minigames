package dev.slohth.minigames.utils.framework.assemble

import dev.slohth.minigames.utils.framework.assemble.events.AssembleBoardCreateEvent
import dev.slohth.minigames.utils.framework.assemble.events.AssembleBoardDestroyEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class AssembleListener(private val assemble: Assemble) : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val createEvent = AssembleBoardCreateEvent(e.player)
        Bukkit.getServer().pluginManager.callEvent(createEvent)
        if (createEvent.isCancelled) return
        assemble.boards[e.player.uniqueId] = AssembleBoard(e.player, assemble)
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val destroyEvent = AssembleBoardDestroyEvent(e.player)
        Bukkit.getServer().pluginManager.callEvent(destroyEvent)
        if (destroyEvent.isCancelled) return
        assemble.boards.remove(e.player.uniqueId)
        e.player.scoreboard = Bukkit.getServer().scoreboardManager!!.mainScoreboard
    }
}