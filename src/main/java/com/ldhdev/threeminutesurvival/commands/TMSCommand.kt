package com.ldhdev.threeminutesurvival.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack

@Suppress("UnstableApiUsage")
interface TMSCommand {

    fun register(): LiteralArgumentBuilder<CommandSourceStack>
}