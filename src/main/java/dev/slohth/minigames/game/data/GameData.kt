package dev.slohth.minigames.game.data

import dev.slohth.minigames.game.Game

class GameData(private val game: Game) {

    private var kills: Int = 0
    private var deaths: Int = 0
    private var killstreak: Int = 0

    fun compareTo(other: GameData, statistic: GameDataStatistic): Int {
        return when (statistic) {
            GameDataStatistic.KILLS -> if (kills == other.kills()) 0 else if (kills > other.kills) 1 else -1
            GameDataStatistic.DEATHS -> if (deaths == other.deaths()) 0 else if (deaths > other.deaths) 1 else -1
            GameDataStatistic.KILLSTREAK -> if (killstreak == other.killstreak()) 0 else if (killstreak > other.killstreak) 1 else -1
            GameDataStatistic.KILL_DEATH_RATIO -> { return 0 }
        }
    }

    fun game(): Game { return game }

    fun kills(): Int { return kills }
    fun kills(kills: Int) { this.kills = kills }

    fun deaths(): Int { return deaths }
    fun deaths(deaths: Int) { this.deaths = deaths }

    fun killstreak(): Int { return killstreak }
    fun killstreak(killstreak: Int) { this.killstreak = killstreak }

}