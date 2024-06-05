package com.ldhdev.threeminutesurvival.handler

import com.ldhdev.threeminutesurvival.common.*
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.block.Chest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TMSManager(private val minLocation: Location, private val maxLocation: Location) {

    companion object {

        private val UUID_MAP = mapOf(
            Material.RED_WOOL to MINED_APPLE_UUID,
            Material.GREEN_WOOL to GGUMONG_UUID,
            Material.LIME_WOOL to FG_COUNTER_UUID
        )

        var instance: TMSManager? = null
            set(value) {
                field?.timerJob?.cancel()

                field = value
            }

        val current
            get() = instance?.takeIf { it.ongoing }

    }

    private val logger by logger()

    private var timerJob: Job? = null

    var ongoing = false
        private set

    var initialLocation = Location(
        minLocation.world,
        (minLocation.x + maxLocation.x) / 2,
        minLocation.y + 1,
        (minLocation.z + maxLocation.z) / 2
    )

    private fun initPlayers() {
        allPlayers {
            it.inventory.clear()

            it.gameMode = GameMode.SURVIVAL
            it.health = it.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            it.saturation = 20f
            it.foodLevel = 20

            it.teleportAsync(initialLocation.clone().apply {
                yaw = it.yaw
                pitch = it.pitch
            })
        }
    }

    fun start(duration: Duration) {
        logger.info("Starting new TMS for $duration at $initialLocation")

        ongoing = true

        for (x in minLocation.blockX..maxLocation.blockX) {
            for (y in minLocation.blockY..maxLocation.blockY) {
                for (z in minLocation.blockZ..maxLocation.blockZ) {
                    val blockState = initialLocation.world.getBlockState(x, y, z)

                    if (blockState is Chest) {
                        val under = blockState.location.add(0.0, -1.0, 0.0).block

                        val uuid = UUID_MAP[under.type] ?: continue

                        blockState.persistentDataContainer.set(CHEST_OWNER_KEY, UUIDDataType(), uuid)

                        blockState.update()
                    }
                }
            }
        }

        timerJob = CoroutineScope(Dispatchers.Default).launch {

            repeat(3) {
                allPlayers { player ->
                    player.sendActionBar(
                        text("게임 시작까지 ", NamedTextColor.GREEN)
                            .append(text("${3 - it}", NamedTextColor.GOLD))
                            .append(text("초", NamedTextColor.GREEN))
                    )
                }
                delay(1.seconds)
            }

            allPlayers {
                it.sendActionBar(text("게임 시작!", NamedTextColor.GREEN))
            }

            withContext(BukkitDispatcher()) {
                initPlayers()
            }

            delay(duration)

            end()
        }
    }

    fun end() {
        logger.info("Ending TMS")

        instance = null

        initPlayers()

        allPlayers {
            it.sendActionBar(text("게임 종료!", NamedTextColor.GOLD))
        }
    }
}