package sh.talonfloof.draco_std.modmenu

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import sh.talonfloof.draco_std.mixins.client.IScreenAccessor
import sh.talonfloof.dracoloader.api.EnvironmentType
import sh.talonfloof.dracoloader.api.Side
import kotlin.math.absoluteValue

@Side(EnvironmentType.CLIENT)
class DracoConfigScreen(private val parentScreen: Screen, private val namespace: String) : Screen(Component.literal("Draco Config Screen")) {
    private var menuPopoutTarget: Int = 0
    private var menuPopout: Double = 0.0

    override fun init() {
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
        gfx.blit(HEADER_SEPARATOR,0,0,0.0F,0.0F,gfx.guiWidth(),2,32,2)
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
        }
        return false
    }

    override fun shouldCloseOnEsc(): Boolean = true
}