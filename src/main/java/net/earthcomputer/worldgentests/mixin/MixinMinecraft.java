package net.earthcomputer.worldgentests.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

	@Inject(method = "run", at = @At("HEAD"))
	private void onRun(CallbackInfo ci) {
		System.out.println("Hello from a mixin!");
	}

}
