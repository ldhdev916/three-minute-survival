package com.ldhdev.threeminutesurvival

import com.ldhdev.threeminutesurvival.common.CHEST_OWNER_KEY
import com.ldhdev.threeminutesurvival.common.UUIDDataType
import com.ldhdev.threeminutesurvival.handler.ConfigHandler
import com.ldhdev.threeminutesurvival.handler.TMSManager
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerInteractEvent

object TMSEventListener : Listener {

    @EventHandler
    fun onAttack(e: EntityDamageByEntityEvent) {
        if (TMSManager.current == null) return
        if (ConfigHandler.get(ConfigHandler.Key.ALLOW_PVP)) return

        if (e.entity is Player && e.damager is Player) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onClickChest(e: PlayerInteractEvent) {
        if (TMSManager.current == null) return
        if (ConfigHandler.get(ConfigHandler.Key.ALLOW_OPEN_CHEST)) return

        if (e.action == Action.RIGHT_CLICK_BLOCK) {
            val blockState = e.clickedBlock?.state

            if (blockState is Chest) {

                val owner = blockState.persistentDataContainer.get(CHEST_OWNER_KEY, UUIDDataType())

                if (owner != null && e.player.uniqueId != owner) {
                    e.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onAdvancementDone(e: PlayerAdvancementDoneEvent) {
        if (TMSManager.current == null) return

        e.message(null)
    }
}