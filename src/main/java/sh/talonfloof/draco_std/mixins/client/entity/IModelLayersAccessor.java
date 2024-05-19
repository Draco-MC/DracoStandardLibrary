package sh.talonfloof.draco_std.mixins.client.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ModelLayers.class)
public interface IModelLayersAccessor {
    @Accessor("ALL_MODELS")
    static Set<ModelLayerLocation> getModels() {
        throw new AssertionError("This is a stub for mixins, this error shouldn't normally occur");
    }
}
