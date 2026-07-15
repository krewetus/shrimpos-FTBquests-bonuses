package com.shrimpo.shrimpoftbquests.mixin;

import com.mojang.datafixers.util.Pair;
import com.shrimpo.shrimpoftbquests.client.ADTMHelper;
import com.shrimpo.shrimpoftbquests.client.CodeTextField;
import com.shrimpo.shrimpoftbquests.client.HRuleWidget;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftbquests.client.gui.quests.ViewQuestPanel;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

import static com.shrimpo.shrimpoftbquests.client.ADTMHelper.stripPrefix;

@Mixin(value = ViewQuestPanel.class, remap = false)
public abstract class AddDescriptionTextMixin {

    @Shadow @Final private List<Pair<Integer, Integer>> pageIndices;
    @Shadow private BlankPanel panelText;
    @Shadow private Quest quest;
    @Shadow private int getCurrentPage() { return 0; }

    @Inject(method = "addDescriptionText", at = @At("HEAD"), cancellable = true)
    private void addDescriptionText(boolean canEdit, Component subtitle, CallbackInfo callinfo) {
        callinfo.cancel();

        Pair<Integer, Integer> pageSpan = pageIndices.get(getCurrentPage());
        if (!TextUtils.isComponentEmpty(subtitle)) {
            panelText.add(new VerticalSpaceWidget(panelText, 7));
        }

        Font font = Minecraft.getInstance().font;

        for (int i = pageSpan.getFirst(); i <= pageSpan.getSecond() && i < quest.getDescription().size(); i++) {
            Component component = quest.getDescription().get(i);

            boolean center = false;
            int headerLevel = 0;
            String plain = component.getString();

            if (ADTMHelper.isHorizontalRule(component)) {
                panelText.add(new HRuleWidget(panelText, ADTMHelper.extractHRuleColor(component)));
                continue;
            }

            if (plain.startsWith("= ")) {
                center = true;
                component = stripPrefix(component, "= ");
                plain = component.getString();
            }

            if (plain.startsWith("### ")) {
                headerLevel = 3;
                component = stripPrefix(component, "### ");
            } else if (plain.startsWith("## ")) {
                headerLevel = 2;
                component = stripPrefix(component, "## ");
            } else if (plain.startsWith("# ")) {
                headerLevel = 1;
                component = stripPrefix(component, "# ");
            }

            component = ADTMHelper.processInlineCode(component);

            if (headerLevel > 0) {
                addHeader(canEdit, i, component, center, font, headerLevel);
                continue;
            }

            addNormal(component, center, font);
        }
    }

    @Unique
    private void addNormal(Component text, boolean center, Font font) {
        int usableWidth = this.panelText.width - 6;
        for (FormattedText fl : font.getSplitter().splitLines(text, usableWidth, Style.EMPTY)) {
            Component lc = ADTMHelper.formattedTextToComponent(fl);
            List<ADTMHelper.CodeBox> boxes = ADTMHelper.precomputeCodeBoxes(font, lc);
            int centeringWidth = ADTMHelper.getCenteringWidth(font, lc);
            int prefixWidth = ADTMHelper.getWidthNoPrefix(font, lc);
            this.panelText.add(new CodeTextField(
                    this.panelText,
                    lc,
                    center,
                    centeringWidth,
                    prefixWidth,
                    boxes,
                    font,
                    usableWidth,
                    3,
                    0
            ));
        }
    }

    @Unique
    public void addHeader(boolean canEdit, int line, Component text, boolean center, Font font, int level) {
        float scale = (level == 1) ? 1.5F : ((level == 2) ? 1.25F : 1.1F);
        int textColor = -1;
        int innerW = this.panelText.width - 6;
        List<ADTMHelper.CodeBox> boxes = ADTMHelper.precomputeCodeBoxes(font, text);
        int centeringWidth = ADTMHelper.getCenteringWidth(font, text);
        int prefixWidth = ADTMHelper.getWidthNoPrefix(font, text);
        boolean addLine = (level == 5);
        int lineColor = 0x44000000 | textColor & 0xFFFFFF;
        int widgetH = (int)(9.0F * scale) + ((level == 1) ? 6 : 3);
        int panelW = this.panelText.width;
        Objects.requireNonNull(font);

        this.panelText.add(new ADTMHelper.HeaderPanel(
                this.panelText,
                line,
                level,
                scale,
                textColor,
                innerW,
                center,
                centeringWidth,
                prefixWidth,
                boxes,
                addLine,
                lineColor,
                canEdit,
                widgetH,
                panelW,
                text
        ));
    }
}
