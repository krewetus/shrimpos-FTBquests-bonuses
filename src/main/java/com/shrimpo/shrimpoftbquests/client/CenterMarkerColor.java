package com.shrimpo.shrimpoftbquests.client;

import dev.ftb.mods.ftblibrary.util.text.CustomTextColor;

public final class CenterMarkerColor extends CustomTextColor {
    public static final CenterMarkerColor INSTANCE = new CenterMarkerColor();

    private CenterMarkerColor() {
        super("shrimposftbquestsbonuses:center");
    }
}