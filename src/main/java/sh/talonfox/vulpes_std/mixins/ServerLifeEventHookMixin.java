package sh.talonfox.vulpes_std.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.vulpes_std.events.v1.EventRegistry;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class ServerLifeEventHookMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickChildren(Ljava/util/function/BooleanSupplier;)V"), method = "tickServer")
    private void onTickStarting(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        EventRegistry.invokeEvent("vulpes:server_life_tick_starting");
    }
}
