package dev.slohth.minigames.game.types.oitc

import dev.slohth.minigames.Minigames
import dev.slohth.minigames.game.Game
import dev.slohth.minigames.game.GameListener
import dev.slohth.minigames.game.GameState
import dev.slohth.minigames.profile.Profile
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerDropItemEvent

class OITCListener(private val core: Minigames, private val game: Game) : GameListener, Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, core.plugin())
    }

    override fun unregister() {
        PlayerDropItemEvent.getHandlerList().unregister(this)
        ProjectileHitEvent.getHandlerList().unregister(this)
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
        PlayerDeathEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun onDropItem(e: PlayerDropItemEvent) {
        val profile: Profile? = core.profileManager().profile(e.player.uniqueId)
        if (!isInGame(profile) || (game.state() == GameState.ONGOING || game.state() == GameState.ENDED)) return
        e.isCancelled = true
    }

    @EventHandler
    fun onProjectileHit(e: ProjectileHitEvent) {
        if (e.entity.shooter !is Player) return
        val shooter: Profile? = core.profileManager().profile((e.entity.shooter as Player).uniqueId)

        if (e.hitEntity == null || e.hitEntity !is Player || (e.hitEntity as Player).uniqueId == (e.entity.shooter as Player).uniqueId) {
            if (isInGame(shooter)) e.entity.remove()
            return
        }

        val hit: Profile? = core.profileManager().profile((e.hitEntity as Player).uniqueId)

        if (!isInGame(shooter) || !isInGame(hit) || game.state() != GameState.ONGOING) return
        game.handleDeath(hit!!, shooter!!)
        e.entity.remove()
    }

    @EventHandler
    fun onDamageByEntity(e: EntityDamageByEntityEvent) {
        if (e.entity !is Player || e.damager !is Player) return

        val damager: Profile? = core.profileManager().profile((e.damager as Player).uniqueId)
        val damaged: Profile? = core.profileManager().profile((e.entity as Player).uniqueId)

        if (!isInGame(damager) || !isInGame(damaged)) return

        if (game.state() != GameState.ONGOING) {
            e.isCancelled = true
            return
        }

        if (e.finalDamage >= damaged!!.player().health) {
            e.isCancelled = true
            game.handleDeath(damaged, damager!!)
        }
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        val profile: Profile? = core.profileManager().profile(e.entity.uniqueId)
        if (!isInGame(profile) || profile!!.data()!!.game().state() != GameState.ONGOING) return
        game.handleDeath(profile, null)
    }

    private fun isInGame(profile: Profile?): Boolean {
        return (profile != null && (profile.inGame() && profile.data()!!.game() == game))
    }

}