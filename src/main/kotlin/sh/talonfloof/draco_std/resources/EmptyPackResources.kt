package sh.talonfloof.draco_std.resources

import com.google.gson.JsonObject
import net.minecraft.SharedConstants
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.AbstractPackResources
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.metadata.MetadataSectionSerializer
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.resources.IoSupplier
import net.minecraft.util.GsonHelper
import java.io.InputStream


class EmptyPackResources(info: PackLocationInfo) : AbstractPackResources(info) {

    override fun close() {

    }

    override fun getRootResource(vararg s: String?): IoSupplier<InputStream>? {
        return null
    }

    override fun getResource(pt: PackType, rl: ResourceLocation): IoSupplier<InputStream>? {
        return null
    }

    override fun listResources(type: PackType, namespace: String, startingPath: String, consumer: PackResources.ResourceOutput) {

    }

    override fun getNamespaces(pt: PackType): MutableSet<String> {
        return mutableSetOf()
    }

    override fun <T> getMetadataSection(metaReader: MetadataSectionSerializer<T>) : T? {
        return null
    }

    class EmptyResourcesSupplier : Pack.ResourcesSupplier {
        override fun openPrimary(p0: PackLocationInfo): PackResources {
            return EmptyPackResources(p0)
        }
        override fun openFull(p0: PackLocationInfo, p1: Pack.Metadata): PackResources {
            return openPrimary(p0)
        }
    }
}