import java.util.Scanner;

public class Main {
    public static int[] frequencyAnalysis(String text) {
        int[] frequency = new int[26];

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z') {
                c = (char) (c - 'a' + 'A');
            }
            if (c >= 'A' && c <= 'Z') {
                frequency[c - 'A']++;
            }
        }
        return frequency;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();
        int[] frequency = frequencyAnalysis(text);

        for (int i = 0; i < frequency.length; i++) {
            char letter = (char) ('A' + i);
            System.out.println(letter + " -> " + frequency[i]);
        }
    }
}
