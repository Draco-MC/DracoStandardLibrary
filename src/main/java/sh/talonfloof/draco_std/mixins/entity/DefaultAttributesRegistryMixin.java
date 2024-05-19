package sh.talonfloof.draco_std.mixins.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;
import sh.talonfloof.draco_std.listeners.IAttributeRegisterListener;
import sh.talonfloof.draco_std.listeners.IRegisterListener;
import sh.talonfloof.draco_std.loading.DracoLoadingScreen;
import sh.talonfloof.dracoloader.api.DracoListenerManager;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(DefaultAttributes.class)
public class DefaultAttributesRegistryMixin {
    @Shadow
    @Final
    @Mutable
    private static Map<EntityType<? extends LivingEntity>, AttributeSupplier> SUPPLIERS;

    @Inject(method = "<clinit>*", at = @At("TAIL"))
    private static void draco$attributeInjection(CallbackInfo ci) {
        SUPPLIERS = new IdentityHashMap<>(SUPPLIERS);
        DracoEarlyLog.addToLog("HOOK IAttributeRegisterListener");
        var instances = DracoListenerManager.getListeners(IAttributeRegisterListener.class);
        if (instances != null) {
            DracoLoadingScreen.createCustomProgressBar("IAttributeRegisterListener","HOOK IAttributeRegisterListener",instances.size());
            final int[] i = {0};
            instances.forEach((clazz) -> {
                ((IAttributeRegisterListener) clazz).attributeRegister();
                i[0]++;
                DracoLoadingScreen.updateCustomBar("IAttributeRegisterListener",null,i[0],null);
            });
            DracoLoadingScreen.updateCustomBar("IAttributeRegisterListener",null,null,null);
        }
    }
}
