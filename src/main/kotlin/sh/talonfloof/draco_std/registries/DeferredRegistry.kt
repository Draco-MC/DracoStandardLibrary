package sh.talonfloof.draco_std.registries

import com.mojang.datafixers.util.Either
import net.minecraft.core.Holder
import net.minecraft.core.HolderOwner
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import java.util.*
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Stream


open class DeferredHolder<R, T : R>(private val key: ResourceKey<R>) : Holder<R>, Supplier<T> {
    private var holder: Holder<R>? = null

    fun getRegistry(): Registry<R>? {
        return BuiltInRegistries.REGISTRY[key.registry()] as Registry<R>?
    }

    protected fun attemptRetrieval(throwOnMissingRegistry: Boolean) {
        if (holder != null) return
        val registry = getRegistry()
        if (registry != null) {
            holder = registry.getHolder(key).orElse(null)
        } else check(!throwOnMissingRegistry) { "Registry not present for " + this + ": " + key.registry() }
    }

    override fun value(): T {
        attemptRetrieval(true)
        if(holder == null) {
            throw NullPointerException("Attempting to access " + this.key + " while unbound")
        }
        return (holder!!.value()) as T
    }

    override fun isBound(): Boolean {
        attemptRetrieval(false)
        return this.holder != null && this.holder!!.isBound()
    }

    override fun `is`(id: ResourceLocation): Boolean = id == this.key.location()

    override fun tags(): Stream<TagKey<R>> {
        attemptRetrieval(false)
        return if (holder != null) holder!!.tags() else Stream.empty()
    }

    override fun unwrap(): Either<ResourceKey<R>, R> = Either.left(this.key)

    override fun unwrapKey(): Optional<ResourceKey<R>> = Optional.of(this.key)

    override fun kind(): Holder.Kind = Holder.Kind.REFERENCE

    override fun canSerializeIn(owner: HolderOwner<R>): Boolean {
        attemptRetrieval(false)
        return this.holder != null && this.holder!!.canSerializeIn(owner)
    }

    @Deprecated("Deprecated in Java")
    override fun `is`(holder: Holder<R>): Boolean {
        attemptRetrieval(false)
        return this.holder != null && this.holder!!.`is`(holder)
    }

    override fun `is`(tag: TagKey<R>): Boolean = holder != null && holder!!.`is`(tag)

    override fun `is`(filter: Predicate<ResourceKey<R>>): Boolean = filter.test(this.key)

    override fun `is`(key: ResourceKey<R>): Boolean = key == this.key

    override fun get(): T = this.value()
}

open class DeferredRegistry<T> protected constructor(private val registryKey: ResourceKey<out Registry<T>>, private val namespace: String) {
    val deferredEntries: MutableMap<DeferredHolder<T, out T>, Supplier<out T>> = mutableMapOf()

    companion object {
        @JvmStatic
        fun <T> create(key: ResourceKey<out Registry<T>>, namespace: String): DeferredRegistry<T> {
            return DeferredRegistry(key, namespace)
        }
    }

    fun <I : T> register(name: String, sup: Supplier<out I>) : DeferredHolder<T,I> {
        val key = ResourceLocation.tryBuild(namespace, name)!!
        val ret: DeferredHolder<T, I> = DeferredHolder(ResourceKey.create(registryKey, key))
        require(deferredEntries.putIfAbsent(ret,sup) == null) { "Duplicate registration $name" }
        return ret
    }

    fun register() {
        for((k,v) in deferredEntries) {
            Registry.register(k.getRegistry()!!,k.unwrapKey().get(),v.get()!!)
        }
    }
}