package sh.talonfloof.draco_std.mixins.client;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;
import sh.talonfloof.draco_std.loading.DracoLoadingScreen;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Mixin(Main.class)
public class ClientMainMixin {

    @Inject(method="main", at=@At("HEAD"))
    private static void draco$earlyLoad(String[] args, CallbackInfo ci) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        DracoEarlyLog.addToLog("Loading Draco 1.20.6-alpha0.1");
        DracoEarlyLog.addToLog("LOAD DracoEarlyWindow");
        System.setProperty("java.awt.headless","false");
        Color color = new Color(239, 50, 61);
        File options = new File(".").toPath().resolve("options.txt").toFile();
        if(options.exists()) {
            try (var stream = new FileInputStream(options)) {
                var content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                int index = content.indexOf("darkMojangStudiosBackground:");
                if(index != 0) {
                    if(content.substring(index+("darkMojangStudiosBackground:".length())).startsWith("true")) {
                        color = Color.BLACK;
                    }
                }
            }
        }
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        UIManager.getDefaults().put("ProgressBar.horizontalSize", new Dimension(146, 12));
        UIManager.getDefaults().put("ProgressBar.font", UIManager.getFont("ProgressBar.font").deriveFont(18f));
        DracoLoadingScreen.screen = new DracoLoadingScreen(color);
        DracoLoadingScreen.screen.setVisible(true);
        DracoLoadingScreen.diffX = (int)(DracoLoadingScreen.screen.getBounds().getWidth() - DracoLoadingScreen.screen.getRootPane().getBounds().getWidth());
        DracoLoadingScreen.diffY = (int)(DracoLoadingScreen.screen.getBounds().getHeight() - DracoLoadingScreen.screen.getRootPane().getBounds().getHeight());
        DracoLoadingScreen.screen.setSize(854+DracoLoadingScreen.diffX,480+DracoLoadingScreen.diffY);
        DracoLoadingScreen.createCustomProgressBar("minecraft_load","Bootstrap Minecraft",0);
    }
}
