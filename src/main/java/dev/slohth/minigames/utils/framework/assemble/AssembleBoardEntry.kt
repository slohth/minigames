package dev.slohth.minigames.utils.framework.assemble

import org.bukkit.ChatColor
import org.bukkit.scoreboard.Team

class AssembleBoardEntry(private val board: AssembleBoard, private var text: String, position: Int) {
    private var identifier: String?
    private var team: Team? = null
    fun setText(text: String) {
        this.text = text
    }

    fun setIdentifier(identifier: String?) {
        this.identifier = identifier
    }

    fun setup() {
        val scoreboard = board.scoreboard ?: return
        var teamName = identifier
        if (teamName!!.length > 16) teamName = teamName.substring(0, 16)
        var team = scoreboard.getTeam(teamName)
        if (team == null) team = scoreboard.registerNewTeam(teamName)
        if (team.entries == null || team.entries.isEmpty() || !team.entries.contains(identifier)) team.addEntry(
            identifier!!
        )
        if (!board.entries.contains(this)) board.entries.add(this)
        this.team = team
    }

    fun send(position: Int) {
        if (text.length > 16) {
            var prefix = text.substring(0, 16)
            var suffix: String
            if (prefix[15] == ChatColor.COLOR_CHAR) {
                prefix = prefix.substring(0, 15)
                suffix = text.substring(15)
            } else if (prefix[14] == ChatColor.COLOR_CHAR) {
                prefix = prefix.substring(0, 14)
                suffix = text.substring(14)
            } else {
                suffix = if (ChatColor.getLastColors(prefix)
                        .equals(ChatColor.getLastColors(identifier!!), ignoreCase = true)
                ) {
                    text.substring(16)
                } else {
                    ChatColor.getLastColors(prefix) + text.substring(16)
                }
            }
            if (suffix.length > 16) suffix = suffix.substring(0, 16)
            team!!.prefix = prefix
            team!!.suffix = suffix
        } else {
            team!!.prefix = text
            team!!.suffix = ""
        }
        val score = board.objective!!.getScore(identifier!!)
        score.score = position
    }

    fun remove() {
        board.identifiers.remove(identifier)
        board.scoreboard.resetScores(identifier!!)
    }

    init {
        identifier = board.getUniqueIdentifier(position)
        setup()
    }
}