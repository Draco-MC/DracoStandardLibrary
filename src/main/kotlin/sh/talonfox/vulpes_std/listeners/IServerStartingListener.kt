package sh.talonfox.vulpes_std.listeners

import net.minecraft.server.MinecraftServer

public interface IServerStartingListener {
    fun serverStarting(server: MinecraftServer)
}