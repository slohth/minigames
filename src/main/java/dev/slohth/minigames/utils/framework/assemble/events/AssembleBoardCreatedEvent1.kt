package dev.slohth.minigames.utils.framework.assemble.events

import dev.slohth.minigames.utils.framework.assemble.AssembleBoard
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class AssembleBoardCreatedEvent(val board: AssembleBoard) : Event() {
    var isCancelled = false
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        var handlerList = HandlerList()
    }
}