package dev.slohth.minigames.game.data.comparator

import dev.slohth.minigames.game.data.GameDataStatistic
import dev.slohth.minigames.profile.Profile

class KDRComparator : Comparator<Profile> {

    override fun compare(profile: Profile, other: Profile): Int {
        if (profile.data() == null || other.data() == null) return 0
        return profile.data()!!.compareTo(other.data()!!, GameDataStatistic.KILL_DEATH_RATIO)
    }


}