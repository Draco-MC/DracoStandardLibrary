/*
 * Copyright 2022 Vulpes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sh.talonfox.vulpes_std.events.v1
import net.minecraft.server.MinecraftServer

abstract class EventInformation {
    open protected fun getID(): String {
        return "invalid"
    }

    open fun get(key: String): Any? {
        if(!key.contains(":") || key.startsWith(":") || key.endsWith(":") || key.lowercase() != key) {
            return null
        } else if(key == "vulpes:id") {
            return this.getID()
        }
        return null
    }
}

open class ServerLifeTickInfo(val Server: MinecraftServer, val IsTickEnding: Boolean) : EventInformation() {
    override open protected fun getID(): String {
        return "vulpes:server_life_tick"
    }

    override open fun get(key: String): Any? {
        if(key == "minecraft:server") {
            return Server
        } else if(key == "minecraft:tick_type") {
            return if(IsTickEnding) "ending" else "starting"
        } else {
            return super.get(key)
        }
    }
}