package com.example.speech.aiservice.vn.model.enums;

import java.awt.*;

public enum ColorType {
    OFF_WHITE(new Color(255, 255, 240)),       // Soft off-white for main text
    DARK_OUTLINE(new Color(20, 20, 20)),       // Dark outline for better contrast
    WARM_YELLOW(new Color(255, 230, 160)),     // Warm yellow for titles and headers
    SOFT_BLUE(new Color(200, 240, 255)),       // Soft blue for chapter titles
    LIGHT_RED(new Color(255, 100, 100)),       // Light red for emphasis or alerts
    LIGHT_GRAY(new Color(180, 180, 180));      // Light gray for secondary or subtle text

    private final Color color;

    ColorType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
