package sh.talonfloof.draco_std.mixins.networking;

import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.networking.CustomPayloadHolder;
import sh.talonfloof.draco_std.networking.DracoMessageRegistry;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Inject(at = @At("HEAD"), method = "handleCustomPayload", cancellable = true)
    public void draco$serverboundHandler(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if(packet.payload() instanceof DiscardedPayload) {
            var receivers = DracoMessageRegistry.getServerReceivers();
            if (receivers.containsKey(packet.payload().type().id())) {
                receivers.get(packet.payload().type().id()).receiveServerbound(((CustomPayloadHolder)packet.payload()).draco$getBuf(),((ServerGamePacketListenerImpl)(Object)this).getPlayer().getServer(),((ServerGamePacketListenerImpl)(Object)this).getPlayer());
                ci.cancel();
            }
        }
    }
}
