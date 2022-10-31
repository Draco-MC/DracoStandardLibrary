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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfox.vulpes_std.modmenu.VulpesModMenuScreen;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Shadow @Final private PanoramaRenderer panorama;
    private static ResourceLocation vulpes_logo = new ResourceLocation("vulpes:textures/vulpes.png");
    private static long ticks = 0;

    @Inject(at = @At("HEAD"), method = "mouseClicked")
    public void clickVulpesModButton(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        if(mouseX >= 5 && mouseX <= 5+64 && mouseY >= 5 && mouseY <= 5+16 && mouseButton == 0) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            Minecraft.getInstance().setScreen(new VulpesModMenuScreen(((TitleScreen)(Object)this),panorama));
        }
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void renderVulpesModButton(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if(mouseX >= 5 && mouseX <= 5+64 & mouseY >= 5 && mouseY <= 5+16) {
            var intensity = (int)(Math.abs(Math.sin(Math.toRadians(ticks*9)))*128);
            GuiComponent.fill(matrices,5,5,5+64,5+16,(0x80000000 | (intensity << 16) | (intensity << 8) | intensity));
        } else {
            ticks = 0;
            GuiComponent.fill(matrices,5,5,5+64,5+16,0x80000000);
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, vulpes_logo);
        GuiComponent.blit(matrices,5,5,16,16,0F,0F,512,512,512,512);
        GuiComponent.drawCenteredString(matrices, Minecraft.getInstance().font, "Mods", 45, 9, 0xffffff);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tickVulpesModButton(CallbackInfo ci) {
        ticks++;
    }
}
