package sh.talonfloof.draco_std.entity

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import sh.talonfloof.draco_std.DracoStandardLibrary.Companion.LOGGER
import sh.talonfloof.draco_std.mixins.entity.IAttributeRegistryAccessor


object DracoDefaultAttributeRegistry {
    fun register(type: EntityType<out LivingEntity>, container: AttributeSupplier.Builder) {
        register(type,container.build())
    }

    fun register(type: EntityType<out LivingEntity>, container: AttributeSupplier) {
        if (IAttributeRegistryAccessor.getSuppliers().put(type, container) != null) {
            LOGGER.debug("Overriding existing registration for entity type {}", BuiltInRegistries.ENTITY_TYPE.getId(type))
        }
    }
}