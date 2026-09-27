package library_management.util;

import java.util.Scanner;

/**
 * InputUtil - Safe Console Input Helper
 * 
 * Solves the common Scanner newline bug (when mixing nextInt and nextLine)
 * and provides robust validation against invalid non-numeric inputs.
 */
public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);

    private InputUtil() {
    }

    /**
     * Reads a non-empty string line from the console.
     */
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[!] Input cannot be empty. Please try again.");
        }
    }

    /**
     * Reads a string line from the console, allowing empty (e.g. for optional updates).
     */
    public static String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Reads an integer with bounds checking.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid number format. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads an integer within a specific minimum and maximum range.
     */
    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.printf("[!] Please enter a number between %d and %d.%n", min, max);
        }
    }
}
