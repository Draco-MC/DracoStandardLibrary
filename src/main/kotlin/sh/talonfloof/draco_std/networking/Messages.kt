package sh.talonfloof.draco_std.networking

import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.network.Connection
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.ServerCommonPacketListener
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import sh.talonfloof.dracoloader.api.EnvironmentType
import sh.talonfloof.dracoloader.api.Side

enum class DracoPacketType {
    CONFIG,
    GAMEPLAY
}

object Messages {
    @JvmStatic
    fun sendGameplayToClient(player: ServerPlayer, id: ResourceLocation, sender: ClientboundMessageSender) {
        var buf = RegistryFriendlyByteBuf(Unpooled.buffer(), player.server.registryAccess())
        buf.writeResourceLocation(id)
        sender.sendClientbound(buf)
        player.connection.send(ClientboundCustomPayloadPacket.GAMEPLAY_STREAM_CODEC.decode(buf))
    }

    @JvmStatic
    fun sendConfigToClient(connection: Connection, id: ResourceLocation, sender: ClientboundMessageSender) {
        var buf = FriendlyByteBuf(Unpooled.buffer())
        buf.writeResourceLocation(id)
        sender.sendClientbound(buf)
        connection.send(ClientboundCustomPayloadPacket.CONFIG_STREAM_CODEC.decode(buf))
    }

    @JvmStatic
    @Side(EnvironmentType.CLIENT)
    fun sendGameplayToServer(id: ResourceLocation, sender: ServerboundMessageSender) {
        var buf = RegistryFriendlyByteBuf(Unpooled.buffer(), Minecraft.getInstance().player!!.registryAccess())
        buf.writeResourceLocation(id)
        sender.sendServerbound(buf)
        Minecraft.getInstance().player!!.connection.send(ServerboundCustomPayloadPacket.STREAM_CODEC.decode(buf))
    }

    @JvmStatic
    @Side(EnvironmentType.CLIENT)
    fun sendConfigToServer(id: ResourceLocation, sender: ServerboundMessageSender) {
        var buf = FriendlyByteBuf(Unpooled.buffer())
        buf.writeResourceLocation(id)
        sender.sendServerbound(buf)
        Minecraft.getInstance().connection!!.send(ServerboundCustomPayloadPacket.STREAM_CODEC.decode(buf))
    }
}