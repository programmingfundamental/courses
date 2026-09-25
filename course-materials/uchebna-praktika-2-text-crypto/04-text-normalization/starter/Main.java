import java.util.Scanner;

public class Main {
    public static String normalizeText(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // TODO: преобразувайте a-z в A-Z
            // TODO: добавяйте само символи A-Z
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(normalizeText(scanner.nextLine()));
    }
}
