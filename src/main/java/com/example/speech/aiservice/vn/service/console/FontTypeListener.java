package com.example.speech.aiservice.vn.service.console;

import com.example.speech.aiservice.vn.model.enums.FontType;

import java.util.Scanner;

public class FontTypeListener {

    public static FontType chooseFontFromConsole() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("🎨 Available Fonts:");
        FontType[] fonts = FontType.values();
        for (int i = 0; i < fonts.length; i++) {
            System.out.printf("  [%d] %s%n", i + 1, fonts[i].name());
        }

        FontType selectedFont = null;
        while (selectedFont == null) {
            System.out.print("📝 Enter the number of the font you want to use: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= fonts.length) {
                    selectedFont = fonts[choice - 1];
                } else {
                    System.out.println("⚠️ Invalid number. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
            }
        }

        System.out.println("✅ You selected: " + selectedFont.name());
        return selectedFont;
    }
}
