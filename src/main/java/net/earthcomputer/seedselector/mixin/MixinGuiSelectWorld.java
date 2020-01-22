package net.earthcomputer.seedselector.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSelectWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiSelectWorld.class)
public class MixinGuiSelectWorld {

    @Redirect(method = "selectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;func_6272_a(Lnet/minecraft/src/GuiScreen;)V"))
    private void redirectOpenScreen(Minecraft mc, GuiScreen screen) {
        // nop
    }

}
