package sh.talonfloof.draco_std.modmenu

import com.google.common.collect.ImmutableList
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.Mth
import sh.talonfloof.draco_std.config.ConfigType
import sh.talonfloof.draco_std.config.ModConfig
import sh.talonfloof.draco_std.config.ModConfig.Companion.getConfigs
import sh.talonfloof.draco_std.mixins.client.IScreenAccessor
import sh.talonfloof.dracoloader.api.EnvironmentType
import sh.talonfloof.dracoloader.api.Side
import kotlin.math.absoluteValue

@Side(EnvironmentType.CLIENT)
open class ConfigScreenEntry(private val name: Component, var topSeparator: Boolean, var bottomSeparator: Boolean) {
    open fun render(gfx: GuiGraphics, mouseX: Int, mouseY: Int) {
        //DracoModMenuScreen.renderBox(gfx,18,y+18,gfx.guiWidth()-18,32)
        RenderSystem.enableBlend()
        if(topSeparator)
            gfx.blit(Screen.HEADER_SEPARATOR,0,0,0.0F,0.0F,gfx.guiWidth()-18,1,32,2)
        if(bottomSeparator)
            gfx.blit(Screen.HEADER_SEPARATOR,0,31,0.0F,0.0F,gfx.guiWidth()-18,1,32,2)
        gfx.drawString(Minecraft.getInstance().font,name,64-18,16-(Minecraft.getInstance().font.lineHeight/2),(0xffffffff).toInt(),true)
    }

    open fun mouseClicked(x: Int, y: Int, button: Int) : Boolean {
        return false
    }
}

@Side(EnvironmentType.CLIENT)
class CategoryConfigScreenEntry(private val name: Component, private val children: List<ConfigScreenEntry>) : ConfigScreenEntry(name,true,false) {
    init {
        if(children.isEmpty()) {
            this.bottomSeparator = true
        } else {
            children.last.bottomSeparator = true
        }
    }

    override fun render(gfx: GuiGraphics, mouseX: Int, mouseY: Int) {
        super.render(gfx,mouseX,mouseY)
        gfx.pose().pushPose()
        gfx.pose().translate(16F,16F,0F)
        gfx.pose().mulPose(Axis.ZP.rotationDegrees(90F))
        gfx.pose().translate(-2F,-(7/2F),0F)
        RenderSystem.enableBlend()
        gfx.blit(ResourceLocation("draco","textures/gui/category_arrow.png"),0,0,4,7,0F,0F,4,7,4,7)
        gfx.pose().popPose()
        if(mouseX in 0..31) {
            gfx.fill(0,0,32,32,(0x40ffffff).toInt())
        }
    }
}

@Side(EnvironmentType.CLIENT)
class DracoConfigScreen(private val parentScreen: Screen, private val namespace: String) : Screen(Component.literal("Draco Config Screen")) {
    private var menuPopoutTarget: Int = 0
    private var menuPopout: Double = 0.0
    private var configs: List<Pair<ConfigType, ModConfig>> = getConfigs(namespace)!!
    private var selectedConfig = 0
    private var entries: MutableList<out ConfigScreenEntry> = mutableListOf()

    fun createConfigEntries(map: Map<String, Any>) : MutableList<ConfigScreenEntry> {
        val l = mutableListOf<ConfigScreenEntry>()
        for(entry in map.entries) {
            if(entry.value is Map<*,*>) {
                val entries = createConfigEntries(entry.value as Map<String, Any>)
                val category = CategoryConfigScreenEntry(Component.literal(entry.key),entries)
                l.add(category)
                l.addAll(entries)
            } else {
                l.add(ConfigScreenEntry(Component.literal(entry.key),false,false))
            }
        }
        return l
    }

    fun loadConfig() {
        val c = configs[selectedConfig]
        entries.clear()
        val finalTable = mutableMapOf<String, Any>()
        for(entry in c.second.values.entries) {
            var t = finalTable
            val path = entry.key.split(".")
            path.dropLast(1).forEach {
                if(t[it] == null) {
                    t[it] = mutableMapOf<String,Any>()
                }
                t = (t[it]!! as MutableMap<String, Any>)
            }
            t[path.last] = entry.value
        }
        entries = createConfigEntries(finalTable)
    }

