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

package sh.talonfox.vulpes_std.modmenu

import com.google.common.hash.Hashing
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.PanoramaRenderer
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import sh.talonfox.vulpesloader.mod.VulpesModLoader
import java.nio.file.Paths
import java.util.jar.JarFile

class VulpesModMenuScreen(
    parentScreen: Screen,
    panorama: PanoramaRenderer?
) : Screen(Component.literal("Vulpes Mod Menu")) {
    private val superClassScreen: Screen = parentScreen
    private val Panorama: PanoramaRenderer? = panorama
    private val vulpesIcon: ResourceLocation = ResourceLocation("vulpes:textures/vulpes.png")
    private var scrollPosition: Int = 0;
    private var columnCount: Int = 0
    private var modIcons: MutableMap<String, Pair<ResourceLocation,Pair<Int,Int>>> = mutableMapOf()

    override fun init() {
        if(modIcons.isEmpty()) {
            VulpesModLoader.Mods.forEach { (id, mod) ->
                val jar = JarFile(
                    Paths.get(VulpesModLoader.ModJars[id]!!).toFile())
                val iconZipEntry = jar.getEntry("pack.png")
                if (iconZipEntry != null) {
                    val img = DynamicTexture(NativeImage.read(jar.getInputStream(iconZipEntry)))
                    val resourceLocation =
                        ResourceLocation("vulpes", "mod_icon/" + Hashing.sha1().hashString(id, Charsets.UTF_8))
                    minecraft!!.textureManager.register(resourceLocation,img)
                    modIcons[id] = Pair(resourceLocation,Pair(img.pixels!!.width,img.pixels!!.height))
                }
            }
        }
    }

    override fun render(ps: PoseStack, a: Int, b: Int, c: Float) {
        super.render(ps,a,b,c)
        if(Panorama != null) {
            Panorama.render(c, 1.0F)
        } else {
            this.renderDirtBackground(0)
        }
        RenderSystem.setShader{ GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0,vulpesIcon)
        RenderSystem.enableBlend()
        ps.pushPose()
        ps.scale(0.25F,0.25F,0.25F)
        fill(ps,0,0,width*4,128,0x7f000000)
        fill(ps,0,(height*4)-128,width*4,height*4,0x7f000000)
        blit(ps,0,0,128,128,0F,0F,512,512,512,512)
        ps.popPose()
        drawString(ps,font,"Vulpes Mod Menu",(width/2)-(font.width("Vulpes Mod Menu")/2),12,0xffffffff.toInt())
        val keys = VulpesModLoader.Mods.keys.stream().sorted().toArray()
        for(i in scrollPosition until scrollPosition+((height-(256/4))/(128/4))) {
            if(keys.getOrNull(i) == null) {
                break
            }
            if(VulpesModLoader.Mods[keys[i]] != null) {
                if(modIcons.contains(keys[i])) {
                    RenderSystem.setShader{ GameRenderer.getPositionTexShader() }
                    RenderSystem.setShaderTexture(0,modIcons[keys[i]]!!.first)
                    val dimensions = modIcons[keys[i]]!!.second
                    blit(ps,0,(128 / 4) + (i * (128 / 4)),128/4,128/4,0F,0F,dimensions.first,dimensions.second,dimensions.first,dimensions.second)
                }
                drawString(
                    ps,
                    font,
                    VulpesModLoader.Mods[keys[i]]?.getName()!!,
                    130/4,
                    (128 / 4) + (i * (128 / 4)),
                    0xffffffff.toInt()
                )
                drawString(
                    ps,
                    font,
                    VulpesModLoader.Mods[keys[i]]?.getVersion()!!,
                    130/4,
                    (128 / 4) + (i * (128 / 4))+8,
                    0xff808080.toInt()
                )
            } else {
                break
            }
        }
    }

    override fun shouldCloseOnEsc(): Boolean = true
}