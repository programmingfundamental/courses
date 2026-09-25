import java.util.Scanner;

public class Main {
    public static int[] xorToNumbers(String text, int key) {
        int[] values = new int[text.length()];
        for (int i = 0; i < text.length(); i++) {
            values[i] = text.charAt(i) ^ key;
        }
        return values;
    }

    public static String xorNumbersToText(int[] values, int key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            result.append((char) (values[i] ^ key));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();
        System.out.print("Числов ключ (например 7): ");
        int key = scanner.nextInt();

        int[] encrypted = xorToNumbers(text, key);
        System.out.print("XOR стойности: ");
        for (int i = 0; i < encrypted.length; i++) {
            if (i > 0) System.out.print(" ");
            System.out.print(encrypted[i]);
        }
        System.out.println();

        System.out.println("Повторен XOR възстановява: "
                + xorNumbersToText(encrypted, key));
    }
}
