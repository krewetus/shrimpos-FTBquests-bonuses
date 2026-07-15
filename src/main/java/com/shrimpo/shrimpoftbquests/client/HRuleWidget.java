package com.shrimpo.shrimpoftbquests.client;

import dev.ftb.mods.ftblibrary.ui.BlankPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;

public class HRuleWidget extends BlankPanel {
    private final int ruleColor;

    public HRuleWidget(Panel parent, int ruleColor) {
        super(parent, "HRule");
        this.ruleColor = ruleColor;
        setSize(parent.width, 3 * 2 + 1);
    }

    @Override
    public void drawBackground(GuiGraphics g, Theme t, int x, int y, int w, int h) {
        int lineY = y + 3;
        int innerW = w - 4 * 2;
        int ruleW = (int) (innerW * 0.8f);
        int ruleX = x + 4 + (innerW - ruleW) / 2;
        g.fill(ruleX, lineY, ruleX + ruleW, lineY + 1, ruleColor);
    }
}
