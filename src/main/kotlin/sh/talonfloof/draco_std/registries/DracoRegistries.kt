package sh.talonfloof.draco_std.registries

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import sh.talonfloof.draco_std.fluids.FluidType


object DracoRegistries {
    @JvmField
    val FLUID_TYPES_KEY = key<FluidType>("fluid_types")

    @JvmField
    val FLUID_TYPES: Registry<FluidType> = RegistryBuilder<FluidType>(FLUID_TYPES_KEY).create()


    private fun <T> key(name: String): ResourceKey<Registry<T>> {
        return ResourceKey.createRegistryKey<T>(ResourceLocation("draco", name))
    }
}