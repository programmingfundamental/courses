import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("1. Анализ  2. Нормализиране  3. Обръщане");
            System.out.println("4. Палиндром  5. Честоти  6-11. Шифри  0. Изход");
            String choice = scanner.nextLine();
            // TODO: обработете избора и извикайте съответен метод
            if (choice.equals("0")) {
                running = false;
            }
        }
    }

    public static String normalizeText(String text) {
        // TODO
        return "";
    }

    // TODO: добавете методи за анализ, обръщане, честоти и шифри
}
