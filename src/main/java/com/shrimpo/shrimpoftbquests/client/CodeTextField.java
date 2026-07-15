package com.shrimpo.shrimpoftbquests.client;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CodeTextField extends TextField {
    private final boolean center;
    private final int centeringWidth;
    private final int prefixWidth;
    private final List<ADTMHelper.CodeBox> boxes;
    private final Font font;

    public CodeTextField(
            Panel parent,
            Component text,
            boolean center,
            int centeringWidth,
            int prefixWidth,
            List<ADTMHelper.CodeBox> boxes,
            Font font,
            int usableWidth,
            int posX,
            int posY
    ) {
        super(parent);
        this.center = center;
        this.centeringWidth = centeringWidth;
        this.prefixWidth = prefixWidth;
        this.boxes = boxes;
        this.font = font;
        setMaxWidth(usableWidth).setSpacing(9).setText(text);
        setPosAndSize(posX, posY, usableWidth, this.height);
    }

    private int getShiftedX(int x, int w) {
        return center ? x + Math.max(0, (w - centeringWidth) / 2) - prefixWidth : x;
    }

    @Override
    public void draw(GuiGraphics g, Theme t, int x, int y, int w, int h) {
        int textX = getShiftedX(x, w);
        Objects.requireNonNull(font);
        int textY = y + (h - 9) / 2;

        for (ADTMHelper.CodeBox box : boxes) {
            int x0 = textX + box.relativeX() - 2;
            int x1 = textX + box.relativeX() + box.width() + 2;
            int y0 = textY - 1;
            int y1 = textY + 9 + 1;
            g.fill(x0, y0, x1, y1, ADTMHelper.COL_CODE_BG);
            g.fill(x0, y0, x1, y0 + 1, ADTMHelper.COL_CODE_BORDER);
            g.fill(x0, y1 - 1, x1, y1, ADTMHelper.COL_CODE_BORDER);
            g.fill(x0, y0, x0 + 1, y1, ADTMHelper.COL_CODE_BORDER);
            g.fill(x1 - 1, y0, x1, y1, ADTMHelper.COL_CODE_BORDER);
        }

        super.draw(g, t, textX, y, w, h);
    }

    @Override
    public Optional<Style> getComponentStyleAt(Theme theme, int mouseX, int mouseY) {
        int offset = getShiftedX(getX(), this.width) - getX();
        return super.getComponentStyleAt(theme, mouseX - offset, mouseY);
    }
}
