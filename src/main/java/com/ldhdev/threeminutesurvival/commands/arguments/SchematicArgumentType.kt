package com.ldhdev.threeminutesurvival.commands.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import io.papermc.paper.adventure.AdventureComponent
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import net.kyori.adventure.text.Component.text
import java.io.File
import java.util.concurrent.CompletableFuture

@Suppress("UnstableApiUsage")
class SchematicArgumentType : CustomArgumentType<Clipboard, String> {

    companion object {

        private val ERROR_UNKNOWN_SCHEMATIC = DynamicCommandExceptionType {
            AdventureComponent(text("Unknown schematic file '$it'"))
        }

        private val dir by lazy {
            val worldEdit = WorldEdit.getInstance()

            worldEdit.getWorkingDirectoryPath(worldEdit.configuration.saveDir).toFile()
        }

        fun schematic() = SchematicArgumentType()

        private fun findFile(dir: File, name: String): File? {
            return ClipboardFormats.getFileExtensionArray()
                .map { File(dir, "$name.$it") }
                .find { it.exists() }
        }
    }

    override fun parse(reader: StringReader): Clipboard {
        val name = reader.readString()

        val exception = ERROR_UNKNOWN_SCHEMATIC.createWithContext(reader, name)

        val file = findFile(dir, name) ?: throw exception

        val format = ClipboardFormats.findByFile(file) ?: throw exception

        return file.inputStream().use { stream ->
            format.getReader(stream).use { reader ->
                reader.read()
            }
        }
    }

    override fun getNativeType(): ArgumentType<String> = StringArgumentType.string()

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return CoroutineScope(Dispatchers.IO).async {

            dir.listFiles()
                .orEmpty()
                .filter {
                    it.nameWithoutExtension.startsWith(builder.remaining, ignoreCase = true)
                }
                .forEach {
                    ClipboardFormats.findByFile(it) ?: return@forEach

                    builder.suggest(it.nameWithoutExtension)
                }


            builder.build()
        }.asCompletableFuture()
    }
}