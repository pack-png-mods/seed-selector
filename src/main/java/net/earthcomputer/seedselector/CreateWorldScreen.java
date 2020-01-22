package net.earthcomputer.seedselector;

import net.earthcomputer.seedselector.mixin.GuiButtonAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.World;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.Random;

public class CreateWorldScreen extends GuiScreen {

    private final String worldName;
    private TextField seedTextField;
    private boolean isRandomSeed;
    private long randomSeed;

    public CreateWorldScreen(String worldName) {
        this.worldName = worldName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void func_6448_a() {
        Keyboard.enableRepeatEvents(true);
        String seed = seedTextField == null ? "" : seedTextField.getText();
        seedTextField = new TextField(field_6451_g, width / 4, height / 2, width / 2, 20);
        seedTextField.setMaxStringLength(256);
        seedTextField.setText(seed);

        GuiButton button = new GuiButton(0, width / 2 - 50, height / 2 + 40, "Done");
        //noinspection ConstantConditions
        GuiButtonAccessor buttonAccessor = (GuiButtonAccessor) button;
        buttonAccessor.setWidth(100);
        buttonAccessor.setHeight(20);
        controlList.add(button);
    }

    @Override
    public void func_6449_h() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        seedTextField.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled && button.id == 0)
            confirm();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        seedTextField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_RETURN)
            confirm();
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        seedTextField.mouseClicked(x, y, button);
        super.mouseClicked(x, y, button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        func_578_i();
        drawCenteredString(field_6451_g, "Select Seed for " + worldName, width / 2, 20, 0xffffff);
        long seed = evaluateSeed();
        drawCenteredString(field_6451_g, "Seed evaluates to " + seed, width / 2, 50, 0xffffff);
        if (!validSeed(seed)) {
            drawCenteredString(field_6451_g, "This seed cannot generate naturally", width / 2, 70, 0xffff80);
        }
        seedTextField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void confirm() {
        long seed = evaluateSeed();
        mc.func_6261_a(null);
        System.gc();
        World world = new World(new File(Minecraft.func_6240_b(), "saves"), worldName, seed);
        if (world.field_1033_r) {
            mc.func_6263_a(world, "Generating level");
        } else {
            mc.func_6263_a(world, "Loading level");
        }
        mc.func_6272_a(null);
    }

    private long evaluateSeed() {
        if (seedTextField.getText().isEmpty()) {
            if (!isRandomSeed) {
                isRandomSeed = true;
                randomSeed = new Random().nextLong();
            }
            return randomSeed;
        }
        long seed;
        try {
            seed = Long.parseLong(seedTextField.getText());
        } catch (NumberFormatException e) {
            seed = seedTextField.getText().hashCode();
        }
        if (seed == 0) {
            if (!isRandomSeed) {
                isRandomSeed = true;
                randomSeed = new Random().nextLong();
            }
            return randomSeed;
        }
        isRandomSeed = false;
        return seed;
    }

    // https://twitter.com/Geosquare_/status/1169623192153010176
    boolean validSeed(long a){long b=18218081,c=1L<<48,d=7847617,e=((((d*((24667315*(a>>>32)+b*(int)a+67552711)>>32)-b*((-4824621*(a>>>32)+d*(int)a+d)>>32))-11)*0xdfe05bcb1365L)%c);return((((0x5deece66dL*e+11)%c)>>16)<<32)+(int)(((0xbb20b4600a69L*e+0x40942de6baL)%c)>>16)==a;}
}
