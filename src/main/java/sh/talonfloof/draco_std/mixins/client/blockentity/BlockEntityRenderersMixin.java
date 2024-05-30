package sh.talonfloof.draco_std.mixins.client.blockentity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;
import sh.talonfloof.draco_std.rendering.DracoBlockEntityRendering;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(BlockEntityRenderers.class)
public class BlockEntityRenderersMixin {
    @Shadow
    @Final
    private static Map<BlockEntityType<?>, BlockEntityRendererProvider<?>> PROVIDERS;

    @Inject(method = "<clinit>*", at = @At(value = "RETURN"))
    private static void draco$registerBlockEntityRenderers(CallbackInfo info) throws InterruptedException {
        DracoEarlyLog.addToLog("REGISTER BlockEntityRenderers");
        DracoEarlyLog.customBarProgress = 0;
        DracoEarlyLog.customBarName = "REGISTER BlockEntityRenderers";
        AtomicInteger i = new AtomicInteger();
        var renders = DracoBlockEntityRendering.getBlockEntityRenderers();
        var max = renders.size();
        renders.forEach((key, value) -> {
            PROVIDERS.put(key,value);
            i.getAndIncrement();
            DracoEarlyLog.customBarProgress = (float)i.get()/(float)max;
        });
        DracoEarlyLog.customBarName = "";
    }
}
