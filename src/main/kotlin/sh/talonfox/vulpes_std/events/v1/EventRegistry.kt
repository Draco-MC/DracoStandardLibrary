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

import java.util.function.Consumer

object EventRegistry {
    private var EventHooks: MutableMap<String,MutableList<Consumer<EventInformation>>> = mutableMapOf()

    @JvmStatic
    fun invokeEvent(eventID: String, info: EventInformation) {
        if(!eventID.contains(":") || eventID.startsWith(":") || eventID.endsWith(":")) {
            System.err.println("A mod attempted to invoke an event with the invalid name, \"$eventID\"")
            return
        }
        for(i in EventHooks[eventID]!!.iterator()) {
            i.accept(info)
        }
    }

    @JvmStatic
    fun registryEventHook(eventID: String, method: Consumer<EventInformation>) {
        if(!EventHooks.containsKey(eventID)) {
            EventHooks[eventID] = mutableListOf(method)
        } else {
            EventHooks[eventID]?.add(method)
        }
    }
}