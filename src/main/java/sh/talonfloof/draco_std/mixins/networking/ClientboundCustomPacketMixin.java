package sh.talonfloof.draco_std.mixins.networking;

import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.networking.CustomPayloadHolder;
import sh.talonfloof.draco_std.networking.DracoMessageRegistry;
import sh.talonfloof.dracoloader.api.EnvironmentType;
import sh.talonfloof.dracoloader.api.Side;

@Side(EnvironmentType.CLIENT)
@Mixin(ClientboundCustomPayloadPacket.class)
public abstract class ClientboundCustomPacketMixin {
    @Shadow
    public abstract CustomPacketPayload payload();

    @Inject(method = "handle(Lnet/minecraft/network/protocol/common/ClientCommonPacketListener;)V", at = @At("HEAD"), cancellable = true)
    private void draco$clientboundHandle(ClientCommonPacketListener listener, CallbackInfo ci) {
        if(payload() instanceof DiscardedPayload) {
            var receivers = DracoMessageRegistry.getClientReceivers();
            if (receivers.containsKey(payload().type().id())) {
                receivers.get(payload().type().id()).receiveClientbound(((CustomPayloadHolder)payload()).draco$getBuf());
                ci.cancel();
            }
        }
    }
}
