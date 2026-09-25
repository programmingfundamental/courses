import java.util.Scanner;

public class Main {
    public static int findCharacter(String text, char target) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                return i;
            }
        }
        return -1;
    }

    public static int findSubstring(String text, String pattern) {
        if (pattern.length() == 0) {
            return 0;
        }

        for (int start = 0; start <= text.length() - pattern.length(); start++) {
            int offset = 0;
            while (offset < pattern.length()
                    && text.charAt(start + offset) == pattern.charAt(offset)) {
                offset++;
            }
            if (offset == pattern.length()) {
                return start;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Текст: ");
        String text = scanner.nextLine();
        System.out.print("Символ за търсене: ");
        String symbolInput = scanner.nextLine();
        System.out.print("Подниз за търсене: ");
        String pattern = scanner.nextLine();

        if (symbolInput.length() == 0) {
            System.out.println("Не е въведен символ.");
            return;
        }

        System.out.println("Позиция на символа: "
                + findCharacter(text, symbolInput.charAt(0)));
        System.out.println("Позиция на подниза: " + findSubstring(text, pattern));
        System.out.println("-1 означава, че няма съвпадение.");
    }
}
