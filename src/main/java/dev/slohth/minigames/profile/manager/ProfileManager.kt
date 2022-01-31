package dev.slohth.minigames.profile.manager

import dev.slohth.minigames.Minigames
import dev.slohth.minigames.arena.Lobby
import dev.slohth.minigames.game.GameState
import dev.slohth.minigames.profile.Profile
import dev.slohth.minigames.utils.CC
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
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
        Lobby.team().addEntry(e.player.name)
        Lobby.spawnPlayer(e.player)
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
        e.format = when (e.player.uniqueId) {
            //UUID.fromString("09c34f63-faca-4963-ac47-14ee71391e38") -> CC.color("&c${e.player.name}: &7${e.message}")
            else -> CC.color("&9${e.player.name}: &7${e.message}")
        }
    }

    @EventHandler
    fun onRightClickEntity(e: PlayerInteractAtEntityEvent) {
        if (e.rightClicked !is Player) return
        val clicked: Profile = core.profileManager().profile((e.rightClicked as Player).uniqueId) ?: return
        val profile: Profile = core.profileManager().profile(e.player.uniqueId) ?: return
        if ((!clicked.inGame() || clicked.data()!!.game().state() != GameState.ONGOING) &&
            (!profile.inGame() || profile.data()!!.game().state() != GameState.ONGOING)) {
            clicked.player().addPassenger(profile.player())
        }
    }

}