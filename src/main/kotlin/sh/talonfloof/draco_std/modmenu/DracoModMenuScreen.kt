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

package sh.talonfloof.draco_std.modmenu

import com.google.common.hash.Hashing
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import sh.talonfloof.draco_std.config.ModConfig
import sh.talonfloof.dracoloader.api.EnvironmentType
import sh.talonfloof.dracoloader.api.Side
import sh.talonfloof.dracoloader.mod.DracoModLoader
import java.nio.file.Paths
import java.util.jar.JarFile
import kotlin.io.path.toPath
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sin

@Side(EnvironmentType.CLIENT)
class DracoModMenuScreen(
    private val parentScreen: Screen?
) : Screen(Component.literal("Draco Mod Menu")) {
    private var scrollPosition: Int = 0
    private var scrollTransitionPrevious: Double = 0.0
    private var scrollTransition: Double = 0.0
    private var secondScrollPosition: Int = 0
    private var secondScrollTransitionPrevious: Double = 0.0
    private var secondScrollTransition: Double = 0.0
    private var selectedMod: Int = -1
    private var modIcons: MutableMap<String, Pair<ResourceLocation,Pair<Int,Int>>> = mutableMapOf()
    private val keys = DracoModLoader.MODS.keys.stream().sorted().toArray()

    override fun init() {
        if(modIcons.isEmpty()) {
            DracoModLoader.MODS.forEach { (id, mod) ->
                if(DracoModLoader.MOD_PATHS[id]!!.toPath().toString().endsWith(".jar")) {
                    val jar = JarFile(
                        Paths.get(DracoModLoader.MOD_PATHS[id]!!).toFile()
                    )
                    val iconZipEntry = jar.getEntry("pack.png")
                    if (iconZipEntry != null) {
                        val img = DynamicTexture(NativeImage.read(jar.getInputStream(iconZipEntry)))
                        val resourceLocation =
                            ResourceLocation("draco", "mod_icon/" + Hashing.sha1().hashString(id, Charsets.UTF_8))
                        minecraft!!.textureManager.register(resourceLocation, img)
                        modIcons[id] = Pair(resourceLocation, Pair(img.pixels!!.width, img.pixels!!.height))
                    }
                } else {
                    val icon = Paths.get(DracoModLoader.MOD_PATHS[id]!!).toFile().resolve("pack.png")
                    if(icon.exists()) {
                        val img = DynamicTexture(NativeImage.read(icon.inputStream()))
                        val resourceLocation =
                            ResourceLocation("draco", "mod_icon/" + Hashing.sha1().hashString(id, Charsets.UTF_8))
                        minecraft!!.textureManager.register(resourceLocation, img)
                        modIcons[id] = Pair(resourceLocation, Pair(img.pixels!!.width, img.pixels!!.height))
                    }
                }
            }
        }
    }

    override fun tick() {
        super.tick()
        if((secondScrollTransition.toInt() - secondScrollPosition).absoluteValue > 0.1) {
            secondScrollTransitionPrevious = secondScrollTransition
            for(i in 0 until 3) {
                secondScrollTransition = Mth.lerp(0.3, secondScrollTransition, secondScrollPosition.toDouble())
            }
        } else {
            secondScrollTransitionPrevious = secondScrollTransition
            secondScrollTransition = secondScrollPosition.toDouble()
        }
        if(scrollTransition != 0.0) {
            scrollTransitionPrevious = scrollTransition
            for(i in 0 until 3) {
                scrollTransition = Mth.lerp(0.3, scrollTransition, 0.0)
            }
        }
    }

    companion object {
        @JvmStatic
        public fun renderBox(gfx: GuiGraphics, x: Int, y: Int, w: Int, h: Int) {
            RenderSystem.enableBlend();
            gfx.blit(
                ResourceLocation("textures/gui/menu_list_background.png"),
                x,
                y + 2,
                (x + w).toFloat(),
                (y + h - 4).toFloat(),
                w,
                h - 4,
                32,
                32
            )
            val head = HEADER_SEPARATOR
            val foot = FOOTER_SEPARATOR
            gfx.blit(head, x, y, 0.0f, 0.0f, w, 2, 32, 2)
            gfx.blit(foot, x, y + h - 2, 0.0f, 0.0f, w, 2, 32, 2)
            RenderSystem.disableBlend();
        }
    }

    override fun render(gfx: GuiGraphics, a: Int, b: Int, c: Float) {
        super.render(gfx,a,b,c)
        //this.renderBlurredBackground(c);
        //gfx.fill( 0, 32, width, height-32, 0x7f000000)
        renderBox(gfx,8,32,(width.toDouble()*0.5).toInt()-8,height-64);
        val secondSize = width-8-((width.toDouble()*0.5).toInt()+8)
        val secondX = (width.toDouble()*0.5).toInt()+8;
        renderBox(gfx,secondX,32,secondSize,height-64);

        gfx.pose().pushPose()
        gfx.pose().translate(8.0,-Mth.lerp(c.toDouble(),scrollTransitionPrevious,scrollTransition),0.0)
        gfx.enableScissor(8,34,(width.toDouble()*0.5).toInt(),32+(height-64)-2)
        val size = ((height-64)/32)
        val beginOffset = ((height-64)/2)-(size*32/2)
        for(index in scrollPosition-1 until scrollPosition+size+1) {
            val y = index-scrollPosition
            if(keys.getOrNull(index) != null) {
                if (DracoModLoader.MOD_PATHS[keys[index]] != null) {
                    if(index == selectedMod) {
                        gfx.fill(0,beginOffset + 32 + (y * 32),width,(beginOffset + 32 + (y * 32))+32,(0x20ffffff).toInt())
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
                        DracoModLoader.MODS[keys[index]]?.getName()!!,
                        33,
                        beginOffset + 32 + (y * 32),
                        0xffffffff.toInt()
                    )
                    var t = font.splitter.splitLines(DracoModLoader.MODS[keys[index]]?.getDescription()!!,(width.toDouble()*0.5).toInt()-8-32, Style.EMPTY)
                    var ind = 0;
                    for(text in t) {
                        if(ind+1 >= 2) {
                            gfx.drawString(font, text.string+"...", 33, beginOffset + 32 + (y * 32)+8+(ind*font.lineHeight), 0xff808080.toInt())
                            break
                        }
                        gfx.drawString(font, text.string, 33, beginOffset + 32 + (y * 32)+8+(ind*font.lineHeight), 0xff808080.toInt())
                        ind++
                    }
                }
            }
        }
        gfx.disableScissor()
        gfx.pose().popPose()
        gfx.pose().pushPose()
        gfx.pose().translate(secondX.toDouble(),32.0-Mth.lerp(c.toDouble(),secondScrollTransitionPrevious,secondScrollTransition), 0.0)
        gfx.enableScissor(secondX,34,width-8,32+(height-64)-2)
        if (selectedMod != -1) {
            if(keys.getOrNull(selectedMod) != null) {
                if (DracoModLoader.MODS[keys[selectedMod]] != null) {
                    if (modIcons.contains(keys[selectedMod])) {
                        val dimensions = modIcons[keys[selectedMod]]!!.second
                        gfx.blit(
                            modIcons[keys[selectedMod]]!!.first,
                            2,
                            4,
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
                        DracoModLoader.MODS[keys[selectedMod]]?.getName()!!,
                        35,
                        4,
                        0xffffffff.toInt()
                    )
                    gfx.drawString(
                        font,
                        DracoModLoader.MODS[keys[selectedMod]]?.getVersion()!!,
                        35,
                        4 + 8,
                        0xff808080.toInt()
                    )
                    gfx.drawString(
                        font,
                        "By " + DracoModLoader.MODS[keys[selectedMod]]?.getAuthors()!!,
                        35,
                        4 + 16,
                        0xff808080.toInt()
                    )
                    var t = font.splitter.splitLines(DracoModLoader.MODS[keys[selectedMod]]?.getDescription()!!,secondSize, Style.EMPTY)
                    var index = 0;
                    for(text in t) {
                        gfx.drawString(font, text.string, 2, 4+32+1+(index*font.lineHeight), 0xffffffff.toInt())
                        index++
                    }
                    if(ModConfig.hasConfig(DracoModLoader.MODS[keys[selectedMod]]?.getID()!!)) {
                        renderBox(gfx, secondSize - 26, 6, 20, 20)
                        gfx.blit(ResourceLocation("draco","textures/gui/configure_button.png"),secondSize - 26, 6,20,20,0F,0F,20,20,20,20)
                        if (a >= (secondX + secondSize - 26) && a < (secondX + secondSize - 6) && b >= 32 && b >= ((32+6) - secondScrollTransition) && b < ((32+6) - secondScrollTransition) + 20) {
                            gfx.fill(
                                secondSize - 26,
                                6,
                                secondSize - 6,
                                26,
                                (0x40ffffff).toInt()
                            )
                        }
                    }
                }
            }
        }
        gfx.disableScissor()
        gfx.pose().popPose()
        RenderSystem.enableBlend()
        gfx.drawString(font,"Draco Mod Menu",(width/2)-(font.width("Draco Mod Menu")/2),12,0xffffffff.toInt())
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, idk: Double, scrollAmount: Double): Boolean {
        super.mouseScrolled(mouseX, mouseY, idk, scrollAmount)
        if(mouseY >= 34 && mouseY <= height-34 && mouseX >= 8 && mouseX <= (width.toDouble()*0.5).toInt()) {
            val prevScroll = scrollPosition
            scrollPosition = (scrollPosition - scrollAmount.toInt()).coerceIn(0, modIcons.size)
            if ((prevScroll - scrollAmount.toInt()) == scrollPosition) {
                scrollTransitionPrevious = 32.0 * scrollAmount
                scrollTransition = 32.0 * scrollAmount
            }
        } else if(mouseY >= 34 && mouseY <= height-34 && mouseX >= (width.toDouble()*0.5).toInt()+8 && mouseX <= width-8) {
            secondScrollPosition = (secondScrollPosition - (scrollAmount * 16.0).toInt()).coerceAtLeast(0)
        }
        return true;
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super.mouseClicked(mouseX, mouseY, button)
        val x2 =(width.toDouble()*0.5).toInt()+8
        val y2 = 38-secondScrollTransition
        val w2 = width-8-((width.toDouble()*0.5).toInt()+8)
        return if(mouseY >= 34 && mouseY <= height-34 && mouseX >= 8 && mouseX <= (width.toDouble()*0.6).toInt()) {
            val size = ((height - 64) / 32)
            val beginOffset = ((height - 64) / 2) - (size * 32 / 2)
            for (i in 0 until size) {
                if (mouseY >= 32 + beginOffset + (i * 32) && mouseY <= 32 + beginOffset + ((i + 1) * 32)) {
                    selectedMod = i + scrollPosition
                    secondScrollPosition = 0
                    return true
                }
            }
            selectedMod = -1
            false
        } else if(mouseX >= (x2+w2-26) && mouseX <= (x2+w2-6) && mouseY >= 32 && mouseY >= y2 && mouseY <= y2+20 && ModConfig.hasConfig(DracoModLoader.MODS[keys[selectedMod]]?.getID()!!)) {
            minecraft!!.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F))
            minecraft!!.setScreen(DracoConfigScreen(this,DracoModLoader.MODS[keys[selectedMod]]?.getID()!!))
            true
        } else {
            if(mouseY < 32) {
                selectedMod = -1
            }
            false
        }
    }

    override fun onClose() {
        minecraft!!.setScreen(parentScreen)
    }

    override fun shouldCloseOnEsc(): Boolean = true
}