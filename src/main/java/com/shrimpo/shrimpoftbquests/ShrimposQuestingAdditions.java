package com.shrimpo.shrimpoftbquests;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(ShrimposQuestingAdditions.MOD_ID)
public class ShrimposQuestingAdditions {
    public static final String MOD_ID = "shrimposquestingadditions";

    public ShrimposQuestingAdditions(IEventBus modEventBus, ModContainer modContainer) {
        Config.register(modContainer);
    }
}
