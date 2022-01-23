package dev.slohth.minigames.game.data

class GameData {

    private var kills: Int = 0
    private var deaths: Int = 0
    private var killstreak: Int = 0

    fun kills(): Int { return kills }
    fun kills(kills: Int) { this.kills = kills }

    fun deaths(): Int { return deaths }
    fun deaths(deaths: Int) { this.deaths = deaths }

    fun killstreak(): Int { return killstreak }
    fun killstreak(killstreak: Int) { this.killstreak = killstreak }

}