    override fun init() {
        loadConfig()
        addRenderableWidget(DracoButton(this.width-154,this.height-24,150,20,Component.literal("Save & Close")) {
            minecraft!!.setScreen(parentScreen)
        })
    }

    override fun onClose() {
        minecraft!!.setScreen(parentScreen)
    }

    override fun tick() {
        DracoButton.ticks += 1
        if((menuPopout.toInt() - menuPopoutTarget).absoluteValue > 0.1) {
            for (i in 0 until 3) {
                menuPopout = Mth.lerp(0.3, menuPopout, menuPopoutTarget.toDouble())
            }
        } else {
            menuPopout = menuPopoutTarget.toDouble()
        }
    }

    override fun render(gfx: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(gfx,mouseX,mouseY,delta)
        gfx.pose().pushPose()
        gfx.pose().translate(menuPopout.toFloat(),0F,0F)
        for (i in (this as IScreenAccessor).renderables) {
            i.render(gfx,mouseX,mouseY,delta)
        }
        gfx.pose().popPose()
        RenderSystem.enableBlend()
        gfx.pose().pushPose()
        gfx.pose().mulPose(Axis.ZP.rotationDegrees(90F))
        gfx.pose().translate(0F,-18F-menuPopout.toFloat(),0F)
        gfx.blit(HEADER_SEPARATOR,0,0,0.0F,0.0F,gfx.guiHeight(),2,32,2)
        gfx.pose().popPose()
        gfx.blit(
            ResourceLocation("textures/gui/menu_list_background.png"),
            0,
            0,
            0F,
            0F,
            17+menuPopout.toInt(),
            gfx.guiHeight(),
            32,
            32
        )
        if(mouseX >= menuPopoutTarget && mouseX <= menuPopoutTarget+16) {
            gfx.fill(menuPopoutTarget,0,menuPopoutTarget+18,gfx.guiHeight(),(0x40ffffff).toInt())
        }
        RenderSystem.disableBlend()
        gfx.drawCenteredString(minecraft!!.font,if(menuPopoutTarget == 0) ">" else "<",8+menuPopout.toInt(),gfx.guiHeight()/2,(0xffffffff).toInt())
        gfx.pose().pushPose()
        gfx.pose().translate(menuPopout.toFloat(),0F,0F)
        RenderSystem.enableBlend()
        gfx.blit(HEADER_SEPARATOR,18,16,0.0F,0.0F,gfx.guiWidth()-18,1,32,2)
        RenderSystem.disableBlend()
        gfx.drawString(minecraft!!.font, Component.literal(namespace).append(Component.literal(" > ").withStyle(ChatFormatting.GRAY)).append(Component.literal(configs[selectedConfig].second.getFile().name)), 19, (minecraft!!.font.lineHeight/2), -1)
        gfx.enableScissor(18+menuPopout.toInt(),17,gfx.guiWidth(),gfx.guiHeight()-25)
        for(i in 0..<entries.size) {
            var mX = -1
            var mY = -1
            if(mouseX > 18+menuPopout.toInt() && mouseY >= 18+(32*i) && mouseY <= 18+((32*i)+31)) {
                mX = mouseX - (18+menuPopout.toInt())
                mY = mouseY - (32 * i)
            }
            gfx.pose().pushPose()
            gfx.pose().translate(18F,18F+(32*i).toFloat(),0F)
            entries[i].render(gfx, mX, mY)
            gfx.pose().popPose()
        }
        gfx.disableScissor()
        gfx.pose().popPose()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(menuPopoutTarget == 0) {
            if(super.mouseClicked(mouseX,mouseY,button))
                return true
        }
        if(mouseX <= 16 && menuPopoutTarget == 0) {
            menuPopoutTarget = (this.width/2)-18
            return true
        } else if(mouseX >= menuPopoutTarget && mouseX <= menuPopoutTarget+16 && menuPopoutTarget != 0) {
            menuPopoutTarget = 0
            return true
        } else {
            for(i in 0..<entries.size) {
                if(mouseX >= 18 && mouseY >= 32*i && mouseY <= (32*i)+31) {
                    return entries[i].mouseClicked(mouseX.toInt(),mouseY.toInt(),button)
                }
            }
        }
        return false
    }

    override fun shouldCloseOnEsc(): Boolean = true
}