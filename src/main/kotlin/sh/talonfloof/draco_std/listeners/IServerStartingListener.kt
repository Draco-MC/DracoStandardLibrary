package sh.talonfloof.draco_std.listeners

import net.minecraft.server.MinecraftServer

public interface IServerStartingListener {
    fun serverStarting(server: MinecraftServer)
}