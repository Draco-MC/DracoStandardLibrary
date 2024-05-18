package sh.talonfloof.draco_std.mixins;

import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTab.class)
public interface ICreativeModeTabAccessor {
    @Accessor
    @Mutable
    @Final
    void setRow(CreativeModeTab.Row row);

    @Accessor
    @Mutable
    @Final
    void setColumn(int column);
}
