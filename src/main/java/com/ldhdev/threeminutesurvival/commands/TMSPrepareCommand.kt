package com.ldhdev.threeminutesurvival.commands

import com.ldhdev.threeminutesurvival.commands.arguments.SchematicArgumentType
import com.ldhdev.threeminutesurvival.common.getValue
import com.ldhdev.threeminutesurvival.handler.TMSManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.Vector3
import com.sk89q.worldedit.session.ClipboardHolder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min

@Suppress("UnstableApiUsage")
object TMSPrepareCommand : TMSCommand {

    private fun Vector3.toLocation(world: World): Location {
        return Location(world, x(), y(), z())
    }


    override fun register(): LiteralArgumentBuilder<CommandSourceStack> {
        return literal("prepare")
            .then(
                argument("clipboard", SchematicArgumentType.schematic())
                    .executes {

                        val player = it.source.sender as Player
                        val actor = BukkitAdapter.adapt(player)

                        val worldEdit = WorldEdit.getInstance()
                        val clipboard: Clipboard by it

                        val session = worldEdit.sessionManager.get(actor)
                        val holder = ClipboardHolder(clipboard)
                        val region = clipboard.region

                        val pastePosition = session.getPlacementPosition(actor)

                        worldEdit.newEditSession(BukkitAdapter.adapt(player.world)).use { editSession ->
                            val operation = holder
                                .createPaste(editSession)
                                .to(pastePosition)
                                .build()

                            Operations.complete(operation)

                            session.remember(editSession)
                        }

                        val clipboardOffset = region.minimumPoint.subtract(clipboard.origin)
                        val realTo = pastePosition.toVector3().add(holder.transform.apply(clipboardOffset.toVector3()))
                        val max = realTo.add(
                            holder.transform.apply(
                                region.maximumPoint.subtract(region.minimumPoint).toVector3()
                            )
                        )

                        val loc1 = realTo.toLocation(player.world)
                        val loc2 = max.toLocation(player.world)

                        TMSManager.instance = TMSManager(
                            Location(player.world, min(loc1.x, loc2.x), min(loc1.y, loc2.y), min(loc1.z, loc2.z)),
                            Location(player.world, max(loc1.x, loc2.x), max(loc1.y, loc2.y), max(loc1.z, loc2.z))
                        )

                        Command.SINGLE_SUCCESS
                    }
            )
    }
}