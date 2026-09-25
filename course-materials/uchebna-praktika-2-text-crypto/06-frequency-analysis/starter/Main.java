import java.util.Scanner;

public class Main {
    public static int[] frequencyAnalysis(String text) {
        int[] frequency = new int[26];
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // TODO: преобразувайте a-z в A-Z
            // TODO: увеличете frequency[c - 'A'] за букви A-Z
        }
        return frequency;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] frequency = frequencyAnalysis(scanner.nextLine());
        // TODO: изведете таблица за всички букви A-Z
    }
}
