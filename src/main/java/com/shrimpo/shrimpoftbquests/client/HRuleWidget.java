package com.shrimpo.shrimpoftbquests.client;

import dev.ftb.mods.ftblibrary.ui.BlankPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;

public class HRuleWidget extends BlankPanel {
    private static final int HORIZONTAL_PADDING = 4;
    private static final int VERTICAL_PADDING = 3;
    private static final int RULE_THICKNESS = 1;

    private final int ruleColor;

    public HRuleWidget(Panel parent, int ruleColor) {
        super(parent, "HRule");
        this.ruleColor = ruleColor;
        setSize(parent.width, VERTICAL_PADDING * 2 + RULE_THICKNESS);
    }

    @Override
    public void drawBackground(GuiGraphics g, Theme t, int x, int y, int w, int h) {
        int lineY = y + VERTICAL_PADDING;
        int innerW = w - HORIZONTAL_PADDING * 2;
        int ruleW = (int) (innerW * 0.8f);
        int ruleX = x + HORIZONTAL_PADDING + (innerW - ruleW) / 2;
        g.fill(ruleX, lineY, ruleX + ruleW, lineY + RULE_THICKNESS, ruleColor);
    }
}
