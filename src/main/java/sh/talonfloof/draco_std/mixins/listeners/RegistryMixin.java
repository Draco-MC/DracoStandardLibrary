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

import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;
import sh.talonfloof.draco_std.listeners.IRegisterListener;
import sh.talonfloof.draco_std.loading.DracoLoadingScreen;
import sh.talonfloof.dracoloader.api.DracoListenerManager;


@Mixin(BuiltInRegistries.class)
public class RegistryMixin {
    @Inject(
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true,
            method = "Lnet/minecraft/core/registries/BuiltInRegistries;freeze()V"
    )
    private static void draco$registryHook(CallbackInfo ci) throws InterruptedException {
        DracoEarlyLog.addToLog("HOOK IRegisterListener");
        var instances = DracoListenerManager.getListeners(IRegisterListener.class);
        if (instances != null) {
            DracoLoadingScreen.createCustomProgressBar("IRegisterListener","HOOK IRegisterListener",instances.size());
            final int[] i = {0};
            instances.forEach((clazz) -> {
                ((IRegisterListener) clazz).register();
                i[0]++;
                DracoLoadingScreen.updateCustomBar("IRegisterListener",null,i[0],null);
            });
            DracoLoadingScreen.updateCustomBar("IRegisterListener","FREEZE BuiltinRegistries",null,0);
        }
        DracoEarlyLog.addToLog("FREEZE BuiltInRegistries");
    }
    @Inject(
            at = @At(
                    value = "TAIL"
            ),
            method = "Lnet/minecraft/core/registries/BuiltInRegistries;freeze()V"
    )
    private static void draco$endRegistryHook(CallbackInfo ci) {
        DracoLoadingScreen.updateCustomBar("IRegisterListener",null,null,null);
    }
}
