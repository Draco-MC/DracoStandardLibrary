package sh.talonfloof.draco_std

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartNames
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.*
import net.minecraft.world.level.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sh.talonfloof.draco_std.CommonEntrypoint.Companion.MODEL_TEST_LAYER
import sh.talonfloof.draco_std.entity.DracoDefaultAttributeRegistry
import sh.talonfloof.draco_std.listeners.IAttributeRegisterListener
import sh.talonfloof.draco_std.listeners.IRegisterListener
import sh.talonfloof.draco_std.rendering.DracoEntityRendering
import sh.talonfloof.draco_std.rendering.LayerDefinitionSupplier


class TestEntity(entityType: EntityType<out PathfinderMob>, world: Level) : PathfinderMob(entityType, world) {

}

class TestEntityModel(modelPart: ModelPart) : EntityModel<TestEntity>() {
    private val base: ModelPart

    init {
        base = modelPart.getChild(PartNames.CUBE)
    }

    companion object {
        @JvmStatic
        fun getLayerDefinition(): LayerDefinition {
            val mesh = MeshDefinition()
            val part = mesh.root
            part.addOrReplaceChild(
                PartNames.CUBE,
                CubeListBuilder.create().texOffs(0, 0).addBox(-6F, 12F, -6F, 12F, 12F, 12F),
                PartPose.ZERO
            )
            return LayerDefinition.create(mesh, 64, 64)
        }
    }

    override fun renderToBuffer(
        p0: PoseStack,
        p1: VertexConsumer,
        p2: Int,
        p3: Int,
        p4: Float,
        p5: Float,
        p6: Float,
        p7: Float
    ) {
        listOf(this.base).forEach {
            it.render(p0,p1,p2,p3,p4,p5,p6,p7)
        }
    }

    override fun setupAnim(p0: TestEntity, p1: Float, p2: Float, p3: Float, p4: Float, p5: Float) {

    }

}

class TestEntityRenderer(context: EntityRendererProvider.Context) : MobRenderer<TestEntity, TestEntityModel>(context, TestEntityModel(context.bakeLayer(MODEL_TEST_LAYER)), 0.5F) {

    override fun getTextureLocation(p0: TestEntity): ResourceLocation = ResourceLocation("draco","textures/entity/test.png")
}

open class CommonEntrypoint : IRegisterListener, IAttributeRegisterListener {
    init {
        LOGGER.info("Draco Standard Library $VERSION")
    }

    companion object {
        @JvmField
        val LOGGER: Logger = LogManager.getLogger("DracoStandardLibrary")
        const val VERSION = "1.20.6-alpha0.1"
        val MODEL_TEST_LAYER = ModelLayerLocation(ResourceLocation("draco", "test"), "main")
    }

    lateinit var TEST: EntityType<TestEntity>

    override fun register() {
        TEST = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
        ResourceLocation("draco", "test"),
            EntityType.Builder.of(::TestEntity, MobCategory.CREATURE).sized(0.75F,0.75F).build("test")
        )
        DracoEntityRendering.registerEntityRenderer<TestEntity>(TEST) {
            TestEntityRenderer(it)
        }
        DracoEntityRendering.registerModelLayer(MODEL_TEST_LAYER, TestEntityModel::getLayerDefinition)
    }

    override fun attributeRegister() {
        DracoDefaultAttributeRegistry.register(TEST, Mob.createMobAttributes())
    }
}