package dev.slohth.minigames.game.data.comparator

import dev.slohth.minigames.game.data.GameDataStatistic
import dev.slohth.minigames.profile.Profile

class GameDataComparator(private val statistic: GameDataStatistic) : Comparator<Profile> {

    override fun compare(profile: Profile, other: Profile): Int {
        if (profile.data() == null || other.data() == null) return 0
        return profile.data()!!.compareTo(other.data()!!, statistic)
    }

}