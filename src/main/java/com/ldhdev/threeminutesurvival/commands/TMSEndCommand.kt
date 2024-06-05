package com.ldhdev.threeminutesurvival.commands

import com.ldhdev.threeminutesurvival.handler.TMSManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import io.papermc.paper.command.brigadier.CommandSourceStack

@Suppress("UnstableApiUsage")
object TMSEndCommand : TMSCommand {
    override fun register(): LiteralArgumentBuilder<CommandSourceStack> {
        return literal<CommandSourceStack?>("end")
            .executes {

                TMSManager.current?.end()

                Command.SINGLE_SUCCESS
            }
    }
}