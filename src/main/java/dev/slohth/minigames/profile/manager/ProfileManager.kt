package dev.slohth.minigames.profile.manager

import dev.slohth.minigames.Minigames
import dev.slohth.minigames.profile.Profile
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.collections.HashMap

class ProfileManager(private val core: Minigames)  : Listener {

    private val profiles: MutableMap<UUID, Profile> = HashMap()

    init {
        Bukkit.getPluginManager().registerEvents(this, core.plugin())
    }

    fun profile(uuid: UUID): Profile? { return profiles[uuid] }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        profiles[e.player.uniqueId] = Profile(e.player.uniqueId)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        profiles.remove(e.player.uniqueId)
    }

}