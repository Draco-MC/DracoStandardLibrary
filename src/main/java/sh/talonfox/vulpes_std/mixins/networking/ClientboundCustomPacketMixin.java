package sh.talonfox.vulpes_std.mixins.networking;

import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.vulpes_std.networking.CustomPayloadHolder;
import sh.talonfox.vulpes_std.networking.VulpesMessageRegistry;

@Mixin(ClientboundCustomPayloadPacket.class)
public abstract class ClientboundCustomPacketMixin {
    @Shadow
    public abstract CustomPacketPayload payload();

    @Inject(method = "handle(Lnet/minecraft/network/protocol/common/ClientCommonPacketListener;)V", at = @At("HEAD"), cancellable = true)
    private void vulpes$clientboundHandle(ClientCommonPacketListener listener, CallbackInfo ci) {
        if(payload() instanceof DiscardedPayload) {
            var receivers = VulpesMessageRegistry.getClientReceivers();
            if (receivers.containsKey(payload().type().id())) {
                receivers.get(payload().type().id()).receiveClientbound(((CustomPayloadHolder)payload()).vulpes$getBuf());
                ci.cancel();
            }
        }
    }
}
