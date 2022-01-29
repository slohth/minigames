package dev.slohth.minigames

import dev.slohth.minigames.profile.Profile
import dev.slohth.minigames.utils.framework.command.Command
import dev.slohth.minigames.utils.framework.command.CommandArgs
import dev.slohth.minigames.utils.framework.command.ICommand
import net.md_5.bungee.api.ChatColor
import java.awt.Color
import java.util.*

class TestCommand(private val core: Minigames) : ICommand {

    @Command(name = "leave", inGameOnly = true)
    fun leave(args: CommandArgs) {
        val profile: Profile = core.profileManager().profile(args.getPlayer()!!.uniqueId) ?: return
        profile.data().let { profile.data()!!.game().removePlayer(profile) }
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