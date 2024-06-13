package sh.talonfloof.draco_std.mixins.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfloof.draco_std.networking.CustomPayloadHolder;
import sh.talonfloof.draco_std.networking.DracoPacketType;

@Mixin(DiscardedPayload.class)
public abstract class DiscardedPayloadMixin implements CustomPayloadHolder {
    @Unique
    public ByteBuf draco$customBuffer;
    @Unique
    public DracoPacketType draco$type;

    @Override
    public void draco$setBuf(ByteBuf buf) {
        draco$customBuffer = buf;
    }

    @Override
    public FriendlyByteBuf draco$getBuf() {
        return new FriendlyByteBuf(draco$customBuffer);
    }

    @Override
    public void draco$setType(@NotNull DracoPacketType t) {
        draco$type = t;
    }

    @Override
    public @NotNull DracoPacketType draco$getType() {
        return draco$type;
    }

    @Inject(at = @At("RETURN"), cancellable = true, method = "codec(Lnet/minecraft/resources/ResourceLocation;I)Lnet/minecraft/network/codec/StreamCodec;")
    private static void draco$customPacketCodec(ResourceLocation r, int size, CallbackInfoReturnable<StreamCodec<? extends FriendlyByteBuf, DiscardedPayload>> cir) {
        cir.setReturnValue(CustomPacketPayload.codec((payload, buffer) -> {
            ((CustomPayloadHolder)(Object)payload).draco$setType(buffer instanceof RegistryFriendlyByteBuf ? DracoPacketType.GAMEPLAY : DracoPacketType.CONFIG);
            buffer.writeBytes(((CustomPayloadHolder)(Object)payload).draco$getBuf().copy());
        }, buf -> {
            var payload = new DiscardedPayload(r);
            ((CustomPayloadHolder)(Object)payload).draco$setBuf(buf.readBytes(buf.readableBytes()));
            ((CustomPayloadHolder)(Object)payload).draco$setType(buf instanceof RegistryFriendlyByteBuf ? DracoPacketType.GAMEPLAY : DracoPacketType.CONFIG);
            return payload;
        }));
    }
}
