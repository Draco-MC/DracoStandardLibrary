package sh.talonfloof.draco_std.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfloof.draco_std.creative_tab.DracoCreativeModeTab;
import sh.talonfloof.draco_std.creative_tab.DracoCreativeTabVars;
import sh.talonfloof.dracoloader.api.EnvironmentType;
import sh.talonfloof.dracoloader.api.Side;

@Side(EnvironmentType.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryMixin<T extends AbstractContainerMenu> extends EffectRenderingInventoryScreen<T> {
    private static int currentPage = 0;
    private static boolean leftSidePressed = false;
    private static boolean rightSidePressed = false;

    @Unique
    private static final ResourceLocation CREATIVE_ICONS = ResourceLocation.tryBuild("draco","textures/gui/creative_buttons.png");

    public CreativeInventoryMixin(T $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void draco$render(GuiGraphics gfx, int mouseX, int mouseY, float $$3, CallbackInfo ci) {
        if(DracoCreativeTabVars.pageCount > 1) {
            int xpos = this.leftPos + 170;
            int ypos = this.topPos + 4;
            gfx.fill(xpos + 1, ypos, xpos + 11, ypos + 1, leftSidePressed ? 0x80ffffff : 0x80000000);
            gfx.fill(xpos, ypos + 1, xpos + 11, ypos + 10, leftSidePressed ? 0x80ffffff : 0x80000000);
            gfx.fill(xpos + 1, ypos + 10, xpos + 11, ypos + 11, leftSidePressed ? 0x80ffffff : 0x80000000);

            gfx.fill(xpos + 11, ypos, xpos + 21, ypos + 1, rightSidePressed ? 0x80ffffff : 0x80000000);
            gfx.fill(xpos + 11, ypos + 1, xpos + 22, ypos + 10, rightSidePressed ? 0x80ffffff : 0x80000000);
            gfx.fill(xpos + 11, ypos + 10, xpos + 21, ypos + 11, rightSidePressed ? 0x80ffffff : 0x80000000);
            gfx.blit(CREATIVE_ICONS, xpos, ypos, 22, 11, 0F, 0F, 22, 11, 256, 256);

            if (mouseX >= xpos && mouseX < xpos + 22 && mouseY >= ypos && mouseY < ypos + 11) {
                gfx.renderTooltip(font, Component.literal("Page " + (currentPage + 1) + "/" + (DracoCreativeTabVars.pageCount)), mouseX, mouseY);
            }
        }
    }

    @Inject(method = "renderTabButton", at = @At("HEAD"), cancellable = true)
    private void draco$renderTabIcon(GuiGraphics gfx, CreativeModeTab tab, CallbackInfo info) {
        if (((DracoCreativeModeTab)tab).getPage() != currentPage && tab.getType() == CreativeModeTab.Type.CATEGORY) {
            info.cancel();
        }
    }

    @Inject(method = "checkTabClicked", at = @At("HEAD"), cancellable = true)
    private void draco$isClickInTab(CreativeModeTab tab, double mx, double my, CallbackInfoReturnable<Boolean> info) {
        if (((DracoCreativeModeTab)tab).getPage() != currentPage && tab.getType() == CreativeModeTab.Type.CATEGORY) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "checkTabHovering", at = @At("HEAD"), cancellable = true)
    private void draco$renderTabTooltipIfHovered(GuiGraphics gfx, CreativeModeTab tab, int mx, int my, CallbackInfoReturnable<Boolean> info) {
        if (((DracoCreativeModeTab)tab).getPage() != currentPage && tab.getType() == CreativeModeTab.Type.CATEGORY) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void draco$onMouseClicked(double x, double y, int type, CallbackInfoReturnable<Boolean> cir) {
        if(DracoCreativeTabVars.pageCount > 1) {
            int xpos = this.leftPos + 170;
            int ypos = this.topPos + 4;
            if (x >= xpos && x < xpos + 11 && y >= ypos && y < ypos + 11) { // Previous Tab
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                leftSidePressed = true;
            }
            if (x >= xpos + 11 && x < xpos + 22 && y >= ypos && y < ypos + 11) { // Next Tab
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                rightSidePressed = true;
            }
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void draco$onMouseRelease(double x, double y, int type, CallbackInfoReturnable<Boolean> cir) {
        if(DracoCreativeTabVars.pageCount > 1) {
            if (leftSidePressed && currentPage > 0) {
                currentPage -= 1;
            } else if (rightSidePressed && currentPage < (DracoCreativeTabVars.pageCount) - 1) {
                currentPage += 1;

            }
            leftSidePressed = false;
            rightSidePressed = false;
        }
    }
}
