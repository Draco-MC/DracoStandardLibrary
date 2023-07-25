package sh.talonfox.vulpes_std.mixins.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryMixin<T extends AbstractContainerMenu> extends EffectRenderingInventoryScreen<T> {
    @Unique
    private static final ResourceLocation CREATIVE_ICONS = new ResourceLocation("vulpes","textures/gui/creative_buttons.png");

    public CreativeInventoryMixin(T $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(GuiGraphics gfx, int $$1, int $$2, float $$3, CallbackInfo ci) {
        int xpos = this.leftPos + 170;
        int ypos = this.topPos + 4;
        gfx.fill(xpos,ypos+1,xpos+22,ypos+1+9,0x80000000);
        gfx.fill(xpos+1,ypos,xpos+1+20,ypos+1,0x80000000);
        gfx.fill(xpos+1,ypos+10,xpos+1+20,ypos+10+1,0x80000000);
        gfx.blit(CREATIVE_ICONS,xpos, ypos, 22, 11, 0F, 0F, 22, 11, 256, 256);
    }
}
