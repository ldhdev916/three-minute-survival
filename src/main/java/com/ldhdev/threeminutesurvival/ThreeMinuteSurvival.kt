package com.ldhdev.threeminutesurvival

import com.ldhdev.threeminutesurvival.commands.*
import io.papermc.paper.command.brigadier.Commands.literal
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UnstableApiUsage")
class ThreeMinuteSurvival : JavaPlugin() {

    companion object {
        lateinit var instance: ThreeMinuteSurvival
    }

    init {
        instance = this
    }

    override fun onEnable() {

        server.pluginManager.registerEvents(TMSEventListener, this)

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands = event.registrar()

            val tmsCommands: List<TMSCommand> = listOf(
                TMSTeleportCommand,
                TMSPrepareCommand,
                TMSConfigCommand,
                TMSStartCommand,
                TMSEndCommand
            )

            commands.register(
                literal("three-minute-survival")
                    .requires { it.sender is Player && it.sender.isOp }
                    .apply {
                        tmsCommands.forEach {
                            then(it.register())
                        }
                    }
                    .build(),
                listOf("3ms")
            )
        }
    }
}