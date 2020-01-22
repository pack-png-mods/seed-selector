package net.earthcomputer.seedselector.mixin;

import net.minecraft.src.GuiButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiButton.class)
public interface GuiButtonAccessor {

    @Accessor
    void setWidth(int width);

    @Accessor
    void setHeight(int height);

}
