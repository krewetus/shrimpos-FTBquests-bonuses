package com.shrimpo.shrimpoftbquests.mixin;

import dev.ftb.mods.ftblibrary.util.client.ImageComponent;
import dev.ftb.mods.ftbquests.client.gui.ImageComponentWidget;
import dev.ftb.mods.ftbquests.client.gui.quests.ViewQuestPanel;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ViewQuestPanel.class, remap = false)
public interface ViewQuestPanelInvoker {
    @Invoker("findImageComponent")
    ImageComponent invokeFindImageComponent(Component c);

    @Invoker("makeImageComponentWidget")
    ImageComponentWidget invokeMakeImageComponentWidget(ImageComponent img, int idx);
}