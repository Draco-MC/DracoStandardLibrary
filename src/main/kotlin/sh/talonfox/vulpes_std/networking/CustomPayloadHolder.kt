package sh.talonfox.vulpes_std.networking

import io.netty.buffer.ByteBuf
import net.minecraft.network.FriendlyByteBuf

interface CustomPayloadHolder {
    fun `vulpes$setBuf`(buf: ByteBuf)

    fun `vulpes$getBuf`() : FriendlyByteBuf
}