package sh.talonfloof.draco_std.listeners

import net.minecraft.server.MinecraftServer

public interface IServerStartTickListener {
    fun serverStartTick(server: MinecraftServer)
}