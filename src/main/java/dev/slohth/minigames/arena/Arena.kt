package dev.slohth.minigames.arena

import dev.slohth.minigames.game.GameType
import dev.slohth.minigames.game.data.GameData
import dev.slohth.minigames.profile.Profile
import org.bukkit.Location

abstract class Arena(private val path: String, private val type: GameType) {

    abstract fun load()

    abstract fun getSpawn(profile: Profile, data: GameData): Location

    fun path(): String { return path }
    fun type(): GameType { return type }

}