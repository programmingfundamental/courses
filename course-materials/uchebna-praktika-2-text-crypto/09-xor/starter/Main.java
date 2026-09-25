import java.util.Scanner;

public class Main {
    public static int[] xorToNumbers(String text, int key) {
        int[] values = new int[text.length()];
        // TODO: запишете text.charAt(i) ^ key на всяка позиция
        return values;
    }

    public static String xorNumbersToText(int[] values, int key) {
        StringBuilder result = new StringBuilder();
        // TODO: приложете същия XOR ключ и преобразувайте стойностите към char
        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        int key = scanner.nextInt();
        // TODO: покажете числовите стойности и възстановения текст
    }
}
