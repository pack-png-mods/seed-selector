package net.earthcomputer.seedselector.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EnumOS2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {

    @SuppressWarnings("PublicStaticMixinMember")
    @Invoker("func_6267_r")
    static EnumOS2 getOS() {
        return EnumOS2.windows;
    }

}
