package sh.talonfloof.draco_std.rendering

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import sh.talonfloof.dracoloader.api.EnvironmentType
import sh.talonfloof.dracoloader.api.Side

@Side(EnvironmentType.CLIENT)
object DracoBlockEntityRendering {
    private val renderers: MutableMap<BlockEntityType<*>, BlockEntityRendererProvider<*>> = mutableMapOf()

    @JvmStatic
    fun <T : BlockEntity> registerBlockEntityRenderer(type: BlockEntityType<T>, renderer: BlockEntityRendererProvider<T>) {
        renderers[type] = renderer
    }

    @JvmStatic
    fun getBlockEntityRenderers() : Map<BlockEntityType<*>, BlockEntityRendererProvider<*>> = renderers.toMap()
}