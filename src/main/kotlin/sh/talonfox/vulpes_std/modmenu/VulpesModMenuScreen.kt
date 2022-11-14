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
import net.minecraft.util.Mth
import sh.talonfox.vulpesloader.mod.VulpesModLoader
import java.nio.file.Paths
import java.util.jar.JarFile

class VulpesModMenuScreen(
    private val ParentScreen: Screen?,
    private val Panorama: PanoramaRenderer?
) : Screen(Component.literal("Vulpes Mod Menu")) {
    private val vulpesIcon: ResourceLocation = ResourceLocation("vulpes:textures/vulpes.png")
    private var scrollPosition: Int = 0
    private var scrollTransition: Double = 0.0
    private var selectedMod: Int = -1
    private var modIcons: MutableMap<String, Pair<ResourceLocation,Pair<Int,Int>>> = mutableMapOf()
    private val keys = VulpesModLoader.Mods.keys.stream().sorted().toArray()

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

    override fun tick() {
        super.tick()
        if(scrollTransition != 0.0) {
            scrollTransition = Mth.lerp(scrollTransition,0.0,0.3)
        }
    }

    override fun render(ps: PoseStack, a: Int, b: Int, c: Float) {
        super.render(ps,a,b,c)
        if(Panorama != null) {
            Panorama.render(c, 1.0F)
        } else {
            this.renderDirtBackground((c*32.0).toInt())
            fill(ps, 0, 32, width, height-32, 0x7f000000)
        }
        ps.pushPose()
        ps.translate(0.0,-scrollTransition,0.0)
        enableScissor(0,32,0+width,32+(height-64))
        val size = ((height-64)/32)
        val beginOffset = ((height-64)/2)-(size*32/2)
        for(index in scrollPosition-1 until scrollPosition+size+1) {
            val y = index-scrollPosition
            if(keys.getOrNull(index) != null) {
                if (VulpesModLoader.Mods[keys[index]] != null) {
                    if (modIcons.contains(keys[index])) {
                        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
                        RenderSystem.setShaderTexture(0, modIcons[keys[index]]!!.first)
                        val dimensions = modIcons[keys[index]]!!.second
                        blit(
                            ps,
                            0,
                            beginOffset + 32 + (y * 32),
                            32,
                            32,
                            0F,
                            0F,
                            dimensions.first,
                            dimensions.second,
                            dimensions.first,
                            dimensions.second
                        )
                    }
                    drawString(
                        ps,
                        font,
                        VulpesModLoader.Mods[keys[index]]?.getName()!!,
                        33,
                        beginOffset + 32 + (y * 32),
                        0xffffffff.toInt()
                    )
                    drawString(
                        ps,
                        font,
                        VulpesModLoader.Mods[keys[index]]?.getVersion()!!,
                        33,
                        beginOffset + 32 + (y * 32) + 8,
                        0xff808080.toInt()
                    )
                    drawString(
                        ps,
                        font,
                        VulpesModLoader.Mods[keys[index]]?.getAuthors()!!,
                        33,
                        beginOffset + 32 + (y * 32) + 16,
                        0xff808080.toInt()
                    )
                }
            }
        }
        disableScissor()
        ps.popPose()
        RenderSystem.setShader{ GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0,vulpesIcon)
        RenderSystem.enableBlend()
        ps.pushPose()
        ps.scale(0.25F,0.25F,0.25F)
        if(Panorama != null) {
            fill(ps, 0, 0, width * 4, 128, 0x7f000000)
            fill(ps, 0, (height * 4) - 128, width * 4, height * 4, 0x7f000000)
        }
        blit(ps,0,0,128,128,0F,0F,512,512,512,512)
        ps.popPose()
        drawString(ps,font,"Vulpes Mod Menu",(width/2)-(font.width("Vulpes Mod Menu")/2),12,0xffffffff.toInt())
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollAmount: Double): Boolean {
        super.mouseScrolled(mouseX, mouseY, scrollAmount)
        val prevScroll = scrollPosition
        scrollPosition = (scrollPosition - scrollAmount.toInt()).coerceIn(0,modIcons.size)
        if((prevScroll - scrollAmount.toInt()) == scrollPosition) {
            scrollTransition = 32.0 * scrollAmount
        }
        return true;
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super.mouseClicked(mouseX, mouseY, button)
        return if(mouseY >= 32 && mouseY <= height-32) {
            val size = ((height-64)/32)
            val beginOffset = ((height-64)/2)-(size*32/2)
            for(i in 0 until size) {
                if(mouseY >= (i * 32) && mouseY <= ((i+1) * 32)) {

                }
            }
            true
        } else {
            false
        }
    }

    override fun shouldCloseOnEsc(): Boolean = true
}