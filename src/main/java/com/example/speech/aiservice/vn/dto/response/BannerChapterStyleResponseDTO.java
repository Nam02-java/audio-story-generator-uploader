package com.example.speech.aiservice.vn.dto.response;

import java.awt.*;

public class BannerChapterStyleResponseDTO {
    private final String fontPath;
    private final Color fontColor;
    private final Color borderColor;
    private final float fontSize;
    private final int space;


    public BannerChapterStyleResponseDTO(String fontPath, Color fontColor, Color borderColor, float fontSize, int space) {
        this.fontPath = fontPath;
        this.fontColor = fontColor;
        this.borderColor = borderColor;
        this.fontSize = fontSize;
        this.space = space;
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

    public int getSpace() {
        return space;
    }
}
