package sh.talonfloof.draco_std.mixins.client;

import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Window.class)
public interface IWindowAccessor {
    @Accessor("windowedX")
    @Mutable
    void setX(int x);

    @Accessor("windowedY")
    @Mutable
    void setY(int x);
}
