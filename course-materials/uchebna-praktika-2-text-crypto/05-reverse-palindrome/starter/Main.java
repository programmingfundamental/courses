import java.util.Scanner;

public class Main {
    public static String reverse(String text) {
        StringBuilder result = new StringBuilder();
        // TODO: обходете текста от последния индекс към първия
        return result.toString();
    }

    public static boolean isPalindrome(String text) {
        int left = 0;
        int right = text.length() - 1;
        // TODO: пропускайте пунктуация и сравнявайте от двата края
        return false;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        System.out.println(reverse(text));
        System.out.println(isPalindrome(text));
    }
}
