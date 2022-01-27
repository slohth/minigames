package dev.slohth.minigames.utils.framework.assemble

import dev.slohth.minigames.utils.framework.assemble.events.AssembleBoardCreatedEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*

class AssembleBoard(player: Player, assemble: Assemble) {
    val entries: MutableList<AssembleBoardEntry?> = ArrayList()
    val identifiers: MutableList<String?> = ArrayList()
    val uuid: UUID
    val assemble: Assemble
    val scoreboard: Scoreboard
        get() {
            val player = Bukkit.getServer().getPlayer(uuid)
            return if (assemble.isHook || player!!.scoreboard !== Bukkit.getServer().scoreboardManager!!
                    .mainScoreboard
            ) player!!.scoreboard else Bukkit.getServer().scoreboardManager!!.newScoreboard
        }
    val objective: Objective?
        get() {
            val scoreboard = scoreboard
            if (scoreboard.getObjective("Assemble") == null) {
                val objective = scoreboard.registerNewObjective("Assemble", "dummy")
                objective.displaySlot = DisplaySlot.SIDEBAR
                objective.displayName = assemble.adapter.getTitle(Bukkit.getServer().getPlayer(uuid))!!
                return objective
            }
            return scoreboard.getObjective("Assemble")
        }

    private fun setup(player: Player) {
        val scoreboard = scoreboard
        player.scoreboard = scoreboard
        objective
        val createdEvent = AssembleBoardCreatedEvent(this)
        Bukkit.getServer().pluginManager.callEvent(createdEvent)
    }

    fun getEntryAtPosition(pos: Int): AssembleBoardEntry? {
        return if (pos >= entries.size) {
            null
        } else {
            entries[pos]
        }
    }

    fun getUniqueIdentifier(position: Int): String {
        var identifier = getRandomChatColor(position) + ChatColor.WHITE
        while (identifiers.contains(identifier)) identifier =
            identifier + getRandomChatColor(position) + ChatColor.WHITE
        if (identifier.length > 16) return getUniqueIdentifier(position)
        identifiers.add(identifier)
        return identifier
    }

    companion object {
        private fun getRandomChatColor(position: Int): String {
            return ChatColor.values()[position].toString()
        }
    }

    init {
        uuid = player.uniqueId
        this.assemble = assemble
        setup(player)
    }
}