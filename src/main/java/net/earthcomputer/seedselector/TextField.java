package net.earthcomputer.seedselector;

import net.earthcomputer.seedselector.mixin.MinecraftAccessor;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.function.Predicate;

public class TextField extends Gui {
    private final FontRenderer font;
    public int x;
    public int y;
    /**
     * The width of this text field.
     */
    private final int width;
    private final int height;
    /**
     * Has the current text being edited on the textbox.
     */
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    private boolean canLoseFocus = true;
    /**
     * If this value is true along with isEnabled, keyTyped will process the keys.
     */
    private boolean isFocused;
    /**
     * If this value is true along with isFocused, keyTyped will process the keys.
     */
    private boolean isEnabled = true;
    /**
     * The current character index that should be used as start of the rendered text.
     */
    private int lineScrollOffset;
    private int cursorPosition;
    /**
     * other selection position, maybe the same as the cursor
     */
    private int selectionEnd;
    private int enabledColor = 0xe0e0e0;
    private int disabledColor = 0x707070;
    /**
     * True if this textbox is visible
     */
    private boolean visible = true;
    /**
     * Called to check if the text is valid
     */
    private Predicate<String> validator = s -> true;

    public TextField(FontRenderer font, int x, int y, int width, int height) {
        this.font = font;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter() {
        cursorCounter++;
    }

    /**
     * Sets the text of the textbox, and moves the cursor to the end.
     */
    public void setText(String textIn) {
        if (validator.test(textIn)) {
            if (textIn.length() > maxStringLength) {
                text = textIn.substring(0, maxStringLength);
            } else {
                text = textIn;
            }

            setCursorPositionEnd();
        }
    }

    /**
     * Returns the contents of the textbox
     */
    public String getText() {
        return text;
    }

    /**
     * returns the text between the cursor and selectionEnd
     */
    public String getSelectedText() {
        int i = Math.min(cursorPosition, selectionEnd);
        int j = Math.max(cursorPosition, selectionEnd);
        return text.substring(i, j);
    }

    public void setValidator(Predicate<String> theValidator) {
        validator = theValidator;
    }

    private static boolean isAllowedCharacter(char c) {
        return c != 167 && c >= ' ' && c != 127;
    }

    private static String filterAllowedCharacters(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (isAllowedCharacter(c))
                sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Adds the given text after the cursor, or replaces the currently selected text if there is a selection.
     */
    public void writeText(String textToWrite) {
        String s = "";
        String s1 = filterAllowedCharacters(textToWrite);
        int i = Math.min(cursorPosition, selectionEnd);
        int j = Math.max(cursorPosition, selectionEnd);
        int k = maxStringLength - text.length() - (i - j);

        if (!text.isEmpty()) {
            s = s + text.substring(0, i);
        }

        int l;

        if (k < s1.length()) {
            s = s + s1.substring(0, k);
            l = k;
        } else {
            s = s + s1;
            l = s1.length();
        }

        if (!text.isEmpty() && j < text.length()) {
            s = s + text.substring(j);
        }

        if (validator.test(s)) {
            text = s;
            moveCursorBy(i - selectionEnd + l);
        }
    }

    /**
     * Deletes the given number of words from the current cursor's position, unless there is currently a selection, in
     * which case the selection is deleted instead.
     */
    public void deleteWords(int num) {
        if (!text.isEmpty()) {
            if (selectionEnd != cursorPosition) {
                writeText("");
            } else {
                deleteFromCursor(getNthWordFromCursor(num) - cursorPosition);
            }
        }
    }

    /**
     * Deletes the given number of characters from the current cursor's position, unless there is currently a selection,
     * in which case the selection is deleted instead.
     */
    public void deleteFromCursor(int num) {
        if (!text.isEmpty()) {
            if (selectionEnd != cursorPosition) {
                writeText("");
            } else {
                boolean flag = num < 0;
                int i = flag ? cursorPosition + num : cursorPosition;
                int j = flag ? cursorPosition : cursorPosition + num;
                String s = "";

                if (i >= 0) {
                    s = text.substring(0, i);
                }

                if (j < text.length()) {
                    s = s + text.substring(j);
                }

                if (validator.test(s)) {
                    text = s;

                    if (flag) {
                        moveCursorBy(num);
                    }
                }
            }
        }
    }

    /**
     * Gets the starting index of the word at the specified number of words away from the cursor position.
     */
    public int getNthWordFromCursor(int numWords) {
        return getNthWordFromPos(numWords, getCursorPosition());
    }

    /**
     * Gets the starting index of the word at a distance of the specified number of words away from the given position.
     */
    public int getNthWordFromPos(int n, int pos) {
        return getNthWordFromPosWS(n, pos, true);
    }

    /**
     * Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces
     */
    public int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
        int i = pos;
        boolean flag = n < 0;
        int j = Math.abs(n);

        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = text.length();
                i = text.indexOf(32, i);

                if (i == -1) {
                    i = l;
                } else {
                    while (skipWs && i < l && text.charAt(i) == ' ') {
                        i++;
                    }
                }
            } else {
                while (skipWs && i > 0 && text.charAt(i - 1) == ' ') {
                    i--;
                }

                while (i > 0 && text.charAt(i - 1) != ' ') {
                    i--;
                }
            }
        }

