package sh.talonfloof.draco_std.mixins.client;

import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import sh.talonfloof.dracoloader.api.EnvironmentType;
import sh.talonfloof.dracoloader.api.Side;

import java.util.List;

@Side(EnvironmentType.CLIENT)
@Mixin(GridLayout.class)
public interface IAccessorGridLayout {
    @Accessor
    List<LayoutElement> getChildren();
}
