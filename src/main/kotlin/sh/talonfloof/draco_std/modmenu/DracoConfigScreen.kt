package sh.talonfloof.draco_std.modmenu

import com.google.common.collect.ImmutableList
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.CommonColors
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.Mth
import org.lwjgl.glfw.GLFW
import sh.talonfloof.draco_std.config.ConfigType
import sh.talonfloof.draco_std.config.ModConfig
import sh.talonfloof.draco_std.config.ModConfig.Companion.getConfigs
import sh.talonfloof.draco_std.mixins.client.IScreenAccessor
import sh.talonfloof.dracoloader.api.EnvironmentType
import sh.talonfloof.dracoloader.api.Side
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

@Side(EnvironmentType.CLIENT)
open class ConfigScreenEntry(private val screen: DracoConfigScreen, private val name: Component, var topSeparator: Boolean, var bottomSeparator: Boolean) {
    open fun render(gfx: GuiGraphics, mouseX: Int, mouseY: Int, focused: Boolean) {
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
    open fun charTyped(c: Char, i: Int) : Boolean {
        return false
    }
    open fun keyPressed(key: Int) : Boolean {
        return false
    }
    open fun mouseDragged(x: Int, y: Int) : Boolean {
        return false
    }
    open fun onFocusRemoved() {

    }
}

@Side(EnvironmentType.CLIENT)
class SliderConfigScreenEntry(private val screen: DracoConfigScreen, private val name: Component, private val entry: ModConfig.ConfigValue<*>) : ConfigScreenEntry(screen,name,false,false) {
    var dragging = false

    override fun render(gfx: GuiGraphics, mouseX: Int, mouseY: Int, focused: Boolean) {
        super.render(gfx, mouseX, mouseY, focused)
        RenderSystem.enableBlend()
        DracoModMenuScreen.renderBox(gfx,gfx.guiWidth()-18-32-128,6,128,20)
        val min = entry.minimumValue as Long
        val max = entry.maximumValue as Long
        val range = max-min
        gfx.drawCenteredString(Minecraft.getInstance().font,"Value: "+entry.get().toString(),gfx.guiWidth()-18-32-64,16-(Minecraft.getInstance().font.lineHeight/2),-1)
        RenderSystem.enableBlend()
        gfx.pose().pushPose()
        gfx.pose().translate(
            (((gfx.guiWidth() - 18 - 32 - 128).toFloat()) + 4F) + ((120F / range) * ((entry.get() as Long) - min)),
            16F,
            1F
        )
        gfx.pose().mulPose(Axis.ZP.rotationDegrees(90F))
        gfx.pose().translate(-10F,-4F,0F)
        DracoModMenuScreen.renderBox(gfx,0,0,20,8)
        gfx.pose().popPose()
        RenderSystem.disableBlend()
    }

    override fun mouseClicked(x: Int, y: Int, button: Int) : Boolean {
        dragging = false
        val min = entry.minimumValue as Long
        val max = entry.maximumValue as Long
        val range = max-min
        val barX = (((screen.width - 18 - 32 - 128).toFloat())) + ((120F / range) * ((entry.get() as Long) - min))
        if(x >= barX && x < barX+8) {
            Minecraft.getInstance().soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F))
            dragging = true
            return true
        }
        return super.mouseClicked(x,y,button)
    }

    override fun mouseDragged(x: Int, y: Int) : Boolean {
        if(dragging) {
            val curX = (x-((screen.width - 18 - 32 - 128).toFloat()) - 4F).coerceIn(0F,120F)
            val min = entry.minimumValue as Long
            val max = entry.maximumValue as Long
            val range = max-min
            entry.set(((curX/(120.0 / range))+min).roundToLong())
        }
        return super.mouseDragged(x,y)
    }
}

