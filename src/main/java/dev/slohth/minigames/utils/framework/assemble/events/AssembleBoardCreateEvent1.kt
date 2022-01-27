package dev.slohth.minigames.utils.framework.assemble.events

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class AssembleBoardCreateEvent(val player: Player) : Event(), Cancellable {
    private var cancelled = false
    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(b: Boolean) {
        cancelled = b
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        var handlerList = HandlerList()
    }
}