package  com.ldhdev.threeminutesurvival.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : Any> T.logger() = object : ReadOnlyProperty<T, Logger> {

    private var logger: Logger? = null

    override fun getValue(thisRef: T, property: KProperty<*>): Logger {
        if (logger == null) {
            logger = LoggerFactory.getLogger(T::class.java)
        }

        return logger!!
    }
}