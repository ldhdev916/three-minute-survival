package com.ldhdev.threeminutesurvival.commands

import com.ldhdev.threeminutesurvival.commands.arguments.DurationArgumentType
import com.ldhdev.threeminutesurvival.handler.ConfigHandler
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor

@Suppress("UnstableApiUsage")
object TMSConfigCommand : TMSCommand {

    private fun <T> buildSet(
        key: ConfigHandler.Key<T>,
        argumentType: ArgumentType<out T>,
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return literal(key.path)
            .then(
                argument("value", argumentType)
                    .executes {
                        val value = it.getArgument("value", Any::class.java) as T

                        ConfigHandler.set(key, value)

                        Command.SINGLE_SUCCESS
                    }
            )
    }

    private fun <T> buildGet(key: ConfigHandler.Key<T>): LiteralArgumentBuilder<CommandSourceStack> {
        return literal(key.path)
            .executes {
                it.source.sender.sendMessage(
                    text("${key.path} = ")
                        .append(text("${ConfigHandler.get(key)}", NamedTextColor.GREEN))
                )

                Command.SINGLE_SUCCESS
            }
    }

    override fun register(): LiteralArgumentBuilder<CommandSourceStack> {

        val configs = listOf(
            ConfigHandler.Key.DURATION to DurationArgumentType.duration(),
            ConfigHandler.Key.ALLOW_PVP to BoolArgumentType.bool(),
            ConfigHandler.Key.ALLOW_OPEN_CHEST to BoolArgumentType.bool()
        )

        return literal("config")
            .then(
                literal("set").apply {
                    configs.forEach { (key, argumentType) ->
                        then(buildSet(key, argumentType))
                    }
                }
            )
            .then(
                literal("get").apply {
                    configs.forEach { (key, _) ->
                        then(buildGet(key))
                    }
                }
            )
    }
}