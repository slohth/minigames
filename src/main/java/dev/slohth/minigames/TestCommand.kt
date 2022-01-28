package dev.slohth.minigames

import dev.slohth.minigames.utils.ItemStackSerializer
import dev.slohth.minigames.utils.TextComponentBuilder
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList

class TestCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, leabel: String, args: Array<String>): Boolean {

        val player: Player = sender as Player

//        if (args.size >= 2) {
//            val a: MutableList<String> = ArrayList()
//            for (i in 1 until args.size) a.add(args[i])
//            player.sendMessage(gradient(args[0], *a.toTypedArray()))
//        }

        if (args.isNotEmpty()) {

            val input: StringBuilder = StringBuilder();

            for (i in args.indices) {
                input.append(args[i]).append(if (i != (args.size - 1)) " " else "")
            }

            player.sendMessage(input.toString())
            player.inventory.addItem(ItemStackSerializer.deserialize(input.toString()))

        } else {

            val serialized: String = ItemStackSerializer.serialize(player.inventory.itemInMainHand)
            player.spigot().sendMessage(
                TextComponentBuilder("&aItem serialized! Click to copy.")
                    .hover(HoverEvent.Action.SHOW_TEXT, "Click to copy")
                    .click(ClickEvent.Action.SUGGEST_COMMAND, serialized).build())

        }

        return true
    }

    fun gradient(input: String, vararg hex: String): String {
        val sb: StringBuilder = StringBuilder()

        val colors: MutableList<Pair<Int, ChatColor>> = LinkedList()

        colors.add(Pair(0, ChatColor.of(hex[0])))
        for (i in 1 until (hex.size - 1)) {
            colors.add(Pair(1 + i * ((input.length - 2) / (hex.size - 1)), ChatColor.of(hex[i])))
        }
        colors.add(Pair(input.length - 1, ChatColor.of(hex[hex.size - 1])))

        for (i in 0 until colors.size - 1) {

            val firstIndex: Int = colors[i].first
            val first: ChatColor = colors[i].second

            val secondIndex: Int = colors[i + 1].first
            val second: ChatColor = colors[i + 1].second

            if (i == 0) sb.append(first).append(input[firstIndex])

            val length: Double = (secondIndex - firstIndex).toDouble()
            for (n in 1 until secondIndex - firstIndex) {
                val index: Int = firstIndex + n
                val percent: Double = n / length

                val red: Int = first.color.red + ((second.color.red - first.color.red) * percent).toInt()
                val green: Int = first.color.green + ((second.color.green - first.color.green) * percent).toInt()
                val blue: Int = first.color.blue + ((second.color.blue - first.color.blue) * percent).toInt()

                sb.append(ChatColor.of(Color(red, green, blue))).append(input[index])
            }

            sb.append(second).append(input[secondIndex])
        }

        return sb.toString()
    }

}