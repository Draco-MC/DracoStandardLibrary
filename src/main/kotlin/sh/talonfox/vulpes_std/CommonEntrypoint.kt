/*
 * Copyright 2022-2024 Vulpes
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

package sh.talonfox.vulpes_std

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sh.talonfox.vulpes_std.listeners.IRegisterListener

open class CommonEntrypoint : IRegisterListener {
    private companion object {
        @JvmField
        val LOGGER: Logger = LogManager.getLogger("VulpesStandardLibrary")
    }

    override fun register() {
        LOGGER.info("Vulpes Standard Library for 1.20.6")
    }
}