package sh.talonfloof.draco_std.networking

import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer


object Messages {
    fun sendToClient(player: ServerPlayer, id: ResourceLocation, sender: ClientboundMessageSender) {
        var buf = RegistryFriendlyByteBuf(Unpooled.buffer(), player.server.registryAccess())
        buf.writeResourceLocation(id)
        sender.sendClientbound(buf)
        player.connection.send(ClientboundCustomPayloadPacket.GAMEPLAY_STREAM_CODEC.decode(buf))
    }

    fun sendToServer(id: ResourceLocation, sender: ServerboundMessageSender) {
        var buf = RegistryFriendlyByteBuf(Unpooled.buffer(), Minecraft.getInstance().player!!.registryAccess())
        buf.writeResourceLocation(id)
        sender.sendServerbound(buf)
        Minecraft.getInstance().player!!.connection.send(ServerboundCustomPayloadPacket.STREAM_CODEC.decode(buf))
    }
}