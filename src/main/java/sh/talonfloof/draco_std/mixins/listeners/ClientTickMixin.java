package sh.talonfloof.draco_std.mixins.listeners;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.listeners.client.IClientEndTickListener;
import sh.talonfloof.draco_std.listeners.client.IClientStartTickListener;
import sh.talonfloof.draco_std.loading.DracoLoadingScreen;
import sh.talonfloof.dracoloader.api.DracoListenerManager;

@Mixin(Minecraft.class)
public class ClientTickMixin {
    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;startTick()V"))
    private void draco$clientTickStart(CallbackInfo ci) throws ClassNotFoundException {
        var instances = DracoListenerManager.getListeners(IClientStartTickListener.class);
        if(instances != null) {
            instances.forEach((clazz) -> ((IClientStartTickListener) clazz).clientStartTick());
        }
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;endTick()V"))
    private void draco$clientTickEnd(CallbackInfo ci) {
        var instances = DracoListenerManager.getListeners(IClientEndTickListener.class);
        if(instances != null) {
            instances.forEach((clazz) -> ((IClientEndTickListener) clazz).clientEndTick());
        }
    }
}
