package com.shrimpo.shrimpoftbquests;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec CLIENT_SPEC;

    public static final ModConfigSpec.BooleanValue CENTER_ALL_DESC;

    static {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        CENTER_ALL_DESC = BUILDER.comment(new String[] { "Automatically center all quest description text.", "Default: false" }).define("center_all_desc", false);
        CLIENT_SPEC = BUILDER.build();
    }

    public static void register(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
    }
}
