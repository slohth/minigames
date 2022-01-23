package dev.slohth.minigames.game.types.oitc

import dev.slohth.minigames.Minigames
import dev.slohth.minigames.arena.Lobby
import dev.slohth.minigames.arena.types.OITCArena
import dev.slohth.minigames.game.Game
import dev.slohth.minigames.game.GameState
import dev.slohth.minigames.game.GameType
import dev.slohth.minigames.game.data.GameData
import dev.slohth.minigames.profile.Profile
import dev.slohth.minigames.utils.CC
import dev.slohth.minigames.utils.ItemBuilder
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

data class OITC(private val core: Minigames) : Game(core, GameType.ONE_IN_THE_CHAMBER, 2, 8) {

    private val players: MutableMap<Profile, GameData> = HashMap()

    private var countdown: BukkitTask? = null

    override fun addPlayer(profile: Profile): Boolean {
        if (players.size >= maxPlayers() || state() == GameState.ONGOING || players.containsKey(profile)) return false

        players[profile] = GameData()
        profile.player().gameMode = GameMode.ADVENTURE

        if (players.size == minPlayers() && state() == GameState.AWAITNG) initCountdown()

        broadcast("&3&l☞ &7${profile.player().name} has joined the queue")
        return true
    }

    override fun removePlayer(profile: Profile) {
        players.remove(profile)

        if (profile.player().isGlowing) profile.player().isGlowing = false
        profile.player().gameMode = GameMode.ADVENTURE
        profile.player().teleport(Lobby.location())
        profile.game(null)

        if (players.size < minPlayers() && state() == GameState.COUNTDOWN) {
            broadcast("&3&l☞ &7Not enough players to start")
            countdown?.cancel()
        } else if (players.size == 1 && state() == GameState.ONGOING) {
            end(*players.keys.toTypedArray())
        }

        if (players.isEmpty()) TODO("Unregistering game")
    }

    override fun playerCount(): Int {
        return players.size
    }

    private fun initCountdown() {
        state(GameState.COUNTDOWN)
        if (countdown != null && !countdown!!.isCancelled) countdown!!.cancel()

        countdown = object: BukkitRunnable() {
            var time: Int = 15
            override fun run() {
                if (time == 0) { start(); cancel(); return; }
                if (time == 15 || time <= 5)
                    broadcast("&3&l☞ &7Starting in $time ${ if (time == 1) "second..." else "seconds..." }")
                time--
            }
        }.runTaskTimer(core.plugin(), 20, 20)
    }

    override fun handleDeath(profile: Profile, killer: Profile?) {
        if (!players.containsKey(profile) || (killer != null && !players.containsKey(killer))) return

        players[profile]!!.killstreak(0)
        players[profile]!!.kills(players[profile]!!.kills() + 1)
        profile.player().inventory.clear()
        profile.player().gameMode = GameMode.SPECTATOR
        profile.player().sendTitle(CC.color("&cYou died!"), CC.color("&7Respawning"), 20, 20, 20)

        object: BukkitRunnable() {
            override fun run() {
                if (state() == GameState.ENDED) { cancel(); return; }
                handleInventory(profile)
                profile.player().gameMode = GameMode.ADVENTURE
                profile.player().teleport(arena().getSpawn(profile, players[profile]!!))
            }
        }
        if (killer == null) return

        val data: GameData = players[killer]!!
        val player: Player = killer.player()

        data.killstreak(data.killstreak() + 1)
        data.kills(data.kills() + 1)

        player.health = 20.0
        player.saturation = 20.0f
        player.inventory.addItem(ItemBuilder(Material.ARROW).build())

        player.playEffect(profile.player().location.add(0.0, 0.5, 0.0), Effect.MOBSPAWNER_FLAMES, 1)
        player.playSound(player.location, Sound.ENTITY_PLAYER_BIG_FALL, 100f, 0f)
        if (data.killstreak() % 5 == 0) player.world.strikeLightningEffect(player.location)

        broadcast("&3&l☞ &3${profile.player().name} &7was killed by &3${player.name} &7" +
                if (data.killstreak() == 1) "" else "(${ if (data.killstreak() % 5 == 0) "&b" else "" }${data.killstreak()}x&7)")

        if (data.kills() >= 20) end(killer)
    }

    private fun handleInventory(profile: Profile) {
        if (state() == GameState.ENDED) return
        profile.player().inventory.clear()
        profile.player().inventory.addItem(
            ItemBuilder(Material.WOODEN_SWORD).unbreakable(true).build(),
            ItemBuilder(Material.BOW).unbreakable(true).build(),
            ItemBuilder(Material.ARROW).build()
        )
        profile.player().health = 20.0
        profile.player().saturation = 20.0f
        profile.player().foodLevel = 20
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun end(vararg winners: Profile) {
        TODO("Not yet implemented")
    }

    private fun broadcast(vararg msg: String) {
        for (profile: Profile in players.keys) profile.msg(*msg)
    }

}