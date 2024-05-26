package sh.talonfloof.draco_std.resources

import com.google.common.collect.Sets
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.FilePackResources.FileResourcesSupplier
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackSelectionConfig
import net.minecraft.server.packs.PathPackResources.PathResourcesSupplier
import net.minecraft.server.packs.repository.*
import net.minecraft.world.flag.FeatureFlagSet
import sh.talonfloof.draco_std.mixins.IPackRepoAccessor
import sh.talonfloof.draco_std.resources.EmptyPackResources.EmptyResourcesSupplier
import sh.talonfloof.dracoloader.mod.DracoModLoader
import java.net.URI
import java.nio.file.Paths
import java.util.*
import java.util.Set
import java.util.function.Consumer
import kotlin.collections.component1
import kotlin.collections.component2

object DataPackPopulation {
    @JvmStatic
    fun populateDataPack(repo: PackRepository) {
        val packs = mutableListOf<Pack>()
        val access = repo as IPackRepoAccessor
        val sources = Sets.newHashSet(Objects.requireNonNull(access.getSources()))
        sources.add { packList: Consumer<Pack> ->
            DracoModLoader.MOD_PATHS.forEach { (id: String, jar: URI) ->
                val info = PackLocationInfo(
                    id,
                    Component.literal(
                        Objects.requireNonNull<String>(
                            DracoModLoader.MODS.get(id)!!.getName()
                        )
                    ),
                    PackSource.BUILT_IN,
                    Optional.empty<KnownPack>()
                )
                val pack = Pack(
                    info,
                    if (jar.toString()
                            .endsWith(".jar")
                    ) FileResourcesSupplier(Paths.get(jar)) else PathResourcesSupplier(Paths.get(jar)),
                    Pack.Metadata(
                        Component.literal("Mod Data"),
                        PackCompatibility.COMPATIBLE,
                        FeatureFlagSet.of(),
                        listOf()
                    ),
                    PackSelectionConfig(true, Pack.Position.TOP, false)
                )
                (pack as PackChildrenHolder).`draco$setHidden`(true)
                packList.accept(pack)
                packs.add(pack)
            }
        }
        sources.add {
            val info = PackLocationInfo(
                "draco_group",
                Component.literal("Mod Data"),
                PackSource.BUILT_IN,
                Optional.empty()
            )
            val pack = Pack(
                info,
                EmptyResourcesSupplier(),
                Pack.Metadata(
                    Component.literal("Data for ${DracoModLoader.MOD_PATHS.size} Draco Mods"),
                    PackCompatibility.COMPATIBLE,
                    FeatureFlagSet.of(),
                    listOf()
                ),
                PackSelectionConfig(true, Pack.Position.TOP, false)
            )
            (pack as PackChildrenHolder).`draco$setChildren`(packs)
            it.accept(pack)
        }
        access.setSources(Set.copyOf(sources))
    }
}