package sh.talonfox.vulpes_std.mixins;

import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeTabs.class)
public class ItemGroupsMixin {
    @Inject(method = "bootstrap", at = @At("HEAD"))
    private static void bootstrap(Registry<CreativeModeTab> tabs, CallbackInfoReturnable<CreativeModeTab> cir) {

    }
}
