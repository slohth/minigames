package dev.slohth.minigames.arena.types

import dev.slohth.minigames.Minigames
import dev.slohth.minigames.arena.Arena
import dev.slohth.minigames.game.GameType
import dev.slohth.minigames.game.data.GameData
import dev.slohth.minigames.profile.Profile
import dev.slohth.minigames.utils.framework.Config
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ThreadLocalRandom

class OITCArena(private val core: Minigames) : Arena("arenas.ONE_IN_THE_CHAMBER.basic", GameType.ONE_IN_THE_CHAMBER) {

    private val locations: MutableList<Location> = ArrayList()
    private val cooldown: MutableList<Location> = ArrayList()

    init { load() }

    override fun load() {
        for (key: String in Config.CONFIG.getStringList(path())) {
            val loc: List<String> = key.split(",")
            locations.add(Location(
                Bukkit.getWorld(loc[0]),
                loc[1].toDouble(),
                loc[2].toDouble(),
                loc[3].toDouble(),
                loc[4].toFloat(),
                loc[5].toFloat()
            ))
        }
    }

    override fun getSpawn(profile: Profile, data: GameData): Location {
        if (cooldown.size == locations.size) return random()
        var spawn: Location = random()

        while (cooldown.contains(spawn)) spawn = random()
        cooldown.add(spawn)

        Bukkit.getScheduler().runTaskLaterAsynchronously(core.plugin(), Runnable { cooldown.remove(spawn) }, 60)

        return spawn
    }

    private fun random(): Location {
        if (locations.isEmpty()) return Bukkit.getWorlds()[0].spawnLocation
        return locations[ThreadLocalRandom.current().nextInt(locations.size)]
    }

}