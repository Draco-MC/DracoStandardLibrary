package sh.talonfloof.draco_std.mixins;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(MappedRegistry.class)
public class MappedRegistryMixin {
    @Redirect(method = "freeze", at = @At(value = "FIELD", target="Lnet/minecraft/core/MappedRegistry;unregisteredIntrusiveHolders:Ljava/util/Map;", opcode= Opcodes.PUTFIELD))
    private void draco$preserveIntrusiveHolders(MappedRegistry<?> registry, Map<?, Holder.Reference<?>> holders) {

    }
}
