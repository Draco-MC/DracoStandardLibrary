package sh.talonfox.vulpes_std.mixins.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
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
import sh.talonfox.vulpes_std.debug.VulpesEarlyLog;
import sh.talonfox.vulpes_std.listeners.IRegisterListener;
import sh.talonfox.vulpesloader.api.VulpesListenerManager;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {
    @Unique
    private static boolean firstLoad = true;

    @Unique
    private static float fade = 0;

    @Inject(method="render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setOverlay(Lnet/minecraft/client/gui/screens/Overlay;)V"))
    private void vulpes$endFirstLoad(CallbackInfo ci) {
        firstLoad = false;
    }

    @Unique
    private static void vulpes$drawString(GuiGraphics $$0, int x, int y, String s, float v) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 1);
        for(int i=0; i < s.length(); i++) {
            int num = Byte.toUnsignedInt((byte)s.charAt(i));
            $$0.setColor(1.0F, 1.0F, 1.0F, v);
            $$0.blit(new ResourceLocation("vulpes", "textures/monocraft.png"), x+(i*6), y, 8, 8, (num&0xF)*8F, ((num&0xF0)>>4)*8F, 8, 8, 128, 128);
            $$0.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    @Unique
    private static void vulpes$drawProgressBar(GuiGraphics $$0, int x, int y, int w, int h, float v, float progress) {
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

    @Inject(method="<init>", at = @At("TAIL"))
    private void loadMods(Minecraft minecraft, ReloadInstance $$1, Consumer<Optional<Throwable>> $$2, boolean $$3, CallbackInfo ci) {
        if(firstLoad) {
            Thread t = new Thread(() -> {
                VulpesEarlyLog.addToLog("LOAD Lateinit");
                /*VulpesEarlyLog.customBarName = "Vulpes Late Initialization";
                for(int i=0; i < 10; i ++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    VulpesEarlyLog.customBarProgress = (float)(i+1)/10.0F;
                }
                VulpesEarlyLog.customBarName = "";*/
            });
            t.start();
        }
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "INVOKE", target="Lnet/minecraft/server/packs/resources/ReloadInstance;isDone()Z"))
    private boolean vulpes$delayFinish(ReloadInstance instance) {
        return instance.isDone() && VulpesEarlyLog.customBarName.isEmpty();
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target="drawProgressBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIF)V"))
    private void vulpes$moveBarUpward(Args args) {
        if(firstLoad) {
            int y1 = args.get(2);
            int y2 = args.get(4);
            int diff = y1 - (int) (y1 * 0.6);
            args.set(2, y1 - diff);
            args.set(4, y2 - diff);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void vulpes$captureFade(GuiGraphics gfx, int $$1, int $$2, float delta, CallbackInfo ci, @Local(ordinal=1) float f) {
        if(firstLoad) {
            fade = f;
        }
    }

    @Inject(method = "drawProgressBar", at = @At("TAIL"))
    public void vulpes$addVulpesLogo(GuiGraphics gfx, int $$1, int $$2, int $$3, int $$4, float a, CallbackInfo ci) {
        if(firstLoad) {
            // Heap Bar
            var mem = ManagementFactory.getMemoryMXBean();
            final MemoryUsage heapusage = mem.getHeapMemoryUsage();
            String heap = String.format("Heap: %d/%d MB (%.1f%%) OffHeap: %d MB", heapusage.getUsed() >> 20, heapusage.getMax() >> 20, ((float) heapusage.getUsed() / heapusage.getMax()) * 100.0, mem.getNonHeapMemoryUsage().getUsed() >> 20);
            vulpes$drawString(gfx, gfx.guiWidth() / 2 - ((heap.length() * 6) / 2), 4, heap, a);
            vulpes$drawProgressBar(gfx, $$1, (int) (gfx.guiHeight() / 16.0), $$3 - $$1, $$4 - $$2, a, (float) heapusage.getUsed() / heapusage.getMax());
            // Draw Early Logs
            for (int i = 0; i < VulpesEarlyLog.INSTANCE.getLog().size(); i++) {
                String s = VulpesEarlyLog.INSTANCE.getLog().get(i);
                vulpes$drawString(gfx, 2, gfx.guiHeight() - (i * 12) - 12, s, a * Mth.lerp((float) i / 5, 1F, 0.25F));
            }
            vulpes$drawString(gfx, $$1, $$2 - 10, "Minecraft Resource Initialization", a);
            if (!VulpesEarlyLog.customBarName.isEmpty()) {
                vulpes$drawString(gfx, $$1, $$2 + 32 - 10, VulpesEarlyLog.customBarName, a);
                vulpes$drawProgressBar(gfx, $$1, $$2 + 32, $$3 - $$1, $$4 - $$2, a, VulpesEarlyLog.customBarProgress);
            }
        }
    }

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 6)
    private int vulpes$moveUpward(int x) {
        if(firstLoad) {
            return Mth.lerpInt((float)Math.sin(Math.toRadians(Mth.clamp((fade-0.5F)/0.5F,0F,1F)*90F)),x + x + (x / 2),x);
        } else {
            return x;
        }
    }
}
