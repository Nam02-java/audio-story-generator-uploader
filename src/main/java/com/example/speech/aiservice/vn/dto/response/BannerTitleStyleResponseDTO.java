package com.example.speech.aiservice.vn.dto.response;

import java.awt.*;

public class BannerTitleStyleResponseDTO {
    private final String fontPath;
    private final Color fontColor;
    private final Color borderColor;
    private final float fontSize;

    public BannerTitleStyleResponseDTO(String fontPath, Color fontColor, Color borderColor, float fontSize) {
        this.fontPath = fontPath;
        this.fontColor = fontColor;
        this.borderColor = borderColor;
        this.fontSize = fontSize;
    }

    public String getFontPath() {
        return fontPath;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public float getFontSize() {
        return fontSize;
    }
}
