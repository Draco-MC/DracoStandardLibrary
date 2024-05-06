/*
 * Copyright 2022 Vulpes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sh.talonfox.vulpes_std.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfox.vulpes_std.modmenu.VulpesButton;
import sh.talonfox.vulpes_std.modmenu.VulpesModMenuScreen;

import java.util.List;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        VulpesButton.ticks += 1;
    }

    @Inject(at = @At("TAIL"), method = "createNormalMenuOptions")
    public void vulpes$injectButton(int a, int b, CallbackInfo ci) {
        int modsButtonIndex = -1;
        int buttonsY = ((TitleScreen)(Object)this).height / 4 + 48;
        final int spacing = 24;
        final List<GuiEventListener> buttons = (List<GuiEventListener>)((TitleScreen)(Object)this).children();
        for(int i = 0; i < buttons.size(); i++) {
            GuiEventListener widget = buttons.get(i);
            if (widget instanceof Button button) {
                if(button.visible) {
                    if (modsButtonIndex == -1) {
                        button.setY(button.getY() - spacing / 2);
                        buttonsY = button.getY();
                    } else if (!(widget instanceof AbstractButton && button.getMessage().equals(Component.translatable("title.credits")))) {
                        button.setY(button.getY() + spacing / 2);
                    }
                }
                ComponentContents c = button.getMessage().getContents();
                if((c instanceof TranslatableContents) && ((TranslatableContents)c).getKey().equals("menu.online")) {
                    modsButtonIndex = i + 1;
                    if (button.visible) {
                        buttonsY = button.getY();
                    }
                }
            }
        }
        if (modsButtonIndex != -1) {
            VulpesButton button = new VulpesButton(((TitleScreen) (Object) this).width / 2 - 100, buttonsY + spacing, 200, 20, Component.literal("Mods"), (x) -> {
                Minecraft.getInstance().setScreen(new VulpesModMenuScreen(((TitleScreen) (Object) this)));
            });
            ((IScreenAccessor)((TitleScreen)(Object)this)).getRenderables().add(modsButtonIndex, button);
            ((IScreenAccessor)((TitleScreen)(Object)this)).getNarratables().add(modsButtonIndex, button);
            buttons.add(modsButtonIndex, button);
        }
    }
}
