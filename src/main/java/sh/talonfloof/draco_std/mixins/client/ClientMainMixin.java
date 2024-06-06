package sh.talonfloof.draco_std.mixins.client;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.DracoStandardLibrary;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;
import sh.talonfloof.draco_std.loading.DracoLoadingScreen;
import sh.talonfloof.dracoloader.api.EnvironmentType;
import sh.talonfloof.dracoloader.api.Side;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

@Side(EnvironmentType.CLIENT)
@Mixin(Main.class)
public class ClientMainMixin {

    @Inject(method="main", at=@At("HEAD"))
    private static void draco$earlyLoad(String[] args, CallbackInfo ci) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, NoSuchMethodException, InvocationTargetException {
        DracoLoadingScreen.updateCustomBar("minecraft_load","Launching Minecraft",null,null);
    }

    @Inject(method="main", at=@At(value = "INVOKE", target = "Lnet/minecraft/server/Bootstrap;bootStrap()V"))
    private static void draco$bootResourceLoad(CallbackInfo ci) {
        DracoLoadingScreen.updateCustomBar("minecraft_load","Loading Bootstrap Resources",null,null);
    }
}
