package dev.slohth.minigames.profile.manager

import dev.slohth.minigames.Minigames
import dev.slohth.minigames.profile.Profile
import dev.slohth.minigames.utils.CC
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
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
        val profile: Profile = profiles[e.player.uniqueId] ?: return
        if (profile.data() != null) profile.data()!!.game().removePlayer(profile)
        profiles.remove(e.player.uniqueId)
    }


    /////////////////////////////////////////////////////////////

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        e.message = CC.color(e.message)
        e.format = CC.color("&9${e.player.name}: &7${e.message}")
    }

}