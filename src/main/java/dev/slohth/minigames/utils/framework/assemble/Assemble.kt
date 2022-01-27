package dev.slohth.minigames.utils.framework.assemble

import dev.slohth.minigames.utils.framework.assemble.events.AssembleBoardCreateEvent
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Assemble(plugin: JavaPlugin?, adapter: AssembleAdapter) {
    val plugin: JavaPlugin
    val adapter: AssembleAdapter
    val boards: MutableMap<UUID?, AssembleBoard?>
    var thread: AssembleThread? = null
        private set
    var listeners: AssembleListener? = null
        private set
    val ticks: Long = 2
    val isHook = false
    val assembleStyle = AssembleStyle.MODERN
    val isDebugMode = true
    fun setup() {
        listeners = AssembleListener(this)
        plugin.server.pluginManager.registerEvents(listeners!!, plugin)
        if (thread != null) {
            thread!!.stop()
            thread = null
        }
        for (player in Bukkit.getServer().onlinePlayers) {
            val createEvent = AssembleBoardCreateEvent(player)
            Bukkit.getServer().pluginManager.callEvent(createEvent)
            if (createEvent.isCancelled) return
            boards.putIfAbsent(player.uniqueId, AssembleBoard(player, this))
        }
        thread = AssembleThread(this)
    }

    fun cleanup() {
        if (thread != null) {
            thread!!.stop()
            thread = null
        }
        if (listeners != null) {
            HandlerList.unregisterAll(listeners!!)
            listeners = null
        }
        for (uuid in boards.keys) {
            val player = Bukkit.getServer().getPlayer(uuid!!)
            if (player == null || !player.isOnline) {
                continue
            }
            boards.remove(uuid)
            player.scoreboard = Bukkit.getServer().scoreboardManager!!.newScoreboard
        }
    }

    init {
        if (plugin == null) {
            throw RuntimeException("Assemble can not be instantiated without a plugin instance!")
        }
        this.plugin = plugin
        this.adapter = adapter
        boards = ConcurrentHashMap()
        setup()
    }
}