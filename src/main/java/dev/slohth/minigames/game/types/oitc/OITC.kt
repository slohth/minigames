package dev.slohth.minigames.game.types.oitc

import dev.slohth.minigames.Minigames
import dev.slohth.minigames.arena.Lobby
import dev.slohth.minigames.game.Game
import dev.slohth.minigames.game.GameListener
import dev.slohth.minigames.game.GameState
import dev.slohth.minigames.game.GameType
import dev.slohth.minigames.game.data.GameData
import dev.slohth.minigames.game.data.GameDataStatistic
import dev.slohth.minigames.game.data.comparator.GameDataComparator
import dev.slohth.minigames.profile.Profile
import dev.slohth.minigames.utils.CC
import dev.slohth.minigames.utils.ItemBuilder
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*


data class OITC(private val core: Minigames) : Game(core, GameType.ONE_IN_THE_CHAMBER, 2, 8) {

    private val players: MutableSet<Profile> = HashSet()
    private val listener: OITCListener = OITCListener(core, this)

    private var countdown: BukkitTask? = null

    override fun addPlayer(profile: Profile): Boolean {
        if (players.size >= maxPlayers() || state() == GameState.ONGOING || players.contains(profile)) return false

        players.add(profile)
        profile.data(GameData(this))

        profile.player().gameMode = GameMode.ADVENTURE
        profile.player().exp = 0F
        profile.player().level = 0

        if (players.size == minPlayers() && state() == GameState.AWAITNG) initCountdown()

        broadcast("&3&l☞ &7${profile.player().name} has joined the queue")

        updateSign()
        return true
    }

    override fun removePlayer(profile: Profile) {
        players.remove(profile)

        if (profile.player().isGlowing) profile.player().isGlowing = false
        profile.data(null)
        profile.player().inventory.clear()
        Lobby.spawnPlayer(profile.player())

        if (players.size < minPlayers() && state() == GameState.COUNTDOWN) {
            broadcast("&3&l☞ &7Not enough players to start")
            countdown?.cancel()
        } else if (players.size == 1 && state() == GameState.ONGOING) {
            end(*players.toTypedArray())
        }

        updateSign()
        if (players.isEmpty()) core.gameManager().unregister(this)
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
        if (!players.contains(profile) || (killer != null && !players.contains(killer))) return

        profile.data()!!.killstreak(0)
        profile.data()!!.deaths(profile.data()!!.deaths() + 1)
        profile.player().inventory.clear()
        profile.player().gameMode = GameMode.SPECTATOR
        profile.player().sendTitle(CC.color("&cYou died!"), CC.color("&7Respawning"), 20, 20, 20)

        object: BukkitRunnable() {
            override fun run() {
                if (state() == GameState.ENDED) { cancel(); return; }
                handleInventory(profile)
                profile.player().gameMode = GameMode.ADVENTURE
                profile.player().teleport(arena().getSpawn(profile, profile.data()!!))
            }
        }.runTaskLater(core.plugin(), 100)
        if (killer == null) return

        val data: GameData = killer.data()!!
        val player: Player = killer.player()

        data.killstreak(data.killstreak() + 1)
        data.kills(data.kills() + 1)
        player.giveExpLevels(1)

        player.health = 20.0
        player.saturation = 20.0f
        player.inventory.addItem(ItemBuilder(Material.ARROW).build())

        player.playEffect(profile.player().location.add(0.0, 0.5, 0.0), Effect.MOBSPAWNER_FLAMES, 1)
        player.playSound(player.location, Sound.ENTITY_PLAYER_BIG_FALL, 100f, 0f)
        if (data.killstreak() % 5 == 0) player.world.strikeLightningEffect(player.location)

        broadcast("&3&l☞ &3${profile.player().name} &7was killed by &3${player.name} " +
                "&7${if (data.killstreak() == 1) "" else "(${ if (data.killstreak() % 5 == 0) "&b" else "" }${data.killstreak()}x&7)"}")

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
        state(GameState.ONGOING)
        for (profile: Profile in players) {
            profile.player().teleport(arena().getSpawn(profile, profile.data()!!))
            for (effect: PotionEffect in profile.player().activePotionEffects) profile.player().removePotionEffect(effect.type)
            handleInventory(profile)
        }
    }

    override fun end(vararg winners: Profile) {
        state(GameState.ENDED)
        val message: MutableList<String> = ArrayList(listOf("&7Top killers this game:"))

        var index = 1
        for (profile: Profile in topKillers()) {
            message.add("&3${index++}: &7${profile.player().name} - ${profile.data()!!.kills()} " +
                    if (profile.data()!!.kills() == 1) "kill" else "kills")
        }
        message.add(" ")

        for (profile: Profile in players) {
            if (profile != winners[0]) profile.player().gameMode = GameMode.SPECTATOR
            profile.player().sendTitle(ChatColor.GOLD.toString() + winners[0].player().name, "Won the game!", 20, 20, 20)

            Bukkit.getScheduler().runTaskLater(core.plugin(),
                Runnable { profile.msg(" ", "&6" + winners[0].player().name + " has won the game!", " ") }, 10)
            Bukkit.getScheduler().runTaskLater(core.plugin(), Runnable { profile.msg(*CC.color(message).toTypedArray()) }, 20)
            Bukkit.getScheduler().runTaskLater(core.plugin(), Runnable { removePlayer(profile) }, 100)
        }

        winners[0].player().inventory.helmet = ItemBuilder(Material.GOLDEN_HELMET).enchantment(Enchantment.DURABILITY).build()
        winners[0].player().isGlowing = true
    }

    override fun listener(): GameListener {
        return listener
    }

    fun topKillers(): LinkedList<Profile> {
        val killers: LinkedList<Profile> = LinkedList(players)
        Collections.sort(killers, GameDataComparator(GameDataStatistic.KILLS))
        killers.reverse()
        return killers
    }

    private fun broadcast(vararg msg: String) {
        for (profile: Profile in players) profile.msg(*msg)
    }

}