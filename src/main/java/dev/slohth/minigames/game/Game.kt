package dev.slohth.minigames.game

import dev.slohth.minigames.Minigames
import dev.slohth.minigames.arena.Arena
import dev.slohth.minigames.profile.Profile
import java.util.*

abstract class Game(private val core: Minigames, private val type: GameType, private val minPlayers: Int, private val maxPlayers: Int) {

    private val id: UUID = UUID.randomUUID()
    private val arena: Arena = core.arenaManager().random(type)

    private var state: GameState = GameState.AWAITNG

    abstract fun addPlayer(profile: Profile): Boolean
    abstract fun removePlayer(profile: Profile)
    abstract fun playerCount(): Int

    abstract fun handleDeath(profile: Profile, killer: Profile?)

    abstract fun start()
    abstract fun end(vararg winners: Profile)

    fun type(): GameType { return type }
    fun arena(): Arena { return arena }
    fun minPlayers(): Int { return minPlayers }
    fun maxPlayers(): Int { return maxPlayers }
    fun id(): UUID { return id }

    fun state(): GameState { return state }
    fun state(state: GameState) { this.state = state }

}