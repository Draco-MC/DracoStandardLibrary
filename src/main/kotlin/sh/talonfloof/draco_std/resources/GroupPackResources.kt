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


class GroupPackResources(private val info: PackLocationInfo, val packs: MutableList<PackResources>, private val type: PackType) : AbstractPackResources(info) {

    override fun close() {

    }

    override fun getRootResource(vararg s: String?): IoSupplier<InputStream>? {
        return null
    }

    override fun getResource(pt: PackType, rl: ResourceLocation): IoSupplier<InputStream>? {
        for(pack in packs.reversed()) {
            val s = pack.getResource(pt,rl)
            if(s != null) {
                return s
            }
        }
        return null
    }

    override fun listResources(type: PackType, namespace: String, startingPath: String, consumer: PackResources.ResourceOutput) {
        for(pack in packs.reversed()) {
            pack.listResources(type,namespace,startingPath,consumer)
        }
    }

    override fun getNamespaces(pt: PackType): MutableSet<String> {
        val ns: MutableSet<String> = mutableSetOf()
        for(pack in packs.reversed()) {
            ns.addAll(pack.getNamespaces(pt))
        }
        return ns
    }

    override fun <T> getMetadataSection(metaReader: MetadataSectionSerializer<T>) : T? {
        val json = JsonObject()
        val packJson = JsonObject()
        packJson.addProperty("description",if(type == PackType.CLIENT_RESOURCES) "All Mod Resources, grouped into a single pack" else "All Mod Data, grouped into a single pack")
        packJson.addProperty("pack_format", if(type == PackType.CLIENT_RESOURCES) SharedConstants.RESOURCE_PACK_FORMAT else SharedConstants.DATA_PACK_FORMAT)
        json.add("pack", packJson)
        if (!json.has(metaReader.metadataSectionName)) {
            return null
        } else {
            return metaReader.fromJson(GsonHelper.getAsJsonObject(json, metaReader.metadataSectionName))
        }
    }

    class FileResourcesSupplier(private val packs: MutableList<PackResources>, private val type: PackType) : Pack.ResourcesSupplier {
        override fun openPrimary(p0: PackLocationInfo): PackResources {
            return GroupPackResources(p0,packs,type)
        }

        override fun openFull(p0: PackLocationInfo, p1: Pack.Metadata): PackResources {
            return openPrimary(p0)
        }

    }
}