@Side(EnvironmentType.CLIENT)
class BooleanConfigScreenEntry(private val screen: DracoConfigScreen, private val name: Component, private val entry: ModConfig.ConfigValue<*>) : ConfigScreenEntry(screen,name,false,false) {
    override fun render(gfx: GuiGraphics, mouseX: Int, mouseY: Int, focused: Boolean) {
        super.render(gfx, mouseX, mouseY, focused)
        RenderSystem.enableBlend()
        DracoModMenuScreen.renderBox(gfx,gfx.guiWidth()-18-32-128,6,128,20)
        if(mouseX >= (gfx.guiWidth()-18-32-128) && mouseX < (gfx.guiWidth()-18-32) && mouseY >= 6 && mouseY < 26) {
            gfx.fill((gfx.guiWidth()-18-32-128),6,(gfx.guiWidth()-18-32),26,(0x20ffffff).toInt())
        }
        gfx.drawCenteredString(Minecraft.getInstance().font,Component.translatable(if(entry.get() as Boolean) "gui.yes" else "gui.no"),gfx.guiWidth()-18-32-64,16-(Minecraft.getInstance().font.lineHeight/2),-1)
        RenderSystem.disableBlend()
    }

    override fun mouseClicked(x: Int, y: Int, button: Int): Boolean {
        if(x >= (this.screen.width-18-32-128) &&x < (this.screen.width-18-32) && y >= 6 && y < 26) {
            Minecraft.getInstance().soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F))
            entry.set(!(entry.get() as Boolean))
            return true
        }
        return super.mouseClicked(x,y,button)
    }
}

@Side(EnvironmentType.CLIENT)
class TextBoxConfigScreenEntry(private val screen: DracoConfigScreen, private val name: Component, private val entry: ModConfig.ConfigValue<*>) : ConfigScreenEntry(screen,name,false,false) {
    var value: String = entry.get().toString()

    override fun render(gfx: GuiGraphics, mouseX: Int, mouseY: Int, focused: Boolean) {
        super.render(gfx, mouseX, mouseY, focused)
        RenderSystem.enableBlend()
        DracoModMenuScreen.renderBox(gfx,gfx.guiWidth()-18-32-128,6,128,20)
        val comp = Component.literal(Minecraft.getInstance().font.plainSubstrByWidth(value,128,true)).append(Component.literal(if(focused) "|" else "").withStyle(ChatFormatting.GRAY))
        gfx.drawString(Minecraft.getInstance().font,comp,gfx.guiWidth()-18-32-128+1,16-(Minecraft.getInstance().font.lineHeight/2),-1,false)
        RenderSystem.disableBlend()
    }

    override fun charTyped(c: Char, i: Int) : Boolean {
        value += c
        return true
    }

    override fun keyPressed(key: Int) : Boolean {
        if(key == GLFW.GLFW_KEY_BACKSPACE) {
            value = value.dropLast(1)
            return true
        } else if(key == GLFW.GLFW_KEY_ENTER) {
            screen.focused = -1
            onFocusRemoved()
            return true
        }
        return super.keyPressed(key)
    }

    override fun onFocusRemoved() {
        if(entry.get() is Number) {
            if(entry.get() is Double) {
                value.toDoubleOrNull()?.let { entry.set(it) }
            } else if(entry.get() is Float) {
                value.toFloatOrNull()?.let { entry.set(it) }
            } else {
                value.toLongOrNull()?.let { entry.set(it) }
            }
        } else if(entry.get() is String) {
            entry.set(value)
        }
        value = entry.get().toString()
    }
}

@Side(EnvironmentType.CLIENT)
open class CategoryConfigScreenEntry(private val screen: DracoConfigScreen, private val name: Component, private val children: MutableList<out ConfigScreenEntry>) : ConfigScreenEntry(screen,name,true,true) {
    var expanded: Boolean = false

    override fun render(gfx: GuiGraphics, mouseX: Int, mouseY: Int, focused: Boolean) {
        super.render(gfx,mouseX,mouseY,focused)
        gfx.pose().pushPose()
        gfx.pose().translate(16F,16F,0F)
        gfx.pose().mulPose(Axis.ZP.rotationDegrees(if(expanded) 90F else 0F))
        gfx.pose().translate(-2F,-(7/2F),0F)
        RenderSystem.enableBlend()
        gfx.blit(ResourceLocation("draco","textures/gui/category_arrow.png"),0,0,4,7,0F,0F,4,7,4,7)
        gfx.pose().popPose()
        if(mouseX in 0..31) {
            gfx.fill(0,0,32,32,(0x40ffffff).toInt())
        }
    }

