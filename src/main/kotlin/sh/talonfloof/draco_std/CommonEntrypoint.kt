package sh.talonfloof.draco_std

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sh.talonfloof.draco_std.listeners.IRegisterListener

open class CommonEntrypoint : IRegisterListener {
    init {
        LOGGER.info("Draco Standard Library $VERSION")
    }

    companion object {
        @JvmField
        val LOGGER: Logger = LogManager.getLogger("DracoStandardLibrary")
        const val VERSION = "1.20.6-alpha0.1"
    }

    override fun register() {
    }
}