package sh.talonfloof.draco_std.mixins;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Set;

@Mixin(PackRepository.class)
public interface IPackRepoAccessor {
    @Accessor("sources")
    @Final
    Set<RepositorySource> getSources();
    @Accessor("sources")
    @Mutable
    @Final
    void setSources(Set<RepositorySource> sources);

    @Accessor("selected")
    @Mutable
    @Final
    List<Pack> getSelected();

    @Accessor("selected")
    @Mutable
    @Final
    void setSelected(List<Pack> selected);
}