    fun setExpand(newVal: Boolean) {
        if(!newVal && expanded) {
            for(entry in children) {
                if(entry is CategoryConfigScreenEntry) {
                    entry.setExpand(false)
                }
            }
            screen.entries.removeIf {
                children.contains(it)
            }
            this.bottomSeparator = true
        } else if(newVal && !expanded) {
            if(this.children.isNotEmpty()) {
                this.children.last.bottomSeparator = true
                this.bottomSeparator = false
            } else {
                this.bottomSeparator = true
            }
            screen.entries.addAll((screen.entries.withIndex().find { it.value == this }!!).index+1,children as Collection<Nothing>)
        }
        expanded = newVal
    }

    override fun mouseClicked(x: Int, y: Int, button: Int) : Boolean {
        if(x < 32) {
            Minecraft.getInstance().soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F))
            setExpand(!expanded)
            return true
        }
        return super.mouseClicked(x,y,button)
    }
}

@Side(EnvironmentType.CLIENT)
class ListConfigScreenEntry(private val screen: DracoConfigScreen, private val name: Component, private val entry: ModConfig.ConfigValue<*>) : CategoryConfigScreenEntry(screen,name,mutableListOf()){
    override fun render(gfx: GuiGraphics, mouseX: Int, mouseY: Int, focused: Boolean) {
        super.render(gfx,mouseX,mouseY,focused)
        gfx.drawString(Minecraft.getInstance().font,"+",gfx.guiWidth()-18-32-16+(Minecraft.getInstance().font.width("+")/2),16-(Minecraft.getInstance().font.lineHeight/2),-1,false)
    }

    override fun mouseClicked(x: Int, y: Int, button: Int) : Boolean {
        if(x >= this.screen.width-18-64 && x < this.screen.width-18-32) {
            Minecraft.getInstance().soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F))
            return true
        }
        return super.mouseClicked(x,y,button)
    }
}

@Side(EnvironmentType.CLIENT)
class ListEntryConfigScreenEntry(private val screen: DracoConfigScreen, private val parent: ListConfigScreenEntry, var value: Any) : ConfigScreenEntry(screen,Component.empty(),false,false) {
    override fun render(gfx: GuiGraphics, mouseX: Int, mouseY: Int, focused: Boolean) {
        super.render(gfx,mouseX,mouseY,focused)
        gfx.drawString(Minecraft.getInstance().font,value.toString(),64-18,16-(Minecraft.getInstance().font.lineHeight/2),(0xffffffff).toInt(),true)
        gfx.drawString(Minecraft.getInstance().font,"-",16-(Minecraft.getInstance().font.width("-")/2),16-(Minecraft.getInstance().font.lineHeight/2),-1,false)
        if(mouseX in 0..31) {
            gfx.fill(0,0,32,32,(0x40ffffff).toInt())
        }
    }
}

@Side(EnvironmentType.CLIENT)
class DracoConfigScreen(private val parentScreen: Screen, private val namespace: String) : Screen(Component.literal("Draco Config Screen")) {
    private var menuPopoutTarget: Int = 0
    private var menuPopout: Double = 0.0
    private var configScrollTarget: Int = 0
    private var configScroll: Double = 0.0
    private var optionsScrollTarget: Int = 18
    private var optionsScroll: Double = 18.0
    private var configs: List<Pair<ConfigType, ModConfig>> = getConfigs(namespace)!!
    private var selectedConfig = 0
    var entries: MutableList<out ConfigScreenEntry> = mutableListOf()
    var focused = -1
    var unfocusButton = false

    init {
        loadConfig()
    }

