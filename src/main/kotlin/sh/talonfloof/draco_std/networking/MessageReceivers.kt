package sh.talonfloof.draco_std.networking

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

fun interface ClientboundMessageSender {
    fun sendClientbound(buffer: FriendlyByteBuf)
}

fun interface ServerboundMessageSender {
    fun sendServerbound(buffer: FriendlyByteBuf)
}

fun interface ClientboundMessageReceiver {
    fun receiveClientbound(buffer: FriendlyByteBuf)
}

fun interface ServerboundMessageReceiver {
    fun receiveServerbound(buffer: FriendlyByteBuf, server: MinecraftServer, player: ServerPlayer)
}