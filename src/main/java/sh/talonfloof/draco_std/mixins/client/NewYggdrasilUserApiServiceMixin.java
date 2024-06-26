package sh.talonfloof.draco_std.mixins.client;

import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import com.mojang.authlib.yggdrasil.response.UserAttributesResponse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sh.talonfloof.dracoloader.api.EnvironmentType;
import sh.talonfloof.dracoloader.api.Side;

@Side(EnvironmentType.CLIENT)
@Mixin(YggdrasilUserApiService.class)
public class NewYggdrasilUserApiServiceMixin {
    @Redirect(method = "fetchProperties", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/yggdrasil/response/UserAttributesResponse$Privileges;getTelemetry()Z", remap = false), remap = false, require = 0)
    private boolean getTelemetry(UserAttributesResponse.Privileges privileges) {
        return false;
    }
}