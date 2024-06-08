/*
 * Copyright 2022 Vulpes
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

package sh.talonfloof.draco_std.mixins.listeners;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;
import sh.talonfloof.draco_std.listeners.IRegisterListener;
import sh.talonfloof.draco_std.loading.DracoLoadingScreen;
import sh.talonfloof.dracoloader.api.DracoListenerManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;


@Mixin(BuiltInRegistries.class)
public class RegistryMixin {
    @Inject(
            at = @At(
                    value = "TAIL"
            ),
            method = "Lnet/minecraft/core/registries/BuiltInRegistries;freeze()V"
    )
    private static void draco$registryHook(CallbackInfo ci) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException {
        DracoLoadingScreen.updateCustomBar("minecraft_load","Launching Mods",null,null);
        for(Registry<?> i : BuiltInRegistries.REGISTRY) {
            i.getClass().getDeclaredMethod("unfreeze").invoke(i);
        }
        DracoEarlyLog.addToLog("HOOK IRegisterListener");
        var instances = DracoListenerManager.getListeners(IRegisterListener.class);
        DracoLoadingScreen.createCustomProgressBar("IRegisterListener","HOOK IRegisterListener",0);
        if (instances != null) {
            DracoLoadingScreen.updateCustomBar("IRegisterListener","HOOK IRegisterListener",0,instances.size());
            final int[] i = {0};
            instances.forEach((clazz) -> {
                ((IRegisterListener) clazz).register();
                i[0]++;
                DracoLoadingScreen.updateCustomBar("IRegisterListener",null,i[0],null);
            });
            DracoLoadingScreen.updateCustomBar("IRegisterListener",null,null,null);
        }
        DracoEarlyLog.addToLog("Registries -> FREEZE_DATA");
        DracoLoadingScreen.updateCustomBar("minecraft_load","Transition Registries -> FREEZE_DATA",null,null);
        for(Registry<?> i : BuiltInRegistries.REGISTRY) {
            i.freeze();
            Thread.sleep(1);
        }
        DracoEarlyLog.addToLog("BlockStates -> FREEZE_DATA");
        DracoLoadingScreen.updateCustomBar("minecraft_load","Transition BlockStates -> FREEZE_DATA",null,null);
        {
            Iterator<Map.Entry<ResourceKey<Block>, Block>> it = BuiltInRegistries.BLOCK.entrySet().stream().iterator();
            while (it.hasNext()) {
                var entry = it.next();
                if (!entry.getKey().location().getNamespace().equals("minecraft")) {
                    for (BlockState state : entry.getValue().getStateDefinition().getPossibleStates()) {
                        Block.BLOCK_STATE_REGISTRY.add(state);
                        state.initCache();
                    }
                    entry.getValue().getLootTable();
                }
            }
            Thread.sleep(1);
        }
        DracoEarlyLog.addToLog("BlockToItem -> FREEZE_DATA");
        DracoLoadingScreen.updateCustomBar("minecraft_load","Transition BlockToItem -> FREEZE_DATA",null,null);
        {
            Iterator<Map.Entry<ResourceKey<Item>, Item>> it = BuiltInRegistries.ITEM.entrySet().stream().iterator();
            while (it.hasNext()) {
                var entry = it.next();
                if (!entry.getKey().location().getNamespace().equals("minecraft")) {
                    if (entry.getValue() instanceof BlockItem blockItem) {
                        Item.BY_BLOCK.put(blockItem.getBlock(), blockItem);
                    }
                }
            }
            Thread.sleep(1);
        }
        DracoLoadingScreen.updateCustomBar("minecraft_load","Loading Late Resources",null,null);
    }
}
