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

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.listeners.IServerEndTickListener;
import sh.talonfloof.draco_std.listeners.IServerStartTickListener;
import sh.talonfloof.draco_std.listeners.IServerStartingListener;
import sh.talonfloof.dracoloader.api.DracoListenerManager;

@Mixin(MinecraftServer.class)
public class ServerLifeMixin {
    @Inject(
            method = "runServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;initServer()Z")
    )
    private void onServerStarting(CallbackInfo ci) {
        var listeners = DracoListenerManager.getListeners(IServerStartingListener.class);
        if(listeners != null) {
            listeners.forEach(x -> ((IServerStartingListener)x).serverStarting((MinecraftServer)(Object)this));
        }
    }
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickChildren(Ljava/util/function/BooleanSupplier;)V"), method = "tickServer")
    private void onTickStarting(CallbackInfo ci) {
        var listeners = DracoListenerManager.getListeners(IServerStartTickListener.class);
        if(listeners != null) {
            listeners.forEach(x -> ((IServerStartTickListener)x).serverStartTick((MinecraftServer)(Object)this));
        }
    }
    @Inject(at = @At("TAIL"), method = "tickServer")
    private void onTickEnding(CallbackInfo ci) {
        var listeners = DracoListenerManager.getListeners(IServerEndTickListener.class);
        if(listeners != null) {
            listeners.forEach(x -> ((IServerEndTickListener)x).serverEndTick((MinecraftServer)(Object)this));
        }
    }
}
