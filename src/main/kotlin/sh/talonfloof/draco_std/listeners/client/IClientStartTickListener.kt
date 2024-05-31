package sh.talonfloof.draco_std.listeners.client

import sh.talonfloof.dracoloader.api.EnvironmentType
import sh.talonfloof.dracoloader.api.Side

@Side(EnvironmentType.CLIENT)
interface IClientStartTickListener {
    fun clientStartTick()
}