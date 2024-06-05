package com.ldhdev.threeminutesurvival.handler

import com.ldhdev.threeminutesurvival.ThreeMinuteSurvival
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object ConfigHandler {

    private val config by lazy {
        ThreeMinuteSurvival.instance.config
    }

    fun <T> get(key: Key<T>): T {
        val value = config.get(key.path) ?: return key.default

        return key.convertTo(value) ?: key.default
    }

    fun <T> set(key: Key<T>, value: T) {
        config.set(key.path, key.convertFrom(value))

        ThreeMinuteSurvival.instance.saveConfig()
    }

    interface Key<out T> {
        val path: String

        val default: T

        fun convertTo(value: Any): T? = value as T

        fun convertFrom(value: @UnsafeVariance T): Any? = value

        companion object {
            val DURATION = object : Key<Duration> {
                override val path: String = "duration"

                override val default: Duration = 3.seconds

                override fun convertTo(value: Any): Duration? {
                    return Duration.parseOrNull(value.toString())
                }

                override fun convertFrom(value: Duration): Any {
                    return value.toString()
                }
            }

            val ALLOW_PVP = object : Key<Boolean> {
                override val path: String = "pvp"

                override val default: Boolean = false
            }

            val ALLOW_OPEN_CHEST = object : Key<Boolean> {
                override val path: String = "chest"

                override val default: Boolean = false
            }
        }
    }
}