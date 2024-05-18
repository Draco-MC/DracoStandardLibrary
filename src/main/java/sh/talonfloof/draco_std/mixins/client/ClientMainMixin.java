package sh.talonfloof.draco_std.mixins.client;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;
import sh.talonfloof.draco_std.loading.DracoLoadingScreen;

import javax.swing.*;
import java.awt.*;

@Mixin(Main.class)
public class ClientMainMixin {

    @Inject(method="main", at=@At("HEAD"))
    private static void draco$earlyLoad(String[] args, CallbackInfo ci) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        DracoEarlyLog.addToLog("LOAD DracoEarlyWindow");
        System.setProperty("java.awt.headless","false");
        /*JFrame.setDefaultLookAndFeelDecorated( true );
        JDialog.setDefaultLookAndFeelDecorated( true );
        System.setProperty("flatlaf.useWindowDecorations","true");
        System.setProperty("flatlaf.menuBarEmbedded","true");
        FlatDarkLaf.setup();*/
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        UIManager.getDefaults().put("ProgressBar.horizontalSize", new Dimension(146, 12));
        UIManager.getDefaults().put("ProgressBar.font", UIManager.getFont("ProgressBar.font").deriveFont(18f));
        DracoLoadingScreen.screen = new DracoLoadingScreen();
        DracoLoadingScreen.screen.setVisible(true);
    }
}
