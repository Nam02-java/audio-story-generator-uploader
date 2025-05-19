package com.example.speech.aiservice.vn.service.console;

import com.example.speech.aiservice.vn.model.enums.ColorType;
import com.example.speech.aiservice.vn.model.enums.LineSpacingType;

import java.util.Scanner;

public class LineSpcaeTypeListener {
    public static LineSpacingType chooseSpacingFromConsole() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("🎨 Available spacing:");
        LineSpacingType[] spacingTypes = LineSpacingType.values();
        for (int i = 0; i < spacingTypes.length; i++) {
            System.out.printf("  [%d] %s%n", i + 1, spacingTypes[i].name());
        }

        LineSpacingType selectedSpacing = null;
        while (selectedSpacing == null) {
            System.out.print("📝 Enter the number of the spacing you want to use: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= spacingTypes.length) {
                    selectedSpacing = spacingTypes[choice - 1];
                } else {
                    System.out.println("⚠️ Invalid number. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
            }
        }

        System.out.println("✅ You selected: " + selectedSpacing.name());
        return selectedSpacing;
    }
}
