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
import net.minecraft.client.gui.GuiGraphics
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
import kotlin.math.abs
import kotlin.math.sin

class VulpesModMenuScreen(
    private val ParentScreen: Screen?
) : Screen(Component.literal("Vulpes Mod Menu")) {
    private val vulpesIcon: ResourceLocation = ResourceLocation("vulpes:textures/vulpes.png")
    private var scrollPosition: Int = 0
    private var scrollTransition: Double = 0.0
    private var ticks: Long = 0
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
        ticks += 1
        if(scrollTransition != 0.0) {
            for(i in 0 until 3) {
                scrollTransition = Mth.lerp(0.3, scrollTransition, 0.0)
            }
        }
    }

    override fun render(gfx: GuiGraphics, a: Int, b: Int, c: Float) {
        super.render(gfx,a,b,c)
        this.renderBlurredBackground(c);
        //gfx.fill( 0, 32, width, height-32, 0x7f000000)

        gfx.pose().pushPose()
        gfx.pose().translate(0.0,-scrollTransition,0.0)
        gfx.enableScissor(0,32,0+width,32+(height-64))
        val size = ((height-64)/32)
        val beginOffset = ((height-64)/2)-(size*32/2)
        for(index in scrollPosition-1 until scrollPosition+size+1) {
            val y = index-scrollPosition
            if(keys.getOrNull(index) != null) {
                if (VulpesModLoader.Mods[keys[index]] != null) {
                    if(index == selectedMod) {
                        val intensity = (abs(sin(Math.toRadians((ticks * 9).toDouble()))) * 128).toInt()
                        gfx.fill(0,beginOffset + 32 + (y * 32),width,(beginOffset + 32 + (y * 32))+32,((intensity.toUInt() shl 24) or (intensity.toUInt() shl 16) or (intensity.toUInt() shl 8) or intensity.toUInt()).toInt())
                    }
                    if (modIcons.contains(keys[index])) {
                        val dimensions = modIcons[keys[index]]!!.second
                        gfx.blit(
                            modIcons[keys[index]]!!.first,
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
                    gfx.drawString(
                        font,
                        VulpesModLoader.Mods[keys[index]]?.getName()!!,
                        33,
                        beginOffset + 32 + (y * 32),
                        0xffffffff.toInt()
                    )
                    gfx.drawString(
                        font,
                        VulpesModLoader.Mods[keys[index]]?.getVersion()!!,
                        33,
                        beginOffset + 32 + (y * 32) + 8,
                        0xff808080.toInt()
                    )
                    gfx.drawString(
                        font,
                        VulpesModLoader.Mods[keys[index]]?.getAuthors()!!,
                        33,
                        beginOffset + 32 + (y * 32) + 16,
                        0xff808080.toInt()
                    )
                }
            }
        }
        gfx.disableScissor()
        gfx.pose().popPose()
        RenderSystem.enableBlend()
        gfx.pose().pushPose()
        gfx.pose().scale(0.25F,0.25F,0.25F)
        gfx.fill( 0, 0, width * 4, 128, 0x7f000000)
        gfx.fill( 0, (height * 4) - 128, width * 4, height * 4, 0x7f000000)
        gfx.pose().popPose()
        gfx.drawString(font,"Vulpes Mod Menu",(width/2)-(font.width("Vulpes Mod Menu")/2),12,0xffffffff.toInt())
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, idk: Double, scrollAmount: Double): Boolean {
        super.mouseScrolled(mouseX, mouseY, idk, scrollAmount)
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
                if(mouseY >= 32 + beginOffset + (i * 32) && mouseY <= 32 + beginOffset + ((i+1) * 32)) {
                    selectedMod = i+scrollPosition
                    ticks = 5
                    return true
                }
            }
            selectedMod = -1
            false
        } else {
            if(mouseY < 32) {
                selectedMod = -1
            }
            false
        }
    }

    override fun shouldCloseOnEsc(): Boolean = true
}