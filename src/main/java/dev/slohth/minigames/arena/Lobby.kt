package dev.slohth.minigames.arena

import dev.slohth.minigames.utils.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Lobby {

    private fun location(): Location {
        return Location(Bukkit.getWorld("world"), 90.5, 21.0, 368.5, -180f, 0f)
    }

    fun spawnPlayer(player: Player) {
        player.exp = 0f
        player.level = 0
        player.health = 20.0
        player.foodLevel = 20
        player.activePotionEffects.clear()
        player.gameMode = GameMode.ADVENTURE
        for (effect: PotionEffect in player.activePotionEffects) player.removePotionEffect(effect.type)
        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, false, false))
        player.inventory.helmet = ItemBuilder(Material.GLASS).name("Space Helmet").build()
        player.teleport(location())
    }

}