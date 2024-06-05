package com.ldhdev.threeminutesurvival.commands

import com.ldhdev.threeminutesurvival.common.commandText
import com.ldhdev.threeminutesurvival.common.getValue
import com.ldhdev.threeminutesurvival.handler.ConfigHandler
import com.ldhdev.threeminutesurvival.handler.TMSManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor

@Suppress("UnstableApiUsage")
object TMSStartCommand : TMSCommand {

    private fun CommandContext<CommandSourceStack>.startGame(initial: Boolean) {
        val instance = TMSManager.instance

        if (instance == null) {
            source.sender.sendMessage(
                text("현재 준비된 게임이 없습니다 ", NamedTextColor.RED)
                    .append(commandText("prepare", suggest = true))
                    .append(text(" 커맨드를 먼저 사용 해 주세요", NamedTextColor.RED))
            )
            return
        }

        if (TMSManager.current != null) {
            source.sender.sendMessage(
                text("이미 게임이 진행중입니다 ", NamedTextColor.RED)
                    .append(commandText("end"))
                    .append(text(" 커맨드를 먼저 사용 해 주세요", NamedTextColor.RED))
            )
            return
        }

        if (initial) {
            instance.initialLocation = source.location
        }

        instance.start(ConfigHandler.get(ConfigHandler.Key.DURATION))

    }

    override fun register(): LiteralArgumentBuilder<CommandSourceStack> {
        return literal("start")
            .then(
                argument("initial", BoolArgumentType.bool())
                    .executes {

                        val initial: Boolean by it

                        it.startGame(initial)

                        Command.SINGLE_SUCCESS
                    }
            )
            .executes {
                it.startGame(false)

                Command.SINGLE_SUCCESS
            }
    }
}