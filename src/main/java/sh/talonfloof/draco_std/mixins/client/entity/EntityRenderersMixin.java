package sh.talonfloof.draco_std.mixins.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;
import sh.talonfloof.draco_std.rendering.DracoEntityRendering;
import sh.talonfloof.dracoloader.api.EnvironmentType;
import sh.talonfloof.dracoloader.api.Side;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Side(EnvironmentType.CLIENT)
@Mixin(EntityRenderers.class)
public class EntityRenderersMixin {
    @Shadow
    @Final
    private static Map<EntityType<?>, EntityRendererProvider<?>> PROVIDERS;

    @Inject(method = "<clinit>*", at = @At(value = "RETURN"))
    private static void draco$registerRenderers(CallbackInfo info) throws InterruptedException {
        var renders = DracoEntityRendering.getEntityRenderers();
        DracoEarlyLog.addToLog("REGISTER EntityRenderers");
        DracoEarlyLog.customBarProgress = 0;
        DracoEarlyLog.customBarName = "REGISTER EntityRenderers";
        AtomicInteger i = new AtomicInteger();
        var max = renders.size();
        renders.forEach((key, value) -> {
            PROVIDERS.put(key,value);
            i.getAndIncrement();
            DracoEarlyLog.customBarProgress = (float)i.get()/(float)max;
        });
        DracoEarlyLog.customBarName = "";
    }
}
