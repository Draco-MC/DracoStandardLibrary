package sh.talonfloof.draco_std.mixins;

import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sh.talonfloof.draco_std.resources.PackChildrenHolder;

import java.util.*;

@Debug(print = false)
@Mixin(PackRepository.class)
public class PackRepositoryMixin {
    @Inject(method = "rebuildSelected", at = @At(value = "INVOKE", target = "java/util/stream/Stream.collect(Ljava/util/stream/Collector;)Ljava/lang/Object;", shift = At.Shift.BY, by = 3), locals = LocalCapture.CAPTURE_FAILHARD)
    private void draco$rebuildWithChildren(Collection<String> val, CallbackInfoReturnable<?> cir, List<Pack> packs) {
        SequencedSet<Pack> orderedPackSet = new LinkedHashSet<>();
        var iterator = packs.iterator();
        while (iterator.hasNext()) {
            var rootPack = iterator.next();
            if (((PackChildrenHolder)rootPack).draco$isHidden()) {
                continue;
            }
            orderedPackSet.addLast(rootPack);
            for (var pack : ((PackChildrenHolder)rootPack).draco$getChildren()) {
                orderedPackSet.addLast(pack);
            }
        }
        var l = new ArrayList<>(orderedPackSet);
        packs.clear();
        packs.addAll(l);
    }

    @Inject(method = "getAvailableIds", at = @At("RETURN"), cancellable = true)
    public void draco$getAvailableIds(CallbackInfoReturnable<Collection<String>> cir) {
        var v = ((PackRepository)(Object)this).getAvailablePacks().stream().filter(p -> !((PackChildrenHolder)p).draco$isHidden()).map(Pack::getId).collect(ImmutableSet.toImmutableSet());
        cir.setReturnValue(v);
    }

    @Inject(method = "getSelectedIds", at = @At("RETURN"), cancellable = true)
    public void draco$getSelectedIds(CallbackInfoReturnable<Collection<String>> cir) {
        var v = ((PackRepository)(Object)this).getSelectedPacks().stream().filter(p -> !((PackChildrenHolder)p).draco$isHidden()).map(Pack::getId).collect(ImmutableSet.toImmutableSet());
        cir.setReturnValue(v);
    }
}
