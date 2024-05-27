/*
 * Copyright 2022 Vulpes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sh.talonfloof.draco_std.mixins.client;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfloof.draco_std.loading.DracoLoadingScreen;
import sh.talonfloof.draco_std.mixins.IPackRepoAccessor;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;
import sh.talonfloof.draco_std.resources.EmptyPackResources;
import sh.talonfloof.draco_std.resources.PackChildrenHolder;
import sh.talonfloof.dracoloader.api.EnvironmentType;
import sh.talonfloof.dracoloader.api.Side;
import sh.talonfloof.dracoloader.mod.DracoModLoader;

import java.nio.file.Paths;
import java.util.*;

@Side(EnvironmentType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "allowsTelemetry", at = @At("HEAD"), cancellable = true)
    public void draco$disableTelemetry(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Redirect(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"),
            method = "<init>(Lnet/minecraft/client/main/GameConfig;)V"
    )
    private void draco$addResources(PackRepository packRepository) {
        DracoEarlyLog.addToLog("DISCOVER ModResources");
        DracoLoadingScreen.createCustomProgressBar("resources","DISCOVER ModResources",0);
        final var packs = new ArrayList<Pack>();
        final var access = (IPackRepoAccessor)packRepository;
        final var sources = Sets.newHashSet(Objects.requireNonNull(access.getSources()));
        sources.add((packList) -> DracoModLoader.INSTANCE.getMOD_PATHS().forEach((id, jar) -> {
            final var info = new PackLocationInfo("mod_resources/"+id, Component.literal(Objects.requireNonNull(DracoModLoader.INSTANCE.getMODS().get(id).getName())), PackSource.BUILT_IN, Optional.empty());
            final var pack = new Pack(info,jar.toString().endsWith(".jar") ? new FilePackResources.FileResourcesSupplier(Paths.get(jar)) : new PathPackResources.PathResourcesSupplier(Paths.get(jar)),new Pack.Metadata(Component.literal(info.title().getString()+" Resources"), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(),List.of()),new PackSelectionConfig(true,Pack.Position.TOP,false));
            ((PackChildrenHolder)pack).draco$setHidden(true);
            packs.add(pack);
            packList.accept(pack);
        }));
        sources.add((packList) -> {
            final var info = new PackLocationInfo("draco_group",Component.literal("Mod Resources"),PackSource.BUILT_IN,Optional.empty());
            final var pack = new Pack(info,new EmptyPackResources.EmptyResourcesSupplier(),new Pack.Metadata(Component.literal("Resources for "+DracoModLoader.INSTANCE.getMOD_PATHS().size()+" Draco Mod"+(DracoModLoader.INSTANCE.getMOD_PATHS().size() != 1 ? "s" : "")),PackCompatibility.COMPATIBLE,FeatureFlagSet.of(),List.of()),new PackSelectionConfig(true,Pack.Position.TOP,false));
            ((PackChildrenHolder)pack).draco$setChildren(packs);
            packList.accept(pack);
        });
        access.setSources(Set.copyOf(sources));
        packRepository.reload();
        DracoLoadingScreen.updateCustomBar("resources",null,null,null);
        DracoLoadingScreen.updateCustomBar("minecraft_load","Minecraft Load",0,100);
    }
}
