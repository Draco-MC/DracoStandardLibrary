package sh.talonfloof.draco_std.mixins.client.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import sh.talonfloof.dracoloader.api.EnvironmentType;
import sh.talonfloof.dracoloader.api.Side;

import java.util.Set;

@Side(EnvironmentType.CLIENT)
@Mixin(ModelLayers.class)
public interface IModelLayersAccessor {
    @Accessor("ALL_MODELS")
    static Set<ModelLayerLocation> getModels() {
        throw new AssertionError("This is a stub for mixins, this error shouldn't normally occur");
    }
}
