package com.example.speech.aiservice.vn.model.enums;

public enum FontType {
    BE_VIETNAM_PRO("be-vietnam-pro"),
    MERRIWEATHER("merriweather"),
    MERRIWEATHER_ITALIC("merriweather-italic"),
    PATRICK_HAND("patrickhand-regular");

    private final String propertyKey;

    FontType(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyKey() {
        return propertyKey;
    }
}
