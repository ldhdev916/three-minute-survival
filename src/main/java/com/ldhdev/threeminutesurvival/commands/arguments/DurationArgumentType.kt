package com.ldhdev.threeminutesurvival.commands.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import io.papermc.paper.adventure.AdventureComponent
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import net.kyori.adventure.text.Component.text
import kotlin.time.Duration

@Suppress("UnstableApiUsage")
class DurationArgumentType : CustomArgumentType<Duration, String> {

    companion object {
        private val ERROR_INVALID_DURATION = DynamicCommandExceptionType {
            AdventureComponent(text("Invalid duration $it"))
        }

        fun duration() = DurationArgumentType()
    }

    override fun parse(reader: StringReader): Duration {
        val s = reader.readString()

        return Duration.parseOrNull(s) ?: throw ERROR_INVALID_DURATION.createWithContext(reader, s)
    }

    override fun getNativeType(): ArgumentType<String> = StringArgumentType.string()
}