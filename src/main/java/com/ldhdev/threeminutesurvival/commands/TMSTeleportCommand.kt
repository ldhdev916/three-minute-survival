package com.ldhdev.threeminutesurvival.commands

import com.ldhdev.threeminutesurvival.common.downCast
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import net.minecraft.commands.arguments.coordinates.Vec2Argument
import org.bukkit.Bukkit

@Suppress("UnstableApiUsage")
object TMSTeleportCommand : TMSCommand {
    override fun register(): LiteralArgumentBuilder<CommandSourceStack> {
        return literal("tp")
            .then(
                argument("pos", Vec2Argument.vec2())
                    .executes {
                        val pos = Vec2Argument.getVec2(it.downCast(), "pos")

                        val location = it.source.location.clone().apply {
                            x = pos.x.toDouble()
                            z = pos.y.toDouble()
                        }

                        val maxY = location.world.getHighestBlockYAt(location)

                        val newLocation = location.clone().apply {
                            y = maxY.toDouble() + 1
                        }

                        Bukkit.getOnlinePlayers().forEach { player ->
                            player.teleportAsync(newLocation.clone().apply {
                                yaw = player.yaw
                                pitch = player.pitch
                            })
                        }

                        Command.SINGLE_SUCCESS
                    }
            )
    }
}