    fun createConfigEntries(map: Map<String, Any>) : MutableList<ConfigScreenEntry> {
        val l = mutableListOf<ConfigScreenEntry>()
        for(entry in map.entries) {
            val fancyName = run {
                val splitName = mutableListOf<String>()
                splitName.addAll(entry.key.split("_"))
                splitName.replaceAll {
                    it.replaceFirstChar {
                        it.titlecase()
                    }
                }
                splitName.joinToString(" ")
            }
            if(entry.value is Map<*,*>) {
                val entries = createConfigEntries(entry.value as Map<String, Any>)
                val category = CategoryConfigScreenEntry(this,Component.literal(fancyName),entries)
                l.add(category)
            } else {
                if(entry.value is ModConfig.ConfigValue<*>) {
                    val value = (entry.value as ModConfig.ConfigValue<*>)
                    if(value.defaultValue is MutableList<*>) {
                        l.add(ListConfigScreenEntry(this, Component.literal(fancyName), value))
                    } else if(value.defaultValue !is Boolean) {
                        if(value.minimumValue != null && value.maximumValue != null) {
                            l.add(SliderConfigScreenEntry(this, Component.literal(fancyName), value))
                        } else {
                            l.add(TextBoxConfigScreenEntry(this, Component.literal(fancyName), value))
                        }
                    } else {
                        l.add(BooleanConfigScreenEntry(this,Component.literal(fancyName),value))
                    }
                } else {
                    throw RuntimeException("Unknown value type detected in config!")
                }
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
        addRenderableWidget(DracoButton(19,this.height-24,150,20,Component.translatable("gui.abuseReport.draft.discard").append(" ").append(Component.translatable("mco.backup.changes.tooltip"))) {
            if(focused != -1)
                entries[focused].onFocusRemoved()
            focused = -1
            configs.forEach {
                it.second.load()
            }
            loadConfig()
            unfocusButton = true
        })
        addRenderableWidget(DracoButton(this.width-154,this.height-24,150,20,Component.translatable("selectWorld.edit.save").append(" & ").append(Component.translatable("spectatorMenu.close"))) {
            unfocusButton = true
            this.onClose()
        })
    }

    override fun onClose() {
        if(focused != -1)
            entries[focused].onFocusRemoved()
        configs.forEach {
            it.second.save()
        }
        minecraft!!.setScreen(parentScreen)
    }

    override fun tick() {
        if(unfocusButton) {
            this.clearFocus()
            unfocusButton = false
        }
        if((menuPopout.toInt() - menuPopoutTarget).absoluteValue > 0.1) {
            for (i in 0 until 3) {
                menuPopout = Mth.lerp(0.3, menuPopout, menuPopoutTarget.toDouble())
            }
        } else {
            menuPopout = menuPopoutTarget.toDouble()
        }
        if((optionsScroll.toInt() - optionsScrollTarget).absoluteValue > 0.1) {
            for (i in 0 until 3) {
                optionsScroll = Mth.lerp(0.3, optionsScroll, optionsScrollTarget.toDouble())
            }
        } else {
            optionsScroll = optionsScrollTarget.toDouble()
        }
        if((configScroll.toInt() - configScrollTarget).absoluteValue > 0.1) {
            for (i in 0 until 3) {
                configScroll = Mth.lerp(0.3, configScroll, configScrollTarget.toDouble())
            }
        } else {
            configScroll = configScrollTarget.toDouble()
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
        if(menuPopoutTarget != 0) {
            gfx.enableScissor(0,0,menuPopout.toInt(),gfx.guiHeight())
            gfx.pose().pushPose()
            gfx.pose().translate((-menuPopoutTarget)+menuPopout,configScroll,0.0)
            for (config in configs.withIndex()) {
                if(selectedConfig == config.index) {
                    gfx.fill(1, 1 + config.index * 32, menuPopoutTarget, 33 + config.index * 32, (0x20ffffff).toInt())
                }
                gfx.blit(ResourceLocation("draco","textures/gui/config.png"),1,1+config.index*32,32,32,0F,0F,128,128,128,128)
                gfx.drawString(minecraft!!.font,config.value.second.getName(),35,1+config.index*32,-1,true)
                gfx.drawString(minecraft!!.font,config.value.first.name,35,(1+config.index*32)+minecraft!!.font.lineHeight,CommonColors.GRAY,true)
            }
            gfx.pose().popPose()
            gfx.disableScissor()
        }
        RenderSystem.disableBlend()
        gfx.drawCenteredString(minecraft!!.font,if(menuPopoutTarget == 0) ">" else "<",8+menuPopout.toInt(),gfx.guiHeight()/2,(0xffffffff).toInt())
        gfx.pose().pushPose()
        gfx.pose().translate(menuPopout.toFloat(),0F,0F)
        RenderSystem.enableBlend()
        gfx.blit(HEADER_SEPARATOR,18,16,0.0F,0.0F,gfx.guiWidth()-18,1,32,2)
        RenderSystem.disableBlend()
        gfx.drawString(minecraft!!.font, Component.literal(namespace).append(Component.literal(" > ").withStyle(ChatFormatting.GRAY)).append(Component.literal(configs[selectedConfig].second.getName())), 19, (minecraft!!.font.lineHeight/2), -1)
        gfx.enableScissor(18+menuPopout.toInt(),17,gfx.guiWidth(),gfx.guiHeight()-25)
        for(i in 0..<entries.size) {
            var mX = -1
            var mY = -1
            if(mouseX > 18+menuPopout.toInt() && mouseY >= optionsScroll+(32*i) && mouseY <= optionsScroll+((32*i)+31)) {
                mX = mouseX - (18+menuPopout.toInt())
                mY = mouseY - (optionsScroll.toInt()+(32 * i))
            }
            gfx.pose().pushPose()
            gfx.pose().translate(18F,optionsScroll.toFloat()+(32*i).toFloat(),0F)
            entries[i].render(gfx, mX, mY, focused == i)
            gfx.pose().popPose()
        }
        gfx.disableScissor()
        gfx.pose().popPose()
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaMouseX: Double, deltaMouseY: Double): Boolean {
        if(focused != -1) {
            return entries[focused].mouseDragged(mouseX.toInt()-(18+menuPopout.toInt()),mouseY.toInt()-((optionsScroll.toInt())+(32*focused)))
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaMouseX, deltaMouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val oldFocus = focused
        focused = -1
        if(menuPopoutTarget == 0) {
            if(super.mouseClicked(mouseX,mouseY,button)) {
                return true
            }
        }
        if(mouseX <= 16 && menuPopoutTarget == 0) {
            Minecraft.getInstance().soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F))
            menuPopoutTarget = (this.width/2)-18
            if(oldFocus != -1)
                entries[oldFocus].onFocusRemoved()
            return true
        } else if(mouseX >= menuPopoutTarget && mouseX <= menuPopoutTarget+16 && menuPopoutTarget != 0) {
            Minecraft.getInstance().soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F))
            menuPopoutTarget = 0
            if(oldFocus != -1)
                entries[oldFocus].onFocusRemoved()
            return true
        } else if(mouseX < menuPopout && menuPopoutTarget != 0) {
            for(i in configs.indices) {
                if(mouseY >= 1+configScroll+(32*i) && mouseY <= 32+configScroll+(32*i)) {
                    if(oldFocus != -1)
                        entries[oldFocus].onFocusRemoved()
                    if(selectedConfig != i) {
                        selectedConfig = i
                        optionsScrollTarget = 18
                        loadConfig()
                        menuPopoutTarget = 0
                    }
                    return true
                }
            }
        } else {
            for(i in 0..<entries.size) {
                if(mouseX >= 18+menuPopout.toInt() && mouseY >= optionsScroll+(32*i) && mouseY <= optionsScroll+((32*i)+31)) {
                    focused = i
                    if(oldFocus != -1)
                        entries[oldFocus].onFocusRemoved()
                    return entries[i].mouseClicked((mouseX.toInt())-(18+menuPopout.toInt()),(mouseY.toInt())-((optionsScroll.toInt())+(32*i)),button)
                }
            }
        }
        if(oldFocus != -1)
            entries[oldFocus].onFocusRemoved()
        return false
    }

