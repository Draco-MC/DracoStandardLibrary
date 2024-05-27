package sh.talonfloof.draco_std.mixins.client;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import sh.talonfloof.dracoloader.api.EnvironmentType;
import sh.talonfloof.dracoloader.api.Side;

import java.util.List;

@Side(EnvironmentType.CLIENT)
@Mixin(Screen.class)
public interface IScreenAccessor {
    @Accessor("renderables")
    @Final
    List<Renderable> getRenderables();

    @Accessor("narratables")
    @Final
    List<NarratableEntry> getNarratables();
}
