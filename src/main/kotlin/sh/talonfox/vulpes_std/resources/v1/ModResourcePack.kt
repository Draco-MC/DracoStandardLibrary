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

package sh.talonfox.vulpes_std.resources.v1

import net.minecraft.server.packs.FilePackResources
import java.io.File
import java.nio.file.Path

open class ModResourcePack(s: Path, pn: String) : FilePackResources(pn, File(s.toUri()), true) {
    private val source: Path = s
    private val packName: String = pn

    /*private fun getSource(): Path = source

    override fun getName(): String = packName

    override fun close() {}

    override fun getResource(name: String): InputStream {
        val jar = JarFile(getSource().toFile())
        if(jar.getEntry(name) == null) {
            throw FileNotFoundException("Unable to locate resource " + name + " at " + getSource())
        }
        return jar.getInputStream(jar.getEntry(name))
    }

    override fun hasResource(name: String): Boolean {
        val jar = JarFile(getSource().toFile())
        return jar.getEntry(name) != null
    }

    override fun getResources(
        type: PackType,
        namespace: String,
        path: String,
        filter: Predicate<ResourceLocation>
    ): MutableCollection<ResourceLocation> {
        try {
            val jar = JarFile(getSource().toFile())
            val root = jar.entries()
            val resources: MutableList<ResourceLocation> = mutableListOf()
            val pathVar1 = type.directory
            val pathVar2 = "$pathVar1/$namespace/"
            val pathVar3 = "$pathVar2/$path/"
            while(root.hasMoreElements()) {
                val entry = root.nextElement()
                val zipEntry = entry as ZipEntry
                if(!zipEntry.isDirectory) {
                    val name = zipEntry.name
                    if (!name.endsWith(".mcmeta") && name.startsWith(pathVar3)) {
                        val anotherName: String = name.substring(pathVar2.length)
                        val identifier = ResourceLocation.tryBuild(namespace, anotherName)
                        if(identifier == null) {
                            LOGGER.warn("Invalid path in mod: $namespace:$anotherName, ignoring")
                        } else if(filter.test(identifier)) {
                            resources.add(identifier)
                        }
                    }
                }
            }
            return resources
        } catch (e: IOException) {
            return Collections.emptyList()
        }
    }

    override fun getNamespaces(type: PackType): MutableSet<String> {
        return try {
            val jar = JarFile(getSource().toFile())
            val root = jar.entries()
            val namespaces: MutableSet<String> = mutableSetOf()
            while(root.hasMoreElements()) {
                val entry = root.nextElement()
                val zipEntry = entry as ZipEntry
                val name = zipEntry.name
                if(name.startsWith(type.directory + "/")) {
                    val nameList: List<String> = Lists.newArrayList(FilePackResources.SPLITTER.split(name))
                    if(nameList.size > 1) {
                        val value = nameList[1]
                        if(value == value.lowercase()) {
                            namespaces.add(value)
                        } else {
                            LOGGER.warn(value)
                        }
                    }
                }
            }
            namespaces
        } catch(e: IOException) {
            if (type == PackType.SERVER_DATA) {
                this.getNamespaces(PackType.CLIENT_RESOURCES)
            } else {
                Collections.emptySet()
            }
        }
    }

    @Throws(IOException::class)
    override fun getResource(type: PackType, location: ResourceLocation): IoSupplier<InputStream>? {
        return if (location.path.startsWith("lang/")) {
            super.getResource(PackType.CLIENT_RESOURCES, location)
        } else {
            super.getResource(type, location)
        }
    }

    override fun hasResource(type: PackType, location: ResourceLocation): Boolean {
        return if (location.path.startsWith("lang/")) {
            super.hasResource(PackType.CLIENT_RESOURCES, location)
        } else {
            super.hasResource(type, location)
        }
    }*/

}