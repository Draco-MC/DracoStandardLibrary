package sh.talonfloof.draco_std.networking

import net.minecraft.resources.ResourceLocation
import sh.talonfloof.dracoloader.api.EnvironmentType
import sh.talonfloof.dracoloader.api.Side

object DracoMessageRegistry {
    @Side(EnvironmentType.CLIENT)
    @JvmStatic
    val clientReceivers: MutableMap<ResourceLocation, ClientboundMessageReceiver> = HashMap()
    @JvmStatic
    val serverReceivers: MutableMap<ResourceLocation, ServerboundMessageReceiver> = HashMap()

    @Side(EnvironmentType.CLIENT)
    fun registerClientboundReceiver(location: ResourceLocation, handler: ClientboundMessageReceiver) {
        clientReceivers[location] = handler
    }

    fun registerServerboundReceiver(location: ResourceLocation, handler: ServerboundMessageReceiver) {
        serverReceivers[location] = handler
    }
}