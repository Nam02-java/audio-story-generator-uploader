package com.example.speech.aiservice.vn.service.console;

import com.example.speech.aiservice.vn.model.enums.ColorType;
import com.example.speech.aiservice.vn.model.enums.FontSizeType;

import java.util.Scanner;

public class FontSizeTypeListener {
    public static FontSizeType chooseSizeFromConsole(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("🎨 Available sizes:");
        FontSizeType[] fontSizeTypes = FontSizeType.values();
        for (int i = 0; i < fontSizeTypes.length; i++) {
            System.out.printf("  [%d] %s%n", i + 1, fontSizeTypes[i].name());
        }

        FontSizeType selectedFontSize = null;
        while (selectedFontSize == null) {
            System.out.print("📝 Enter the number of the size you want to use: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= fontSizeTypes.length) {
                    selectedFontSize = fontSizeTypes[choice - 1];
                } else {
                    System.out.println("⚠️ Invalid number. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
            }
        }

        System.out.println("✅ You selected: " + selectedFontSize.name());
        return selectedFontSize;
    }
}
