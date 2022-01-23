package dev.slohth.minigames.profile

import dev.slohth.minigames.game.data.GameData
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.lang.StringBuilder
import java.util.*

data class Profile(private val id: UUID) {

    private var data: GameData? = null

    fun msg(vararg messages: String) {
        val builder: StringBuilder = StringBuilder()
        for (m: String in  messages) builder.append(ChatColor.translateAlternateColorCodes('&', m)).append("\n")
        player().sendMessage(builder.toString())
    }

    fun player(): Player {
        return Bukkit.getPlayer(id)!!
    }

    fun id(): UUID { return id }

    fun data(): GameData? { return data }
    fun data(data: GameData?) { this.data = data }

}