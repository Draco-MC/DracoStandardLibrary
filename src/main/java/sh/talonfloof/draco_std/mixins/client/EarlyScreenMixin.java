package sh.talonfloof.draco_std.mixins.client;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.util.datafix.DataFixers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;

import java.io.File;
import java.util.OptionalInt;

@Mixin(value = Minecraft.class, priority = 9999)
public abstract class EarlyScreenMixin {
    @Shadow
    @Final
    public File gameDirectory;
    @Shadow protected abstract String createTitle();
    @Shadow public abstract void setWindowActive(boolean bl);
    @Mutable
    @Shadow @Final private DataFixer fixerUpper;
    @Mutable @Shadow @Final private Window window;

    @Inject(method = "<init>(Lnet/minecraft/client/main/GameConfig;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/main/GameConfig$FolderData;getExternalAssetSource()Ljava/nio/file/Path;", unsafe = true))
    private void draco$setEarlyGameWindow(GameConfig runArgs, CallbackInfo ci) {
        DracoEarlyLog.addToLog("LOAD DracoEarlyWindow");
        fixerUpper = DataFixers.getDataFixer();
        RenderSystem.initBackendSystem();
        VirtualScreen windowProvider = new VirtualScreen((Minecraft)(Object) this);
        Options options = new Options((Minecraft)(Object) this, gameDirectory);
        DisplayData windowSettings = new DisplayData(options.overrideWidth > 0 ? options.overrideWidth : 854,options.overrideHeight > 0 ? options.overrideHeight : 480, OptionalInt.empty(), OptionalInt.empty(), options.fullscreen().get());

        // We can't use localized strings because game resources aren't loaded at this point.
        // This is the reason why we are using "loading" as hardcoded string here.
        window = windowProvider.newWindow(
                windowSettings,
                options.fullscreenVideoModeString,
                createTitle() + " - Draco Early Load"
        );
        setWindowActive(true);
    }

    @Redirect(method = "<init>(Lnet/minecraft/client/main/GameConfig;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/VirtualScreen;newWindow(Lcom/mojang/blaze3d/platform/DisplayData;Ljava/lang/String;Ljava/lang/String;)Lcom/mojang/blaze3d/platform/Window;"))
    private Window useEarlyGameWindow(VirtualScreen instance, DisplayData windowSettings, String string, String string2) {
        return window;
    }
}
