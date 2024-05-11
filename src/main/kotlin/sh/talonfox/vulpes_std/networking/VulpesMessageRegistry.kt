package sh.talonfox.vulpes_std.networking

import net.minecraft.resources.ResourceLocation

object VulpesMessageRegistry {
    @JvmStatic
    val clientReceivers: MutableMap<ResourceLocation, ClientboundMessageReceiver> = HashMap()
    @JvmStatic
    val serverReceivers: MutableMap<ResourceLocation, ServerboundMessageReceiver> = HashMap()

    fun registerClientboundReceiver(location: ResourceLocation, handler: ClientboundMessageReceiver) {
        clientReceivers[location] = handler
    }

    fun registerServerboundReceiver(location: ResourceLocation, handler: ServerboundMessageReceiver) {
        serverReceivers[location] = handler
    }
}