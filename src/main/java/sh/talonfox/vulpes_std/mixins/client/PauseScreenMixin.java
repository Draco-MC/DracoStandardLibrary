package sh.talonfox.vulpes_std.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CenteredStringWidget;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.vulpes_std.modmenu.VulpesButton;
import sh.talonfox.vulpes_std.modmenu.VulpesModMenuScreen;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {
    @Shadow private Button disconnectButton;
    private static boolean isAfter = false;

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        VulpesButton.ticks += 1;
    }

    @Inject(at = @At("HEAD"), method = "init")
    public void resetIsAfter(CallbackInfo ci) {
        isAfter = false;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/GridWidget;addChild(Lnet/minecraft/client/gui/components/AbstractWidget;IIII)Lnet/minecraft/client/gui/components/AbstractWidget;"), method = "createPauseMenu")
    public <T extends net.minecraft.client.gui.components.AbstractWidget> T buttonOverride(GridWidget instance, T widget, int row, int column, int rows, int columns) {
        Component text = widget.getMessage();
        ComponentContents textContent = text.getContents();
        if(textContent instanceof TranslatableContents && (((TranslatableContents)textContent).getKey().equals("menu.returnToMenu") || ((TranslatableContents)textContent).getKey().equals("menu.disconnect"))) {
            System.out.println("ADD IT NOW!");
            instance.addChild(new VulpesButton(0,0,204,20,Component.literal("Mods"),(x) -> {
                Minecraft.getInstance().setScreen(new VulpesModMenuScreen(((PauseScreen)(Object)this),null));
            }),row,column,rows,columns);
            return instance.addChild(widget, row+1, column, rows, columns);
        }
        return instance.addChild(widget, row, column, rows, columns);
    }

}