        return i;
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursorBy(int num) {
        setCursorPosition(selectionEnd + num);
    }

    /**
     * Sets the current position of the cursor.
     */
    public void setCursorPosition(int pos) {
        int i = text.length();
        if (pos < 0)
            pos = 0;
        if (pos > i)
            pos = i;
        cursorPosition = pos;
        setSelectionPos(cursorPosition);
    }

    /**
     * Moves the cursor to the very start of this text box.
     */
    public void setCursorPositionZero() {
        setCursorPosition(0);
    }

    /**
     * Moves the cursor to the very end of this text box.
     */
    public void setCursorPositionEnd() {
        setCursorPosition(text.length());
    }

    /**
     * Call this method from your GuiScreen to process the keys into the textbox
     */
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        if (!isFocused) {
            return false;
        } else if (isKeyComboCtrlA(keyCode)) {
            setCursorPositionEnd();
            setSelectionPos(0);
            return true;
        } else if (isKeyComboCtrlC(keyCode)) {
            setClipboardString(getSelectedText());
            return true;
        } else if (isKeyComboCtrlV(keyCode)) {
            if (isEnabled) {
                writeText(GuiScreen.getClipboardString());
            }

            return true;
        } else if (isKeyComboCtrlX(keyCode)) {
            setClipboardString(getSelectedText());

            if (isEnabled) {
                writeText("");
            }

            return true;
        } else {
            switch (keyCode) {
                case Keyboard.KEY_BACK:

                    if (isCtrlKeyDown()) {
                        if (isEnabled) {
                            deleteWords(-1);
                        }
                    } else if (isEnabled) {
                        deleteFromCursor(-1);
                    }

                    return true;
                case Keyboard.KEY_HOME:

                    if (isShiftKeyDown()) {
                        setSelectionPos(0);
                    } else {
                        setCursorPositionZero();
                    }

                    return true;
                case Keyboard.KEY_LEFT:

                    if (isShiftKeyDown()) {
                        if (isCtrlKeyDown()) {
                            setSelectionPos(getNthWordFromPos(-1, getSelectionEnd()));
                        } else {
                            setSelectionPos(getSelectionEnd() - 1);
                        }
                    } else if (isCtrlKeyDown()) {
                        setCursorPosition(getNthWordFromCursor(-1));
                    } else {
                        moveCursorBy(-1);
                    }

                    return true;
                case Keyboard.KEY_RIGHT:

                    if (isShiftKeyDown()) {
                        if (isCtrlKeyDown()) {
                            setSelectionPos(getNthWordFromPos(1, getSelectionEnd()));
                        } else {
                            setSelectionPos(getSelectionEnd() + 1);
                        }
                    } else if (isCtrlKeyDown()) {
                        setCursorPosition(getNthWordFromCursor(1));
                    } else {
                        moveCursorBy(1);
                    }

                    return true;
                case Keyboard.KEY_END:

                    if (isShiftKeyDown()) {
                        setSelectionPos(text.length());
                    } else {
                        setCursorPositionEnd();
                    }

                    return true;
                case Keyboard.KEY_DELETE:

                    if (isCtrlKeyDown()) {
                        if (isEnabled) {
                            deleteWords(1);
                        }
                    } else if (isEnabled) {
                        deleteFromCursor(1);
                    }

                    return true;
                default:

                    if (isAllowedCharacter(typedChar)) {
                        if (isEnabled) {
                            writeText(Character.toString(typedChar));
                        }

                        return true;
                    } else {
                        return false;
                    }
            }
        }
    }

    /**
     * Called when mouse is clicked, regardless as to whether it is over this button or not.
     */
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;

        if (canLoseFocus) {
            setFocused(flag);
        }

        if (isFocused && flag && mouseButton == 0) {
            int i = mouseX - x;

            if (enableBackgroundDrawing) {
                i -= 4;
            }

            String s = trimStringToWidth(font, text.substring(lineScrollOffset), getWidth());
            setCursorPosition(trimStringToWidth(font, s, i).length() + lineScrollOffset);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Draws the textbox
     */
    public void drawTextBox() {
        if (getVisible()) {
            if (getEnableBackgroundDrawing()) {
                drawRect(x - 1, y - 1, x + width + 1, y + height + 1, 0xffa0a0a0);
                drawRect(x, y, x + width, y + height, 0xff000000);
            }

            int i = isEnabled ? enabledColor : disabledColor;
            int j = cursorPosition - lineScrollOffset;
            int k = selectionEnd - lineScrollOffset;
            String s = trimStringToWidth(font, text.substring(lineScrollOffset), getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = isFocused && cursorCounter / 6 % 2 == 0 && flag;
            int l = enableBackgroundDrawing ? x + 4 : x;
            int i1 = enableBackgroundDrawing ? y + (height - 8) / 2 : y;
            int j1 = l;

            if (k > s.length()) {
                k = s.length();
            }

            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                font.drawStringWithShadow(s1, l, i1, i);
                j1 += font.getStringWidth(s1) + 1;
            }

            boolean flag2 = cursorPosition < text.length() || text.length() >= getMaxStringLength();
            int k1 = j1;

            if (!flag) {
                k1 = j > 0 ? l + width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length()) {
                font.drawStringWithShadow(s.substring(j), j1, i1, i);
                //j1 += this.font.getStringWidth(s.substring(j));
            }

            if (flag1) {
                if (flag2) {
                    drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + 9, 0xffd0d0d0);
                } else {
                    font.drawStringWithShadow("_", k1, i1, i);
                }
            }

            if (k != j) {
                int l1 = l + font.getStringWidth(s.substring(0, k));
                drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
            }
        }
    }

    /**
     * Draws the blue selection box.
     */
    private void drawSelectionBox(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY) {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > x + width) {
            endX = x + width;
        }

        if (startX > x + width) {
            startX = x + width;
        }

        Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(0f, 0f, 255f, 255f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);
        tessellator.startDrawingQuads();
        tessellator.addVertex(startX, endY, 0.0D);
        tessellator.addVertex(endX, endY, 0.0D);
        tessellator.addVertex(endX, startY, 0.0D);
        tessellator.addVertex(startX, startY, 0.0D);
        tessellator.draw();
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Sets the maximum length for the text in this text box. If the current text is longer than this length, the
     * current text will be trimmed.
     */
    public void setMaxStringLength(int length) {
        maxStringLength = length;

        if (text.length() > length) {
            text = text.substring(0, length);
        }
    }

    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    public int getMaxStringLength() {
        return maxStringLength;
    }

    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition() {
        return cursorPosition;
    }

    /**
     * Gets whether the background and outline of this text box should be drawn (true if so).
     */
    public boolean getEnableBackgroundDrawing() {
        return enableBackgroundDrawing;
    }

    /**
     * Sets whether or not the background and outline of this text box should be drawn.
     */
    public void setEnableBackgroundDrawing(boolean enableBackgroundDrawingIn) {
        enableBackgroundDrawing = enableBackgroundDrawingIn;
    }

    /**
     * Sets the color to use when drawing this text box's text. A different color is used if this text box is disabled.
     */
    public void setTextColor(int color) {
        enabledColor = color;
    }

    /**
     * Sets the color to use for text in this text box when this text box is disabled.
     */
    public void setDisabledTextColor(int color) {
        disabledColor = color;
    }

    /**
     * Sets focus to this gui element
     */
    public void setFocused(boolean isFocusedIn) {
        if (isFocusedIn && !isFocused) {
            cursorCounter = 0;
        }

        isFocused = isFocusedIn;
    }

    /**
     * Getter for the focused field
     */
    public boolean isFocused() {
        return isFocused;
    }

    /**
     * Sets whether this text box is enabled. Disabled text boxes cannot be typed in.
     */
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    /**
     * the side of the selection that is not the cursor, may be the same as the cursor
     */
    public int getSelectionEnd() {
        return selectionEnd;
    }

    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    public int getWidth() {
        return getEnableBackgroundDrawing() ? width - 8 : width;
    }

    /**
     * Sets the position of the selection anchor (the selection anchor and the cursor position mark the edges of the
     * selection). If the anchor is set beyond the bounds of the current text, it will be put back inside.
     */
    public void setSelectionPos(int position) {
        int i = text.length();

        if (position > i) {
            position = i;
        }

        if (position < 0) {
            position = 0;
        }

        selectionEnd = position;

        if (font != null) {
            if (lineScrollOffset > i) {
                lineScrollOffset = i;
            }

            int j = getWidth();
            String s = trimStringToWidth(font, text.substring(lineScrollOffset), j);
            int k = s.length() + lineScrollOffset;

            if (position == lineScrollOffset) {
                lineScrollOffset -= trimStringToWidth(font, text, j, true).length();
            }

            if (position > k) {
                lineScrollOffset += position - k;
            } else if (position <= lineScrollOffset) {
                lineScrollOffset -= lineScrollOffset - position;
            }

            if (lineScrollOffset < 0)
                lineScrollOffset = 0;
            else if (lineScrollOffset > i)
                lineScrollOffset = i;
        }
    }

    /**
     * Sets whether this text box loses focus when something other than it is clicked.
     */
    public void setCanLoseFocus(boolean canLoseFocusIn) {
        canLoseFocus = canLoseFocusIn;
    }

    /**
     * returns true if this textbox is visible
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * Sets whether or not this textbox is visible
     */
    public void setVisible(boolean isVisible) {
        visible = isVisible;
    }

    private static boolean isCtrlKeyDown() {
        if (MinecraftAccessor.getOS() == EnumOS2.macos) {
            return Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA);
        } else {
            return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        }
    }

    private static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    private static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
    }

    private static boolean isKeyComboCtrlA(int keyCode) {
        return keyCode == Keyboard.KEY_A && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    private static boolean isKeyComboCtrlC(int keyCode) {
        return keyCode == Keyboard.KEY_C && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    private static boolean isKeyComboCtrlV(int keyCode) {
        return keyCode == Keyboard.KEY_V && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    private static boolean isKeyComboCtrlX(int keyCode) {
        return keyCode == Keyboard.KEY_X && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    private static void setClipboardString(String text) {
        try {
            StringSelection selection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        } catch (Exception ignore) {
        }
    }

    private static String trimStringToWidth(FontRenderer font, String text, int maxWidth) {
        return trimStringToWidth(font, text, maxWidth, false);
    }

    private static String trimStringToWidth(FontRenderer font, String text, int maxWidth, boolean reverse) {
        int width = 0;
        int length;
        for (length = 0; length < text.length() && width < maxWidth; length++)
            width += font.getStringWidth(Character.toString(text.charAt(reverse ? text.length() - 1 - length : length)));
        return reverse ? text.substring(text.length() - length) : text.substring(0, length);
    }

}
