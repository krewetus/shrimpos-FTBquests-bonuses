package com.shrimpo.shrimpoftbquests.client;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.IScreenWrapper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.util.TextComponentParser;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = "shrimposftbquestsbonuses", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class QuestDescriptionCentering {

    static {
        // Same extension point FTB Quests uses for &z (rainbow text).
        // Registers 'x' so the parser accepts &x instead of throwing.
        TextComponentParser.SPECIAL_COLOR_CODES.put('x', CenterMarkerColor.INSTANCE);
    }

    private static final Set<TextField> PROCESSED =
            Collections.newSetFromMap(new WeakHashMap<>());

    private static final Field RAW_TEXT_FIELD;
    static {
        try {
            RAW_TEXT_FIELD = TextField.class.getDeclaredField("rawText");
            RAW_TEXT_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        if (screen instanceof IScreenWrapper wrapper) {
            BaseScreen gui = wrapper.getGui();
            if (gui instanceof QuestScreen questScreen) {
                walk(questScreen.viewQuestPanel);
            }
        }
    }

    private static void walk(Widget widget) {
        if (widget instanceof TextField field && !PROCESSED.contains(field)) {
            PROCESSED.add(field);
            applyCenterIfMarked(field);
        }
        if (widget instanceof Panel panel) {
            for (Widget child : panel.getWidgets()) {
                walk(child);
            }
        }
    }

    private static void applyCenterIfMarked(TextField field) {
        Component text = readRawText(field);
        if (text == null || !containsMarker(text)) {
            return;
        }

        // ViewQuestPanel already stretched this field to the full
        // description-panel width; setText() -> resize() shrinks it back
        // down, so capture and restore it around the call.
        int fullBoxWidth = field.width;

        field.setText(stripMarker(text));
        field.setWidth(fullBoxWidth);
        field.addFlags(Theme.CENTERED);
    }

    private static boolean containsMarker(Component text) {
        return text.visit(
                (style, s) -> isMarked(style) ? Optional.of(Boolean.TRUE) : Optional.<Boolean>empty(),
                Style.EMPTY
        ).orElse(false);
    }

    private static boolean isMarked(Style style) {
        return style.getColor() instanceof CenterMarkerColor;
    }

    private static Component stripMarker(Component text) {
        MutableComponent result = Component.empty();

        text.visit((style, s) -> {
            if (!s.isEmpty()) {
                Style outStyle = isMarked(style) ? style.withColor((TextColor) null) : style;
                result.append(Component.literal(s).setStyle(outStyle));
            }
            return Optional.<Void>empty();
        }, Style.EMPTY);

        return result;
    }

    private static Component readRawText(TextField field) {
        try {
            return (Component) RAW_TEXT_FIELD.get(field);
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}