@file:Suppress("UnstableApiUsage")

package com.ldhdev.threeminutesurvival.common

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
fun CommandContext<CommandSourceStack>.downCast(): CommandContext<net.minecraft.commands.CommandSourceStack> =
    this as CommandContext<net.minecraft.commands.CommandSourceStack>

inline operator fun <reified T> CommandContext<CommandSourceStack>.getValue(
    thisRef: Any?,
    property: KProperty<*>
) = getArgument(property.name, T::class.java)!!

inline fun allPlayers(action: (Player) -> Unit) {
    Bukkit.getOnlinePlayers().forEach(action)
}