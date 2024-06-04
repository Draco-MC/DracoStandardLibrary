package sh.talonfloof.draco_std.registries

import com.mojang.serialization.Lifecycle
import net.minecraft.core.DefaultedMappedRegistry
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.jetbrains.annotations.Nullable


class RegistryBuilder<T>(registryKey: ResourceKey<out Registry<T>?>) {
    private val registryKey: ResourceKey<out Registry<T>?>

    @Nullable
    private var defaultKey: ResourceLocation? = null

    init {
        this.registryKey = registryKey
    }

    fun defaultKey(key: ResourceLocation?): RegistryBuilder<T> {
        defaultKey = key
        return this
    }

    fun defaultKey(key: ResourceKey<T>): RegistryBuilder<T> {
        defaultKey = key.location()
        return this
    }

    fun create(): Registry<T> {
        val registry: MappedRegistry<T> = if (defaultKey != null) DefaultedMappedRegistry<T>(
            defaultKey.toString(),
            registryKey, Lifecycle.stable(), false
        ) else MappedRegistry<T>(registryKey, Lifecycle.stable(), false)
        return registry
    }
}