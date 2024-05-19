package sh.talonfloof.draco_std.rendering

import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

object DracoEntityRendering {
    private val renderers: MutableMap<EntityType<*>, EntityRenderer<*>> = mutableMapOf()

    @JvmStatic
    fun <T : Entity> registerEntityRenderer(type: EntityType<T>, renderer: EntityRenderer<T>) {
        renderers[type] = renderer
    }

    @JvmStatic
    fun getEntityRenderers() : Map<EntityType<*>, EntityRenderer<*>> = renderers.toMap()
}