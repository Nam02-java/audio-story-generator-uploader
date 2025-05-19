package com.example.speech.aiservice.vn.model.enums;

public enum LineSpacingType {
    TIGHT(80), // Close together
    NORMAL(110), // Default is fine
    SPACED(140), // A bit more open
    LARGE(170), // Clearly wide - recommend !
    XLARGE(200), // Extremely open
    XXLARGE(250), // Very wide - for long titles
    XXXLARGE(300); // Ultra wide - final boss

    private final int spacing;

    LineSpacingType(int spacing) {
        this.spacing = spacing;
    }

    public int getSpacing() {
        return spacing;
    }
}
