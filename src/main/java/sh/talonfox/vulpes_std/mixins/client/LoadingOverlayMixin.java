package sh.talonfox.vulpes_std.mixins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {
    @Inject(method = "drawProgressBar", at = @At("TAIL"))
    public void addVulpesLogo(GuiGraphics gfx, int $$1, int $$2, int $$3, int $$4, float a, CallbackInfo ci) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        gfx.setColor(1.0F, 1.0F, 1.0F, a);
        gfx.blit(new ResourceLocation("vulpes","textures/powered_by.png"),(gfx.guiWidth()/2)-(55/2),gfx.guiHeight()-(28+8),55,8,0F,0F,55,8,55,8);
        gfx.blit(new ResourceLocation("vulpes","textures/vulpes_banner.png"),(gfx.guiWidth()/2)-51,gfx.guiHeight()-28,51*2,13*2,0F,0F,51,13,51,13);
        gfx.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
}
