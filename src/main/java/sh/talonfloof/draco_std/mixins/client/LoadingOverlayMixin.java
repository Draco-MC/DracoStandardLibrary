package sh.talonfloof.draco_std.mixins.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sun.management.OperatingSystemMXBean;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import sh.talonfloof.draco_std.debug.DracoEarlyLog;
import sh.talonfloof.dracoloader.api.EnvironmentType;
import sh.talonfloof.dracoloader.api.Side;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Optional;
import java.util.function.Consumer;

import static sh.talonfloof.draco_std.DracoStandardLibrary.VERSION;

@Side(EnvironmentType.CLIENT)
@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {
    @Unique
    private static boolean firstLoad = true;

    @Unique
    private static float fade = 0;

    @Unique
    private static long logoFadeStart = Long.MAX_VALUE;
    @Unique
    private static int draco$guiScale = 0;
    @Unique
    private static int draco$logoHeight = 0;

    @Unique
    private static OperatingSystemMXBean draco$osBean;

    @Unique
    private static String draco$cpu = "CPU: 0.0%";

    @Inject(method="render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setOverlay(Lnet/minecraft/client/gui/screens/Overlay;)V"))
    private void draco$endFirstLoad(CallbackInfo ci) {
        firstLoad = false;
    }

    @Unique
    private static void draco$drawString(GuiGraphics $$0, int x, int y, String s, float v) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 1);
        for(int i=0; i < s.length(); i++) {
            int num = Byte.toUnsignedInt((byte)s.charAt(i));
            $$0.setColor(1.0F, 1.0F, 1.0F, v);
            $$0.blit(ResourceLocation.tryBuild("draco", "textures/monocraft.png"), x+(i*6), y, 8, 8, (num&0xF)*8F, ((num&0xF0)>>4)*8F, 8, 8, 128, 128);
            $$0.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    @Unique
    private static void draco$drawProgressBar(GuiGraphics $$0, int x, int y, int w, int h, float v, float progress) {
        int x2 = x+w;
        int y2 = y+h;
        int $$6 = Mth.ceil((float)(x2 - x - 2) * progress);
        int $$7 = Math.round(v * 255.0F);
        int $$8 = FastColor.ARGB32.color($$7, 255, 255, 255);
        $$0.fill(x + 2, y + 2, x + $$6, y2 - 2, $$8);
        $$0.fill(x + 1, y, x2 - 1, y + 1, $$8);
        $$0.fill(x + 1, y2, x2 - 1, y2 - 1, $$8);
        $$0.fill(x, y, x + 1, y2, $$8);
        $$0.fill(x2, y, x2 - 1, y2, $$8);
    }

    @Unique
    private static void draco$drawMemoryBar(GuiGraphics $$0, int x, int y, int w, int h, float v, float progress) {
        int x2 = x+w;
        int y2 = y+h;
        int $$6 = Mth.ceil((float)(x2 - x - 2) * progress);
        int $$7 = Math.round(v * 255.0F);
        var col = Color.getHSBColor(((1.0f - (float)Math.pow(progress,1.5f)) / 3f), 1.0f, 0.5f);
        int $$8 = FastColor.ARGB32.color($$7, 255, 255, 255);
        int $$9 = FastColor.ARGB32.color($$7, col.getRed(), col.getGreen(), col.getBlue());
        $$0.fill(x + 2, y + 2, x + $$6, y2 - 2, $$9);
        $$0.fill(x + 1, y, x2 - 1, y + 1, $$8);
        $$0.fill(x + 1, y2, x2 - 1, y2 - 1, $$8);
        $$0.fill(x, y, x + 1, y2, $$8);
        $$0.fill(x2, y, x2 - 1, y2, $$8);
    }

    @Inject(method="<init>", at = @At("TAIL"))
    private void loadMods(Minecraft minecraft, ReloadInstance $$1, Consumer<Optional<Throwable>> $$2, boolean $$3, CallbackInfo ci) {
        if(firstLoad) {
            draco$osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            Thread t = new Thread(() -> {
                while(firstLoad) {
                    var cpuLoad = draco$osBean.getProcessCpuLoad();
                    if (cpuLoad == -1.0) {
                        draco$cpu = String.format("*CPU: %.1f%%", draco$osBean.getCpuLoad() * 100f);
                    } else {
                        draco$cpu = String.format("CPU: %.1f%%", cpuLoad * 100f);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            t.start();
        }
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "INVOKE", target="Lnet/minecraft/server/packs/resources/ReloadInstance;isDone()Z"))
    private boolean draco$delayFinish(ReloadInstance instance) {
        return instance.isDone() && DracoEarlyLog.customBarName.isEmpty();
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void draco$tick(GuiGraphics gfx, int mX, int mY, float delta, CallbackInfo ci) {
        if(firstLoad && logoFadeStart == Long.MAX_VALUE) {
            logoFadeStart = Util.getMillis()+100;
        }
        if(firstLoad) {
            draco$guiScale = Minecraft.getInstance().screen.width / gfx.guiWidth();
        }
    }


    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;setColor(FFFF)V", ordinal = 0))
    private void draco$fadeInLogo(Args args) {
        args.set(3,Math.min(args.get(3),(float)(((double)(Util.getMillis()-logoFadeStart))/1000.0)));
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target="drawProgressBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIF)V"))
    private void draco$moveBarUpward(Args args) {
        if(firstLoad) {
            int base = 10+((draco$guiScale*2)+(8*draco$guiScale))+1+(int)draco$logoHeight+12;
            int y1 = args.get(2);
            int y2 = args.get(4);
            int diff = y1 - base;
            args.set(2, y1 - diff);
            args.set(4, y2 - diff);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void draco$captureFade(GuiGraphics gfx, int $$1, int $$2, float delta, CallbackInfo ci, @Local(ordinal=1) float f) {
        if(firstLoad) {
            fade = f;
        }
    }

    @Inject(method = "drawProgressBar", at = @At("TAIL"))
    public void draco$addDracoLogo(GuiGraphics gfx, int $$1, int $$2, int $$3, int $$4, float a, CallbackInfo ci) {
        if(firstLoad) {
            // Heap Bar
            var mem = ManagementFactory.getMemoryMXBean();
            final MemoryUsage heapusage = mem.getHeapMemoryUsage();
            String heap = String.format("Heap: %d/%d MB (%.1f%%) OffHeap: %d MB  %s", heapusage.getUsed() >> 20, heapusage.getMax() >> 20, ((float) heapusage.getUsed() / heapusage.getMax()) * 100.0, mem.getNonHeapMemoryUsage().getUsed() >> 20, draco$cpu);
            draco$drawString(gfx, gfx.guiWidth() / 2 - ((heap.length() * 6) / 2), 1, heap, a);
            draco$drawMemoryBar(gfx, $$1, 10, $$3 - $$1, $$4 - $$2, a, (float) heapusage.getUsed() / heapusage.getMax());
            // Draw Early Logs
            for (int i = 0; i < DracoEarlyLog.INSTANCE.getLog().size(); i++) {
                String s = DracoEarlyLog.INSTANCE.getLog().get(i);
                draco$drawString(gfx, 1, gfx.guiHeight() - (i * 10) - 10, s, a * Mth.lerp((float) i / 5, 1F, 1F));
            }
            draco$drawString(gfx, $$1, $$2 - 9, "Minecraft Progress", a);
            if (!DracoEarlyLog.customBarName.isEmpty()) {
                draco$drawString(gfx, $$1, $$2 + 32 - 10, DracoEarlyLog.customBarName, a);
                draco$drawProgressBar(gfx, $$1, $$2 + 32, $$3 - $$1, $$4 - $$2, a, DracoEarlyLog.customBarProgress);
            }
            draco$drawString(gfx,gfx.guiWidth()-(VERSION.length()*6),gfx.guiHeight()-9,VERSION,a);
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 1);
            gfx.setColor(1.0F, 1.0F, 1.0F, a);
            gfx.blit(ResourceLocation.tryBuild("draco","draco_monochrome.png"), gfx.guiWidth()-40, gfx.guiHeight()-9-51, 39, 50, 0F, 0F, 39, 50, 39, 50);
            gfx.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIFFIIII)V"))
    private void  draco$moveUpward(Args args) {
        if(firstLoad) {
            int base = 10+((draco$guiScale*2)+(8*draco$guiScale))+1;
            int i = (Integer)args.get(2);
            args.set(2,Mth.lerpInt((float)Math.sin(Math.toRadians(Mth.clamp((fade-0.5F)/0.5F,0F,1F)*90F)),base,i));
            draco$logoHeight = args.get(4);
        }
    }
}
