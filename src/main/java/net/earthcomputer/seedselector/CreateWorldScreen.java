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
    private TextField spawnXTextField;
    private TextField spawnZTextField;
    private boolean isRandomSeed;
    private long randomSeed;

    public CreateWorldScreen(String worldName) {
        this.worldName = worldName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void func_6448_a() {
        Keyboard.enableRepeatEvents(true);
        String oldText = seedTextField == null ? "" : seedTextField.getText();
        seedTextField = new TextField(field_6451_g, width / 4, 100, width / 2, 20);
        seedTextField.setMaxStringLength(256);
        seedTextField.setText(oldText);

        oldText = spawnXTextField == null ? "" : spawnXTextField.getText();
        spawnXTextField = new TextField(field_6451_g, width / 4, 160, width / 4 - 20, 20);
        spawnXTextField.setText(oldText);

        oldText = spawnZTextField == null ? "" : spawnZTextField.getText();
        spawnZTextField = new TextField(field_6451_g, width / 2 + 20, 160, width / 4 - 20, 20);
        spawnZTextField.setText(oldText);

        GuiButton button = new GuiButton(0, width / 2 - 50, 210, "Done");
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
        spawnXTextField.updateCursorCounter();
        spawnZTextField.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled && button.id == 0)
            confirm();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        seedTextField.textboxKeyTyped(typedChar, keyCode);
        spawnXTextField.textboxKeyTyped(typedChar, keyCode);
        spawnZTextField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_RETURN)
            confirm();
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        seedTextField.mouseClicked(x, y, button);
        spawnXTextField.mouseClicked(x, y, button);
        spawnZTextField.mouseClicked(x, y, button);
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

        drawCenteredString(field_6451_g, "Spawn point:", width / 2, 140, 0xffffff);
        seedTextField.drawTextBox();
        drawString(field_6451_g, "X:", width / 4 - 15, 164, 0xffffff);
        spawnXTextField.drawTextBox();
        drawString(field_6451_g, "Z:", width / 2 + 5, 164, 0xffffff);
        spawnZTextField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void confirm() {
        long seed = evaluateSeed();
        mc.func_6261_a(null);
        System.gc();
        World world = new World(new File(Minecraft.func_6240_b(), "saves"), worldName, seed);
        try {
            int spawnX = Integer.parseInt(spawnXTextField.getText());
            int spawnZ = Integer.parseInt(spawnZTextField.getText());
            world.spawnX = spawnX;
            world.spawnZ = spawnZ;
        } catch (NumberFormatException ignore) {}

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
                do {
                    randomSeed = new Random().nextLong();
                } while (randomSeed == 0);
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
                do {
                    randomSeed = new Random().nextLong();
                } while (randomSeed == 0);
            }
            return randomSeed;
        }
        isRandomSeed = false;
        return seed;
    }

    // https://twitter.com/Geosquare_/status/1169623192153010176
    boolean validSeed(long a){long b=18218081,c=1L<<48,d=7847617,e=((((d*((24667315*(a>>>32)+b*(int)a+67552711)>>32)-b*((-4824621*(a>>>32)+d*(int)a+d)>>32))-11)*0xdfe05bcb1365L)%c);return((((0x5deece66dL*e+11)%c)>>16)<<32)+(int)(((0xbb20b4600a69L*e+0x40942de6baL)%c)>>16)==a;}
}
