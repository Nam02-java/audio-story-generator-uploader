package com.example.speech.aiservice.vn.service.console;

import com.example.speech.aiservice.vn.model.enums.ColorType;

import java.util.Scanner;

public class ColorTypeListener {

    public static ColorType chooseColorFromConsole() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("🎨 Available Fonts:");
        ColorType[] colors = ColorType.values();
        for (int i = 0; i < colors.length; i++) {
            System.out.printf("  [%d] %s%n", i + 1, colors[i].name());
        }

        ColorType selectedColor = null;
        while (selectedColor == null) {
            System.out.print("📝 Enter the number of the color you want to use: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= colors.length) {
                    selectedColor = colors[choice - 1];
                } else {
                    System.out.println("⚠️ Invalid number. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
            }
        }

        System.out.println("✅ You selected: " + selectedColor.name());
        return selectedColor;
    }
}
