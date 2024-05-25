package sh.talonfloof.draco_std

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sh.talonfloof.draco_std.listeners.IRegisterListener
import sh.talonfloof.draco_std.registries.DeferredRegistry
import sh.talonfloof.draco_std.transformation.RegistryUnfreezeTransformer
import sh.talonfloof.draco_std.transformation.ResourceLocationHackTransformer
import sh.talonfloof.dracoloader.transform.DracoTransformerRegistry

open class CommonEntrypoint : IRegisterListener {
    init {
        LOGGER.info("Draco Standard Library $VERSION")
    }

    companion object {
        @JvmField
        val LOGGER: Logger = LogManager.getLogger("DracoStandardLibrary")
        const val VERSION = "1.20.6-alpha0.1"
    }

    override fun register() {
    }
}