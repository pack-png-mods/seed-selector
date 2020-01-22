package net.earthcomputer.seedselector.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;
import java.io.File;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

	@Shadow public static File func_6240_b() {
		return null;
	}

	@Shadow public abstract void func_6261_a(World var1);

	@Shadow public abstract void func_6263_a(World var1, String var2);

	@Inject(method = "func_6247_b", at = @At("HEAD"), cancellable = true)
	private void onRun(String worldName, CallbackInfo ci) {
		File levelDat = new File(func_6240_b(), "saves/" + worldName + "/level.dat");
		if (!levelDat.exists()) {
			String answer = JOptionPane.showInputDialog(null, "Ender seed (leave blank for no seed)");
			if (answer != null && !answer.isEmpty()) {
				long seed;
				try {
					seed = Long.parseLong(answer);
				} catch (NumberFormatException e) {
					seed = answer.hashCode();
				}
				if (!validSeed(seed))
					JOptionPane.showMessageDialog(null, "Note: this seed could not have been generated normally", "Warning", JOptionPane.WARNING_MESSAGE);

				func_6261_a(null);
				System.gc();
				World world = new World(new File(func_6240_b(), "saves"), worldName, seed);
				if (world.field_1033_r) {
					func_6263_a(world, "Generating level");
				} else {
					func_6263_a(world, "Loading level");
				}

				ci.cancel();
			}
		}
	}

	// https://twitter.com/Geosquare_/status/1169623192153010176
	boolean validSeed(long a){long b=18218081,c=1L<<48,d=7847617,e=((((d*((24667315*(a>>>32)+b*(int)a+67552711)>>32)-b*((-4824621*(a>>>32)+d*(int)a+d)>>32))-11)*0xdfe05bcb1365L)%c);return((((0x5deece66dl*e+11)%c)>>16)<<32)+(int)(((0xbb20b4600a69L*e+0x40942de6baL)%c)>>16)==a;}

}
