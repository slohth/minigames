package dev.slohth.minigames.game

enum class GameType(private val key: String) {

    ONE_IN_THE_CHAMBER("OITC"),
    CAPTURE_THE_FLAG("CTF"),
    DEATH_TAG("Death Tag"),
    DRAGON_ESCAPE("Dragon Escape"),
    DEATH_RUN("Death Run"),
    TURF_WARS("Turf Wars");

    fun key(): String { return key }

}