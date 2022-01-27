package dev.slohth.minigames.utils.framework.assemble.adapter

import dev.slohth.minigames.game.GameState
import dev.slohth.minigames.game.GameType
import dev.slohth.minigames.game.types.oitc.OITC
import dev.slohth.minigames.plugin.MinigamesPlugin
import dev.slohth.minigames.utils.framework.assemble.AssembleAdapter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ScoreboardAdapter : AssembleAdapter {
    override fun getTitle(player: Player?): String {
        val core = JavaPlugin.getPlugin(MinigamesPlugin::class.java).core()
        val profile = core.profileManager().profile(player!!.uniqueId)
        if (profile?.data() == null) return "null"
        when (profile.data()!!.game().type()) {
            GameType.ONE_IN_THE_CHAMBER -> {
                return if (profile.data()!!.game().state() !== GameState.ONGOING) "null" else "&3&lOITC"
            }
        }
        return "null"
    }

    override fun getLines(player: Player): List<String?>? {
        val core = JavaPlugin.getPlugin(MinigamesPlugin::class.java).core()
        val profile = core.profileManager().profile(player.uniqueId)
        if (profile?.data() == null) return null
        val lines: MutableList<String?> = ArrayList()
        when (profile.data()!!.game().type()) {
            GameType.ONE_IN_THE_CHAMBER -> {
                if (profile.data()!!.game().state() !== GameState.ONGOING) return null
                val game = profile.data()!!.game() as OITC
                val kills = game.topKillers()
                for (killer in kills) lines.add(
                    "&7" + killer.player().name + ": &3" + killer.data()!!
                        .kills()
                )
            }
        }
        return lines
    }
}