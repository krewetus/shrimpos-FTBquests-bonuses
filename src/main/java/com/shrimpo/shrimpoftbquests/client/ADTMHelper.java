package com.shrimpo.shrimpoftbquests.client;

import dev.ftb.mods.ftblibrary.ui.BlankPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ADTMHelper {
    public static final int DEFAULT_HRULE_COLOR = 0xFF6A6A98;
    public static final int COL_CODE_BG = 0xFF2B2D31;
    public static final int COL_CODE_BORDER = 0xFF3A3D41;
    public static final int COL_CODE_TEXT = 0xFFE3E5E8;
    public static final String CODE_INSERTION = "shrimpo_code";

    public record CodeBox(int relativeX, int width) {}

    public static class HeaderPanel extends BlankPanel {

        private final float scale;
        private final int textColor;
        private final int innerW;
        private final boolean center;
        private final int centeringWidth;
        private final int prefixWidth;
        private final List<CodeBox> boxes;
        private final boolean addLine;
        private final int lineColor;
        private final Component text;

        public HeaderPanel(Panel parent, int line, int level, float scale, int textColor, int innerW, boolean center, int centeringWidth, int prefixWidth, List<CodeBox> boxes, boolean addLine, int lineColor, boolean canEdit, int widgetH, int panelW, Component text) {
            super(parent, "H" + level);
            this.scale = scale;
            this.textColor = textColor;
            this.innerW = innerW;
            this.center = center;
            this.centeringWidth = centeringWidth;
            this.prefixWidth = prefixWidth;
            this.boxes = boxes;
            this.addLine = addLine;
            this.lineColor = lineColor;
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

            for (CodeBox box : boxes) {
                int x0 = textX + box.relativeX() - 2;
                int x1 = textX + box.relativeX() + box.width() + 2;
                int y0 = -1;
                int y1 = 9 + 1;
                g.fill(x0, y0, x1, y1, COL_CODE_BG);
                g.fill(x0, y0, x1, y0 + 1, COL_CODE_BORDER);
                g.fill(x0, y1 - 1, x1, y1, COL_CODE_BORDER);
                g.fill(x0, y0, x0 + 1, y1, COL_CODE_BORDER);
                g.fill(x1 - 1, y0, x1, y1, COL_CODE_BORDER);
            }

            g.drawString(font, text, textX, 0, textColor, false);
            if (addLine) {
                g.fill(0, 11, sw, 12, lineColor);
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

    public static boolean hasInlineCode(Component component) {
        return component.visit((style, s) ->
                s.contains("`") ? Optional.of(Boolean.TRUE) : Optional.empty(),
                Style.EMPTY).isPresent();
    }

    public static Style codeStyle(Style base, boolean isCode) {
        if (!isCode) {
            return base;
        }
        Style styled = base.withInsertion(CODE_INSERTION);
        return styled.getColor() == null
                ? styled.withColor(TextColor.fromRgb(COL_CODE_TEXT & 0xFFFFFF))
                : styled;
    }

    public static Component processInlineCode(Component component) {
        if (!hasInlineCode(component)) {
            return component;
        }

        MutableComponent result = Component.empty();
        boolean[] inCode = { false };

        component.visit((style, s) -> {
            StringBuilder seg = new StringBuilder();
            int len = s.length();
            for (int ci = 0; ci < len; ci++) {
                char c = s.charAt(ci);

                if (c == '\\' && ci + 1 < len && s.charAt(ci + 1) == '`') {
                    seg.append('`');
                    ci++; // do not toggle ts please
                    continue;
                }

                if (c == '`') {
                    if (!seg.isEmpty()) {
                        result.append(Component.literal(seg.toString()).setStyle(codeStyle(style, inCode[0])));
                        seg.setLength(0);
                    }
                    inCode[0] = !inCode[0];
                } else {
                    seg.append(c);
                }
            }
            if (!seg.isEmpty()) {
                result.append(Component.literal(seg.toString()).setStyle(codeStyle(style, inCode[0])));
            }
            return Optional.empty();
        }, Style.EMPTY);

        return result;
    }

    public static List<CodeBox> precomputeCodeBoxes(Font font, Component text) {
        List<CodeBox> list = new ArrayList<>();
        int[] curX = { 0 };

        text.visit((style, s) -> {
            if (s.isEmpty()) {
                return Optional.empty();
            }
            int pw = font.width(Component.literal(s).setStyle(style));
            if (CODE_INSERTION.equals(style.getInsertion())) {
                list.add(new CodeBox(curX[0], pw));
            }
            curX[0] += pw;
            return Optional.empty();
        }, Style.EMPTY);

        return list;
    }

    public static Component formattedTextToComponent(FormattedText formattedText) {
        if (formattedText instanceof Component component) {
            return component;
        }

        MutableComponent result = Component.empty();
        formattedText.visit((style, string) -> {
            result.append(Component.literal(string).setStyle(style));
            return Optional.empty();
        }, Style.EMPTY);
        return result;
    }
}
