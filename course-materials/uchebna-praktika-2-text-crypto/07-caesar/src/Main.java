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

    public static String encrypt(String text, int key) {
        String clean = lettersOnly(text);
        StringBuilder result = new StringBuilder();
        int shift = ((key % 26) + 26) % 26;
        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);
            result.append((char) ('A' + (c - 'A' + shift) % 26));
        }
        return result.toString();
    }

    public static String decrypt(String text, int key) {
        return encrypt(text, -key);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Текст (английски букви): ");
        String text = scanner.nextLine();
        System.out.print("Ключ (цяло число): ");
        int key = scanner.nextInt();

        String encrypted = encrypt(text, key);
        System.out.println("Криптиран текст: " + encrypted);
        System.out.println("Декриптиран текст: " + decrypt(encrypted, key));
    }
}
