/*
 * Copyright 2022-2023 Vulpes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sh.talonfox.vulpes_std

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sh.talonfox.vulpes_std.listeners.v1.ICommonEntryListener
import sh.talonfox.vulpes_std.listeners.v1.client.IClientEntryListener
import java.util.function.Supplier

class TestItem(settings: Properties) : Item(settings) {

}

open class CommonEntrypoint : ICommonEntryListener, IClientEntryListener {
    private companion object {
        @JvmField
        val LOGGER: Logger = LogManager.getLogger("VulpesStandardLibrary")
        @JvmField
        var CUSTOM_ITEM: TestItem? = null
    }

    override fun enterClient() {

    }

    override fun enterCommon() {
        CUSTOM_ITEM = TestItem(Item.Properties())
        LOGGER.info("Vulpes Standard Library v1.0.0 for 1.20.1")
        Registry.register(BuiltInRegistries.ITEM,ResourceLocation("vulpes","testitem"),CUSTOM_ITEM!!)
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation("vulpes","test"), CreativeModeTab.builder(null,-1).title(
                Component.literal("Vulpes Test Tab")).icon(
            Supplier { ItemStack(Blocks.DIAMOND_ORE) }).displayItems { _, y ->
                y.accept(ItemStack(CUSTOM_ITEM!!))
            }.build())
    }
}