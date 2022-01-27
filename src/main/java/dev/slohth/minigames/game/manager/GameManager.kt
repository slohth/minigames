package dev.slohth.minigames.game.manager

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import dev.slohth.minigames.Minigames
import dev.slohth.minigames.game.Game
import dev.slohth.minigames.game.GameType
import dev.slohth.minigames.profile.Profile
import dev.slohth.minigames.utils.CC
import dev.slohth.minigames.utils.framework.Config
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*

class GameManager(private val core: Minigames) : Listener {

    private val games: MutableMap<UUID, Game> = HashMap()

    private val activeSigns: BiMap<Location, UUID> = HashBiMap.create()
    private val signs: MutableMap<Location, GameType> = HashMap()

    init {
        Bukkit.getPluginManager().registerEvents(this, core.plugin())

        for (sign: String in Config.CONFIG.getConfig().getConfigurationSection("signs")!!.getKeys(false)) {
            val loc: List<String> = sign.split(",")
            signs[Location(Bukkit.getWorld(loc[0]),
                loc[1].toDouble(),
                loc[2].toDouble(),
                loc[3].toDouble())
            ] = GameType.valueOf(Config.CONFIG.getString("signs.$sign")!!.uppercase())
        }
    }

    fun registerNew(game: Game) {
        games[game.id()] = game
    }

    fun registerNew(game: Game, location: Location) {
        games[game.id()] = game
        activeSigns[location] = game.id()
        signs[location] = game.type()
        updateSign(location)
    }

    fun unregister(game: Game) {
        val sign: Location? = signFromGame(game)
        if (sign != null) activeSigns.remove(sign)
        game.listener().unregister()
        games.remove(game.id())
    }

    fun updateSign(location: Location) {
        val game: Game? = gameFromSign(location)
        if (!activeSigns.containsKey(location) || game == null) return

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

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK || e.clickedBlock == null) return
        val profile: Profile = core.profileManager().profile(e.player.uniqueId) ?: return
        val loc: Location = e.clickedBlock!!.location

        if (activeSign(loc)) {
            gameFromSign(loc)!!.addPlayer(profile)
        } else if (sign(loc)) {
            val game: Game = Game.of(core, signs[loc]!!)
            registerNew(game, loc)
            game.addPlayer(profile)
        }
    }

    fun activeSign(location: Location): Boolean {
        return gameFromSign(location) != null
    }

    fun sign(location: Location): Boolean {
        return signs.containsKey(location)
    }

    fun gameFromSign(location: Location): Game? {
        return games[activeSigns[location]]
    }

    fun signFromGame(game: Game): Location? {
        return activeSigns.inverse()[game.id()]
    }

}