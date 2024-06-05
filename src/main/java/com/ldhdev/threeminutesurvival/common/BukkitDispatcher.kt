package com.ldhdev.threeminutesurvival.common

import com.ldhdev.threeminutesurvival.ThreeMinuteSurvival
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

class BukkitDispatcher : CoroutineDispatcher() {

    private val plugin by lazy { ThreeMinuteSurvival.instance }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (plugin.server.isPrimaryThread) {
            block.run()
        } else {
            plugin.server.scheduler.runTask(plugin, block)
        }
    }
}