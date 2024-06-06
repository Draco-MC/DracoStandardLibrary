package sh.talonfloof.draco_std

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sh.talonfloof.draco_std.debug.DracoEarlyLog.addToLog
import sh.talonfloof.draco_std.listeners.IRegisterListener
import sh.talonfloof.draco_std.loading.DracoLoadingScreen
import sh.talonfloof.draco_std.loading.DracoLoadingScreen.Companion.createCustomProgressBar
import sh.talonfloof.dracoloader.api.ListenerSubscriber
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import javax.swing.UIManager

@ListenerSubscriber
open class DracoStandardLibrary : IRegisterListener {
    init {
        if(!File("./config").exists()) File("./config").mkdir()
    }

    companion object {
        @JvmField
        val LOGGER: Logger = LogManager.getLogger("DracoStandardLibrary")
        const val VERSION = "1.20.6-alpha0.1"
    }

    override fun register() {
    }
}