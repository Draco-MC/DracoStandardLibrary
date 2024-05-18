package sh.talonfloof.draco_std.listeners

import net.minecraft.server.MinecraftServer

public interface IServerEndTickListener {
    fun serverEndTick(server: MinecraftServer)
}