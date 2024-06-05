package com.ldhdev.threeminutesurvival.common

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import java.nio.ByteBuffer
import java.util.*

class UUIDDataType : PersistentDataType<ByteArray, UUID> {

    override fun getPrimitiveType(): Class<ByteArray> = ByteArray::class.java

    override fun getComplexType(): Class<UUID> = UUID::class.java

    override fun fromPrimitive(primitive: ByteArray, context: PersistentDataAdapterContext): UUID {
        val buffer = ByteBuffer.wrap(primitive)

        return UUID(buffer.long, buffer.long)
    }

    override fun toPrimitive(complex: UUID, context: PersistentDataAdapterContext): ByteArray {
        val buffer = ByteBuffer.allocate(16)

        buffer.putLong(complex.mostSignificantBits)
        buffer.putLong(complex.leastSignificantBits)

        return buffer.array()
    }
}