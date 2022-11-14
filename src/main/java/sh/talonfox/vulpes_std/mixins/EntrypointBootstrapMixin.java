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

package sh.talonfox.vulpes_std.mixins;

import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.vulpesloader.api.VulpesEntrypointExecutor;

@Mixin(BuiltInRegistries.class)
public class EntrypointBootstrapMixin {
    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/registries/BuiltInRegistries;freeze()V"
            ),
            method = "bootStrap"
    )
    private static void vulpes$executeEntrypoints(CallbackInfo ci) {
        VulpesEntrypointExecutor.executeEntrypoint("vulpes:common");
        if(MixinEnvironment.getCurrentEnvironment().getSide() != MixinEnvironment.Side.SERVER) {
            VulpesEntrypointExecutor.executeEntrypoint("vulpes:client");
        }
    }
}
