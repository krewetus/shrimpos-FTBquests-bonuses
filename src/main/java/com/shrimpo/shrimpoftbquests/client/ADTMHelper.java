package com.shrimpo.shrimpoftbquests.client;

import dev.ftb.mods.ftblibrary.ui.BlankPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ADTMHelper {
    public static final int DEFAULT_HRULE_COLOR = 0xFF6A6A98;

    public static class HeaderPanel extends BlankPanel {

        private final int line;
        private final int level;
        private final float scale;
        private final int textColor;
        private final int innerW;
        private final boolean center;
        private final int centeringWidth;
        private final int prefixWidth;
        private final boolean addLine;
        private final int lineColor;
        private final boolean canEdit;
        private final Component text;

        public HeaderPanel(Panel parent, int line, int level, float scale, int textColor, int innerW, boolean center, int centeringWidth, int prefixWidth, boolean addLine, int lineColor, boolean canEdit, int widgetH, int panelW, Component text) {
            super(parent, "H" + level);
            this.line = line;
            this.level = level;
            this.scale = scale;
            this.textColor = textColor;
            this.innerW = innerW;
            this.center = center;
            this.centeringWidth = centeringWidth;
            this.prefixWidth = prefixWidth;
            this.addLine = addLine;
            this.lineColor = lineColor;
            this.canEdit = canEdit;
            this.text = text;
            setSize(panelW, widgetH);
        }

        @Override
        public void drawBackground(GuiGraphics g, Theme t, int x, int y, int w, int h) {
            g.pose().pushPose();
            g.pose().translate(x + 3, y + 1, 0);
            g.pose().scale(scale, scale, scale);
            Font font = Minecraft.getInstance().font;
            int sw = (int)(innerW / scale);
            int textX = center ? Math.max(0, (sw - centeringWidth) / 2) - prefixWidth : 0;
            g.drawString(font, text, textX, 0, textColor, false);
            if (addLine) { g.fill(0, 11, sw, 12, lineColor);
            }
            g.pose().popPose();
        }
    }


    public static boolean isHorizontalRule(Component component) {
        String plain = component.getString().trim();
        return plain.length() >= 3 && plain.chars().allMatch(ch -> ch == '-');
    }

    public static int extractHRuleColor(Component component) {
        AtomicInteger color = new AtomicInteger(DEFAULT_HRULE_COLOR);

        component.visit((style, text) -> {
            if (!text.isEmpty() && text.chars().allMatch(ch -> ch == '-')) {
                TextColor textColor = style.getColor();
                if (textColor != null) {
                    color.set(0xFF000000 | textColor.getValue());
                }
            }
            return Optional.empty();
        }, Style.EMPTY);

        return color.get();
    }

    @Unique
    public static Component stripPrefix(Component component, String prefix) {
        MutableComponent result = Component.empty();
        boolean[] done = {false};

        component.visit((style, s) -> {
            String out = s;
            if (!done[0] && out.startsWith(prefix)) {
                out = out.substring(prefix.length());
                done[0] = true;
            }
            if (!out.isEmpty()) {
                result.append(Component.literal(out).setStyle(style));
            }
            return Optional.<Void>empty();
        }, Style.EMPTY);

        return result;
    }

    public static int getCenteringWidth(Font font, Component component) {
        return font.width(component);
    }

    public static int getWidthNoPrefix(Font font, Component component) {
        MutableComponent prefix = Component.empty();
        boolean[] foundFirstCounting = { false };

        component.visit((style, s) -> {
            if (foundFirstCounting[0]) {
                return Optional.empty();
            }

            String cleanStr = s.replaceAll("#", "");
            StringBuilder prefixStr = new StringBuilder();

            int len = cleanStr.length();

            for (int i = 0; i < len; i++) {
                char c = cleanStr.charAt(i);
                if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                    foundFirstCounting[0] = true;
                    break;
                }
                prefixStr.append(c);
            }

            if (!prefixStr.isEmpty()) {
                prefix.append(Component.literal(prefixStr.toString()).setStyle(style));
            }
            return Optional.empty();
        }, Style.EMPTY);
        return font.width(prefix);
    }
}
