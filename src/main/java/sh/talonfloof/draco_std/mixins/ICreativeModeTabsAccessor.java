package sh.talonfloof.draco_std.mixins;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTabs.class)
public interface ICreativeModeTabsAccessor {
    @Accessor("BUILDING_BLOCKS")
    public static ResourceKey<CreativeModeTab> BUILDING_BLOCKS() {
        throw new AssertionError();
    }
    @Accessor("COLORED_BLOCKS")
    public static ResourceKey<CreativeModeTab> COLORED_BLOCKS() {
        throw new AssertionError();
    }
    @Accessor("NATURAL_BLOCKS")
    public static ResourceKey<CreativeModeTab> NATURAL_BLOCKS() {
        throw new AssertionError();
    }
    @Accessor("FUNCTIONAL_BLOCKS")
    public static ResourceKey<CreativeModeTab> FUNCTIONAL_BLOCKS() {
        throw new AssertionError();
    }
    @Accessor("REDSTONE_BLOCKS")
    public static ResourceKey<CreativeModeTab> REDSTONE_BLOCKS() {
        throw new AssertionError();
    }
    @Accessor("HOTBAR")
    public static ResourceKey<CreativeModeTab> HOTBAR() {
        throw new AssertionError();
    }
    @Accessor("SEARCH")
    public static ResourceKey<CreativeModeTab> SEARCH() {
        throw new AssertionError();
    }
    @Accessor("TOOLS_AND_UTILITIES")
    public static ResourceKey<CreativeModeTab> TOOLS_AND_UTILITIES() {
        throw new AssertionError();
    }
    @Accessor("COMBAT")
    public static ResourceKey<CreativeModeTab> COMBAT() {
        throw new AssertionError();
    }
    @Accessor("FOOD_AND_DRINKS")
    public static ResourceKey<CreativeModeTab> FOOD_AND_DRINKS() {
        throw new AssertionError();
    }
    @Accessor("INGREDIENTS")
    public static ResourceKey<CreativeModeTab> INGREDIENTS() {
        throw new AssertionError();
    }
    @Accessor("SPAWN_EGGS")
    public static ResourceKey<CreativeModeTab> SPAWN_EGGS() {
        throw new AssertionError();
    }
    @Accessor("OP_BLOCKS")
    public static ResourceKey<CreativeModeTab> OP_BLOCKS() {
        throw new AssertionError();
    }
    @Accessor("INVENTORY")
    public static ResourceKey<CreativeModeTab> INVENTORY() {
        throw new AssertionError();
    }
}
