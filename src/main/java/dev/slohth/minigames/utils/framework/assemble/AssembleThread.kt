package dev.slohth.minigames.utils.framework.assemble

import org.bukkit.ChatColor
import java.util.*
import java.util.function.Consumer

class AssembleThread internal constructor(private val assemble: Assemble) : Thread() {
    override fun run() {
        while (true) {
            try {
                tick()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                sleep(assemble.ticks * 50)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun tick() {
        for (player in assemble.plugin.server.onlinePlayers) {
            try {
                val board = assemble.boards[player.uniqueId] ?: continue
                val scoreboard = board.scoreboard
                val objective = board.objective
                if (scoreboard == null || objective == null) continue
                val title = ChatColor.translateAlternateColorCodes('&', assemble.adapter.getTitle(player)!!)
                if (objective.displayName != title) objective.displayName = title
                var newLines = assemble.adapter.getLines(player)
                if (newLines == null || newLines.isEmpty()) {
                    board.entries.forEach(Consumer { obj: AssembleBoardEntry? -> obj!!.remove() })
                    board.entries.clear()
                } else {
                    if (assemble.adapter.getLines(player)!!.size > 15) newLines =
                        assemble.adapter.getLines(player)!!.subList(0, 15)
                    if (!assemble.assembleStyle.isDescending) Collections.reverse(newLines)
                    if (board.entries.size > newLines.size) {
                        for (i in newLines.size until board.entries.size) {
                            val entry = board.getEntryAtPosition(i)
                            entry?.remove()
                        }
                    }
                    var cache = assemble.assembleStyle.startNumber
                    for (i in newLines.indices) {
                        var entry = board.getEntryAtPosition(i)
                        val line = ChatColor.translateAlternateColorCodes('&', newLines[i]!!)
                        if (entry == null) entry = AssembleBoardEntry(board, line, i)
                        entry.setText(line)
                        entry.setup()
                        entry.send(if (assemble.assembleStyle.isDescending) cache-- else cache++)
                    }
                }
                if (player.scoreboard !== scoreboard && !assemble.isHook) player.scoreboard = scoreboard
            } catch (e: Exception) {
                throw AssembleException("There was an error updating " + player.name + "'s scoreboard.")
            }
        }
    }

    init {
        start()
    }
}