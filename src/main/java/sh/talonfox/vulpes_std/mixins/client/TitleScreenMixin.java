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

package sh.talonfox.vulpes_std.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfox.vulpes_std.modmenu.VulpesModMenuScreen;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Shadow @Final private PanoramaRenderer panorama;
    private static ResourceLocation vulpes_logo = new ResourceLocation("vulpes:textures/vulpes.png");
    private static long ticks = 0;
    private static int modButtonXCoord = -1;
    private static int modButtonYCoord = -1;

    @Inject(at = @At("HEAD"), method = "createNormalMenuOptions")
    public void calculateButtonPos(int a, int b, CallbackInfo ci) {
        modButtonXCoord = ((TitleScreen)(Object)this).width / 2 - 100;
        modButtonYCoord = (a + b * 3) - 12;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/Button$Builder;bounds(IIII)Lnet/minecraft/client/gui/components/Button$Builder;"), method = "createNormalMenuOptions")
    public Button.Builder buttonOverride(Button.Builder instance, int x, int y, int w, int h) {
        return instance.bounds(x, y-12, w, h);
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked")
    public void clickVulpesModButton(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        if(mouseX >= modButtonXCoord && mouseX <= modButtonXCoord+200 && mouseY >= modButtonYCoord && mouseY <= modButtonYCoord+20 && mouseButton == 0) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            Minecraft.getInstance().setScreen(new VulpesModMenuScreen(((TitleScreen)(Object)this),panorama));
        }
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void renderVulpesModButton(GuiGraphics gfx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if(mouseX >= modButtonXCoord && mouseX <= modButtonXCoord+200 & mouseY >= modButtonYCoord && mouseY <= modButtonYCoord+20) {
            var intensity = (int)(Math.abs(Math.sin(Math.toRadians(ticks*9)))*128);
            gfx.fill(modButtonXCoord,modButtonYCoord,modButtonXCoord+200,modButtonYCoord+20,(0x80000000 | (intensity << 16) | (intensity << 8) | intensity));
        } else {
            ticks = 5;
            gfx.fill(modButtonXCoord,modButtonYCoord,modButtonXCoord+200,modButtonYCoord+20,0x80000000);
        }
        gfx.blit(vulpes_logo,modButtonXCoord,modButtonYCoord,20,20,0F,0F,512,512,512,512);
        gfx.drawCenteredString(Minecraft.getInstance().font, "Mods", modButtonXCoord+100, modButtonYCoord+5, 0xffffff);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tickVulpesModButton(CallbackInfo ci) {
        ticks++;
    }
}
