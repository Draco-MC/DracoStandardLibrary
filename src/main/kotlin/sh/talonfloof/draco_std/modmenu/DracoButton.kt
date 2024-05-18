package sh.talonfloof.draco_std.modmenu

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import sh.talonfloof.draco_std.modmenu.DracoModMenuScreen.Companion.renderBox
import kotlin.math.abs
import kotlin.math.sin

class DracoButton(x: Int, y: Int, width: Int, height: Int, text: Component, press: OnPress) : Button(x,y,width,height,text,press,DEFAULT_NARRATION) {
    private companion object {
        @JvmField
        var ticks: Long = 0
    }

    override fun renderWidget(gfx: GuiGraphics, mouseX: Int, mouseY: Int, d: Float) {
        renderBox(gfx,x,y,width,height)
        if (this.isHoveredOrFocused) {
            val intensity = (abs(sin(Math.toRadians((ticks * 9).toDouble()))) * 128).toInt()
            gfx.fill(
                x,
                y,
                x + width,
                y + height,
                ((intensity.toUInt() shl 24) or (intensity.toUInt() shl 16) or (intensity.toUInt() shl 8) or intensity.toUInt()).toInt()
            )
        } else {
            ticks = 5
        }
        /*gfx.blit(
            vulpes_logo,
            x,
            y,
            height,
            height,
            0f,
            0f,
            512,
            512,
            512,
            512
        )*/
        gfx.drawCenteredString(
            Minecraft.getInstance().font,
            message,
            x + (width/2),
            y + ((height/2)-(9/2)),
            0xffffff
        )
    }
}