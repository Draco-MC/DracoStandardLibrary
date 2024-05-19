package sh.talonfloof.draco_std.mixins.client;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VirtualScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.loading.DracoLoadingScreen;

import javax.annotation.Nullable;
import javax.swing.*;

@Mixin(value = Minecraft.class, priority = 9999)
public abstract class EarlyScreenMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/VirtualScreen;newWindow(Lcom/mojang/blaze3d/platform/DisplayData;Ljava/lang/String;Ljava/lang/String;)Lcom/mojang/blaze3d/platform/Window;"))
    private Window draco$delayWindowOpen(VirtualScreen instance, DisplayData a, @Nullable String b, String c) {
        return instance.newWindow(new DisplayData(1,1,a.fullscreenWidth,a.fullscreenHeight,a.isFullscreen),b,c);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setErrorCallback(Lorg/lwjgl/glfw/GLFWErrorCallbackI;)V", shift = At.Shift.AFTER, unsafe = true))
    private void draco$initWindow(CallbackInfo ci) {
        var win = Minecraft.getInstance().getWindow();
        ((IWindowAccessor) (Object) win).setX(DracoLoadingScreen.screen.getLocationOnScreen().x+8);
        ((IWindowAccessor) (Object) win).setY(DracoLoadingScreen.screen.getLocationOnScreen().y+DracoLoadingScreen.diffY -8);
        win.setWindowed(DracoLoadingScreen.screen.getSize().width-DracoLoadingScreen.diffX,DracoLoadingScreen.screen.getSize().height-DracoLoadingScreen.diffY);
        DracoLoadingScreen.screen.setVisible(false);
        DracoLoadingScreen.memoryThread.interrupt();
    }
}
