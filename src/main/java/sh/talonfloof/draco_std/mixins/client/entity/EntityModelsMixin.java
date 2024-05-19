package sh.talonfloof.draco_std.mixins.client.entity;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;
import sh.talonfloof.draco_std.rendering.DracoEntityRendering;

import java.util.Map;

@Mixin(LayerDefinitions.class)
public class EntityModelsMixin {
    @Inject(method = "createRoots", at = @At(value = "INVOKE", target="Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void draco$layerregister(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> info, ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder) {
        for(var entry : DracoEntityRendering.getLayerProviders().entrySet()) {
            builder.put(entry.getKey(), entry.getValue().getLayerDefinition());
        }
    }
}
