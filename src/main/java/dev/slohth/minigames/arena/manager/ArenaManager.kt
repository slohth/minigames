package dev.slohth.minigames.arena.manager

import dev.slohth.minigames.Minigames
import dev.slohth.minigames.arena.Arena
import dev.slohth.minigames.arena.types.OITCArena
import dev.slohth.minigames.game.GameType
import dev.slohth.minigames.utils.framework.Config
import java.util.concurrent.ThreadLocalRandom

class ArenaManager(private val core: Minigames) {

    private val arenas: MutableMap<String, Pair<Arena, GameType>> = HashMap()

    init {
        for (type: String in Config.CONFIG.getConfig().getConfigurationSection("arenas")!!.getKeys(false)) {
            val gameType: GameType = GameType.valueOf(type.uppercase())

            for (entry: String in Config.CONFIG.getConfig().getConfigurationSection("arenas.$type")!!.getKeys(false)) {

                arenas[entry] = Pair(when (gameType) {
                    GameType.ONE_IN_THE_CHAMBER -> OITCArena(core, entry)
                    GameType.CAPTURE_THE_FLAG -> OITCArena(core, entry)
                    GameType.DEATH_TAG -> OITCArena(core, entry)
                    GameType.DRAGON_ESCAPE -> OITCArena(core, entry)
                    GameType.DEATH_RUN -> OITCArena(core, entry)
                    GameType.TURF_WARS -> OITCArena(core, entry)
                }, gameType)

                //TODO("Other arena types")
            }
        }
    }

    fun random(type: GameType): Arena {
        val valid: MutableList<Arena> = ArrayList()
        for (entry: Map.Entry<String, Pair<Arena, GameType>> in arenas.entries) {
            if (entry.value.second == type) valid.add(entry.value.first)
        }
        return valid[ThreadLocalRandom.current().nextInt(valid.size)]
    }

}