package dev.slohth.minigames.game.manager

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import dev.slohth.minigames.Minigames
import dev.slohth.minigames.game.Game
import dev.slohth.minigames.utils.CC
import org.bukkit.Location
import org.bukkit.block.Sign
import java.util.*

class GameManager(private val core: Minigames) {

    private val games: MutableMap<UUID, Game> = HashMap()
    private val signs: BiMap<Location, UUID> = HashBiMap.create()

    fun registerNew(game: Game) {
        games[game.id()] = game

    }

    fun registerNew(game: Game, location: Location) {
        games[game.id()] = game
        signs[location] = game.id()
        updateSign(location)
    }

    fun unregister(game: Game) {
        val sign: Location? = signFromGame(game)



        game.listener().unregister()
    }

    fun updateSign(location: Location) {
        val game: Game? = gameFromSign(location)
        if (!signs.containsKey(location) || game == null) return

        val sign: Sign = location.block.state as Sign
        sign.isEditable = true

        sign.setLine(0, CC.color("&8<&f&lMinigames&8>"))
        sign.setLine(1, CC.color("&7&l${game.type().key()}"))
        sign.setLine(2, CC.color(
            if (game.playerCount() == 0) "&7" else "&b" + game.playerCount() + "&7/" + game.maxPlayers() + " players"
        ))
        sign.setLine(3, CC.color("&fClick to join"))

        sign.update()
        sign.isEditable = false
    }

    fun gameFromSign(location: Location): Game? {
        return games[signs[location]]
    }

    fun signFromGame(game: Game): Location? {
        return signs.inverse()[game.id()]
    }

}