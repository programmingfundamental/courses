import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();

        System.out.println("Дължина: " + text.length());
        System.out.println("Главни букви: " + text.toUpperCase());
        System.out.println("Малки букви: " + text.toLowerCase());
        System.out.println("Символи един по един:");

        for (int i = 0; i < text.length(); i++) {
            System.out.println(text.charAt(i));
        }
    }
}
