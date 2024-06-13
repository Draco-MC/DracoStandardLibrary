package sh.talonfloof.draco_std

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sh.talonfloof.draco_std.listeners.IRegisterListener
import sh.talonfloof.dracoloader.api.ListenerSubscriber
import java.io.File

@ListenerSubscriber
open class DracoStandardLibrary : IRegisterListener {
    init {
        if(!File("./config").exists()) File("./config").mkdir()
    }

    companion object {
        @JvmField
        val LOGGER: Logger = LogManager.getLogger("DracoStandardLibrary")
        const val VERSION = "1.21-alpha0.2"
    }

    override fun register() {
    }
}