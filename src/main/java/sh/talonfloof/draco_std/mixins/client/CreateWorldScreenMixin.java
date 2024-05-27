package sh.talonfloof.draco_std.mixins.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sh.talonfloof.draco_std.mixins.IPackRepoAccessor;
import sh.talonfloof.draco_std.resources.DataPackPopulation;
import sh.talonfloof.dracoloader.api.EnvironmentType;
import sh.talonfloof.dracoloader.api.Side;
import sh.talonfloof.dracoloader.mod.DracoModLoader;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Side(EnvironmentType.CLIENT)
@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @Redirect(method = "getDataPackSelectionSettings", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"))
    private static void draco$populateDataPacks(PackRepository packRepository) {
        DataPackPopulation.populateDataPack(packRepository);
        packRepository.reload();
    }
}
