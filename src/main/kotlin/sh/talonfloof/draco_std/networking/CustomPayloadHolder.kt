package sh.talonfloof.draco_std.networking

import io.netty.buffer.ByteBuf
import net.minecraft.network.FriendlyByteBuf

interface CustomPayloadHolder {
    fun `draco$setBuf`(buf: ByteBuf)

    fun `draco$getBuf`() : FriendlyByteBuf
}