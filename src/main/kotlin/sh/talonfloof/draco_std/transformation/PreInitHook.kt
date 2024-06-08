package sh.talonfloof.draco_std.transformation

import net.minecraft.util.Mth
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import sh.talonfloof.draco_std.DracoStandardLibrary
import sh.talonfloof.draco_std.debug.DracoEarlyLog
import sh.talonfloof.draco_std.loading.DracoLoadingScreen
import sh.talonfloof.dracoloader.api.DracoEnvironment
import sh.talonfloof.dracoloader.api.DracoTransformer
import sh.talonfloof.dracoloader.api.EnvironmentType
import sh.talonfloof.dracoloader.api.Side
import sh.talonfloof.dracoloader.transform.IDracoTransformer
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import javax.swing.UIManager

@DracoTransformer
class PreInitHook : IDracoTransformer {
    init {
        if(DracoEnvironment.getEnvironment() == EnvironmentType.CLIENT) {
            DracoEarlyLog.addToLog("Loading Draco " + DracoStandardLibrary.VERSION)
            System.setProperty("java.awt.headless", "false")
            System.setProperty("awt.useSystemAAFontSettings","off")
            var color: Color? = Color(239, 50, 61)
            val options = File(".").toPath().resolve("options.txt").toFile()
            if (options.exists()) {
                FileInputStream(options).use { stream ->
                    val content = String(stream.readAllBytes(), StandardCharsets.UTF_8)
                    val index = content.indexOf("darkMojangStudiosBackground:")
                    if (index != -1) {
                        if (content.substring(index + "darkMojangStudiosBackground:".length).startsWith("true")) {
                            color = Color.BLACK
                        }
                    }
                }
            }
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
            DracoLoadingScreen.screen = DracoLoadingScreen(color!!)
            DracoLoadingScreen.screen!!.isVisible = true
            DracoLoadingScreen.diffX =
                (DracoLoadingScreen.screen!!.bounds.getWidth() - DracoLoadingScreen.screen!!.rootPane.bounds.getWidth()).toInt()
            DracoLoadingScreen.diffY =
                (DracoLoadingScreen.screen!!.bounds.getHeight() - DracoLoadingScreen.screen!!.rootPane.bounds.getHeight()).toInt()
            DracoLoadingScreen.screen!!.setSize(854 + DracoLoadingScreen.diffX, 480 + DracoLoadingScreen.diffY)
            DracoLoadingScreen.createCustomProgressBar("minecraft_load", "Draco Early Mod Initialization", 0)
            //Thread.sleep(100)
        }
    }

    override fun transform(loader: ClassLoader, className: String, originalClassData: ByteArray?): ByteArray? {
        return originalClassData
    }
}