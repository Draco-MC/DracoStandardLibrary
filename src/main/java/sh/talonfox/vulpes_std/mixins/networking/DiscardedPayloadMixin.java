package sh.talonfox.vulpes_std.mixins.networking;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfox.vulpes_std.CommonEntrypoint;
import sh.talonfox.vulpes_std.networking.CustomPayloadHolder;

@Mixin(DiscardedPayload.class)
public abstract class DiscardedPayloadMixin implements CustomPayloadHolder {
    @Unique
    public ByteBuf vulpes$customBuffer;

    @Override
    public void vulpes$setBuf(ByteBuf buf) {
        vulpes$customBuffer = buf;
    }

    @Override
    public FriendlyByteBuf vulpes$getBuf() {
        return new FriendlyByteBuf(vulpes$customBuffer);
    }

    @Inject(at = @At("RETURN"), cancellable = true, method = "codec(Lnet/minecraft/resources/ResourceLocation;I)Lnet/minecraft/network/codec/StreamCodec;")
    private static void vulpes$customPacketCodec(ResourceLocation r, int size, CallbackInfoReturnable<StreamCodec<? extends FriendlyByteBuf, DiscardedPayload>> cir) {
        cir.setReturnValue(CustomPacketPayload.codec((payload, buffer) -> {
            buffer.writeBytes(((CustomPayloadHolder)(Object)payload).vulpes$getBuf().copy());
        }, buf -> {
            var payload = new DiscardedPayload(r);
            ((CustomPayloadHolder)(Object)payload).vulpes$setBuf(buf.readBytes(buf.readableBytes()));
            return payload;
        }));
    }
}
