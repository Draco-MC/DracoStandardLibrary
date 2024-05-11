package sh.talonfox.vulpes_std.mixins.networking;

import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.vulpes_std.networking.CustomPayloadHolder;
import sh.talonfox.vulpes_std.networking.VulpesMessageRegistry;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Inject(at = @At("HEAD"), method = "handleCustomPayload", cancellable = true)
    public void vulpes$serverboundHandler(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if(packet.payload() instanceof DiscardedPayload) {
            var receivers = VulpesMessageRegistry.getServerReceivers();
            if (receivers.containsKey(packet.payload().type().id())) {
                receivers.get(packet.payload().type().id()).receiveServerbound(((CustomPayloadHolder)packet.payload()).vulpes$getBuf(),((ServerGamePacketListenerImpl)(Object)this).getPlayer().getServer(),((ServerGamePacketListenerImpl)(Object)this).getPlayer());
                ci.cancel();
            }
        }
    }
}
