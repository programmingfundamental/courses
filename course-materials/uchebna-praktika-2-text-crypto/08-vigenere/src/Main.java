import java.util.Scanner;

public class Main {
    public static String lettersOnly(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z') c = (char) (c - 'a' + 'A');
            if (c >= 'A' && c <= 'Z') result.append(c);
        }
        return result.toString();
    }

    public static String transform(String text, String key, boolean decrypt) {
        String cleanText = lettersOnly(text);
        String cleanKey = lettersOnly(key);
        if (cleanKey.length() == 0) return "";

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cleanText.length(); i++) {
            int textValue = cleanText.charAt(i) - 'A';
            int keyValue = cleanKey.charAt(i % cleanKey.length()) - 'A';
            int resultValue;
            if (decrypt) {
                resultValue = (textValue - keyValue + 26) % 26;
            } else {
                resultValue = (textValue + keyValue) % 26;
            }
            result.append((char) ('A' + resultValue));
        }
        return result.toString();
    }

    public static String encrypt(String text, String key) {
        return transform(text, key, false);
    }

    public static String decrypt(String text, String key) {
        return transform(text, key, true);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Текст (английски букви): ");
        String text = scanner.nextLine();
        System.out.print("Ключ (английски букви): ");
        String key = scanner.nextLine();

        String encrypted = encrypt(text, key);
        System.out.println("Криптиран текст: " + encrypted);
        if (lettersOnly(key).length() == 0) {
            System.out.println("Ключът трябва да съдържа поне една буква A-Z.");
        } else {
            System.out.println("Декриптиран текст: " + decrypt(encrypted, key));
        }
    }
}
