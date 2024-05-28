package sh.talonfloof.draco_std.config

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import sh.talonfloof.draco_std.CommonEntrypoint.Companion.LOGGER
import java.io.File

enum class ConfigType {
    CLIENT,
    COMMON,
    SERVER
}

open class ModConfig(private val name: String, private val type: ConfigType, private val fileName: String?) {
    private var frozen = false
    private var defaultTOML: Toml = Toml()
    val values: MutableMap<String, ConfigValue<*>> = mutableMapOf()
    companion object {
        private val configs: MutableMap<String,MutableList<Pair<ConfigType,ModConfig>>> = mutableMapOf()

        @JvmStatic
        fun hasConfig(name: String) : Boolean {
            return configs.containsKey(name)
        }

        @JvmStatic
        fun addConfig(name: String, value: Pair<ConfigType,ModConfig>) {
            configs.getOrPut(name) {
                mutableListOf()
            }.add(value)
        }
    }

    private fun getFile() : File = if(fileName != null) File("./config/$fileName") else File("./config/$name-${type.name.lowercase()}.toml")

    fun freeze() {
        if(!frozen) {
            frozen = true
            getFile().parentFile.mkdirs()
            if(!getFile().exists()) {
                TomlWriter().write(Toml(defaultTOML).toMap(), getFile())
            } else {
                load()
            }
        }
    }

    private fun load() {
        if(frozen && getFile().exists()) {
            val map = Toml(defaultTOML).read(getFile()).toMap()
            for(entry in values.entries) {
                var t = map
                entry.key.split(".").dropLast(1).forEach {
                    t = (t[it] as Map<String, Any>)
                }
                entry.value.set(t[entry.key.split(".").last]!!)
            }
            save()
        } else {
            throw RuntimeException("Attempted to load config ${getFile().name} before it was frozen")
        }
    }

    fun save() {
        if(frozen) {
            val finalTable = mutableMapOf<String, Any>()
            for(entry in values.entries) {
                var t = finalTable
                val path = entry.key.split(".")
                path.dropLast(1).forEach {
                    if(t[it] == null) {
                        t[it] = mutableMapOf<String,Any>()
                    }
                    t = (t[it]!! as MutableMap<String, Any>)
                }
                t[path.last] = entry.value.defaultValue as Any
            }
            getFile().parentFile.mkdirs()
            TomlWriter().write(finalTable, getFile())
        } else {
            throw RuntimeException("Attempted to save config ${getFile().name} before it was frozen")
        }
    }

    open class ConfigValue<T> {
        var key: String private set
        var defaultValue: T private set
        var minimumValue: T? = null
            private set
        var maximumValue: T? = null
            private set
        private var currentValue: T? = null
        private val modConfig: ModConfig

        constructor(k: String, v: T, conf: ModConfig) {
            key = k
            defaultValue = v
            currentValue = v
            modConfig = conf
        }

        constructor(k: String, v: T, min: T, max: T, conf: ModConfig) {
            key = k
            defaultValue = v
            currentValue = v
            minimumValue = min
            maximumValue = max
            modConfig = conf
        }

        fun get() : T {
            if(!modConfig.frozen) throw RuntimeException("Attempted to get value of $key before the configuration ${modConfig.getFile().name} was frozen!")
            return currentValue!!
        }

        fun set(v: Any) {
            if(!modConfig.frozen) throw RuntimeException("Attempted to set value of $key before the configuration ${modConfig.getFile().name} was frozen!")
            if(!v::class.isInstance(defaultValue)) {
                LOGGER.error("Type mismatch in config ${modConfig.getFile().name}: ($key = ${currentValue.toString()}) -> ($key = ${v.toString()}), Change will not be applied")
                return
            }
            if(minimumValue != null && maximumValue != null) {
                if(defaultValue is Long) {
                    currentValue = (v as Long).coerceIn(minimumValue as Long, maximumValue as Long) as T?
                }
            } else {
                currentValue = v as T?
            }
        }
    }

    class Builder(private val name: String, private val type: ConfigType, private val fileName: String?) {
        val config: ModConfig = ModConfig(name,type,fileName)

        fun <T> define(key: String, value: T) : ConfigValue<T> {
            val r = ConfigValue(key,value,config)
            config.values[key] = r
            return r
        }
        fun defineRange(key: String, default: Long, min: Long, max: Long) : ConfigValue<Long> {
            val r = ConfigValue(key,default,min,max,config)
            config.values[key] = r
            return r
        }

        fun build() : ModConfig {
            val finalTable = mutableMapOf<String, Any>()
            for(entry in config.values.entries) {
                var t = finalTable
                val path = entry.key.split(".")
                path.dropLast(1).forEach {
                    if(t[it] == null) {
                        t[it] = mutableMapOf<String,Any>()
                    }
                    t = (t[it]!! as MutableMap<String, Any>)
                }
                t[path.last] = entry.value.defaultValue as Any
            }
            config.defaultTOML = Toml().read(TomlWriter().write(finalTable))
            config.freeze()
            addConfig(name,Pair(type,config))
            return config
        }
    }
}