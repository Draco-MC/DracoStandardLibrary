package sh.talonfox.vulpes_std.listeners.v1

import net.minecraft.server.MinecraftServer

public interface IServerStartingListener {
    fun serverStarting(server: MinecraftServer)
}