package com.ldhdev.threeminutesurvival.common

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

fun commandText(command: String, suggest: Boolean = false): Component {

    val fullCommand = "/three-minute-survival $command"

    return text(command, NamedTextColor.AQUA)
        .decorate(TextDecoration.UNDERLINED, TextDecoration.ITALIC)
        .clickEvent(
            if (suggest) {
                ClickEvent.suggestCommand(fullCommand)
            } else {
                ClickEvent.runCommand(fullCommand)
            }
        )
        .hoverEvent(HoverEvent.showText(text(fullCommand)))
}
