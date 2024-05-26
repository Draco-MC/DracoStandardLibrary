package sh.talonfloof.draco_std.mixins;

import net.minecraft.server.packs.repository.Pack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.resources.PackChildrenHolder;

import java.util.ArrayList;
import java.util.List;

@Mixin(Pack.class)
public abstract class PackMixin implements PackChildrenHolder {
    @Unique
    private boolean draco$hidden;
    @Unique
    private List<Pack> draco$children;

    @Override
    public boolean draco$isHidden() {
        return draco$hidden;
    }

    @Override
    public void draco$setHidden(boolean hidden) {
        draco$hidden = hidden;
    }

    @Override
    public List<Pack> draco$getChildren() {
        return draco$children;
    }

    @Override
    public void draco$setChildren(List<Pack> children) {
        assert children != null;
        for(var child : children) {
            ((PackChildrenHolder)child).draco$setHidden(true);
        }
        draco$children = children;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void draco$init(CallbackInfo ci) {
        draco$children = new ArrayList<>();
        draco$hidden = false;
    }
}