    override fun resize(mc: Minecraft, x: Int, y: Int) {
        super.resize(mc,x,y)
        if(menuPopoutTarget != 0) {
            menuPopoutTarget = (this.width/2)-18
        }
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, idk: Double, scrollAmount: Double) : Boolean {
        if(mouseX > 18+menuPopout.toInt()) {
            optionsScrollTarget = (optionsScrollTarget + (scrollAmount * 32.0).toInt()).coerceIn(18-(32*(entries.size-1)),18)
        } else if(mouseX < menuPopout.toInt()) {
            configScrollTarget = (configScrollTarget + (scrollAmount * 32.0).toInt()).coerceIn(-(32*(configs.size-1)),0)
        }
        return super.mouseScrolled(mouseX,mouseY,idk,scrollAmount)
    }

    override fun charTyped(c: Char, i: Int) : Boolean {
        if(focused != -1) {
            if(entries[focused].charTyped(c,i))
                return true
        }
        return super.charTyped(c,i)
    }

    override fun keyPressed(key: Int, scanCode: Int, modifier: Int) : Boolean {
        if(focused != -1) {
            if(entries[focused].keyPressed(key))
                return true
        }
        return super.keyPressed(key,scanCode,modifier)
    }

    override fun shouldCloseOnEsc(): Boolean = false
}