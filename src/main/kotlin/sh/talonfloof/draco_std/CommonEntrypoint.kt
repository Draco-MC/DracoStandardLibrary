package sh.talonfloof.draco_std

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sh.talonfloof.draco_std.config.ConfigType
import sh.talonfloof.draco_std.config.ModConfig
import sh.talonfloof.draco_std.listeners.IRegisterListener
import java.io.File

open class CommonEntrypoint : IRegisterListener {
    init {
        LOGGER.info("Draco Standard Library $VERSION")
        if(!File("./config").exists()) File("./config").mkdir()
    }

    companion object {
        @JvmField
        val LOGGER: Logger = LogManager.getLogger("DracoStandardLibrary")
        const val VERSION = "1.20.6-alpha0.1"
    }

    override fun register() {
        val config = ModConfig.Builder("draco", ConfigType.COMMON, null)
        config.defineRange("diamondsPerCluster",5,1,10)
        config.define("category.a",5)
        config.define("category.anotherCategory.isGay",true)
        config.define("yetAnotherCategory.doubleTest",10.0)
        config.define("yetAnotherCategory.a",5)
        config.build()
        val config2 = ModConfig.Builder("draco", ConfigType.CLIENT, null)
        config2.define("client-category.a","Meow")
        config2.build()
    }
}