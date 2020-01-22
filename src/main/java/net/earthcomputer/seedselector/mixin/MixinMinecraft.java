package net.earthcomputer.seedselector.mixin;

import net.earthcomputer.seedselector.CreateWorldScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

	@Shadow public static File func_6240_b() {
		return null;
	}

	@Shadow public abstract void func_6272_a(GuiScreen var1);

	@Inject(method = "func_6247_b", at = @At("HEAD"), cancellable = true)
	private void preCreateWorld(String worldName, CallbackInfo ci) {
		File levelDat = new File(func_6240_b(), "saves/" + worldName + "/level.dat");
		if (!levelDat.exists()) {
			func_6272_a(new CreateWorldScreen(worldName));
			ci.cancel();
		}
	}

	@Inject(method = "func_6247_b", at = @At("TAIL"))
	private void postCreateWorld(CallbackInfo ci) {
		func_6272_a(null);
	}

}
