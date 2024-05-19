package sh.talonfloof.draco_std.rendering

import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import sh.talonfloof.draco_std.mixins.client.entity.IModelLayersAccessor
import java.util.function.Supplier

fun interface LayerDefinitionSupplier {
    fun getLayerDefinition() : LayerDefinition
}

object DracoEntityRendering {
    private val renderers: MutableMap<EntityType<*>, EntityRendererProvider<*>> = mutableMapOf()
    private val layerProviders: MutableMap<ModelLayerLocation, LayerDefinitionSupplier> = mutableMapOf()

    @JvmStatic
    fun <T : Entity> registerEntityRenderer(type: EntityType<T>, renderer: EntityRendererProvider<T>) {
        renderers[type] = renderer
    }

    @JvmStatic
    fun registerModelLayer(location: ModelLayerLocation, supplier: LayerDefinitionSupplier) {
        if(layerProviders.putIfAbsent(location, supplier) != null) {
            throw IllegalArgumentException("Model Layer Registration cannot be replaced [Attempted to overwrite $location]")
        }
        IModelLayersAccessor.getModels().add(location)
    }

    @JvmStatic
    fun getEntityRenderers() : Map<EntityType<*>, EntityRendererProvider<*>> = renderers.toMap()

    @JvmStatic
    fun getLayerProviders() : Map<ModelLayerLocation, LayerDefinitionSupplier> = layerProviders.toMap()
}