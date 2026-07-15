package com.shrimpo.shrimpoftbquests.mixin;

import com.mojang.datafixers.util.Pair;
import dev.ftb.mods.ftblibrary.ui.BlankPanel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.VerticalSpaceWidget;
import dev.ftb.mods.ftblibrary.util.client.ImageComponent;
import dev.ftb.mods.ftbquests.client.gui.ImageComponentWidget;
import dev.ftb.mods.ftbquests.client.gui.quests.ViewQuestPanel;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.util.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(value = ViewQuestPanel.class, remap = false)
public abstract class AddDescriptionTextMixin {

    @Shadow @Final private List<Pair<Integer, Integer>> pageIndices;
    @Shadow private BlankPanel panelText;
    @Shadow private Quest quest;

    @Shadow private int getCurrentPage() { return 0; }
    @Shadow private ImageComponent findImageComponent(Component c) { return null; }
    @Shadow private ImageComponentWidget makeImageComponentWidget(ImageComponent img, int idx) { return null; }

    @Inject(method = "addDescriptionText", at = @At("HEAD"), cancellable = true)
    private void shrimposftbquestsbonuses$addDescriptionText(boolean canEdit, Component subtitle, CallbackInfo ci) {
        ci.cancel();

        Pair<Integer, Integer> pageSpan = pageIndices.get(getCurrentPage());
        if (!TextUtils.isComponentEmpty(subtitle)) {
            panelText.add(new VerticalSpaceWidget(panelText, 7));
        }

        for (int i = pageSpan.getFirst(); i <= pageSpan.getSecond() && i < quest.getDescription().size(); i++) {
            Component component = quest.getDescription().get(i);

            ImageComponent img = findImageComponent(component);
            if (img != null) {
                panelText.add(makeImageComponentWidget(img, i));
                continue;
            }

            boolean center = false;
            String plain = component.getString();

            if (plain.startsWith("<center>")) {
                center = true;
                component = stripPrefix(component, "<center>");
            } else if (plain.startsWith("[center]")) {
                center = true;
                component = stripPrefix(component, "[center]");
            }

            // TODO: headings H1 H2 H3 (#, ##, ###) etc

            TextField field = new TextField(panelText)
                    .setMaxWidth(panelText.width)
                    .setSpacing(9)
                    .setText(component);
            field.setWidth(panelText.width);
            if (center) {
                field.addFlags(Theme.CENTERED);
            }
            panelText.add(field);
        }
    }

    private static Component stripPrefix(Component component, String prefix) {
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
}