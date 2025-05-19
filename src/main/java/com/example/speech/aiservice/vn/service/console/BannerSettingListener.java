package com.example.speech.aiservice.vn.service.console;

import com.example.speech.aiservice.vn.dto.response.BannerChapterStyleResponseDTO;
import com.example.speech.aiservice.vn.dto.response.BannerTitleStyleResponseDTO;
import com.example.speech.aiservice.vn.model.enums.ColorType;
import com.example.speech.aiservice.vn.model.enums.FontSizeType;
import com.example.speech.aiservice.vn.model.enums.FontType;
import com.example.speech.aiservice.vn.model.enums.LineSpacingType;
import com.example.speech.aiservice.vn.service.propertie.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class BannerSettingListener {

    private final PropertiesService propertiesService;

    @Autowired
    public BannerSettingListener(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    public BannerTitleStyleResponseDTO promptTitleStyleFromCode() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("🎨 FONT TYPE:");
        printEnumOptions(FontType.values());

        System.out.println("\n🎨 FONT COLOR:");
        printEnumOptions(ColorType.values());

        System.out.println("\n🎨 BORDER COLOR:");
        printEnumOptions(ColorType.values());

        System.out.println("\n🎨 FONT SIZE:");
        printEnumOptions(FontSizeType.values());


        System.out.print("\n🔢 Enter your code (e.g., 13213): ");
        String code = String.valueOf(1125);


        int fontIndex = getIndex(code.charAt(0), FontType.values().length);
        int fontColorIndex = getIndex(code.charAt(1), ColorType.values().length);
        int borderColorIndex = getIndex(code.charAt(2), ColorType.values().length);
        int fontSizeIndex = getIndex(code.charAt(3), FontSizeType.values().length);

        FontType font = FontType.values()[fontIndex];
        ColorType fontColor = ColorType.values()[fontColorIndex];
        ColorType borderColor = ColorType.values()[borderColorIndex];
        FontSizeType fontSize = FontSizeType.values()[fontSizeIndex];

        String fontPath = propertiesService.getBannerFontPath(font.getPropertyKey());

        return new BannerTitleStyleResponseDTO(
                fontPath,
                fontColor.getColor(),
                borderColor.getColor(),
                fontSize.getSize()
        );
    }

    public BannerChapterStyleResponseDTO promptChapterStyleFromCode() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("🎨 FONT TYPE:");
        printEnumOptions(FontType.values());

        System.out.println("\n🎨 FONT COLOR:");
        printEnumOptions(ColorType.values());

        System.out.println("\n🎨 BORDER COLOR:");
        printEnumOptions(ColorType.values());

        System.out.println("\n🎨 FONT SIZE:");
        printEnumOptions(FontSizeType.values());

        System.out.println("\n🎨 LINE SPACING:");
        printEnumOptions(LineSpacingType.values());

        System.out.print("\n🔢 Enter your code (e.g., 13213): ");
        String code = String.valueOf(11255);

        int fontIndex = getIndex(code.charAt(0), FontType.values().length);
        int fontColorIndex = getIndex(code.charAt(1), ColorType.values().length);
        int borderColorIndex = getIndex(code.charAt(2), ColorType.values().length);
        int fontSizeIndex = getIndex(code.charAt(3), FontSizeType.values().length);
        int spacingIndex = getIndex(code.charAt(4), LineSpacingType.values().length);

        FontType font = FontType.values()[fontIndex];
        ColorType fontColor = ColorType.values()[fontColorIndex];
        ColorType borderColor = ColorType.values()[borderColorIndex];
        FontSizeType fontSize = FontSizeType.values()[fontSizeIndex];
        LineSpacingType spacing = LineSpacingType.values()[spacingIndex];

        String fontPath = propertiesService.getBannerFontPath(font.getPropertyKey());

        return new BannerChapterStyleResponseDTO(
                fontPath,
                fontColor.getColor(),
                borderColor.getColor(),
                fontSize.getSize(),
                spacing.getSpacing()
        );
    }



    private void printEnumOptions(Enum<?>[] values) {
        for (int i = 0; i < values.length; i++) {
            System.out.printf("  [%d] %s%n", i + 1, values[i].name());
        }
    }

    private int getIndex(char digit, int max) {
        int index = Character.getNumericValue(digit) - 1;
        if (index < 0 || index >= max) {
            throw new IllegalArgumentException("❌ Invalid digit in code: " + digit);
        }
        return index;
    }
}
