package sh.talonfloof.draco_std.mixins.client;

import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sh.talonfloof.draco_std.resources.PackChildrenHolder;

import java.util.Collection;

@Mixin(PackSelectionModel.class)
public class PackSelectionModelMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;getAvailablePacks()Ljava/util/Collection;"))
    private Collection<Pack> draco$removeHiddenAvailable(PackRepository repo) {
        return repo.getAvailablePacks().stream().filter(p -> !((PackChildrenHolder)p).draco$isHidden()).toList();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;getSelectedPacks()Ljava/util/Collection;"))
    private Collection<Pack> draco$removeHiddenSelected(PackRepository repo) {
        return repo.getSelectedPacks().stream().filter(p -> !((PackChildrenHolder)p).draco$isHidden()).toList();
    }

    @Redirect(method = "findNewPacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;getAvailablePacks()Ljava/util/Collection;"))
    private Collection<Pack> draco$preventMoveToAvailable(PackRepository repo) {
        return repo.getAvailablePacks().stream().filter(p -> !((PackChildrenHolder)p).draco$isHidden()).toList();
    }
}
