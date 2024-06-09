package sh.talonfloof.draco_std

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sh.talonfloof.draco_std.creative_tab.DracoCreativeTabBuilder
import sh.talonfloof.draco_std.listeners.IRegisterListener
import sh.talonfloof.dracoloader.api.ListenerSubscriber
import java.io.File

@ListenerSubscriber
open class DracoStandardLibrary : IRegisterListener {
    init {
        if(!File("./config").exists()) File("./config").mkdir()
    }

    companion object {
        @JvmField
        val LOGGER: Logger = LogManager.getLogger("DracoStandardLibrary")
        const val VERSION = "1.20.6-alpha0.1"
    }

    override fun register() {
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("draco","test1"), DracoCreativeTabBuilder().title(
            Component.literal("Test Tab 1")).icon { ItemStack(Items.DIAMOND) }.items { _,ctx ->
                ctx.accept(ItemStack(Items.DIAMOND))
        }.build())
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("draco","test2"), DracoCreativeTabBuilder().title(
                Component.literal("Test Tab 2")).icon { ItemStack(Items.IRON_INGOT) }.items { _,ctx ->
                ctx.accept(ItemStack(Items.IRON_INGOT))
            }.build())
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("draco","test3"), DracoCreativeTabBuilder().title(
                Component.literal("Test Tab 3")).icon { ItemStack(Items.GOLD_INGOT) }.items { _,ctx ->
                ctx.accept(ItemStack(Items.GOLD_INGOT))
            }.build())
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("draco","test4"), DracoCreativeTabBuilder().title(
                Component.literal("Test Tab 4")).icon { ItemStack(Items.NETHERITE_INGOT) }.items { _,ctx ->
                ctx.accept(ItemStack(Items.NETHERITE_INGOT))
            }.build())
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("draco","test5"), DracoCreativeTabBuilder().title(
                Component.literal("Test Tab 5")).icon { ItemStack(Items.COAL) }.items { _,ctx ->
                ctx.accept(ItemStack(Items.COAL))
            }.build())
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("draco","test6"), DracoCreativeTabBuilder().title(
                Component.literal("Test Tab 6")).icon { ItemStack(Items.DIAMOND) }.items { _,ctx ->
                ctx.accept(ItemStack(Items.DIAMOND))
            }.build())
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("draco","test7"), DracoCreativeTabBuilder().title(
                Component.literal("Test Tab 7")).icon { ItemStack(Items.DIAMOND) }.items { _,ctx ->
                ctx.accept(ItemStack(Items.DIAMOND))
            }.build())
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("draco","test8"), DracoCreativeTabBuilder().title(
                Component.literal("Test Tab 8")).icon { ItemStack(Items.DIAMOND) }.items { _,ctx ->
                ctx.accept(ItemStack(Items.DIAMOND))
            }.build())
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("draco","test9"), DracoCreativeTabBuilder().title(
                Component.literal("Test Tab 9")).icon { ItemStack(Items.DIAMOND) }.items { _,ctx ->
                ctx.accept(ItemStack(Items.DIAMOND))
            }.build())
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("draco","test10"), DracoCreativeTabBuilder().title(
                Component.literal("Test Tab 10")).icon { ItemStack(Items.DIAMOND) }.items { _,ctx ->
                ctx.accept(ItemStack(Items.DIAMOND))
            }.build())
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("draco","test11"), DracoCreativeTabBuilder().title(
                Component.literal("Test Tab 11")).icon { ItemStack(Items.DIAMOND) }.items { _,ctx ->
                ctx.accept(ItemStack(Items.DIAMOND))
            }.build())
    }
}