import java.util.Scanner;

public class Main {
    public static String encrypt(String text, int columns) {
        if (columns <= 0 || text.length() == 0) return "";
        int rows = (text.length() + columns - 1) / columns;
        char[][] grid = new char[rows][columns];
        int index = 0;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (index < text.length()) {
                    grid[row][column] = text.charAt(index);
                    index++;
                } else {
                    grid[row][column] = 'X';
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                result.append(grid[row][column]);
            }
        }
        return result.toString();
    }

    public static String decrypt(String encrypted, int columns, int originalLength) {
        if (columns <= 0 || originalLength <= 0) return "";
        int rows = (originalLength + columns - 1) / columns;
        char[][] grid = new char[rows][columns];
        int index = 0;

        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                if (index < encrypted.length()) {
                    grid[row][column] = encrypted.charAt(index);
                    index++;
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (result.length() < originalLength) {
                    result.append(grid[row][column]);
                }
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();
        System.out.print("Брой колони: ");
        int columns = scanner.nextInt();

        String encrypted = encrypt(text, columns);
        System.out.println("Транспониран текст: " + encrypted);
        System.out.println("Възстановен текст: "
                + decrypt(encrypted, columns, text.length()));
    }
}
