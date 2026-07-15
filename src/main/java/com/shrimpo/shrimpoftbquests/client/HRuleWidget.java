package com.shrimpo.shrimpoftbquests.client;

import dev.ftb.mods.ftblibrary.ui.BlankPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;

public class HRuleWidget extends BlankPanel {
    private static final int HORIZONTAL_PADDING = 4;

    private final int ruleColor;

    public HRuleWidget(Panel parent, int ruleColor) {
        super(parent, "HRule");
        this.ruleColor = ruleColor;
        setSize(parent.width, 5);
    }

    @Override
    public void drawBackground(GuiGraphics g, Theme t, int x, int y, int w, int h) {
        int mid = y + h / 2;
        int innerW = w - HORIZONTAL_PADDING * 2;
        int ruleW = (int) (innerW * 0.8f);
        int ruleX = x + HORIZONTAL_PADDING + (innerW - ruleW) / 2;
        g.fill(ruleX, mid, ruleX + ruleW, mid + 1, ruleColor);
    }
}
