package sh.talonfox.vulpes_std.mixins;

import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import sh.talonfox.vulpes_std.creative_tab.VulpesCreativeModeTab;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin implements VulpesCreativeModeTab {
    @Unique
    private int tabPage = -1;

    @Override
    public int getPage() {
        if (tabPage < 0) {
            throw new IllegalStateException("Item group has no page");
        }

        return tabPage;
    }

    @Override
    public void setPage(int page) {
        this.tabPage = page;
    }
}
