package sh.talonfloof.draco_std.creative_tab

import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator
import net.minecraft.world.item.ItemStack
import java.util.function.Supplier

class DracoCreativeTabBuilder {
    private val builder = CreativeModeTab.Builder(CreativeModeTab.Row.TOP,0)

    fun title(title: Component): DracoCreativeTabBuilder {
        builder.title(title)
        return this
    }

    fun icon(stack: Supplier<ItemStack>): DracoCreativeTabBuilder {
        builder.icon(stack)
        return this
    }

    /*fun backgroundSuffix(s: String): DracoCreativeTabBuilder {
        builder.backgroundSuffix(s)
        return this
    }*/

    fun items(items: DisplayItemsGenerator): DracoCreativeTabBuilder {
        builder.displayItems(items)
        return this
    }

    fun build() : CreativeModeTab {
        return builder.build()
    }
}